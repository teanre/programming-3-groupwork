package fi.tuni.prog3.sisu;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Class to simulate student object
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
    
    
    public Student(String name, String studentNumber, int startingYear) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.startingYear = startingYear;
    }
    
    public String getName() {
        return name;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public int getStartingYear() {
        return startingYear;
    }
    
    public void addCompletedCourse(Course course) {
        completedCourses.add(course);
    }
    
    public ArrayList<Course> getCompletedCourses() {
        return completedCourses;
    }
    
    public void removeCompletedCourse(Course course) {
        completedCourses.remove(course);
    }
    
    public static Student getCurrentStudent() {
        return currentStudent;
    }
    
    public static void setCurrentStudent(Student student) {
        currentStudent = student;
    }
}
