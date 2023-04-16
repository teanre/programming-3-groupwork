
package fi.tuni.prog3.sisu;

/**
 * Simulates course object inherits degree module properties
 * @author terhi
 */
public class Course extends DegreeModule {
  
    /**
     * Public constructor that calls the superclass constructor
     * @param name name of the course
     * @param id id of the course
     * @param groupId group id of the course
     * @param minCredits credits of the course
     */
    public Course(String name, String id, String groupId, int minCredits) {
        super(name, id, groupId, minCredits);

    }

}
