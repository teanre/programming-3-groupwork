package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * JavaFX Sisu
 */
public class Sisu extends Application {

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
        
        Scene scene = new Scene(root, 800, 500);                      
        stage.setScene(scene);
        stage.setTitle("SisuGUI");
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
        
        TableView<DegreeModule> tableView = new TableView<>();

        // Create a TableColumn for each property of the objects in the ArrayList
        TableColumn<DegreeModule, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<DegreeModule, String> idColumn = new TableColumn<>("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<DegreeModule, String> groupColumn = new TableColumn<>("groupId");
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("groupId"));
        TableColumn<DegreeModule, String> creditsColumn = new TableColumn<>("minCredits");
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("minCredits"));

        // Add the TableColumns to the TableView
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(groupColumn);
        tableView.getColumns().add(creditsColumn);

        // Create an ObservableList from the ArrayList
        ObservableList<DegreeModule> degreeNames = FXCollections.observableArrayList();
        ObservableList<DegreeModule> degreeIds = FXCollections.observableArrayList();
        ObservableList<DegreeModule> degreeGroups = FXCollections.observableArrayList();
        ObservableList<DegreeModule> degreeCredits = FXCollections.observableArrayList();
        
        for (DegreeModule degreeProgramme : degreeProgrammesData) {
            degreeNames.add(degreeProgramme);
            degreeIds.add(degreeProgramme);
            degreeGroups.add(degreeProgramme);
            degreeCredits.add(degreeProgramme);
        }

        // Set the items of the TableView to the ObservableList
        tableView.setItems(degreeNames);
        tableView.setItems(degreeIds);
        tableView.setItems(degreeGroups);
        tableView.setItems(degreeCredits);
        
        //Creating a VBox for the left side.
        VBox leftVBox = new VBox(tableView);
        leftVBox.setPrefWidth(380);

        return leftVBox;
    }
    
    private VBox getRightVBox() {
        //Creating a VBox for the right side.
        try (FileReader reader = new FileReader("studentInfo.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Student info = gson.fromJson(reader, Student.class);
            Label label = new Label("Username: " + info.getName() + "\n" +
                            "Password: " + info.getStudentNumber() + "\n" +
                    "Starting year " + info.getStartingYear());
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