/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.control.TreeItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
 * Testfile for the class StudyTree
 * @author annik
 */
public class StudyTreeTest {
    
    /**
     * Test of getOrientations method, of class StudyTree.
     */
    /*
    @Test
    public void testGetOrientations() {
        StudyTree instance = new StudyTree();
        HashMap<String, String> result = instance.getOrientations();

        HashMap<String, String> expResult = new HashMap<>();
        assertEquals(expResult, result);
    }
    

    /**
     * Test of getCourses method, of class StudyTree.
     */
    
    @Test
    public void testGetCourses() {
        StudyTree instance = new StudyTree();
        ArrayList<Course> result = instance.getCourses();
        ArrayList<Course> expResult = new ArrayList<>();
        assertEquals(expResult, result);
    }
}
