package com.hizencode.excelphoneresolver.data;

import com.hizencode.excelphoneresolver.ui.alertmanager.AlertManager;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class ExcelFileChooser {

    public static Optional<ExcelFileChooserResult> chooseExcelFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an excel file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx")
                ,new FileChooser.ExtensionFilter("Excel 97-2003 Workbook", "*.xls")
        );

        File sourceFile = fileChooser.showOpenDialog(window);

        if(sourceFile == null) {
            return Optional.empty();
        }

        Path sourcePath = sourceFile.toPath();
        Path sourceDirectory = sourcePath.getParent();
        Path newPath = sourceDirectory.resolve(UUID.randomUUID() + ".tmp");

        try {
            Files.copy(sourcePath, newPath);
        } catch (IOException e) {
            AlertManager.showErrorWithTrace(e);
        }

        return Optional.of(new ExcelFileChooserResult(sourceFile, newPath.toFile()));
    }

    public static File saveExcelFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose desired location to save file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx")
                ,new FileChooser.ExtensionFilter("Excel 97-2003 Workbook", "*.xls")
        );

        return fileChooser.showSaveDialog(window);
    }

}
