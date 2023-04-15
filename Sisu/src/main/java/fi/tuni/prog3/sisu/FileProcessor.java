
package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Handles the user data of the programme saved in a json file.
 * @author terhi
 */
public class FileProcessor implements iReadAndWriteToFile {
    
    public FileProcessor(){
        
    }
    
    /**
     * Checks if the current user has used the programme before. If they have, 
     * updates 
     * @param fileName
     * @param student
     * @return
     * @throws Exception 
     */
    public boolean isStudentInFile(String fileName, Student student) throws Exception {
        try (Reader reader = new FileReader(fileName)) {
            // read the file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            // get the students array
            JsonArray students = jsonObject.getAsJsonArray("students");
                        
            if (students != null) {

                for (JsonElement studentElement : students) {
                    JsonObject studentObject = studentElement.getAsJsonObject();
                    String studentNr = studentObject.get("studentNumber").getAsString();

                    if(studentNr.equals(student.getStudentNumber())) {
                        //add the completedCourses data from json to this student's completedCourses attribute
                        JsonArray courseArray = studentObject.getAsJsonArray("completedCourses");
                        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
                        Type type = new TypeToken<ArrayList<Course>>(){}.getType();
                        ArrayList<Course> completedCourses =  gson2.fromJson(courseArray, type);
                        
                        //clear and add the completedCourses data
                        student.completedCourses.clear();
                        student.completedCourses.addAll(completedCourses);
                        return true;
                    }
                }
            }
        } catch (IOException e) {
                System.out.println(e);
                return false;
        }
                
        return false;
    }
    
    /**
     * 
     * @param fileName
     * @param student
     * @return
     * @throws Exception 
     */
    public boolean addStudentToFile(String fileName, Student student) throws Exception {
        try (Reader reader = new FileReader(fileName)) {
            // read the file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            // get the students array
            JsonArray students = jsonObject.getAsJsonArray("students");
            // create a jsonobject of the student
            JsonObject studentObject = gson.toJsonTree(student).getAsJsonObject();
            // add student to json
            students.add(studentObject);
            
            // write the updated JSON object back to the file
            try (FileWriter writer = new FileWriter("studentInfo.json")) {
                gson.toJson(jsonObject, writer);
            } catch (IOException e) {
                System.out.println(e);
            }
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }
    
    /**
     * 
     * @param fileName
     * @return
     * @throws Exception 
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
            System.out.println(e);
            return false;
        }
        return true;
    }
    
    /**
     * updates the userdata json file with current user's info while quitting the programme
     * @param fileName
     * @return
     * @throws Exception 
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
            studentsArray = jsonObject.getAsJsonArray("students");

            //find the current users data from json to update
            for (JsonElement studentElement : studentsArray) {
                JsonObject studentObject = studentElement.getAsJsonObject();
                String studentNr = studentObject.get("studentNumber").getAsString();
                
                // current students data found
                if (studentNr.equals(currentStudent.getStudentNumber())) {
                    //get theri compcourseslist json the jsonfile
                    studentJson = studentObject;
                    //tiedoston sulkeminenkiii
                    break;
                }
            }                       
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        
        // get current users updated completed courses from the programme
        ArrayList<Course> completed = currentStudent.getCompletedCourses();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray jsonArray = gson.toJsonTree(completed).getAsJsonArray();
                    
        // replace the completedCourses field in the JSON object with the new JsonArray
        if (studentJson != null) {
            studentJson.remove("completedCourses");
            studentJson.add("completedCourses", jsonArray);
        }
        
        // update the students array in the JSON object with the updated version
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("students", studentsArray);
        
        // write the updated JSON object back to the file
        try (FileWriter writer = new FileWriter("studentInfo.json")) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    
    }

    @Override
    public boolean readFromFile(String fileName) throws Exception {
        try (Reader reader = new FileReader(fileName)) {
            // read the file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            // get the students array
            JsonArray studentsArray = jsonObject.getAsJsonArray("students");

            if (studentsArray != null) {
                for (JsonElement studentElement : studentsArray) {
                    JsonObject studentObject = studentElement.getAsJsonObject();
                    String name = studentObject.get("name").getAsString();
                    String studentNumber = studentObject.get("studentNumber").getAsString();
                    int startingYear = studentObject.get("startingYear").getAsInt();

                    JsonArray coursesArray = studentObject.getAsJsonArray("completedCourses");
                    ArrayList<Course> completedCourses = new ArrayList<>();
                    //String name, String id, String groupId, int minCredits
                    for (JsonElement courseElement : coursesArray) {
                        JsonObject courseObject = courseElement.getAsJsonObject();
                        String courseName = courseObject.get("name").getAsString();
                        String id  = courseObject.get("id").getAsString();
                        String groupId = courseObject.get("groupId").getAsString();
                        int minCredits = courseObject.get("minCredits").getAsInt();
                        completedCourses.add(new Course(courseName, id, groupId, minCredits));
                    }

                    Student student = new Student(name, studentNumber, startingYear);
                    student.completedCourses = completedCourses;

                }
            }           
            
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
