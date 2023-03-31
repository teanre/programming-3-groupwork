/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.sisu;

import javafx.stage.Stage;

/**
 *
 * @author jamik
 */
public class Main {
    // Main class of the program controls which window to open first
    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.start(new Stage());
    }
}
