package com.hizencode.excelphoneresolver.ui.resolve.controller;

import com.hizencode.excelphoneresolver.ui.alertmanager.AlertManager;
import com.hizencode.excelphoneresolver.data.ExcelData;
import com.hizencode.excelphoneresolver.data.ExcelFileChooser;
import com.hizencode.excelphoneresolver.main.App;
import com.hizencode.excelphoneresolver.ui.resolve.services.ExcelLoadWorkbookService;
import com.hizencode.excelphoneresolver.ui.resolve.services.ExcelProcessWorkbookService;
import com.hizencode.excelphoneresolver.ui.resolve.services.ExcelSaveWorkbookService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
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
    public Button back;
    @FXML
    public Button process;

    public void initialize() {
        chosenFile.setText(ExcelData.getOriginalFileName());

        //Preventing the user to continue until file is loaded and show indicator
        disableInput(true);
        showProcessingIndicator(false);
        showSavingIndicator(false);
        //call it after others as we dont want to turn off progressIndicator
        showLoadingIndicator(true);

        ExcelLoadWorkbookService loadWorkbookService =
                new ExcelLoadWorkbookService(ExcelData.getFile());

        loadWorkbookService.setOnSucceeded(event -> {
            ExcelData.setWorkbook(loadWorkbookService.getValue());
            setSheetsInMenu(sheetMenu, ExcelData.getWorkbook());

            showLoadingIndicator(false);
            disableInput(false);
        });

        loadWorkbookService.setOnFailed(event -> {
            AlertManager.showErrorWithTrace(loadWorkbookService.getException());
        });

        loadWorkbookService.start();
    }

    @FXML
    public void processFile() {
        //first check inputs
        if (!checkInputs()) {
            return;
        }

        disableInput(true);
        showProcessingIndicator(true);

        ExcelProcessWorkbookService processWorkbookService =
                new ExcelProcessWorkbookService(ExcelData.getSheet(), startRange.getText(), endRange.getText());

        processWorkbookService.setOnSucceeded(event -> {
            startExcelSaveWorkbookService();
        });

        processWorkbookService.setOnFailed(event -> {
            showSavingIndicator(false);
            disableInput(false);
            AlertManager.showErrorWithTrace(processWorkbookService.getException());
        });

        processWorkbookService.start();
    }

    @FXML
    public void goBack() {
        switchToFileChooseScene();
    }

    private void startExcelSaveWorkbookService() {
        showProcessingIndicator(false);
        showSavingIndicator(true);

        File file = ExcelFileChooser.saveExcelFile(App.getWindow());

        //Check if there is a chosen file, if not we notify user
        // that progress will be lost if they dont choose a file for saving
        if(file == null) {
            Optional<ButtonType> choice = AlertManager.showConfirmation("Confirmation",
                    "All the progress will be lost",
                    "Are you sure you want to quit?");

            choice.ifPresent(action -> {
                if(action.equals(ButtonType.CANCEL)) {
                    startExcelSaveWorkbookService();
                } else {
                   switchToFileChooseScene();
                }
            });
            return;
        }

        ExcelSaveWorkbookService saveWorkbookService =
                new ExcelSaveWorkbookService(file, ExcelData.getWorkbook());

        saveWorkbookService.setOnSucceeded(e -> {
            switchToFileChooseScene();
        });

        saveWorkbookService.setOnFailed(e -> {
            AlertManager.showErrorWithTrace(saveWorkbookService.getException());
        });

        saveWorkbookService.start();
    }

    //Checks input from both text fields and sheet menu,
    //if there is any wrong will notify user and return false
    private boolean checkInputs() {
        String rangeRegex = "([A-Z]+)([0-9]+)";

        if (!startRange.getText().matches(rangeRegex)
                || !endRange.getText().matches(rangeRegex)) {
            AlertManager.showWarning("Warning",
                    "Range doesn't match format",
                    "Please provide range like A1 : A27");
            return false;
        }

        Pattern pattern = Pattern.compile(rangeRegex);

        Matcher matcherStart = pattern.matcher(startRange.getText());
        Matcher matcherEnd = pattern.matcher(endRange.getText());

        if (matcherStart.find() && matcherEnd.find()) {
            if (!matcherStart.group(1).equals(matcherEnd.group(1))) {
                AlertManager.showWarning("Warning",
                        "Columns should be the same!");
                return false;
            }

            int rowStart = Integer.parseInt(matcherStart.group(2));
            int rowEnd = Integer.parseInt(matcherEnd.group(2));

            if (rowEnd < rowStart) {
                AlertManager.showWarning("Warning",
                        "Range should start from top to bottom!");
                return false;
            }
        }

        if(!ExcelData.isSheetPresent()) {
            AlertManager.showWarning("Warning",
                    "Please choose sheet to work on");
            return false;
        }

        return true;
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

    private static void switchToFileChooseScene() {
        //Clear up excel data and temp files
        try {
            ExcelData.clearData();
        } catch (IOException exception) {
            AlertManager.showErrorWithTrace(exception);
        }
        App.setRoot("chooseFile");
    }

    ///////////////////////////////////////////////////////////////////////
    //Convenience methods to turn on/off indicators or user input
    private void showLoadingIndicator(boolean value) {
        loadingLabel.setVisible(value);
        progressIndicator.setVisible(value);
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
