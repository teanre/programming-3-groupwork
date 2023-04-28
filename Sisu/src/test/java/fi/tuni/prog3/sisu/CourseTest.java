package fi.tuni.prog3.sisu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testfile for the class Course.
 * @author jamik
 */
public class CourseTest {
    
    private final Course course = new Course("Java programming", "Java1", "12345", 5,
                "5-10", "Basic and intermediate Java programming", 
                "Student can implement smaller and bigger programs in Java", 
                "Programming in Java", "Programming 1");
 
    /**
     * Test of getCreditRange, of class Course.
     */
    @Test
    public void testGetCreditRange() {
        assertEquals("5-10", course.getCreditRange());
    }

    /**
     * Test of getContent, of class Course.
     */
    @Test
    public void testGetContent() {
        assertEquals("Basic and intermediate Java programming", course.getContent());
    }

    /**
     * Test of getOutcomes, of class Course.
     */
    @Test
    public void testGetOutcomes() {
        assertEquals("Student can implement smaller and bigger programs in Java", course.getOutcomes());
    }

    /**
     * Test of getLearningMaterial, of class Course.
     */
    @Test
    public void testGetLearningMaterial() {
        assertEquals("Programming in Java", course.getLearningMaterial());
    }

    /**
     * Test of getPrerequisities, of class Course.
     */
    @Test
    public void testGetPrerequisites() {
        assertEquals("Programming 1", course.getPrerequisites());
    }
}
