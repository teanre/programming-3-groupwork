/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.sisu;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 *
 * @author jamik
 */
public class Student {
    // SerializedNames to be able to use these in gson directly
    @SerializedName("name")
    public String name;
    @SerializedName("studentNumber")
    public String studentNumber;
    @SerializedName("startingYear")
    public int startingYear;
    
    public Student(String name, String studentNumber, int startingYear) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.startingYear = startingYear;
    }
    
    public String getName() {
        return name;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public int getStartingYear() {
        return startingYear;
    }
}
