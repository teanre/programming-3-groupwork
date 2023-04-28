package fi.tuni.prog3.sisu;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.File;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import static fi.tuni.prog3.sisu.Constants.*;

/**
 * Creates the login window which is first shown to user when she starts the program
 * Sets user data and reads the data from JSON file on login
 * @author jami
 */
public class LoginWindow extends Application {
    /**
     * Setups the login window and handles login button action.
     * @param primaryStage the primary stage for this application, onto which the
     * application can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        Label headerLabel = new Label("Give user details");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label studentNumberLabel = new Label("Student Number:");
        TextField studentNumberField = new TextField();
        Label startYearLabel = new Label("Choose the starting year of studies:");
        
        // creating choicebox for user to select starting year
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(2023,2022,2021,2020,2019,2018,2017,2016,2015,2014,2013);
        // Setting default value
        choiceBox.setValue(2023);
        
        Button loginBtn = new Button();
        loginBtn.setText("Login");
        
        // Setting login action
        loginBtn.setOnAction((ActionEvent event) -> {
            // Getting student information and creating a new student object
            String name = nameField.getText();
            String studentNumber = studentNumberField.getText();
            int startYear = choiceBox.getValue();   
            
            Student student = new Student(name, studentNumber, startYear);
            
            // set the user student object as current student, so it can be
            // accessed throughout the programme with get
            Student.setCurrentStudent(student);
            
            //open or create new file
            File file = new File(FILENAME);
            if (file.exists()) { //open the file
               try {
                   //check if current user's data is in it. if not, add
                   FileProcessor fileExists = new FileProcessor();
                   // reads the user's data from file
                   if (!fileExists.readFromFile(FILENAME)) {
                       // new user, add to file
                       fileExists.addStudentToFile(FILENAME, student);
                   }
                } catch (Exception e) {  
                    System.out.println(EXCEPTION_MSG + e.getMessage());
                }  
            } else { // create a new file
                FileProcessor newFile = new FileProcessor();
                try {
                    newFile.createNewFile(FILENAME);
                } catch (Exception e) {
                    System.out.println(EXCEPTION_MSG + e.getMessage());
                }
            }
            // Open the main window
            Sisu sisu = new Sisu();
            sisu.start(new Stage());
            // Close the login window
            primaryStage.close();
        });
        
        // Disabling logging in if user haven't given all the details
        BooleanBinding allConditionsMet = Bindings.createBooleanBinding(() -> 
            nameField.getText().trim().isEmpty() ||
            studentNumberField.getText().trim().isEmpty(),
            nameField.textProperty(), studentNumberField.textProperty()
        );
        loginBtn.disableProperty().bind(allConditionsMet);
        
        // Setting some basic UI
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(headerLabel, 0, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(studentNumberLabel, 0, 2);
        grid.add(studentNumberField, 1, 2);
        grid.add(startYearLabel,0,3);
        grid.add(choiceBox,1,3);
        grid.add(loginBtn, 1, 4);

        // Create a Scene and set it to the Stage
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main entry point for the JavaFX application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
