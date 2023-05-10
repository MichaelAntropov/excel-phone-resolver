package com.hizencode.excelphoneresolver.data;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public final class ExcelFileChooser {

    public static void chooseExcelFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an excel file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx")
                , new FileChooser.ExtensionFilter("Excel 97-2003 Workbook", "*.xls")
        );

        File sourceFile = fileChooser.showOpenDialog(window);

        if (sourceFile == null) {
            return;
        }

        ExcelData.setOriginalFileName(sourceFile.getName());
        ExcelData.setFile(sourceFile);
    }

    public static File saveExcelFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose desired location to save file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx")
                , new FileChooser.ExtensionFilter("Excel 97-2003 Workbook", "*.xls")
        );

        return fileChooser.showSaveDialog(window);
    }

}
