module fi.tuni.prog3.sisu {
    requires javafx.controls;
    exports fi.tuni.prog3.sisu;
    requires com.google.gson;
    requires javafx.base;
    requires javafx.graphics;
    opens fi.tuni.prog3.sisu to com.google.gson;
}
