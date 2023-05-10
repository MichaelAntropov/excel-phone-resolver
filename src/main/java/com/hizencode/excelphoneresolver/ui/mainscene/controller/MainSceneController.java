package com.hizencode.excelphoneresolver.ui.mainscene.controller;

import com.hizencode.excelphoneresolver.data.ExcelData;
import com.hizencode.excelphoneresolver.data.ExcelFileChooser;
import com.hizencode.excelphoneresolver.main.App;
import com.hizencode.excelphoneresolver.ui.alertmanager.AlertManager;
import com.hizencode.excelphoneresolver.ui.mainscene.tasks.LoadExcelFileTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Workbook;
import org.controlsfx.control.spreadsheet.*;

import java.io.File;

public class MainSceneController {

    @FXML
    private Button chooseFileButton;

    @FXML
    private TextField chosenFileTextField;

    @FXML
    private TextField columnTextField;

    @FXML
    private CheckBox customRowsCheckBox;

    @FXML
    private TextField endRowTextField;

    @FXML
    private CheckBox hasHeaderCheckBox;

    @FXML
    private Button helpButton;

    @FXML
    private Button processButton;

    @FXML
    private Button settingsButton;

    @FXML
    private TextField startRowTextField;

    @FXML
    private TabPane tabPane;

    @FXML
    private HBox loadingOverlay;

    @FXML
    private HBox noDataOverlay;

    private final BoxBlur boxBlurEffect = new BoxBlur();

    @FXML
    private void initialize() {
        showNoDataOverlay(true);
        disableProcessSettings(true);
        processButton.setDisable(true);
    }

    @FXML
    void chooseExcelFile(ActionEvent event) {
        ExcelFileChooser.chooseExcelFile(App.getWindow());
        if (ExcelData.isExcelFilePresent()) {
            var loadExcelFile = new LoadExcelFileTask(ExcelData.getFile());

            loadExcelFile.setOnRunning(e -> {
                showNoDataOverlay(false);
                showLoadingOverlay(true);
                chooseFileButton.setDisable(true);
            });

            loadExcelFile.setOnSucceeded(e -> {
                ExcelData.setWorkbook(loadExcelFile.getValue());

                setChosenFileTextField(ExcelData.getFile());
                setSheetTabs(ExcelData.getWorkbook());
                attachOnSheetChangeListener();
                disableProcessSettings(false);

                showLoadingOverlay(false);
                chooseFileButton.setDisable(false);
                processButton.setDisable(false);
            });

            loadExcelFile.setOnFailed(e ->
                    AlertManager.showErrorWithTrace(loadExcelFile.getException())
            );

            var thread = new Thread(loadExcelFile);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void setChosenFileTextField(File file) {
        chosenFileTextField.setText(file.getAbsolutePath());
    }

    private void setSheetTabs(Workbook workbook) {
        var sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            var sheet = sheetIterator.next();
            var tab = new Tab(sheet.getSheetName());
            tabPane.getTabs().add(tab);
        }
    }

    private void attachOnSheetChangeListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelectedTab, newSelectedTab) -> {
            // Null check and by newSelectedTab.getContent() != null we make sure that sheet is rendered only once
            if (newSelectedTab == null || newSelectedTab.getContent() != null) {
                return;
            }

            ExcelData.setSheet(ExcelData.getWorkbook().getSheet(newSelectedTab.getText()));

            var sheet = ExcelData.getSheet();
            int lastRowNum = sheet.getLastRowNum();
            int lastColumnNum = -1;
            for (var row : sheet) {
                if (lastColumnNum < row.getLastCellNum()) {
                    lastColumnNum = row.getLastCellNum();
                }
            }
            //Check if there is data in sheet
            if (lastColumnNum < 0 || lastRowNum < 0) {
                return;
            }
            Grid grid = new GridBase(lastRowNum, lastColumnNum);
            var cellEvaluator = ExcelData.getWorkbook().getCreationHelper().createFormulaEvaluator();

            ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
            for (int row = 0; row < grid.getRowCount(); ++row) {
                final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
                for (int column = 0; column < grid.getColumnCount(); ++column) {
                    var cell = sheet.getRow(row).getCell(column);
                    if (cell == null || cellEvaluator.evaluate(cell) == null) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
                    } else {
                        var cellValue = cellEvaluator.evaluate(cell);
                        var value = switch (cellValue.getCellType()) {
                            case NUMERIC -> String.valueOf(cellValue.getNumberValue());
                            case STRING -> cellValue.getStringValue();
                            case BOOLEAN -> cellValue.getBooleanValue() ? "TRUE" : "FALSE";
                            case ERROR -> ErrorEval.getText(cellValue.getErrorValue());
                            default -> "<error - unexpected cell type >";
                        };
                        var spreadsheetCell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, value);
                        spreadsheetCell.setEditable(false);
                        list.add(spreadsheetCell);
                    }
                }
                rows.add(list);
            }
            grid.setRows(rows);

            var spreadSheetView = new SpreadsheetView(grid);
            spreadSheetView.setFixingRowsAllowed(false);
            spreadSheetView.setFixingColumnsAllowed(false);
            for (int i = 0; i < grid.getColumnCount(); i++) {
                spreadSheetView.getColumns().get(i).setPrefWidth(120);
            }
            newSelectedTab.setContent(spreadSheetView);

        });
        tabPane.getSelectionModel().clearSelection();
        tabPane.getSelectionModel().selectFirst();
    }

    private void disableProcessSettings(boolean state) {
        columnTextField.setDisable(state);
        startRowTextField.setDisable(state);
        endRowTextField.setDisable(state);
        hasHeaderCheckBox.setDisable(state);
        customRowsCheckBox.setDisable(state);
    }

    private void showNoDataOverlay(boolean state) {
        noDataOverlay.setVisible(state);
    }

    private void showLoadingOverlay(boolean state) {
        loadingOverlay.setVisible(state);
        if (state) {
            tabPane.setEffect(boxBlurEffect);
        } else {
            tabPane.setEffect(null);
        }
    }
}
