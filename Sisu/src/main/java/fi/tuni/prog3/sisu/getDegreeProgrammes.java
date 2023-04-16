package fi.tuni.prog3.sisu;

/**
 * Fetches all the degree programs from the API and returns them in an array list
 * @author jamik
 */

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;

public class getDegreeProgrammes {
    
    // store the necessary degreeprogramme data here
    private ArrayList<DegreeModule> degreeProgrammes = new ArrayList<>();
    
    /**
    * Public constructor when called fetches the degree programs from KORI API and
    * stores them in an array list as degree modules
    */
    public getDegreeProgrammes() {

        try {
            // Set the request
            URL url = new URL("https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000");
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
            
            // go through the data and append it to the main data structure
            Gson gson = new Gson();
            var jsonObject = gson.fromJson(req, JsonObject.class);
            var searchResults = jsonObject.getAsJsonArray("searchResults");
        
            for (var result : searchResults) {
                // getting all the necessary fields to create a degreemodule object
                var obj = result.getAsJsonObject();
                String id = obj.get("id").getAsString();
                String groupid = obj.get("groupId").getAsString();
                String name = obj.get("name").getAsString();
                int minCredits = obj.getAsJsonObject("credits").get("min").getAsInt();
            
                DegreeModule degree = new DegreeModule(name, id, groupid, minCredits) {};
                degreeProgrammes.add(degree);
            }

        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }
    /**
     * returns the fetched degrees
     * @return ArrayList fetched degrees
     */
    public ArrayList<DegreeModule> getData() {
        return this.degreeProgrammes;
    }
}
