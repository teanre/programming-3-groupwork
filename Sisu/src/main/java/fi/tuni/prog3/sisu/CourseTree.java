package fi.tuni.prog3.sisu;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulates structure of a course program as a tree data structure.
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
