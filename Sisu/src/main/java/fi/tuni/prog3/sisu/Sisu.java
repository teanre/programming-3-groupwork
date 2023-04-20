package fi.tuni.prog3.sisu;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import javafx.util.Pair;


/**
 * JavaFX Sisu
 */
public class Sisu extends Application {
    
    private TabPane tabPane;
    private VBox rightVBox;
    private Label messageLabel;
    private Label progressLabel;
   // private Student currentStudent;

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
       
        // initiate fetching degree programmes
        DegreeProgramme degreeProgramme = new DegreeProgramme();
        
        var degreeProgrammesData = degreeProgramme.addDegreeProgrammes();
        
        ChoiceBox<String> choiceBox = new ChoiceBox<>();

        // add degree programmes to choicebox
        for (DegreeProgramme programme : degreeProgrammesData) {
            choiceBox.getItems().add(programme.getName());
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
            
            // iterate through data st
            for (DegreeProgramme d : degreeProgrammesData) {
                if (d.getName().equals(chosen)) {
           
                    // look for orientation options
                    var studyTree = new getStudyTree(d.getName());
                    studyTree.returnOrientations(d.getGroupId());     
                    var orientationMap = studyTree.getOrientations();
                    
                    // create treeView to present course structures
                    TreeView<String> treeView = new TreeView<>(); 
                    
                    // set the chosen as current degree programme of user
                    Student st = Student.getCurrentStudent();
                    st.setDegreeProgramme(d);

                    // if the selected degree programme has no orientations, 
                    // process course structure tab right away
                    if (orientationMap.isEmpty()) {
                        displayTreeView(d.getGroupId(), d.getName(), treeView);
                        updateProgressLabel();
                    } else {
                         // Display orientations by keeping the groupId but displaying only the name
                        ListView<HashMap.Entry<String, String>> orientations = new ListView<>();
                        orientations.getItems().addAll(orientationMap.entrySet());
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

                        // button to select orientation and launch displaying the studytree
                        Button selectButton = new Button("Select");
                        selectButton.setOnAction(e -> {
                            HashMap.Entry<String, String> selectedItem = orientations.getSelectionModel().getSelectedItem();
                            if (selectedItem != null) {
                                System.out.println("Selected: " + selectedItem.getKey() + " : " + selectedItem.getValue());
                                String degreeName = d.getName();                      

                                TreeItem<String> root = new TreeItem<>(degreeName);
                                treeView.setRoot(root);
                                
                                displayTreeView(selectedItem.getValue(), selectedItem.getKey(), treeView);                              
                                updateProgressLabel();
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

        // disable select button if a degree programme is not chosen
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
        // create a VBox for the left side.
        leftVBox.getChildren().add(grid);
        leftVBox.setPrefWidth(380);

        return leftVBox;        
    }

    /**
     * 
     * @param groupId
     * @param name
     * @param treeView 
     */
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
            var children = rightSide.getChildren();
            if(children.size() > 2) {
                var lastChild = children.get(children.size() - 1);
                children.remove(lastChild);
            }
            rightSide.getChildren().add(messageLabel);
            ArrayList<String> completed = new ArrayList<>();
            try {
                Student user = Student.getCurrentStudent();
                showMessage("Student " + user.getStudentNumber() + ", " + user.getCompletedCourses().size() + " " + "Completions Saved", 2000);
                //update progress label
                updateProgressLabel();
                //update json file
                FileProcessor fp = new FileProcessor();
                try {
                    fp.writeToFile("studentInfo.json");
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } catch (Exception ex) {
                
            }
        });

        // Add a listener to the TreeView items
        treeView.setOnMouseMoved(event -> {
            TreeItem<String> item = (TreeItem<String>) treeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                String value;
                if(item.getValue().startsWith("*")){
                    value = item.getValue().substring(2 ,item.getValue().length()-2);
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
    
    // Create a VBox for the right side to show current users data.  
    private VBox getRightVBox() {
        // set current user's info on the label
        Student user = Student.getCurrentStudent();
        Label label = new Label("Username: " + user.getName() + "\n" +
                            "Student: " + user.getStudentNumber() + "\n" +
                    "Starting year: " + user.getStartingYear());
        
        label.setAlignment(Pos.CENTER);
        label.setPadding(new Insets(10));
        
        progressLabel = new Label("");
        progressLabel.setVisible(false);
        
        progressLabel.setAlignment(Pos.CENTER);
        progressLabel.setPadding(new Insets(10));
            
        rightVBox = new VBox();
        rightVBox.getChildren().addAll(
            new StackPane(label),
            progressLabel
        );
        rightVBox.setPrefWidth(280);
        return rightVBox; 
    }
    
    // Create a button for quitting the application.
    private Button getQuitButton() {
        Button button = new Button("Quit");
        
        // add an event to the button to terminate the application
        button.setOnAction((ActionEvent event) -> {
            // save the progress to the file also while quitting to ensure no lost data
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
    private void markCompleted(TreeItem<String> item, ArrayList<Course> courses) {
        Student user = Student.getCurrentStudent();
        //update completedCourses 
        for (var c : courses) {
            if(c.getName().equals(item.getValue())) {
                user.addCompletedCourse(c);
            }                   
        }
            
        // mark completed in gui
        if(item.isLeaf() && !item.getValue().startsWith("*")){
            item.setValue("**" + item.getValue() + "**");
        }
    }
    
    // Helper function used in demarking completed courses
    private void removeCompletion(TreeItem<String> item) {
        
        // Remove the completion from the item
        if(item.getValue().startsWith("*")){
            item.setValue(item.getValue().substring(2 ,item.getValue().length()-2));
            Student user = Student.getCurrentStudent();

            for (var course : user.getCompletedCourses()) {
                if(course.getName().equals(item.getValue())) {
                    // 
                    user.removeCompletedCourse(course);
                }                   
            }
        }
    }
    
    // Show a message for a given duration
    private void showMessage(String message, int durationMillis) {
        messageLabel.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(durationMillis), e -> messageLabel.setText("")));
        timeline.play();
    }
    
    // Update progress label 
    private void updateProgressLabel() {
        Student user = Student.getCurrentStudent();
        progressLabel.setText(user.getDegreeProgramme().getName() + "\n" +
                                       user.getSumOfCompletedCourses() + "/" 
                                        + user.getDegreeProgramme().getMinCredits() + "\n" 
                                        + user.calculateProgress() + "%");
        progressLabel.setVisible(true);
    }
    
}