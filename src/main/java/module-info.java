module ExcelPhoneResolver {
    requires javafx.fxml;
    requires javafx.controls;
    requires poi;
    requires poi.ooxml;

    opens com.hizencode.javafx.controller to javafx.fxml;
    opens com.hizencode.javafx.main to javafx.graphics;
}