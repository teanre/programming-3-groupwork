
package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * List of students registered to the application, required to write
 * student data to new json file
 * @author terhi
 */
public class StudentList {
    private final ArrayList<Student> students;

    /**
     * public constructor for students registered
     * @param students ArrayList students
     */
    public StudentList(ArrayList<Student> students) {
        this.students = students;
    }
}
