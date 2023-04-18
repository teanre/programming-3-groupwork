package fi.tuni.prog3.sisu;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Class to simulate student object
 * @author Jami
 */
public class Student {
    // SerializedNames to be able to use these in gson directly
    @SerializedName("name")
    public String name;
    @SerializedName("studentNumber")
    public String studentNumber;
    @SerializedName("startingYear")
    public int startingYear;
    
    // their degreeprogramme
    private DegreeProgramme currentProgramme;
    
    //to keep track of their completed courses
    public ArrayList<Course> completedCourses = new ArrayList<>();
    
    //keeps track of who is using the app right now
    private static Student currentStudent;
    
    /**
     * Public constructor creates a student object
     * @param name name of the student
     * @param studentNumber student number of the student
     * @param startingYear starting year of the student
     */
    public Student(String name, String studentNumber, int startingYear) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.startingYear = startingYear;
    }

    /**
     * Sets current degree programme of the student using the application
     * @param degreeProgramme 
     */
    public void setDegreeProgramme(DegreeProgramme degreeProgramme) {
        this.currentProgramme = degreeProgramme;
    }

    public DegreeProgramme getDegreeProgramme() {
        return currentProgramme;
    }

    /**
     * return the students name
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * returns the student number
     * @return student number
     */
    public String getStudentNumber() {
        return studentNumber;
    }
    
    /**
     * returns the starting year of student
     * @return starting year
     */
    public int getStartingYear() {
        return startingYear;
    }
    
    /**
     * adds knowledge of completed course for student
     * @param course completed course
     */
    public void addCompletedCourse(Course course) {
        completedCourses.add(course);
    }
    
    /**
     * returns the completed courses of student
     * @return completed courses
     */
    public ArrayList<Course> getCompletedCourses() {
        return completedCourses;
    }
    
    /**
     * Removes course completion of student
     * @param course course to be removed
     */
    public void removeCompletedCourse(Course course) {
        completedCourses.remove(course);
    }
    
    /**
     * Gets the current student using the application
     * @return current student 
     */
    public static Student getCurrentStudent() {
        return currentStudent;
    }
    
    /**
     * Sets the user as current student
     * @param student current student
     */
    public static void setCurrentStudent(Student student) {
        currentStudent = student;
    }
    
    /**
     * Calculates how many ects student has completed altogether.
     * @return int, the sum of credits
     */
    public int getSumOfCompletedCourses(){
        int sum = 0;
        for(var c : this.completedCourses) {
            sum += c.getMinCredits();
        }
        return sum;
    }
    
    /**
     * Calculates percentage of studies completed.
     * @return double, percentage of studies completed, with accuracy of two decimals
     */
    public double calculateProgress(){
        double denominator = (double) getSumOfCompletedCourses();
        double divisor = (double) this.getDegreeProgramme().getMinCredits();
        double result = (denominator /divisor )*100;
        double roundedResult;
        return roundedResult  = Math.round(result * 100.0) / 100.0;
        
    }
}
