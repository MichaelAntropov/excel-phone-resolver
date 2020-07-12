package com.hizencode.javafx.controller;

import com.hizencode.javafx.data.ExcelData;
import com.hizencode.javafx.data.ExcelFileChooser;
import com.hizencode.javafx.main.App;
import com.hizencode.javafx.service.ExcelLoadWorkbookService;
import com.hizencode.javafx.service.ExcelProcessWorkbookService;
import com.hizencode.javafx.service.ExcelSaveWorkbookService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResolveMenuController {

    @FXML
    public Label loadingLabel;
    @FXML
    public Label savingLabel;
    @FXML
    public Label processingLabel;
    @FXML
    public ProgressIndicator progressIndicator;
    @FXML
    public Label chosenFile;
    @FXML
    public MenuButton sheetMenu;
    @FXML
    public TextField startRange;
    @FXML
    public TextField endRange;
    @FXML
    public Button newFile;
    @FXML
    public Button process;

    public void initialize() {
        chosenFile.setText(ExcelData.getOriginalFileName());

        //Preventing the user to continue until file is loaded and show indicator
        disableInput(true);
        showProcessingIndicator(false);
        showSavingIndicator(false);
        //call it after others as we dont want to turn off ProgressIndicator
        showLoadingIndicator(true);

        ExcelLoadWorkbookService loadWorkbookService = new ExcelLoadWorkbookService(ExcelData.getFile());

        loadWorkbookService.start();

        loadWorkbookService.setOnSucceeded(event -> {
            ExcelData.setWorkbook(loadWorkbookService.getValue());
            setSheetsInMenu(sheetMenu, ExcelData.getWorkbook());

            showLoadingIndicator(false);
            disableInput(false);
        });

        loadWorkbookService.setOnFailed(event -> {
            loadWorkbookService.getException().printStackTrace();
        });
    }

    public void processFile() {
        //first check inputs
        if (!checkInputs()) {
            System.out.println("Wrong input");
            return;
        }

        System.out.println("Correct");

        disableInput(true);
        showProcessingIndicator(true);

        ExcelProcessWorkbookService processWorkbookService =
                new ExcelProcessWorkbookService(ExcelData.getSheet(), startRange.getText(), endRange.getText());

        processWorkbookService.start();

        processWorkbookService.setOnSucceeded(event -> {
            showProcessingIndicator(false);
            showSavingIndicator(true);

            File file = ExcelFileChooser.saveExcelFile(App.getWindow());

            if(file == null) {
                return;
            }

            ExcelSaveWorkbookService saveWorkbookService =
                    new ExcelSaveWorkbookService(file, ExcelData.getWorkbook());

            saveWorkbookService.start();

            saveWorkbookService.setOnSucceeded(e -> {
                disableInput(false);
                showSavingIndicator(false);
                //Clear temporary files that were created
                try {
                    Files.delete(ExcelData.getFile().toPath());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            saveWorkbookService.setOnFailed(e -> {
                saveWorkbookService.getException().printStackTrace();
            });
        });
    }

    private boolean checkInputs() {
        String rangeRegex = "([A-Z]+)([0-9]+)";

        if (!startRange.getText().matches(rangeRegex)
                || !endRange.getText().matches(rangeRegex)) {
            System.out.println("Doesnt match");
            return false;
        }

        Pattern pattern = Pattern.compile(rangeRegex);

        Matcher matcherStart = pattern.matcher(startRange.getText());
        Matcher matcherEnd = pattern.matcher(endRange.getText());

        if (matcherStart.find() && matcherEnd.find()) {
            if (!matcherStart.group(1).equals(matcherEnd.group(1))) {
                System.out.println("Columns should be the same!");
                return false;
            }

            int rowStart = Integer.parseInt(matcherStart.group(2));
            int rowEnd = Integer.parseInt(matcherEnd.group(2));

            if (rowEnd < rowStart) {
                System.out.println("Selected range should start from top to bottom!");
                return false;
            }
        }

        return ExcelData.isSheetPresent();
    }

    private void showLoadingIndicator(boolean value) {
        loadingLabel.setVisible(value);
        progressIndicator.setVisible(value);
    }

    private static void setSheetsInMenu(MenuButton menuButton, Workbook workbook) {

        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            MenuItem menuItem = new MenuItem(sheetIterator.next().getSheetName());
            menuItem.setOnAction(actionEvent -> {
                menuButton.setText(menuItem.getText());
                ExcelData.setSheet(workbook.getSheet(menuItem.getText()));
            });

            menuButton.getItems().add(menuItem);
        }
    }

    private void showSavingIndicator(boolean value) {
        savingLabel.setVisible(value);
        progressIndicator.setVisible(value);
    }

    private void showProcessingIndicator(boolean value) {
        processingLabel.setVisible(value);
        progressIndicator.setVisible(value);
    }

    private void disableInput(boolean value) {
        process.setDisable(value);
        sheetMenu.setDisable(value);
        startRange.setDisable(value);
        endRange.setDisable(value);
    }
}
