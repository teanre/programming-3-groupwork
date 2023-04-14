
package fi.tuni.prog3.sisu;
import com.google.gson.Gson;
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
import javafx.scene.control.TreeView;

public class getStudyTree implements iAPI {
    
    private HashMap<String, String> orientations = new HashMap<>();
    private CourseTree degree;
    private CourseTree currentModule;
    
    public getStudyTree(String name) {
        degree = new CourseTree(name);
        currentModule = degree;
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
     * 
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
    

    public void getStudyTreeOf(String program, TreeItem<String> root) {
        try {
            JsonObject res;
            
            String urlString = createUrlString(program, "Module");               
            res = getJsonObjectFromApi(urlString);
            
            JsonObject nameObj = res.getAsJsonObject("name");
            String programmeName = getNameOfMod(nameObj);
                       
            System.out.println(programmeName);
            TreeItem<String> currRoot = new TreeItem<String>(programmeName);
            root.getChildren().add(currRoot);
            traverseJson(res, currRoot);
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }        
    }
    
    public void processCourseJson(JsonObject courseObject, TreeItem<String> parent) {       
        JsonObject nameObj = courseObject.get("name").getAsJsonObject();
        String name = getNameOfMod(nameObj);;

        TreeItem<String> course = new TreeItem<>(name);
        parent.getChildren().add(course);
       // CourseTree course = new CourseTree(name);
        System.out.println("course: " + name + " " /*+ parent.getName()*/);
    }
        
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
    
    public void traverseJson(JsonObject json, TreeItem<String> parent) {       
        ArrayList<String> ids = new ArrayList<>();
         
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
            ids.add(groupId);
            
            String urlString = createUrlString(groupId, "Course");
                
            res = getJsonObjectFromApi(urlString);
            processCourseJson(res, parent);

        } else { 
            if (type.equals("ModuleRule")) {
                groupId = json.get("moduleGroupId").getAsString();
                ids.add(groupId);

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
    
    public HashMap<String, String> getOrientations() {
        return this.orientations;
    }
    
    public CourseTree getCourseTree() {
        return this.degree;
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
