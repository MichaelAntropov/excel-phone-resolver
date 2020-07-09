package com.hizencode.javafx.controller;

import com.hizencode.javafx.data.ExcelData;
import com.hizencode.javafx.data.ExcelFileChooser;
import com.hizencode.javafx.main.App;
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
