
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
 * Handles the degree programs structure
 */
public class getStudyTree implements iAPI {
    
    private HashMap<String, String> orientations = new HashMap<>();
    private HashMap<String, ArrayList<String>> coursesOfProgramme = new HashMap<>();
    private ArrayList<Course> courses = new ArrayList<>();
    
    /**
     * Public constructor for launching fetching structure of certain degree
     * @param name String name of the degree
     */
    public getStudyTree(String name) {

    }
    
    /**
     * 
     * @param nameObject, JsonObject that has name data
     * @return String, name of the course or module. primarily finnish one, if it's not 
     * given in JsonObject, then return  the english one
     */
    public String getNameOfMod(JsonObject nameObject) {
        String name;
        if (nameObject.has("fi")) {
            name = nameObject.get("fi").getAsString();
        } else {
            name = nameObject.get("en").getAsString();
        }  
        return name;
    }
    
    /**
     * Stores data of a degree programmes orientation options in a HashMap<OrientationName, GroupId>
     * @param orientationOptions, a JsonArray including the orientations
     */
    public void findOrientations(JsonArray orientationOptions) {
        for (JsonElement el : orientationOptions) {
            String groupId = el.getAsJsonObject().get("moduleGroupId").getAsString();

            String urlString = createUrlString(groupId, "Module");
                
            JsonObject res = getJsonObjectFromApi(urlString);
            
            JsonObject nameObj = res.getAsJsonObject("name");
            String name = getNameOfMod(nameObj);
            orientations.put(name, groupId);
        }
    }
    
    /**
     * Uses findOrientations to get the orientations of degree program
     * @param modName 
     */
    public void returnOrientations(String modName) {
        try {
            JsonObject res;
            String urlString = createUrlString(modName, "Module");
                
            res = getJsonObjectFromApi(urlString);
            // check if the programme has orientation options, first rule object is decisive
            JsonObject firstRuleObj = res.getAsJsonObject("rule");
            if (firstRuleObj.get("type").getAsString().equals("CompositeRule")) {
                JsonArray orientationsArr = firstRuleObj.getAsJsonArray("rules");
                findOrientations(orientationsArr);
            }/*else{
                orientations.put("No Separate Orientation", modName);
            }*/
            
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }  
    }
    
    /**
     * gets the structure of the study
     * @param program String selected  degree
     * @param root TreeItem TreeViews root
     */
    public void getStudyTreeOf(String program, TreeItem<String> root) {
        try {
            JsonObject res;
            
            String urlString = createUrlString(program, "Module");               
            res = getJsonObjectFromApi(urlString);
            
            JsonObject nameObj = res.getAsJsonObject("name");
            String programmeName = getNameOfMod(nameObj);
                       
            System.out.println(programmeName);
            TreeItem<String> currRoot = new TreeItem<>(programmeName);
            root.getChildren().add(currRoot);
            traverseJson(res, currRoot);
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }        
    }
    
    /**
     * Processes course objects from modules, saves the course data for use
     * @param courseObject JsonObject includes the course data fetched from API
     * @param parent TreeItem Module that the course is a children of
     */
    public void processCourseJson(JsonObject courseObject, TreeItem<String> parent) {       
        JsonObject nameObj = courseObject.get("name").getAsJsonObject();
        String name = getNameOfMod(nameObj);
        
        // getting some extra data about courses and saving them
        ArrayList<String> data = new ArrayList<>();
        data.add(addCredits(courseObject));
        data.add(addContent(courseObject));
        data.add(addOutComes(courseObject));
        data.add(addLearningMaterial(courseObject));
        data.add(addPrerequisites(courseObject));
        
        coursesOfProgramme.put(name, data);
        
        //create a course object
        Course c = new Course(
                name, 
                courseObject.get("id").getAsString(), 
                courseObject.get("groupId").getAsString(), 
                courseObject.getAsJsonObject("credits").get("min").getAsInt()
        );
        courses.add(c);
        
        Student currentStudent = Student.getCurrentStudent();
        //if this course is in the completedCourses, make name like so to be presented correctly in treeview
        for (var compCourse : currentStudent.getCompletedCourses()) {
            if (c.getGroupId().equals(compCourse.getGroupId())) {
                name = "**" + name + "**";
            }
        }
        
        TreeItem<String> course = new TreeItem<>(name);
        parent.getChildren().add(course);
       // CourseTree course = new CourseTree(name);
        System.out.println("course: " + name + " " /*+ parent.getName()*/);
    }
    
    /**
     * saves the data field from JsonObject for later use
     * @param JsonObject includes the course data fetched from API
     * @return String text to be displayed
     */
    private String addPrerequisites(JsonObject courseObject) {
        var Obj = courseObject.get("prerequisites");
        if(!Obj.isJsonObject()){
            return("Prerequisites: -");
        } else {
            var prerequisitesObj = Obj.getAsJsonObject();
            String data = getNameOfMod(prerequisitesObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return("Prerequisites: " + text);
        }
    }
    
    /**
     * saves the data field from JsonObject for later use
     * @param JsonObject includes the course data fetched from API
     * @return String text to be displayed
     */
    private String addLearningMaterial(JsonObject courseObject) {
        var Obj = courseObject.get("learningMaterial");
        if(!Obj.isJsonObject()){
            return("Learning Material: -");
        } else {
            var learningObj = Obj.getAsJsonObject();
            String data = getNameOfMod(learningObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return("Learning Material: " + text);
        }
    }
    
    /**
     * saves the data field from JsonObject for later use
     * @param JsonObject includes the course data fetched from API
     * @return String text to be displayed
     */
    private String addContent(JsonObject courseObject) {
        var Obj = courseObject.get("content");
        if(!Obj.isJsonObject()){
            return("Content: -");
        } else {
            var contentObj = Obj.getAsJsonObject();
            String data = getNameOfMod(contentObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return("Content: " + text);
        }
    }
    
    /**
     * saves the data field from JsonObject for later use
     * @param JsonObject includes the course data fetched from API
     * @return String text to be displayed
     */
    private String addOutComes(JsonObject courseObject) {
        var Obj = courseObject.get("outcomes");
        if(!Obj.isJsonObject()){
            return("Outcomes: -");
        } else {
            var outcomesObj = Obj.getAsJsonObject();
            String data = getNameOfMod(outcomesObj);
            String text = data.replaceAll("\\<.*?\\>", "");
            return("Outcomes: " + text);
        }
    }
    
    /**
     * saves the data field from JsonObject for later use
     * @param JsonObject includes the course data fetched from API
     * @return String text to be displayed
     */
    private String addCredits(JsonObject courseObject) {
        var Obj = courseObject.get("credits");
        if(!Obj.isJsonObject()){
            return("- Credits");
        } else {
            var creditsObj = Obj.getAsJsonObject();
            var minCredits = creditsObj.get("min");
            var maxCredits = creditsObj.get("max");
            if(minCredits.isJsonNull() && maxCredits.isJsonNull()){
                return("- Credits");
            } else if(minCredits.isJsonNull()){
                return(maxCredits + " Credits");
            } else if(maxCredits.isJsonNull()){
                return(minCredits + " Credits");
            } else {        
                if(minCredits.getAsInt() == maxCredits.getAsInt()) {
                    return(minCredits.getAsInt() + " Credits");
                } else {
                    return(minCredits.getAsInt() + "-" + maxCredits.getAsInt() + " Credits");
                }
            }
        }
    }
    
    /**
     * Processes module fetched from API
     * @param jsonObject the fetched module data
     * @return JsonArray the knowledge whether the module has children
     */
    public JsonArray processJson(JsonObject jsonObject) {
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
     * gets the modules of certain orientation or degree can be then used recursively
     * to get the sub modules and courses of degrees children
     * @param json JsonObject the degree or orientation data
     * @param parent TreeItem parent of which children the traversed module is
     */
    public void traverseJson(JsonObject json, TreeItem<String> parent) {                
        JsonObject res; 
        String groupId;
        String modName;
        
        String type = json.get("type").getAsString();
        JsonArray jsonArray = null;
        
        //these are optional courses/modules, not implemented yet
        if (type.equals("AnyCourseUnitRule") || type.equals("AnyModuleRule") ) {
            System.out.println("course/mod: Vapaasti valittavat kurssit/moduulit");
        } else if (type.equals("CourseUnitRule")) {
            groupId = json.get("courseUnitGroupId").getAsString();
            
            String urlString = createUrlString(groupId, "Course");
                
            res = getJsonObjectFromApi(urlString);
            processCourseJson(res, parent);
        } else { 
            if (type.equals("ModuleRule")) {
                groupId = json.get("moduleGroupId").getAsString();

                String urlString = createUrlString(groupId, "Module");                
                res = getJsonObjectFromApi(urlString);
                
                JsonObject nameObj = res.getAsJsonObject("name");
                modName = getNameOfMod(nameObj);

                System.out.println("mod: " + modName + " " /*+ currentModule.getName()*/);

                TreeItem<String> oldParent = parent;               
                //reassign parent
                parent = new TreeItem<>(modName);
                
                oldParent.getChildren().add(parent);
                jsonArray = processJson(res);
            } else {
                jsonArray = processJson(json);
            }
            
            // note for later: if jsonArray is null, is freely selectable studies
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
     * returns the orientations of degree program
     * @return HashMap orientations
     */
    public HashMap<String, String> getOrientations() {
        return this.orientations;
    }
    
    /**
     * returns the course data of the degree
     * @return HashMap course data
     */
    public HashMap<String, ArrayList<String>> getCoursesOfProgramme() {
        return this.coursesOfProgramme;
    }
    
    /**
     * returns courses of degree
     * @return ArrayList courses
     */
    public  ArrayList<Course> getCourses() {
        return courses;
    }
   
    /**
     * Creates appropriate url for data fecthing from API
     * @param groupId String, groupId of module or course
     * @param type String, clarifies type of data fetched
     * @return String, the correct url
     */
    public String createUrlString(String groupId, String type){
        String urlString;
        if(type.equals("Module")) {
            urlString = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=" +groupId+ "&universityId=tuni-university-root-id";
        } else {
            urlString = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="+groupId+"&universityId=tuni-university-root-id";
        }
        return urlString;
    }
    
    /**
     * Retrieves data from API
     * @param urlString
     * @return a JsonObject containing necessary data from API
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
}
