package fi.tuni.prog3.sisu;


import java.util.ArrayList;

/**
 * Class to simulate Student object
 * @author Jami
 */
public class Student {
    private final String name;
    private final String studentNumber;
    private final int startingYear;
    
    // to keep track of their completed courses. public for gson
    private final ArrayList<Course> completedCourses = new ArrayList<>();
    
    // the current user's chosen degree programme
    private static DegreeProgramme currentProgramme;

    // variable to keep track of who is using the app right now
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
     * @param currentProgramme the current programme
     */
    public void setDegreeProgramme(DegreeProgramme currentProgramme) {
        this.currentProgramme = currentProgramme;
    }
    
    /**
     * Getter for current user's selected degree programme
     * @return DegreeProgramme object 
     */
    public DegreeProgramme getDegreeProgramme() {
        return currentProgramme;
    }

    /**
     * Getter for the students name
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Getter for the student number
     * @return student number
     */
    public String getStudentNumber() {
        return studentNumber;
    }
    
    /**
     * Getter for starting year of student
     * @return int, starting year
     */
    public int getStartingYear() {
        return startingYear;
    }
    
    /**
     * Adds knowledge of completed course for student to data structure
     * @param course completed course
     */
    public void addCompletedCourse(Course course) {
        completedCourses.add(course);
    }
    
    /**
     * Getter for the completed courses of student
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
     * Gets the currently active Student object i.e. the current user
     * @return current user as Student object
     */
    public static Student getCurrentStudent() {
        return currentStudent;
    }
    
    /**
     * Sets the Student object as currently active user of programme
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
     * Calculates percentage of studies completed by student
     * @return double, percentage of studies completed, with accuracy of two decimals
     * if the study programme does not have minimum credits set, will return 0.00
     */
    public double calculateProgress(){
        double denominator = (double) getSumOfCompletedCourses();
        double divisor = (double) this.getDegreeProgramme().getMinCredits();
        if (divisor > 0) {
           double result = (denominator /divisor )*100;
           return Math.round(result * 100.0) / 100.0;   
        } else { //if degree programme has no minCredits set/it is 0, can't be used as divisor
            return 0.00;
        }             
    }
}
