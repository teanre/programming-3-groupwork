package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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

    @Override
    public void start(Stage stage) {
        
        //Creating a new TabPane and TreeView
        TabPane tabPane = new TabPane();
        TreeView treeView = new TreeView();
        VBox vbox = new VBox(treeView);

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
        var tab = new Tab("My profile", root);
        var tabTwo = new Tab("Structure of studies"); //TODO: node (vbox?)
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
            String chosen = choiceBox.getValue().strip();
            System.out.println("Selected: " + choiceBox.getValue().strip());
            // find the selected object and call for its degreetree
            for(DegreeModule d : degreeProgrammesData) {
                if(d.getName().equals(chosen)) {
                    var studyTree = new getStudyTree(d.getName());
                    studyTree.getStudyTreeOf(d.getGroupId());
                    System.out.println(studyTree.getA());
                    //TODO: Create new TreeItems
                    //mod.getChildren().add(new TreeItem(d));
                    break;
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
        
        //Creating a new TreeView
        //TreeView treeView = new TreeView();
        //treeView.setRoot(mod);
        
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(selectText, 0, 0);
        grid.add(choiceBox, 0, 1);
        grid.add(selectBtn, 0, 2);
        //Creating a VBox for the left side.
        VBox leftVBox = new VBox(grid);
        leftVBox.setPrefWidth(380);

        return leftVBox;
    }
    
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
            rightVBox.setPrefWidth(380);
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
}