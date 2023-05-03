module ExcelPhoneResolver {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.hizencode.excelphoneresolver.main to javafx.graphics;
    opens com.hizencode.excelphoneresolver.ui.start.controller to javafx.fxml;
    opens com.hizencode.excelphoneresolver.ui.alertmanager to javafx.fxml;
    opens com.hizencode.excelphoneresolver.ui.resolve.controller to javafx.fxml;
}