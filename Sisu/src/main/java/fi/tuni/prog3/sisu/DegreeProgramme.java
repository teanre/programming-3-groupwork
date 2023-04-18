package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Fetches all the degree programs from the API 
 * @author terhi
 */
public class DegreeProgramme extends DegreeModule implements iAPI {
            
    // Stores data of all degree programmes
    private ArrayList<DegreeProgramme> degreeProgrammes = new ArrayList<>(); 
    
    /**
     * Public constructor to initialize fetching degree programme data.
     */
    public DegreeProgramme () {
        super("", "", "", 0);
    }
    
    /**
     * Constructor to create a new DegreeProgramme object once necessary 
     * data has been fetched from API
     * @param name of the degree programme 
     * @param id
     * @param groupId identifying group id code of the programme
     * @param minCredits
     */
    private DegreeProgramme (String name, String id, String groupId, int minCredits) {
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
     * @param urlString url of the source address
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
    

    /** ! unnecessary?
     * Getter for a list of all created DegreeProgramme objects 
     * (crated with the private constructor)
     * @return ArrayList of DegreeProgramme objects
     */
    public ArrayList<DegreeProgramme> getDegreeProgrammes() {
        return this.degreeProgrammes;
    }
    
}
