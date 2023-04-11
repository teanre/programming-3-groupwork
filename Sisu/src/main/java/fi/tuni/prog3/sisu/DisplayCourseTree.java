package fi.tuni.prog3.sisu;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Takes the selected orientation and its degree program as constructor parameters
 * and iterates over its children to create a tree view item with degree program
 * as its root
 * @author jamik
 * @returns the tree view item of selected study structure that then can be displayed in javaFx
 */
public class DisplayCourseTree {
    
    TreeView<String> display = new TreeView<>();
    TreeItem<String> head;

    public DisplayCourseTree(CourseTree node, String father) {
        head = new TreeItem(father);
        head.setExpanded(true);
        display.setRoot(head);
        TreeItem<String> root = new TreeItem(node.getName());
        head.getChildren().add(root);
        root.setExpanded(true);
        addNodes(node, root);
        
    }
    
    private void addNodes(CourseTree node, TreeItem<String> parentItem) {
        for (CourseTree childNode : node.getChildren()) {
            TreeItem<String> childItem = new TreeItem<>(childNode.getName());
            parentItem.getChildren().add(childItem);
            addNodes(childNode, childItem);
        }
    }
    
    public TreeView<String> getDisplay() {
        return this.display;
    }
    
} 
