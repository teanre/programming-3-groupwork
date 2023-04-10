/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jamik
 */
public class CourseTree {
    private String name;
    private List<CourseTree> children;
    
    
    public CourseTree(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }

    public void addChild(CourseTree child) {
        children.add(child);
    }

    public List<CourseTree> getChildren() {
        return children;
    }
}
