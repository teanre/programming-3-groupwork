
package fi.tuni.prog3.sisu;

import java.io.Serializable;

/**
 * Simulates course object inherits degree module properties
 * @author terhi
 */
public class Course extends DegreeModule implements Serializable {
    // these attributes are not needed in serialization, hence transient
    private transient String creditRange;  
    private transient String content;
    private transient String outcomes;
    private transient String learningMaterial;
    private transient String prerequisites;

    /**
     * Public constructor that calls the superclass constructor
     * @param name name of the course
     * @param id id of the course
     * @param groupId group id of the course
     * @param minCredits credits of the course
     * @param creditRange
     * @param content
     * @param outcomes
     * @param learningMaterial
     * @param prerequisites
     */
    public Course(String name, String id, String groupId, int minCredits,
            String creditRange, String content, String outcomes, 
            String learningMaterial, String prerequisites) {
        super(name, id, groupId, minCredits);
        this.creditRange = creditRange;
        this.content = content;
        this.outcomes = outcomes;
        this.learningMaterial = learningMaterial;
        this.prerequisites = prerequisites;
    }
    
    /**
     * Getter for creditRange
     * @return String, range of credits a course can have
     */
    public String getCreditRange() {
        return creditRange;
    }

    /**
     * Getter for content
     * @return String, content of a course
     */
    public String getContent() {
        return content;
    }

    /**
     * Getter for outcomes
     * @return String, outcomes of a course
     */
    public String getOutcomes() {
        return outcomes;
    }

    /**
     * Getter for learningMaterial
     * @return String, learning materials of a course
     */
    public String getLearningMaterial() {
        return learningMaterial;
    }

    /**
     * Getter for prerequisites
     * @return String, prerequisites for the course
     */
    public String getPrerequisites() {
        return prerequisites;
    }  
}
