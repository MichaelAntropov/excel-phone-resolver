module excelphoneresolver {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;

    opens com.hizencode.excelphoneresolver.main to javafx.graphics;
    opens com.hizencode.excelphoneresolver.ui.mainscene.controller to javafx.fxml;
    opens com.hizencode.excelphoneresolver.ui.alertmanager to javafx.fxml;
}