package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * JavaFX Sisu
 */
public class Sisu extends Application {
    
    private TabPane tabPane;

    @Override
    public void start(Stage stage) {

        //Creating a new BorderPane.
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        
        //Adding HBox to the center of the BorderPane.
        root.setCenter(getCenterHbox());
        
        //Adding button to the BorderPane and aligning it to the right.
        var quitButton = getQuitButton();
        BorderPane.setMargin(quitButton, new Insets(10, 10, 0, 10));
        root.setBottom(quitButton);
        BorderPane.setAlignment(quitButton, Pos.TOP_RIGHT);
        
        //Adding tabs to tabPane
        tabPane = new TabPane();
        var tab = new Tab("My profile", root);
        var tabTwo = new Tab("Structure of studies");
        tabPane.getTabs().addAll(tab, tabTwo);
        
        Scene scene = new Scene(tabPane, 800, 500);                      
        stage.setScene(scene);
        stage.setTitle("SISU");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
    private HBox getCenterHbox() {
        //Creating an HBox.
        HBox centerHBox = new HBox(10);
        
        //Adding two VBox to the HBox.
        centerHBox.getChildren().addAll(getLeftVBox(), getRightVBox());
        
        return centerHBox;
    }
    
    
    private VBox getLeftVBox() {
        
        VBox leftVBox = new VBox();
        
        var degreeProgrammes = new getDegreeProgrammes();
        var degreeProgrammesData = degreeProgrammes.getData();
        
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        
        for (DegreeModule degreeProgramme : degreeProgrammesData) {
            choiceBox.getItems().add(degreeProgramme.getName());
        }
        
        Text selectText = new Text("Select your study programme:");
        selectText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));        
        
        Button selectBtn = new Button();
        selectBtn.setText("Select");
        
               // Triggering event after choice
        selectBtn.setOnAction((ActionEvent event) -> {
            var children = leftVBox.getChildren();
            if (children.size() > 1) {
                var lastChild = children.get(children.size() - 1);
                children.remove(lastChild);
            }
            String chosen = choiceBox.getValue().strip();
            System.out.println("Selected: " + choiceBox.getValue().strip());
            // find the selected object and call for its degreetree
            for(DegreeModule d : degreeProgrammesData) {
                if(d.getName().equals(chosen)) {
                    //First get the orientations
                    var studyTree = new getStudyTree(d.getName());
                    studyTree.returnOrientations(d.getGroupId());     
                    var selected = studyTree.getOrientations();
                    
                    //crete treeView
                    TreeView<String> treeView = new TreeView<>(); 
 
                    
                    // Display orientations by keeping the groupId but displaying only the name
                    ListView<HashMap.Entry<String, String>> orientations = new ListView<>();
                    orientations.getItems().addAll(selected.entrySet());
                    orientations.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(HashMap.Entry<String, String> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getKey());
                        }
                    }});
                    
                    //Button for selecting orientationg and launching displaying the studytree
                    Button selectButton = new Button("Select");
                    selectButton.setOnAction(e -> {
                        HashMap.Entry<String, String> selectedItem = orientations.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            System.out.println("Selected: " + selectedItem.getKey() + " : " + selectedItem.getValue());
                            String degreeName = d.getName();
                            String Orientation = selectedItem.getKey();
                            
                            TreeItem<String> root = new TreeItem<>(degreeName);
                            treeView.setRoot(root);
                            
                            var req = new getStudyTree(selectedItem.getKey());
                            req.getStudyTreeOf(selectedItem.getValue(), root);
                            
    
                            // displaying the treeview in the second tab
                            VBox container = new VBox(treeView);
                            Tab secondTab = tabPane.getTabs().get(1);
                            secondTab.setContent(container);
                            
                            
                            // Add a listener to the TreeView items
                            treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                                
                                // remove select button each time user changes selected course
                                if (oldValue != null && newValue != null && container.getChildren().size() > 1) {
                                    container.getChildren().remove(container.getChildren().get(children.size() - 1));
                                }
                                
                                if (newValue != null) {
                                    // Check if the selected item is already marked as completed
                                    if (newValue.getValue().startsWith("**") && newValue.getValue().endsWith("**")) {
                                        // Create a button to remove the completion and add it to the view
                                        Button removeButton = new Button("Remove Completion");
                                        container.getChildren().add(removeButton);
                                        removeButton.setOnAction(evn -> {
                                            // Remove the completion for the selected item and all its children
                                            removeCompletion(newValue);
                                            container.getChildren().remove(removeButton);
                                        });
                                    } else {
                                        // Create a button to mark the item as completed and add it to the view
                                        Button markButton = new Button("Mark Completed");
                                        container.getChildren().add(markButton);
                                        markButton.setOnAction(evn -> {
                                            // Mark the selected item as completed and all its children
                                            markCompleted(newValue);
                                            container.getChildren().remove(markButton);
                                        });
                                    }
                                }
                            });                     
                            }
                        });
                    
                    Text selectOrientation = new Text("Select orientation:");
                    selectOrientation.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 10));
                    
                    //display orientation choice and select button
                    VBox vbox = new VBox(selectOrientation,orientations, selectButton);
                    vbox.setPadding(new Insets(25, 25, 25, 25));
                    vbox.setSpacing(10);
                    leftVBox.getChildren().add(vbox);
                    break;
                }
            }
            
        });

        // Triggering event after choice
       /* selectBtn.setOnAction((ActionEvent event) -> {
            var children = leftVBox.getChildren();
            if (children.size() > 1) {
                var lastChild = children.get(children.size() - 1);
                children.remove(lastChild);
            }
            String chosen = choiceBox.getValue().strip();
            System.out.println("Selected: " + choiceBox.getValue().strip());
            // find the selected object and call for its degreetree
            for(DegreeModule d : degreeProgrammesData) {
                if(d.getName().equals(chosen)) {
                    //First get the orientations
                    var studyTree = new getStudyTree(d.getName());
                    studyTree.returnOrientations(d.getGroupId());     
                    var selected = studyTree.getOrientations();
                
                //create treeview   
                TreeView<String> treeView = new TreeView<>();    
                 //if the degree programme has no separate orientations, create subtab degree tree
                if(selected.isEmpty()) {
                   
                    TreeItem<String> root = new TreeItem<>(d.getName());
                    treeView.setRoot(root);
                    
                    var req = new getStudyTree(d.getName());
                    req.getStudyTreeOf(d.getGroupId(), root);
                   // var tree = req.getCourseTree();
                    //var display = new DisplayCourseTree(tree, d.getName());
                    //tähä kutsutaa tekemistä uusen funk
                    createSubTab(treeView, children.size()-1);
                } else {                
                    // Display orientations by keeping the groupId but displaying only the name
                    ListView<HashMap.Entry<String, String>> orientations = new ListView<>();
                    orientations.getItems().addAll(selected.entrySet());
                    orientations.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(HashMap.Entry<String, String> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getKey());
                        }
                    }});
                    
                    //Button for selecting orientation and launching displaying the studytree
                    Button selectButton = new Button("Select");
                    selectButton.setOnAction(e -> {
                        HashMap.Entry<String, String> selectedItem = orientations.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            System.out.println("Selected: " + selectedItem.getKey() + " : " + selectedItem.getValue());
                            String degreeName = d.getName();
                            String Orientation = selectedItem.getKey();
                            
                            TreeItem<String> root = new TreeItem<>(degreeName);
                            treeView.setRoot(root);
                            
                            var req = new getStudyTree(selectedItem.getKey());
                            req.getStudyTreeOf(selectedItem.getValue(), root);
                            /*var tree = req.getCourseTree();
                            var display = new DisplayCourseTree(tree, d.getName());
                            //tähä kutsutaa tekemistä uusen funk
                            createSubTab(treeView, children.size()-1);
                                                   
                        }
                    });
                    
                    Text selectOrientation = new Text("Select orientation:");
                    selectOrientation.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 10));
                    
                    //display orientation choice and select button
                    VBox vbox = new VBox(selectOrientation,orientations, selectButton);
                    vbox.setPadding(new Insets(25, 25, 25, 25));
                    vbox.setSpacing(10);
                    leftVBox.getChildren().add(vbox);
                    break;
                }
                }
            }
            
        });
        */
        
        // Disabling select button if a degreeprogramme is not chosen
        selectBtn.setDisable(true);
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.trim().isEmpty()) {
                selectBtn.setDisable(true);
            } else {
                selectBtn.setDisable(false);
            }     
        });
            
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(selectText, 0, 0);
        grid.add(choiceBox, 0, 1);
        grid.add(selectBtn, 0, 2);
        //Creating a VBox for the left side.
        leftVBox.getChildren().add(grid);
        leftVBox.setPrefWidth(380);

        return leftVBox;
    }
    
    //oisko eriksee treeview hommat 
    /*private void createSubTab(TreeView tree, int size) {
       // var treeview = tree.getDisplay();
        // displaying the treeview in the second tab
        VBox container = new VBox(tree);
        Tab secondTab = tabPane.getTabs().get(1);
        secondTab.setContent(container);
                            
        // Add a listener to the TreeView items
        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // remove select button each time user changes selected course
            if (oldValue != null && newValue != null && container.getChildren().size() > 1) {
                 container.getChildren().remove(container.getChildren().get(size));
            }
            
            if (newValue != null ) {
                // Check if the selected item is already marked as completed
                if (newValue.getValue().startsWith("**") && newValue.getValue().endsWith("**")) {
                    // Create a button to remove the completion and add it to the view
                    Button removeButton = new Button("Remove Completion");
                    container.getChildren().add(removeButton);
                    removeButton.setOnAction(evn -> {
                    // Remove the completion for the selected item and all its children
                       // removeCompletion(newValue);
                        container.getChildren().remove(removeButton);
                    });
                } else {
                // Create a button to mark the item as completed and add it to the view
                    Button markButton = new Button("Mark Completed");
                    container.getChildren().add(markButton);
                    markButton.setOnAction(evn -> {
                    // Mark the selected item as completed and all its children
                    //markCompleted(newValue);
                    container.getChildren().remove(markButton);
                    });
                }
            }
        }); 
        
    }*/
    
    private VBox getRightVBox() {
        //Creating a VBox for the right side.
        try (FileReader reader = new FileReader("studentInfo.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Student info = gson.fromJson(reader, Student.class);
            
            Label label = new Label("Username: " + info.getName() + "\n" +
                            "Student: " + info.getStudentNumber() + "\n" +
                    "Starting year: " + info.getStartingYear());
            
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(10));
            
            StackPane userInfo = new StackPane(label);
            VBox rightVBox = new VBox(userInfo);
            rightVBox.setPrefWidth(280);
            return rightVBox;
            
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
    
    private Button getQuitButton() {
        //Creating a button.
        Button button = new Button("Quit");
        
        //Adding an event to the button to terminate the application.
        button.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });
        
        return button;
    }
    
    // Helper function used in marking completed courses
    // iterates selected items children also
    private void markCompleted(TreeItem<String> item) {
        if(!item.getValue().startsWith("**")){
            item.setValue("** " + item.getValue() + " **");

            for (TreeItem<String> child : item.getChildren()) {
                markCompleted(child);
            }
        }
    }
    
    // Helper function used in demarking completed courses
    // iterates selected items children also
    private void removeCompletion(TreeItem<String> item) {
        // Remove the completion from the item
        if(item.getValue().startsWith("**")){
            item.setValue(item.getValue().substring(2, item.getValue().length() - 2));

            // Remove the completion from all children recursively
            for (TreeItem<String> child : item.getChildren()) {
                removeCompletion(child);
            }
        }
    }
}