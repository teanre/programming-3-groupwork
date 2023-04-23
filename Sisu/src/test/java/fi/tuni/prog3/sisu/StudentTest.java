package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jamik
 */
public class StudentTest {
    
     private final Student student = new Student("Timo Virtanen", "123", 2021);

    @Test
    public void testGetters() {
        assertEquals("Timo Virtanen", student.getName());
        assertEquals("123", student.getStudentNumber());
        assertEquals(2021, student.getStartingYear());
    }

    @Test
    public void testSetAndGetDegreeProgramme() {
        DegreeProgramme degreeProgramme = new DegreeProgramme("Computer Science", "ComSci", "Comp", 180);
        student.setDegreeProgramme(degreeProgramme);
        assertEquals(degreeProgramme, student.getDegreeProgramme());
    }

    @Test
    public void testAddAndRemoveCompletedCourse() {
        Course course1 = new Course("Java programming", "Java1", "12345", 5,
                "5-10", "Basic and intermediate Java programming", 
                "Student can implement smaller and bigger programs in Java", 
                "Programming in Java", "Programming 1");
        Course course2 = new Course("Java programming 2", "Java2", "123455", 5,
                "5-10", "Intermediate and advanced Java programming", 
                "Student can implement advanced programs in Java alone and in a team", 
                "Programming in Java 2", "Java1");

        student.addCompletedCourse(course1);
        student.addCompletedCourse(course2);

        ArrayList<Course> completedCourses = student.getCompletedCourses();

        assertTrue(completedCourses.contains(course1));
        assertTrue(completedCourses.contains(course2));

        student.removeCompletedCourse(course1);
        assertFalse(completedCourses.contains(course1));
    }

    @Test
    public void testGetCurrentStudent() {
        Student.setCurrentStudent(student);
        assertEquals(student, Student.getCurrentStudent());
    }

    @Test
    public void testCalculateProgress() {
        DegreeProgramme degreeProgramme = new DegreeProgramme("Computer Science", "ComSci", "Comp", 180);
        student.setDegreeProgramme(degreeProgramme);

        Course course1 = new Course("Java programming", "Java1", "12345", 5,
                "5-10", "Basic and intermediate Java programming", 
                "Student can implement smaller and bigger programs in Java", 
                "Programming in Java", "Programming 1");
        Course course2 = new Course("Java programming 2", "Java2", "123455", 5,
                "5-10", "Intermediate and advanced Java programming", 
                "Student can implement advanced programs in Java alone and in a team", 
                "Programming in Java 2", "Java1");

        student.addCompletedCourse(course1);
        student.addCompletedCourse(course2);

        double expectedProgress = ((double) (course1.getMinCredits() + course2.getMinCredits()) / degreeProgramme.getMinCredits()) * 100;

        assertEquals(Math.round(expectedProgress * 100.0) / 100.0, student.calculateProgress());
    }
}
