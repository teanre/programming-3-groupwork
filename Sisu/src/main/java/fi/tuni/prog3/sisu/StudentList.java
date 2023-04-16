
package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * Student registered to the application. Data public for GSON use
 * @author terhi
 */
public class StudentList {
    public ArrayList<Student> students;

    /**
     * public constructor for students registered
     * @param students ArrayList students
     */
    public StudentList(ArrayList<Student> students) {
        this.students = students;
    }
}
