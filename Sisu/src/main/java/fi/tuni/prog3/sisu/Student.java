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
     * removes course completion of student
     * @param course course to be removed
     */
    public void removeCompletedCourse(Course course) {
        completedCourses.remove(course);
    }
    
    /**
     * gets the current student using the application
     * @return current student 
     */
    public static Student getCurrentStudent() {
        return currentStudent;
    }
    
    /**
     * sets the user as current student
     * @param student current student
     */
    public static void setCurrentStudent(Student student) {
        currentStudent = student;
    }
}
