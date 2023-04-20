
package fi.tuni.prog3.sisu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Simulates course object inherits degree module properties
 * @author terhi
 */
public class Course extends DegreeModule {
    private static String creditRange;  
    private static String content;
    private static String outcomes;
    private static String learningMaterial;
    private static String prerequisites;

    public String getCreditRange() {
        return creditRange;
    }

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
    
    public void setCreditRange(JsonObject courseObject) {
        var Obj = courseObject.get("credits");
        if(!Obj.isJsonObject()){
            Course.creditRange = "- Credits";
        } else {
            var creditsObj = Obj.getAsJsonObject();
            JsonElement minCreditsOf = creditsObj.get("min");
            var maxCredits = creditsObj.get("max");
            if(minCreditsOf.isJsonNull() && maxCredits.isJsonNull()){
                Course.creditRange = "- Credits";
            } else if(minCreditsOf.isJsonNull()){
                Course.creditRange = maxCredits + " Credits";
            } else if(maxCredits.isJsonNull()){
                Course.creditRange = minCreditsOf + " Credits";
            } else {        
                if(minCreditsOf.getAsInt() == maxCredits.getAsInt()) {
                    Course.creditRange = minCreditsOf.getAsInt() + " Credits";
                } else {
                    Course.creditRange = minCreditsOf.getAsInt() + "-" + maxCredits.getAsInt() + " Credits";
                }
            }
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(JsonObject courseObject) {
        var Obj = courseObject.get("content");
        if(!Obj.isJsonObject()){
            Course.content = "Content: -";
        } else {
            var contentObj = Obj.getAsJsonObject();
            String data = getNameOfMod(contentObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            Course.content = "Content: " + text;
        }
    }

    public String getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(JsonObject courseObject) {
        var Obj = courseObject.get("outcomes");
        if(!Obj.isJsonObject()){
            Course.outcomes = "Outcomes: -";
        } else {
            var outcomesObj = Obj.getAsJsonObject();
            String data = getNameOfMod(outcomesObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            Course.outcomes = "Outcomes: " + text;
        }
    }

    public String getLearningMaterial() {
        return learningMaterial;
    }

    public void setLearningMaterial(JsonObject courseObject) {var Obj = courseObject.get("learningMaterial");
        if(!Obj.isJsonObject()){
            Course.learningMaterial = "Learning Material: -";
        } else {
            var learningObj = Obj.getAsJsonObject();
            String data = getNameOfMod(learningObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            Course.learningMaterial = "Learning Material: " + text;
        }
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(JsonObject courseObject) {
        var Obj = courseObject.get("prerequisites");
        if(!Obj.isJsonObject()){
            Course.prerequisites = "Prerequisites: -";
        } else {
            var prerequisitesObj = Obj.getAsJsonObject();
            String data = getNameOfMod(prerequisitesObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            Course.prerequisites = "Prerequisites: " + text;
        }
    }
    
    
     /** 
     * Helper to fetch fetch the name of a module. Primarily finnish one,
     * is used, if it's not defined in the json the english one
     * @param nameObject, JsonObject that has name data
     * @return String, name of the course or module. 
     */
    private String getNameOfMod(JsonObject nameObject) {
        String nameOfMod;
        if (nameObject.has("fi")) {
            nameOfMod = nameObject.get("fi").getAsString();
        } else {
            nameOfMod = nameObject.get("en").getAsString();
        }  
        return nameOfMod;
    }
}
