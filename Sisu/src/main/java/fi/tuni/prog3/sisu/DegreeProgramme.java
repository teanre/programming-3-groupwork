package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Fetches all the degree programmes from the API 
 * @author terhi
 */
public class DegreeProgramme extends DegreeModule implements iAPI {
    private ArrayList<DegreeProgramme> degreeProgrammes = new ArrayList<>();; 
    
    /**
     * Public constructor to initialize fetching degree programme data.
     */
    public DegreeProgramme () {
        super("", "", "", 0);
    }
    
    /**
     * Constructor to create a new DegreeProgramme object once necessary 
     * data has been fetched from API
     * @param name String, name of the degree programme 
     * @param id String, identifying id code of the programme
     * @param groupId String, identifying groupid code of the programme
     * @param minCredits int, minimum credits required for the degree
     */
    public DegreeProgramme (String name, String id, String groupId, int minCredits) {
        super(name, id, groupId, minCredits);
    }
    

     /**
     * Processes json data fetched from API, creates more detailed DegreeProgramme
     * objects for the application to use.
     * @return ArrayList of degreeprogramme objects created
     */
    public ArrayList<DegreeProgramme> addDegreeProgrammes() {
        String urlString = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";
        JsonObject jsonObject = getJsonObjectFromApi(urlString);
        var searchResults = jsonObject.getAsJsonArray("searchResults");        
            for (var result : searchResults) {
                // getting all the necessary fields to create a degreemodule object
                var obj = result.getAsJsonObject();
                String idOf = obj.get("id").getAsString();
                String groupIdOf = obj.get("groupId").getAsString();
                String nameOf = obj.get("name").getAsString();
                int minCreditsOf = obj.getAsJsonObject("credits").get("min").getAsInt();
                DegreeProgramme degreeProgramme = new DegreeProgramme(nameOf, idOf, groupIdOf, minCreditsOf);      
                degreeProgrammes.add(degreeProgramme);                
            }
        return this.getDegreeProgrammes();
    }
    
    /**
     * Fetches degree programme data from API. Implements iAPI.
     * @param urlString String, url of the source address
     * @return JsonObject containing basic data of degreeprogrammes
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

        Gson gson = new Gson();
        var jsonObject = gson.fromJson(response.toString(), JsonObject.class);

        return jsonObject;

        } catch (IOException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Returns the list of all created DegreeProgramme objects that have been 
     * created with the private constructor)
     * @return ArrayList of DegreeProgramme objects
     */
    private ArrayList<DegreeProgramme> getDegreeProgrammes() {
        return this.degreeProgrammes;
    }
    
}
