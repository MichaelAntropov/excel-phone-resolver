package com.hizencode.excelphoneresolver.ui.start.controller;

import com.hizencode.excelphoneresolver.data.ExcelData;
import com.hizencode.excelphoneresolver.data.ExcelFileChooser;
import com.hizencode.excelphoneresolver.main.App;
import javafx.fxml.FXML;


public class ChooseFileController {

    @FXML
    private void chooseFile() {

        ExcelFileChooser.chooseExcelFile(App.getWindow());

        if(ExcelData.isExcelFilePresent()) {
            App.setRoot("resolveMenu");
        }
    }
}
