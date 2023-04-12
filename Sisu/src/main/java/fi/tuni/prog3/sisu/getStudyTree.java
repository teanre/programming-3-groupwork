
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

public class getStudyTree {
    //if a degree has orientation options, stores them <OrientationName, GroupId>
    private HashMap<String, String> orientations = new HashMap<>();
    private CourseTree degree;
    private CourseTree currentModule;
    
    public getStudyTree(String name) {
        degree = new CourseTree(name);
        currentModule = degree;
    }
    
    /*public void getStudyTreeOf(String program) {
        try {
        Gson gson = new Gson();
        String res;
        res = doGroupIdRequest(program, "Module");
        // do request for course if res isn't a module
        if(res.length() < 3) {
            res = doGroupIdRequest(program, "Course");
        }
        JsonArray jsonArray = JsonParser.parseString(res).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject ruleObject = jsonObject.get("rule").getAsJsonObject();
            JsonObject innerRule = ruleObject.get("rule").getAsJsonObject();
            JsonArray ruleArray = innerRule.get("rules").getAsJsonArray();
            for (JsonElement rule : ruleArray) {
                JsonObject moduleRule = rule.getAsJsonObject();
                JsonArray moduleRules = moduleRule.getAsJsonArray("rules");
                for (JsonElement moduleElement : moduleRules) {
                    JsonObject moduleObject = moduleElement.getAsJsonObject();
                    String moduleGroupId = moduleObject.get("moduleGroupId").getAsString();
                    a.add(moduleGroupId);
                }
            }
        }
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }       
    }*/
    public void findOrientations(JsonArray orientationOptions) {
        String modName;
        for (JsonElement el : orientationOptions) {
            //gots to gets from api right, jokanen el kerrallaa
            String groupId = el.getAsJsonObject().get("moduleGroupId").getAsString();

            JsonObject res = doGroupIdRequest(groupId, "Module");
            JsonObject nameObj = res.getAsJsonObject("name");
            if (nameObj.has("fi")) {
                    modName = nameObj.get("fi").getAsString();
            } else {
                    modName = nameObj.get("en").getAsString();
            }              
                
            orientations.put(modName, groupId);
        }
    }
    
    public void returnOrientations(String modName) {
        try {
            JsonObject res;
            res = doGroupIdRequest(modName, "Module");
            String programmeName;
            JsonObject nameObj = res.getAsJsonObject("name");
            if (nameObj.has("fi")) {
                programmeName = nameObj.get("fi").getAsString();
            } else {
                programmeName = nameObj.get("en").getAsString();
            }
            
            // check if the programme has orientation options, first rule object is decisive
            JsonObject firstRuleObj = res.getAsJsonObject("rule");
            if (firstRuleObj.get("type").getAsString().equals("CompositeRule")) {
                JsonArray orientationsArr = firstRuleObj.getAsJsonArray("rules");
                findOrientations(orientationsArr);
            } else{
                orientations.put("No Seperate Orientation", modName);
            }
            
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }  
    }
    
    public void getStudyTreeOf(String program) {
        try {
            JsonObject res;
            res = doGroupIdRequest(program, "Module");
            String programmeName;
            JsonObject nameObj = res.getAsJsonObject("name");
            if (nameObj.has("fi")) {
                programmeName = nameObj.get("fi").getAsString();
            } else {
                programmeName = nameObj.get("en").getAsString();
            }
            
            // check if the programme has orientation options, first rule object is decisive
            JsonObject firstRuleObj = res.getAsJsonObject("rule");
            if (firstRuleObj.get("type").getAsString().equals("CompositeRule")) {
                JsonArray orientationsArr = firstRuleObj.getAsJsonArray("rules");
                findOrientations(orientationsArr);
                
                //!!!implement user choosing an orientation here, and get from orientationsmap the id
                
                //then with it's id get the right jsonobj to traverse
                //res = doGroupIdRequest(program, "Module");
            }
                       
            System.out.println(programmeName);
            traverseStudyTree(res);
        } catch (JsonSyntaxException e) {
            System.out.println(e);
        }        
    }
    
    public void processCourseJson(JsonObject courseObject, CourseTree parent) {
        String name;
        JsonObject nameObj = courseObject.get("name").getAsJsonObject();
        if (nameObj.has("fi")) {
            name = nameObj.get("fi").getAsString();
        } else {
            name = nameObj.get("en").getAsString();
        }
        CourseTree course = new CourseTree(name);
        parent.getChildren().add(course);
        System.out.println("course: " + name + " " + parent.getName());
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
    
    public void traverseStudyTree(JsonObject json) {       
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
            
            res = doGroupIdRequest(groupId, "Course");
            processCourseJson(res,currentModule);

        } else { 
            if (type.equals("ModuleRule")) {
                groupId = json.get("moduleGroupId").getAsString();
                ids.add(groupId);

                res = doGroupIdRequest(groupId, "Module");
                JsonObject nameObj = res.getAsJsonObject("name");
                if (nameObj.has("fi")) {
                    modName = nameObj.get("fi").getAsString();
                } else {
                    modName = nameObj.get("en").getAsString();
                }              
                
                System.out.println("mod: " + modName + " " + currentModule.getName());
                CourseTree module = new CourseTree(modName);
                currentModule.getChildren().add(module);
                currentModule = module;
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
                            traverseStudyTree(obj);
                        }
                    }
                }  
            }
                   
        }       
    }
   
    
    public static JsonObject doGroupIdRequest(String groupId, String type) {
        try {
        URL url;
        // Set the request
        if(type.equals("Module")) {
            url = new URL("https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="+groupId+"&universityId=tuni-university-root-id");
        } else {
            url = new URL("https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="+groupId+"&universityId=tuni-university-root-id");
        }
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
        /*String req = response.toString();
        return req;*/
        
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
    
    public HashMap<String, String> getOrientations() {
        return this.orientations;
    }
    
    public CourseTree getCourseTree() {
        return this.degree;
    }
   
}
