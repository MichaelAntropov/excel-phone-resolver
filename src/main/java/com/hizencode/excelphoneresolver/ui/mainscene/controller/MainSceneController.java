package com.hizencode.excelphoneresolver.ui.mainscene.controller;

import com.hizencode.excelphoneresolver.data.ExcelData;
import com.hizencode.excelphoneresolver.data.ExcelFileChooser;
import com.hizencode.excelphoneresolver.main.App;
import com.hizencode.excelphoneresolver.ui.alertmanager.AlertManager;
import com.hizencode.excelphoneresolver.ui.mainscene.tasks.LoadExcelFileTask;
import com.hizencode.excelphoneresolver.ui.mainscene.tasks.ProcessSelectedCellsTask;
import com.hizencode.excelphoneresolver.ui.mainscene.tasks.SaveExcelFileTask;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Workbook;
import org.controlsfx.control.spreadsheet.*;

import java.io.File;
import java.io.IOException;

public class MainSceneController {

    @FXML
    private Button chooseFileButton;

    @FXML
    private TextField chosenFileTextField;

    @FXML
    private Label numberOfCellsChosen;

    @FXML
    private Button helpButton;

    @FXML
    private Button processButton;

    @FXML
    private Button settingsButton;

    @FXML
    private TabPane tabPane;

    @FXML
    private HBox loadingOverlay;

    @FXML
    private HBox noDataOverlay;

    @FXML
    private HBox processingOverlay;

    private final BoxBlur boxBlurEffect = new BoxBlur();

    @FXML
    private void initialize() {
        showNoDataOverlay(true);
        numberOfCellsChosen.setText("-");
        processButton.setDisable(true);
    }

    @FXML
    void chooseExcelFile() {
        ExcelFileChooser.chooseExcelFile(App.getWindow());
        if (ExcelData.isExcelFilePresent()) {
            var loadExcelFile = new LoadExcelFileTask(ExcelData.getTempFile());

            loadExcelFile.setOnRunning(e -> {
                showNoDataOverlay(false);
                showLoadingOverlay(true);
                chooseFileButton.setDisable(true);
            });

            loadExcelFile.setOnSucceeded(e -> {
                ExcelData.setWorkbook(loadExcelFile.getValue());

                chosenFileTextField.setText(ExcelData.getOriginalFileName());
                setSheetTabs(ExcelData.getWorkbook());
                attachOnSheetChangeListener();
                numberOfCellsChosen.setText("0");

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

    @FXML
    void startProcessing() {
        var currentView = (SpreadsheetView) tabPane.getSelectionModel().getSelectedItem().getContent();

        if (currentView == null) {
            AlertManager.showWarning("Process warning",
                    "No data present!",
                    "Selected sheet doesnt have any data to process")
            ;
            return;
        }
        if (currentView.getSelectionModel().getSelectedCells().isEmpty()) {
            AlertManager.showWarning(
                    "Process warning",
                    "No selection made!",
                    "Please select cells with phone numbers to process"
            );
            return;
        }

        var processSelectedCells = new ProcessSelectedCellsTask(
                ExcelData.getSheet(), currentView.getSelectionModel().getSelectedCells()
        );

        processSelectedCells.setOnRunning(e -> {
            showProcessingOverlay(true);
            chooseFileButton.setDisable(true);
        });

        processSelectedCells.setOnSucceeded(e -> saveExcelFile());

        processSelectedCells.setOnFailed(e ->
                AlertManager.showErrorWithTrace(processSelectedCells.getException())
        );

        var thread = new Thread(processSelectedCells);
        thread.setDaemon(true);
        thread.start();
    }

    private void saveExcelFile() {
        var file = ExcelFileChooser.saveExcelFile(App.getWindow());
        var saveExcelFile = new SaveExcelFileTask(ExcelData.getWorkbook(), file);

        saveExcelFile.setOnSucceeded(e -> {
            clearMainScene();
            try {
                ExcelData.clearData();
            } catch (IOException ex) {
                AlertManager.showErrorWithTrace(saveExcelFile.getException());
            }
        });

        saveExcelFile.setOnFailed(e ->
                AlertManager.showErrorWithTrace(saveExcelFile.getException())
        );

        var thread = new Thread(saveExcelFile);
        thread.setDaemon(true);
        thread.start();
    }

    private void clearMainScene() {
        tabPane.getTabs().clear();

        chosenFileTextField.clear();
        numberOfCellsChosen.setText("-");
        showProcessingOverlay(false);
        showNoDataOverlay(true);
        chooseFileButton.setDisable(false);
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
            if (newSelectedTab == null) {
                return;
            }
            if (newSelectedTab.getContent() != null) {
                ExcelData.setSheet(ExcelData.getWorkbook().getSheet(newSelectedTab.getText()));
                var spreadSheetView = (SpreadsheetView) newSelectedTab.getContent();
                numberOfCellsChosen.setText(String.valueOf(spreadSheetView.getSelectionModel().getSelectedCells().size()));
                return;
            }
            renderSpreadSheetView(newSelectedTab);
            numberOfCellsChosen.setText("0");
        });
        tabPane.getSelectionModel().clearSelection();
        tabPane.getSelectionModel().selectFirst();
    }

    private void renderSpreadSheetView(Tab tab) {
        ExcelData.setSheet(ExcelData.getWorkbook().getSheet(tab.getText()));

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
            final ObservableList<SpreadsheetCell> cells = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                var cell = sheet.getRow(row).getCell(column);
                if (cell == null || cellEvaluator.evaluate(cell) == null) {
                    cells.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
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
                    cells.add(spreadsheetCell);
                }
            }
            rows.add(cells);
        }
        grid.setRows(rows);

        var spreadSheetView = new SpreadsheetView(grid);
        spreadSheetView.setFixingRowsAllowed(false);
        spreadSheetView.setFixingColumnsAllowed(false);
        for (int i = 0; i < grid.getColumnCount(); i++) {
            spreadSheetView.getColumns().get(i).setPrefWidth(120);
        }
        spreadSheetView.getSelectionModel().getSelectedCells().addListener(
                (ListChangeListener<? super TablePosition>) change ->
                        numberOfCellsChosen.setText(
                                String.valueOf(spreadSheetView.getSelectionModel().getSelectedCells().size()
                                )
                        )
        );
        tab.setContent(spreadSheetView);
    }

    private void showNoDataOverlay(boolean state) {
        noDataOverlay.setVisible(state);
    }

    private void showProcessingOverlay(boolean state) {
        processingOverlay.setVisible(state);
        if (state) {
            tabPane.setEffect(boxBlurEffect);
        } else {
            tabPane.setEffect(null);
        }
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
