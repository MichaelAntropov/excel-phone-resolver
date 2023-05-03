module ExcelPhoneResolver {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.hizencode.javafx.controller to javafx.fxml;
    opens com.hizencode.javafx.main to javafx.graphics;
}