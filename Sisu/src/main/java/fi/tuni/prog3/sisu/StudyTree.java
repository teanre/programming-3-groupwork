package fi.tuni.prog3.sisu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.control.TreeItem;
import static fi.tuni.prog3.sisu.Constants.*;

/**
 * Handles the structure of a degree programme. Fetchess necessary data from API
 * as jsonObject, processes it and saves necessary information on orientations 
 * and courses.
 */
public class StudyTree implements iAPI {
    
    private final HashMap<String, String> orientations = new HashMap<>();
    private final ArrayList<Course> courses = new ArrayList<>();
    
    /**
     * Public constructor to launch fetching and presenting the structure
     * of certain degree programme
     */
    public StudyTree() {

    }
    
    /**
     * Returns the orientations of degree program
     * @return HashMap orientations
     */
    public HashMap<String, String> getOrientations() {
        return this.orientations;
    }    
    
    /**
     * Returns list of courses of degree programme
     * @return ArrayList courses
     */
    public  ArrayList<Course> getCourses() {
        return courses;
    }
    
    /**
     * Fetches orientation options of a degree programme if applicable
     * Uses findOrientations to get the orientations of degree program
     * @param moduleGroupId, groupId of the degreeprogrammes module
     */
    public void fetchOrientations(String moduleGroupId) {
        try {                
            JsonObject res = fetchModule(moduleGroupId);
            // check if the programme has orientation options, first rule object is decisive
            JsonObject firstRuleObj = res.getAsJsonObject(RULE);
            if (firstRuleObj.get(TYPE).getAsString().equals(COMPOSITE_RULE)) {
                JsonArray orientationsArray = firstRuleObj.getAsJsonArray(RULES);
                findOrientations(orientationsArray);
            }        
        } catch (JsonSyntaxException e) {
            System.out.println(EXCEPTION_MSG + e.getMessage());
        }  
    }
    
    /**
     * Initiates fetching the course structure of the degree programme. 
     * Delivers needed data for TreeView to present the data in gui.
     * @param programmeGroupId String, selected degreeprogrammes' groupId
     * @param root TreeItem TreeViews root for displaying the info in gui
     */
    public void getStudyTreeOf(String programmeGroupId, TreeItem<String> root) {
        try {            
            JsonObject res = fetchModule(programmeGroupId);
            
            JsonObject nameObj = res.getAsJsonObject(NAME);
            String programmeName = getNameOfModule(nameObj);
                       
            //System.out.println(programmeName);
            TreeItem<String> currRoot = new TreeItem<>(programmeName);
            root.getChildren().add(currRoot);
            // start digging deeper in the degree structure
            traverseJson(res, currRoot);
        } catch (JsonSyntaxException e) {
            System.out.println(EXCEPTION_MSG + e.getMessage());
        }        
    }
    
    /**
     * Retrieves data from API
     * @param urlString, url of the the source
     * @return a JsonObject containing required data from API
     */
    @Override
    public JsonObject getJsonObjectFromApi(String urlString) {
        try {
        URL url = new URL(urlString);
        // Set the request
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(REQUEST_METHOD_GET);

        // read the request data
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        //return a jsonObject
        JsonElement jsonElement = JsonParser.parseString(response.toString());
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        }

        } catch (IOException e) {
            System.out.println(EXCEPTION_MSG + e.getMessage());
        }
        return null;
    }
    

    /** 
     * Used by many methods to fetch the name of a module. Primarily finnish 
     * version is used, if it's not defined in the json, the english one is returned
     * @param nameObject, JsonObject that has name data
     * @return String, name of the course or module
     */
    private String getNameOfModule(JsonObject nameObject) {
        if (!nameObject.has(FI)) {
            return nameObject.get(EN).getAsString();
        } else {
            return nameObject.get(FI).getAsString();
        }  
    }
  
    /**
     * Helper function for returnOrientations() to get orientation options 
     * from JsonArray. Stores them in the orientations attribute as
     * a HashMap<OrientationName, GroupId>
     * @param orientationOptions, a JsonArray including the orientations
     */
    private void findOrientations(JsonArray orientationOptions) {
        for (JsonElement el : orientationOptions) {
            String groupId = el.getAsJsonObject().get(MODULE_GROUP_ID).getAsString();
                
            JsonObject res = fetchModule(groupId);
            
            JsonObject nameObj = res.getAsJsonObject(NAME);
            String name = getNameOfModule(nameObj);
            orientations.put(name, groupId);
        }
    }
       
    /**
     * Processes course objects from modules, saves the course data for use.
     * Delivers needed data for TreeView to present it in gui.
     * @param courseObject JsonObject includes the course data fetched from API
     * @param parent TreeItem Module that the course is a children of
     */
    private void processCourseJson(JsonObject courseObject, TreeItem<String> parent) {       
        JsonObject nameObj = courseObject.get(NAME).getAsJsonObject();
        String name = getNameOfModule(nameObj);        
        
        //create a course object
        Course c = new Course(
                name, 
                courseObject.get(ID).getAsString(), 
                courseObject.get(GROUP_ID).getAsString(), 
                courseObject.getAsJsonObject(CREDITS).get(MIN).getAsInt(),
                setCreditRange(courseObject),
                setContent(courseObject),
                setLearningMaterial(courseObject),
                setOutcomes(courseObject),
                setPrerequisites(courseObject)
        );
        courses.add(c);
        
        Student currentStudent = Student.getCurrentStudent();
        //if this course is in the completedCourses, update name to be 
        //presented correctly in treeview
        for (var compCourse : currentStudent.getCompletedCourses()) {
            if (c.getGroupId().equals(compCourse.getGroupId())) {
                name = COMPLETED_MARK + name + COMPLETED_MARK;
            }
        }
        
        // add the course to treeview
        TreeItem<String> course = new TreeItem<>(name);
        parent.getChildren().add(course);
        System.out.println("course: " + name + " " /*+ parent.getName()*/);
    }
        
    /**
     * Processes a module's json fetched from API. 
     * @param jsonObject the fetched module data
     * @return JsonArray, if it is not empty, means that the module has still
     * children 
     */
    private JsonArray processModuleJson(JsonObject jsonObject) {
        JsonArray finalArray = null;

        //if the original obj has rule, then dig deeper in the json
        if (jsonObject.has(RULE)) {
            JsonObject ruleObject = jsonObject.get(RULE).getAsJsonObject();  
            if (ruleObject.has(RULE)) {    
                JsonObject innerRule = ruleObject.getAsJsonObject(RULE); 
                JsonArray ruleArray = innerRule.getAsJsonArray(RULES); 
                for (JsonElement jsonEl : ruleArray) {
                    JsonObject jsonObj = jsonEl.getAsJsonObject();
                    if (jsonObj.has(RULES)) {
                         finalArray = jsonObj.getAsJsonArray(RULES);                    
                    } else {
                        finalArray = ruleArray;
                    }
                }
            } else {
                 finalArray = ruleObject.getAsJsonArray(RULES);
            }
        } else {
            finalArray = jsonObject.getAsJsonArray(RULES);
        }

        return finalArray;
    }   
    
    /**
     * Helper to recursively go through the course structure of a degree programme/orientation
     * @param json JsonObject the degree programme/orientation data 
     * @param parent TreeItem parent of the t
     */
    private void traverseJson(JsonObject json, TreeItem<String> parent) {                
        JsonObject data; 
        String groupId;
        String moduleName;
        
        String type = json.get(TYPE).getAsString();
        JsonArray jsonArray = null;
        
        //these are optional courses/modules, not implemented yet
        if (type.equals(ANY_COURSE_UNIT_RULE) || type.equals(ANY_MODULE_RULE) ) {
            System.out.println("course/mod: Vapaasti valittavat kurssit/moduulit");
        } else if (type.equals(COURSE_UNIT_RULE)) {
            groupId = json.get(COURSE_UNIT_GROUP_ID).getAsString();           
            data = fetchCourseModule(groupId);
            processCourseJson(data, parent);
        } else { 
            if (type.equals(MODULE_RULE)) {
                groupId = json.get(MODULE_GROUP_ID).getAsString();
                data = fetchModule(groupId);
                
                JsonObject nameObj = data.getAsJsonObject(NAME);
                moduleName = getNameOfModule(nameObj);

                System.out.println("mod: " + moduleName + " " /*+ currentModule.getName()*/);

                TreeItem<String> oldParent = parent;               
                //reassign parent
                parent = new TreeItem<>(moduleName);
                
                oldParent.getChildren().add(parent);
                jsonArray = processModuleJson(data);
            } else {
                jsonArray = processModuleJson(json);
            }
            
            // note for later: if jsonArray is null, is freely selectable studies
            // go through every element in the json array until reached the leaves
            if (jsonArray != null) {
               for(JsonElement el : jsonArray) {
                    if (el.isJsonObject()) {
                        JsonObject obj = el.getAsJsonObject();                   
                        //skip AnyModuleRules at this point, they would be optional modules
                        if (!obj.get(TYPE).getAsString().equals(ANY_MODULE_RULE)) {
                            traverseJson(obj, parent);
                        }
                    }
                }  
            }                   
        }       
    }

    /**
     * Fetches a modules data from API
     * @param moduleGroupId, the groupId of the module
     * @return the module data as a json object
     */
    private JsonObject fetchModule(String moduleGroupId) {       
        String urlString = createUrl(moduleGroupId, MODULE);               
        return getJsonObjectFromApi(urlString);
    }
    
    /**
     * Fetches a course modules data from API
     * @param courseModuleGroupId, the groupId of the course
     * @return JsonObject, the course data as a json object
     */
    private JsonObject fetchCourseModule(String courseModuleGroupId) {       
        String urlString = createUrl(courseModuleGroupId, COURSE);            
        return getJsonObjectFromApi(urlString);
    }
   
    /**
     * Creates appropriate url for data fecthing from API
     * @param groupId String, groupId of module or course
     * @param type String, clarifies type of data fetched
     * @return String, the correct url
     */
    private String createUrl(String groupId, String type){
        if(type.equals(MODULE)) {
            return "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=" + groupId + "&universityId=tuni-university-root-id";
        } else { // otherwise is a course, has different url
            return "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=" + groupId + "&universityId=tuni-university-root-id";
        }
    }

    /**
     * Helper to add creditRange for a Course
     * @param courseObject, JsonObject containing the course data
     * @return String, credit range for the course
     */
    private String setCreditRange(JsonObject courseObject) {
        var Obj = courseObject.get(CREDITS);
        if(!Obj.isJsonObject()){
            return "- Credits";
        } else {
            var creditsObj = Obj.getAsJsonObject();
            JsonElement minCreditsOf = creditsObj.get(MIN);
            var maxCredits = creditsObj.get(MAX);
            if(minCreditsOf.isJsonNull() && maxCredits.isJsonNull()){
                return "- Credits";
            } else if(minCreditsOf.isJsonNull()){
                return maxCredits + " Credits";
            } else if(maxCredits.isJsonNull()){
                return minCreditsOf + " Credits";
            } else {        
                if(minCreditsOf.getAsInt() == maxCredits.getAsInt()) {
                    return minCreditsOf.getAsInt() + " Credits";
                } else {
                    return minCreditsOf.getAsInt() + "-" + maxCredits.getAsInt() + " Credits";
                }
            }
        }
    }
    
    /**
     * Helper to add prerequisites for a Course
     * @param courseObject, JsonObject containing the course data
     * @return String, content for the course
     */
    private String setContent(JsonObject courseObject) {
        var Obj = courseObject.get("content");
        if(!Obj.isJsonObject()){
            return "Content: -";
        } else {
            var contentObj = Obj.getAsJsonObject();
            String data = getNameOfModule(contentObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return "Content: " + text;
        }
    }
    
    /**
     * Helper to add prerequisites for a Course
     * @param courseObject, JsonObject containing the course data
     * @return String, outcomes for the course
     */
    private String setOutcomes(JsonObject courseObject) {
        var Obj = courseObject.get("outcomes");
        if(!Obj.isJsonObject()){
           return "Outcomes: -";
        } else {
            var outcomesObj = Obj.getAsJsonObject();
            String data = getNameOfModule(outcomesObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return "Outcomes: " + text;
        }
    }
    
    /**
     * Helper to add prerequisites for a Course
     * @param courseObject, JsonObject containing the course data
     * @return String, learning materials for the course
     */
    private String setLearningMaterial(JsonObject courseObject) {var Obj = courseObject.get("learningMaterial");
        if(!Obj.isJsonObject()){
            return "Learning Material: -";
        } else {
            var learningObj = Obj.getAsJsonObject();
            String data = getNameOfModule(learningObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return "Learning Material: " + text;
        }
    }
    
    /**
     * Helper to add prerequisites for a Course
     * @param courseObject, JsonObject containing the course data
     * @return String, prerequisites for the course
     */
    private String setPrerequisites(JsonObject courseObject) {
        var Obj = courseObject.get("prerequisites");
        if(!Obj.isJsonObject()){
            return "Prerequisites: -";
        } else {
            var prerequisitesObj = Obj.getAsJsonObject();
            String data = getNameOfModule(prerequisitesObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return "Prerequisites: " + text;
        }
    }       
}
