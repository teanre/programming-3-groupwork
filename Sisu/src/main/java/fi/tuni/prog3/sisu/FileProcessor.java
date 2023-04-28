package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static fi.tuni.prog3.sisu.Constants.FILENAME;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import static fi.tuni.prog3.sisu.Constants.*;

/**
 * Handles the user data of the programme saved in a json file.
 * @author terhi
 */
public class FileProcessor implements iReadAndWriteToFile {
    
    /**
     * Constructor to initiate user data handling.
     */
    public FileProcessor(){
        
    }
    
    /**
     * Saves a new users data to json file.
     * @param fileName, String, name of the file where the data is stored
     * @param student, Student object, user to add to file
     * @return true if successful, false otherwise
     * @throws Exception if reading or writing to file is not successful
     */
    public boolean addStudentToFile(String fileName, Student student) throws Exception {
        try (Reader reader = new FileReader(fileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            // get the students array
            JsonArray students = jsonObject.getAsJsonArray(STUDENTS);
            // create a jsonobject of the student
            JsonObject studentObject = gson.toJsonTree(student).getAsJsonObject();
            // add student to json
            students.add(studentObject);
            
            // write the updated JSON object back to the file
            try (FileWriter writer = new FileWriter(FILENAME)) {
                gson.toJson(jsonObject, writer);
            } catch (IOException e) {
                System.out.println(WRITE_ERROR + e.getMessage());
                return false;
            }
        } catch (IOException e) {
            System.out.println(READ_ERROR + e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Creates a new file to store user data if it does not exist. Adds the 
     * current user to the file
     * @param fileName String, name of source file
     * @return boolean, true if successful, false otherwise
     * @throws Exception if creating a file is not successful
     */
    public boolean createNewFile(String fileName) throws Exception {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ArrayList<Student> students = new ArrayList<>();
            Student st = Student.getCurrentStudent();
            students.add(st);
            gson.toJson(new StudentList(students), writer);
            writer.close();
        } catch (IOException e) {
            System.out.println(FILE_CREATE_ERROR + e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Updates the userdata json file with current user's info 
     * @param fileName String, the source json file's name
     * @return true if successful, false if not
     * @throws Exception if reading or writing to file is not successful
     */
    @Override
    public boolean writeToFile(String fileName) throws Exception {
        //add student and their courses to file
        JsonObject studentJson = null;
        JsonArray studentsArray = null;
        Student currentStudent = Student.getCurrentStudent();
        try (Reader reader = new FileReader(fileName)) {
            // first read the existing file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            // get the students array
            studentsArray = jsonObject.getAsJsonArray(STUDENTS);

            //find the current users data from json to update
            for (JsonElement studentElement : studentsArray) {
                JsonObject studentObject = studentElement.getAsJsonObject();
                String studentNr = studentObject.get(STUDENT_NR).getAsString();
                
                // current students data found
                if (studentNr.equals(currentStudent.getStudentNumber())) {
                    // set as the studenJson to reprocess the completedCourses field
                    studentJson = studentObject;
                    reader.close();
                    break;
                }
            }                       
        } catch (IOException e) {
            System.out.println(WRITE_ERROR + e.getMessage());
            return false;
        }
        
        // get current users completed courses from the programme
        ArrayList<Course> completed = currentStudent.getCompletedCourses();
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonArray jsonArray = gson.toJsonTree(completed).getAsJsonArray();
                    
        // replace the completedCourses field in the JSON object with the new JsonArray
        if (studentJson != null) {
            studentJson.remove(COMPLETED_COURSES);
            studentJson.add(COMPLETED_COURSES, jsonArray);
        }
        
        // update the students array in the JSON object with the updated version
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(STUDENTS, studentsArray);
        
        // write the updated JSON object back to the file
        try (FileWriter writer = new FileWriter(FILENAME)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            System.out.println(WRITE_ERROR + e.getMessage());
            return false;
        }
        return true;    
    }

    /**
     * Reads current user's data from the json file and stores necessary info as 
     * Student and Course objects 
     * @param fileName, string, name of source file
     * @return true, if user has been found from file, false if not. returns
     * false also if there is a problem reading the file
     * @throws Exception 
     */
    @Override
    public boolean readFromFile(String fileName) throws Exception {
        try (Reader reader = new FileReader(fileName)) {
            // read the file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            // get the students array
            JsonArray studentsArray = jsonObject.getAsJsonArray(STUDENTS);
            Student user = Student.getCurrentStudent();
            if (studentsArray != null) {
                for (JsonElement studentElement : studentsArray) {
                    JsonObject studentObject = studentElement.getAsJsonObject();
                   // String name = studentObject.get("name").getAsString();
                    String studentNumber = studentObject.get(STUDENT_NR).getAsString();
                    if (studentNumber.equals(user.getStudentNumber())) { //we have found current user's data
                        JsonArray coursesArray = studentObject.getAsJsonArray(COMPLETED_COURSES);
                        ArrayList<Course> completedCourses = new ArrayList<>();

                        for (JsonElement courseElement : coursesArray) {
                            JsonObject courseObject = courseElement.getAsJsonObject();
                            String courseName = courseObject.get(NAME).getAsString();
                            String id  = courseObject.get(ID).getAsString();
                            String groupId = courseObject.get(GROUP_ID).getAsString();
                            int minCredits = courseObject.get(MIN_CREDITS).getAsInt();
                            completedCourses.add(new Course(courseName, id, groupId, minCredits,
                                                "","","","","")); //the other attributes not necessary for completedcourses data
                        }
                        for (var c : completedCourses) {
                            user.addCompletedCourse(c);
                        }
                        return true;// no need to iterate array further, user found                        
                    }
                }
            }                      
        } catch (IOException e) {
            System.out.println(READ_ERROR + e.getMessage());
            return false;
        }
        // if the user hasn't been found, return false
        return false; 
    }
    
}
