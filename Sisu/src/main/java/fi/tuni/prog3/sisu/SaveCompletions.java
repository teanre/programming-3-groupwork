/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author jamik
 */
public class SaveCompletions implements iReadAndWriteToFile {

    public SaveCompletions() {
        
    }
    
    @Override
    public JsonObject readFromFile(String fileName) throws Exception {
        JsonObject jsonObject = new JsonObject();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            jsonObject = new JsonParser().parse(reader).getAsJsonObject();
        } catch (IOException e) {
            
        }
        return jsonObject;
    }

    @Override
    public boolean writeToFile(String fileName, JsonObject jsonObject) throws Exception {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            fileWriter.write(gson.toJson(jsonObject));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
