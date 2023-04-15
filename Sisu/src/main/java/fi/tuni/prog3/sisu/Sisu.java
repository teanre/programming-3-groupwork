package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
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
import javafx.util.Duration;


/**
 * JavaFX Sisu
 */
public class Sisu extends Application {
    
    private TabPane tabPane;
    private String degree;
    private String orientation;
    private VBox rightVBox;
    private String studentNumber;
    private Label messageLabel;

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
            degree = chosen;
            System.out.println("Selected: " + choiceBox.getValue().strip());
            // find the selected object and call for its degreetree
            for(DegreeModule d : degreeProgrammesData) {
                if(d.getName().equals(chosen)) {
                    //First get the orientations
                    var studyTree = new getStudyTree(d.getName());
                    studyTree.returnOrientations(d.getGroupId());     
                    var selected = studyTree.getOrientations();
                    
                    //create treeView to present course structures
                    TreeView<String> treeView = new TreeView<>(); 
                    
                    // if the selected degree programme has no orientations, process course structure tab
                    if (selected.isEmpty()) {
                        displayTreeView(d.getGroupId(), d.getName(), treeView);
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

                        //Button for selecting orientationg and launching displaying the studytree
                        Button selectButton = new Button("Select");
                        selectButton.setOnAction(e -> {
                            HashMap.Entry<String, String> selectedItem = orientations.getSelectionModel().getSelectedItem();
                            if (selectedItem != null) {
                                System.out.println("Selected: " + selectedItem.getKey() + " : " + selectedItem.getValue());
                                String degreeName = d.getName();
                                String Orientation = selectedItem.getKey();
                                orientation = Orientation;                             

                                TreeItem<String> root = new TreeItem<>(degreeName);
                                treeView.setRoot(root);
                                
                                displayTreeView(selectedItem.getValue(), selectedItem.getKey(), treeView);
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

    
    private void displayTreeView(String groupId, String name, TreeView treeView){
        TreeItem<String> root = new TreeItem<>(name);
        treeView.setRoot(root);
        
        SplitPane splitPane = new SplitPane();
                       
        var req = new getStudyTree(name);
        req.getStudyTreeOf(groupId, root);
        var courses = req.getCoursesOfProgramme();
        ArrayList<Course> courseObjects = req.getCourses();
                            
        // displaying the treeview in the second tab
        VBox container = new VBox(treeView);
        Tab secondTab = tabPane.getTabs().get(1);
        secondTab.setContent(splitPane);
        
        // Create a TextArea to display course information
        TextArea courseInfoTextArea = new TextArea();
        container.getChildren().add(courseInfoTextArea);
        courseInfoTextArea.setEditable(false);
        courseInfoTextArea.setWrapText(true);
        courseInfoTextArea.setMinHeight(400);
        
        VBox rightSide = new VBox(courseInfoTextArea);
        rightSide.setPrefWidth(300);
        container.setPrefWidth(500);
        
        Button statusButton = new Button();
        statusButton.setText("Mark Completed");
        rightSide.getChildren().add(statusButton);
        
        Button saveButton = new Button("Save");
        container.getChildren().add(saveButton);
        
        splitPane.getItems().addAll(container, rightSide);
        
        //Add a listener to save button
        saveButton.setOnAction((ActionEvent eh) -> {
            messageLabel = new Label();
            rightSide.getChildren().add(messageLabel);
            ArrayList<String> completed = new ArrayList<>();
            getCompletedCourses(root, completed);
            try {
                saveCompletedCourses(studentNumber, completed);
                showMessage("Student: " + studentNumber + " Completions Saved", 3000);
            } catch (Exception ex) {
                
            }
        });

        // Add a listener to the TreeView items
        treeView.setOnMouseMoved(event -> {
            TreeItem<String> item = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                String value;
                if(item.getValue().startsWith("*")){
                    value = item.getValue().substring(1);
                } else {
                    value = item.getValue();
                }
                if(courses.containsKey(value)){
                    ArrayList<String> items = courses.get(value);
                    String content = String.join("\n\n", items);
                    courseInfoTextArea.setText(content);
                } else {
                    courseInfoTextArea.setText("");
                }  
            } 
        });

        // Add a listener to the TreeView items
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    // check if the selected item is already marked as completed
                    if (((TreeItem<String>)newValue).getValue().startsWith("*")) {
                        // Set the button text and action for removing completion
                        statusButton.setText("Remove Completion");
                        statusButton.setOnAction(evn -> {
                            // Remove the completion for the selected item 
                            removeCompletion(((TreeItem<String>)newValue));
                        });
                    } else if (((TreeItem<String>)newValue).isLeaf()) {
                        // Check if the selected item is a leaf node and not already completed
                        if (!((TreeItem<String>)newValue).getValue().startsWith("*")) {
                            // Set the button text and action for marking completion
                            statusButton.setText("Mark Completed");
                            statusButton.setOnAction(evn -> {
                                // Mark the selected item as completed
                                markCompleted(((TreeItem<String>)newValue), courseObjects);
                            });
                        } else {
                            // Set the button text for an already completed leaf node
                            statusButton.setText("Remove Completion");
                            statusButton.setOnAction(evn -> {
                                // Remove the completion for the selected item
                                removeCompletion(((TreeItem<String>)newValue));
                            });
                        }
                    }
                }
            }                                      
        });   
    }
    
    private VBox getRightVBox() {
        //Creating a VBox for the right side.
        //set current user's info on the label
        Student currentStudent = Student.getCurrentStudent();
        Label label = new Label("Username: " + currentStudent.getName() + "\n" +
                            "Student: " + currentStudent.getStudentNumber() + "\n" +
                    "Starting year: " + currentStudent.getStartingYear());
        
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(10));
            
        StackPane userInfo = new StackPane(label);
        rightVBox = new VBox(userInfo);
        rightVBox.setPrefWidth(280);
        return rightVBox; 
    }
    
    private Button getQuitButton() {
        //Creating a button.
        Button button = new Button("Quit");
        
        //Adding an event to the button to terminate the application.
        button.setOnAction((ActionEvent event) -> {
            //update json file
            FileProcessor fp = new FileProcessor();
            try {
                fp.writeToFile("studentInfo.json");
            } catch (Exception ex) {
                System.out.println(ex);
            }
            Platform.exit();
        });
        
        return button;
    }
    
    // Helper function used in marking completed courses
    // iterates selected items children also
    private void markCompleted(TreeItem<String> item, ArrayList<Course> courses) {
        Student currentStudent = Student.getCurrentStudent();
        //update completedCourses first
        for (var c : courses) {
            if(c.getName().equals(item.getValue())) {
                currentStudent.addCompletedCourse(c);
            }                   
        }
            
        //then mark completed in gui
        if(item.isLeaf() && !item.getValue().startsWith("*")){
            item.setValue("*" + item.getValue());
            
            //don't allow for children
            /*for (TreeItem<String> child : item.getChildren()) {
                markCompleted(child);
            }*/
        }
    }
    
    // Helper function used in demarking completed courses
    // iterates selected items children also
    private void removeCompletion(TreeItem<String> item) {
        // Remove the completion from the item
        if(item.getValue().startsWith("*")){
            item.setValue(item.getValue().substring(1));
            
            Student currentStudent = Student.getCurrentStudent();
            for (var c : currentStudent.getCompletedCourses()) {
                if(c.getName().equals(item.getValue())) {
                    //tallenna studentin completedcoursesiin
                    System.out.println("matsname loyty");
                    currentStudent.removeCompletedCourse(c);
                }                   
            }
            //ignore children
            // Remove the completion from all children recursively
            /*for (TreeItem<String> child : item.getChildren()) {
                removeCompletion(child);
            }*/
        }
    }
    
    private void getCompletedCourses(TreeItem<String> root, ArrayList<String> items) {
        if(root.getValue().startsWith("*")) {
            items.add(root.getValue().substring(1));
        }
        for (TreeItem<String> child : root.getChildren()) {
            getCompletedCourses(child, items);
        }
    }
    
    private void saveCompletedCourses(String studentNumber, ArrayList<String> items) throws Exception {
        
        SaveCompletions save = new SaveCompletions();
        
         // Read existing data from the file
        JsonObject existingData = save.readFromFile("courseCompletions.json");

        // Check if studentNumber already exists in the file
        if (existingData.has(studentNumber)) {
            // Replace the existing studentNumber and items objects
            existingData.remove(studentNumber);
        }
        existingData.addProperty(studentNumber, new Gson().toJson(items));

        // Write the updated data to the file
        save.writeToFile("courseCompletions.json", existingData);
    }
    
    // Show a message for a given duration
    private void showMessage(String message, int durationMillis) {
        messageLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(durationMillis), e -> messageLabel.setText("")));
        timeline.play();
    }
}