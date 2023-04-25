/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package fi.tuni.prog3.sisu;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.control.TreeItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author annik
 */
public class StudyTreeTest {
    
    public StudyTreeTest() {
    }
    
    /**
     * Test of getOrientations method, of class StudyTree.
     */
    @Test
    public void testGetOrientations() {
        StudyTree instance = new StudyTree();
        HashMap<String, String> expResult = new HashMap<>();
                
        expResult.put("Orientation1", "Description1");
        expResult.put("Orientation2", "Description2");
        
        HashMap<String, String> result = instance.getOrientations();
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

    /**
     * Test of fetchOrientations method, of class StudyTree.
     */
    @Test
    public void testFetchOrientations() {
        String moduleGroupId = "";
        StudyTree instance = new StudyTree();
        instance.fetchOrientations(moduleGroupId);
        assertFalse(instance.getOrientations().isEmpty());
    }

    /**
     * Test of getStudyTreeOf method, of class StudyTree.
     */
    @Test
    public void testGetStudyTreeOf() {
        String programmeGroupId = "Comp";
        TreeItem<String> root = new TreeItem<>();
        StudyTree instance = new StudyTree();
        instance.getStudyTreeOf(programmeGroupId, root);
        assertFalse(root.getChildren().isEmpty());
    }
}
