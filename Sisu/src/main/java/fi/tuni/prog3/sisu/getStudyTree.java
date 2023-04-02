/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author jamik
 */
public class getStudyTree {
    
    String moduleName;
    private ArrayList<String> a = new ArrayList<>();
    
    public getStudyTree(String name) {
        moduleName = name;
    }
    
    public void getStudyTreeOf(String program) {
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
        
    }
    
    public static String doGroupIdRequest(String groupId, String type) {
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
        String req = response.toString();
        return req;

        } catch (IOException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
        return null;
    }
    
    public String getA() {
        return this.a.toString();
    }
}
