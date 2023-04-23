package fi.tuni.prog3.sisu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jamik
 */
public class CourseTest {
    
    private final Course course = new Course("Java programming", "Java1", "12345", 5,
                "5-10", "Basic and intermediate Java programming", 
                "Student can implement smaller and bigger programs in Java", 
                "Programming in Java", "Programming 1");
 
    @Test
    public void testGetCreditRange() {
        assertEquals("5-10", course.getCreditRange());
    }

    @Test
    public void testGetContent() {
        assertEquals("Basic and intermediate Java programming", course.getContent());
    }

    @Test
    public void testGetOutcomes() {
        assertEquals("Student can implement smaller and bigger programs in Java", course.getOutcomes());
    }

    @Test
    public void testGetLearningMaterial() {
        assertEquals("Programming in Java", course.getLearningMaterial());
    }

    @Test
    public void testGetPrerequisites() {
        assertEquals("Programming 1", course.getPrerequisites());
    }
}
