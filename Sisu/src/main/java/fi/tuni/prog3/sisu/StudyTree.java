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

/**
 * Handles the degree programmes structure. Fetchess necessary data from API as
 * jsonObject, processes it and saves necessary information on orientations 
 * and courses.
 */
public class StudyTree implements iAPI {
    
    private HashMap<String, String> orientations = new HashMap<>();
    private ArrayList<Course> courses = new ArrayList<>();
    
    /**
     * Public constructor to launch fetching and presenting the structure
     * of certain degree programme
     * 
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
            JsonObject firstRuleObj = res.getAsJsonObject("rule");
            if (firstRuleObj.get("type").getAsString().equals("CompositeRule")) {
                JsonArray orientationsArr = firstRuleObj.getAsJsonArray("rules");
                findOrientations(orientationsArr);
            }        
        } catch (JsonSyntaxException e) {
            System.out.println(e);
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
            
            JsonObject nameObj = res.getAsJsonObject("name");
            String programmeName = getNameOfModule(nameObj);
                       
            //System.out.println(programmeName);
            TreeItem<String> currRoot = new TreeItem<>(programmeName);
            root.getChildren().add(currRoot);
            // start digging deeper in the degree structure
            traverseJson(res, currRoot);
        } catch (JsonSyntaxException e) {
            System.out.println(e);
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
        con.setRequestMethod("GET");

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
            System.out.println("Exception occurred: " + e.getMessage());
        }
        return null;
    }
    

    /** 
     * Used by many methods to fetch the name of a module. Primarily finnish one,
     * is used, if it's not defined in the json the english one
     * @param nameObject, JsonObject that has name data
     * @return String, name of the course or module. 
     */
    private String getNameOfModule(JsonObject nameObject) {
        String name;
        if (nameObject.has("fi")) {
            name = nameObject.get("fi").getAsString();
        } else {
            name = nameObject.get("en").getAsString();
        }  
        return name;
    }
  
    /**
     * Helper function for returnOrientations() to get orientation options 
     * from JsonArray. Stores them in the orientations attribute as
     * a HashMap<OrientationName, GroupId>
     * @param orientationOptions, a JsonArray including the orientations
     */
    private void findOrientations(JsonArray orientationOptions) {
        for (JsonElement el : orientationOptions) {
            String groupId = el.getAsJsonObject().get("moduleGroupId").getAsString();
                
            JsonObject res = fetchModule(groupId);//getJsonObjectFromApi(urlString);
            
            JsonObject nameObj = res.getAsJsonObject("name");
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
        JsonObject nameObj = courseObject.get("name").getAsJsonObject();
        String name = getNameOfModule(nameObj);        
        
        //create a course object
        Course c = new Course(
                name, 
                courseObject.get("id").getAsString(), 
                courseObject.get("groupId").getAsString(), 
                courseObject.getAsJsonObject("credits").get("min").getAsInt(),
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
                name = "**" + name + "**";
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
        if (jsonObject.has("rule")) {
            JsonObject ruleObject = jsonObject.get("rule").getAsJsonObject();  
            if (ruleObject.has("rule")) {    
                JsonObject innerRule = ruleObject.getAsJsonObject("rule"); 
                JsonArray ruleArray = innerRule.getAsJsonArray("rules"); 
                for (JsonElement jsonEl : ruleArray) {
                    JsonObject jsonObj = jsonEl.getAsJsonObject();
                    if (jsonObj.has("rules")) {
                         finalArray = jsonObj.getAsJsonArray("rules");                    
                    } else {
                        finalArray = ruleArray;
                    }
                }
            } else {
                 finalArray = ruleObject.getAsJsonArray("rules");
            }
        } else {
            finalArray = jsonObject.getAsJsonArray("rules");
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
        
        String type = json.get("type").getAsString();
        JsonArray jsonArray = null;
        
        //these are optional courses/modules, not implemented yet
        if (type.equals("AnyCourseUnitRule") || type.equals("AnyModuleRule") ) {
            System.out.println("course/mod: Vapaasti valittavat kurssit/moduulit");
        } else if (type.equals("CourseUnitRule")) {
            groupId = json.get("courseUnitGroupId").getAsString();           
            data = fetchCourseModule(groupId);
            processCourseJson(data, parent);
        } else { 
            if (type.equals("ModuleRule")) {
                groupId = json.get("moduleGroupId").getAsString();
                data = fetchModule(groupId);
                
                JsonObject nameObj = data.getAsJsonObject("name");
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
                        if (!obj.get("type").getAsString().equals("AnyModuleRule")) {
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
        String urlString = createUrl(moduleGroupId, "Module");               
        return getJsonObjectFromApi(urlString);
    }
    
    /**
     * Fetches a course modules data from API
     * @param courseModuleGroupId, the groupId of the course
     * @return JsonObject, the course data as a json object
     */
    private JsonObject fetchCourseModule(String courseModuleGroupId) {       
        String urlString = createUrl(courseModuleGroupId, "Course");            
        return getJsonObjectFromApi(urlString);
    }
   
    /**
     * Creates appropriate url for data fecthing from API
     * @param groupId String, groupId of module or course
     * @param type String, clarifies type of data fetched
     * @return String, the correct url
     */
    private String createUrl(String groupId, String type){
        String urlString;
        if(type.equals("Module")) {
            urlString = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=" + groupId + "&universityId=tuni-university-root-id";
        } else { // otherwise is a course, has different url
            urlString = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=" + groupId + "&universityId=tuni-university-root-id";
        }
        return urlString;
    }

    /**
     * Helper to add creditRange for a Course
     * @param courseObject, JsonObject containing the course data
     * @return String, credit range for the course
     */
    private String setCreditRange(JsonObject courseObject) {
        var Obj = courseObject.get("credits");
        if(!Obj.isJsonObject()){
            return "- Credits";
        } else {
            var creditsObj = Obj.getAsJsonObject();
            JsonElement minCreditsOf = creditsObj.get("min");
            var maxCredits = creditsObj.get("max");
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
