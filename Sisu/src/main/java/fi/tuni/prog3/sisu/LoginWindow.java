/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package fi.tuni.prog3.sisu;

import java.io.FileWriter;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

/**
 *
 * @author jamik
 */
public class LoginWindow extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label studentNumberLabel = new Label("Student Number:");
        TextField studentNumberField = new TextField();
        Label startYearLabel = new Label("Choose the starting year of studies:");
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(2023,2022,2021,2020,2019,2018,2017,2016,2015,2014,2013);
        Button loginBtn = new Button();
        loginBtn.setText("Login");
        loginBtn.setOnAction((ActionEvent event) -> {
            // Create a Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // Getting student information and creating a new student object
            String name = nameField.getText();
            String studentNumber = studentNumberField.getText();
            int startYear = choiceBox.getValue();   
            Student student = new Student(name, studentNumber, startYear);
            try (FileWriter writer = new FileWriter("studentInfo.json")) {
                    gson.toJson(student, writer);
                } catch (IOException e) {
                    System.out.println(e);
                }
            
            // Open the main window
            Sisu sisu = new Sisu();
            sisu.start(new Stage());
            // Close the login window
            primaryStage.close();
        });
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(studentNumberLabel, 0, 1);
        grid.add(studentNumberField, 1, 1);
        grid.add(startYearLabel,0,2);
        grid.add(choiceBox,1,2);
        grid.add(loginBtn, 1, 3);

        // Create a Scene and set it to the Stage
        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
