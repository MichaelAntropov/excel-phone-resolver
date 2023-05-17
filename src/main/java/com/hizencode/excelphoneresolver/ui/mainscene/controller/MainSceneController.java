package com.hizencode.excelphoneresolver.ui.mainscene.controller;

import com.hizencode.excelphoneresolver.data.ExcelData;
import com.hizencode.excelphoneresolver.data.ExcelFileChooser;
import com.hizencode.excelphoneresolver.i18n.I18N;
import com.hizencode.excelphoneresolver.i18n.I18NService;
import com.hizencode.excelphoneresolver.i18n.Language;
import com.hizencode.excelphoneresolver.main.App;
import com.hizencode.excelphoneresolver.ui.alertmanager.AlertManager;
import com.hizencode.excelphoneresolver.ui.mainscene.tasks.LoadExcelFileTask;
import com.hizencode.excelphoneresolver.ui.mainscene.tasks.ProcessSelectedCellsTask;
import com.hizencode.excelphoneresolver.ui.mainscene.tasks.SaveExcelFileTask;
import com.hizencode.excelphoneresolver.ui.theme.Theme;
import com.hizencode.excelphoneresolver.ui.theme.ThemeService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Workbook;
import org.controlsfx.control.spreadsheet.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainSceneController implements I18N {

    @FXML
    private HBox backgroundOverlay;

    @FXML
    private Button chooseFileButton;

    @FXML
    private TextField chosenFileTextField;

    @FXML
    private Label excelFileLabel;

    @FXML
    private MenuButton languageMenuButton;

    @FXML
    private HBox loadingOverlay;

    @FXML
    private Label loadingOverlayLabel;

    @FXML
    private HBox noDataOverlay;

    @FXML
    private Label noDataOverlayLabel;

    @FXML
    private Label numberOfCellsChosen;

    @FXML
    private Label numberOfCellsChosenLabel;

    @FXML
    private Button processButton;

    @FXML
    private HBox processingOverlay;

    @FXML
    private Label processingOverlayLabel;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuButton themeMenuButton;

    private final BoxBlur boxBlurEffect = new BoxBlur();

    @FXML
    private void initialize() {
        showNoDataOverlay(true);
        numberOfCellsChosen.setText("-");
        processButton.setDisable(true);

        setLanguageMenuItems();
        I18NService.getCurrentLanguageProperty().addListener(
                (observableValue, language, t1) -> changeLanguage()
        );

        setThemeMenuItems();
    }

    @FXML
    private void chooseExcelFile() {
        var chooserResultOptional = ExcelFileChooser.chooseExcelFile(App.getWindow());

        if (chooserResultOptional.isPresent()) {
            clearMainScene();
            try {
                ExcelData.clearData();
            } catch (IOException e) {
                AlertManager.showErrorWithTrace(e);
            }

            ExcelData.setOriginalFile(chooserResultOptional.get().originalFile());
            ExcelData.setTempFile(chooserResultOptional.get().tmpFile());

            var loadExcelFile = new LoadExcelFileTask(ExcelData.getTempFile());

            loadExcelFile.setOnRunning(e -> {
                showNoDataOverlay(false);
                showLoadingOverlay(true);
                chooseFileButton.setDisable(true);
                processButton.setDisable(true);
            });

            loadExcelFile.setOnSucceeded(e -> {
                ExcelData.setWorkbook(loadExcelFile.getValue());

                chosenFileTextField.setText(ExcelData.getOriginalFile().getAbsolutePath());
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
    private void startProcessing() {
        var currentView = (SpreadsheetView) tabPane.getSelectionModel().getSelectedItem().getContent();

        if (currentView == null) {
            AlertManager.showWarning(I18NService.get("alert.no.data.present.title"),
                    I18NService.get("alert.no.data.present.header"),
                    I18NService.get("alert.no.data.present.content"))
            ;
            return;
        }
        if (currentView.getSelectionModel().getSelectedCells().isEmpty()) {
            AlertManager.showWarning(
                    I18NService.get("alert.no.selection.made.title"),
                    I18NService.get("alert.no.selection.made.header"),
                    I18NService.get("alert.no.selection.made.content")
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

    @FXML
    private void openHelpWindow() {
        try {
            var fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/help-scene/help-scene.fxml"),
                    I18NService.getCurrentResourceBundle()
            );
            var root = (Parent) fxmlLoader.load();
            var stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon512.png"))));
            stage.setTitle(I18NService.get("how.to.use.stage.title"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            AlertManager.showErrorWithTrace(e);
        }
    }

    private void saveExcelFile() {
        var file = ExcelFileChooser.saveExcelFile(App.getWindow());

        if (file == null) {
            var result = AlertManager.showConfirmation(
                    I18NService.get("confirmation.data.will.be.lost.title"),
                    I18NService.get("confirmation.data.will.be.lost.header"),
                    I18NService.get("confirmation.data.will.be.lost.content")
            );

            if (result.isPresent() && result.get().equals(ButtonType.OK)) {
                clearMainScene();
                try {
                    ExcelData.clearData();
                } catch (IOException ex) {
                    AlertManager.showErrorWithTrace(ex);
                }
            } else {
                saveExcelFile();
            }
            return;
        }

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

    private void setLanguageMenuItems() {
        var languages = List.of(Language.values());

        var menuItems = new ArrayList<MenuItem>();

        for (var language : languages) {
            var menuItem = new MenuItem();
            menuItem.setUserData(language);
            menuItem.setText(language.getLangName());
            if (language.equals(I18NService.getCurrentLanguageProperty().get())) {
                menuItem.setGraphic(new FontIcon("mdal-check"));
            }

            menuItem.setOnAction(actionEvent -> {
                for (var i : languageMenuButton.getItems()) {
                    i.setGraphic(null);
                }
                var item = (MenuItem)actionEvent.getSource();
                item.setGraphic(new FontIcon("mdal-check"));
                I18NService.getCurrentLanguageProperty().set((Language) item.getUserData());
            });

            menuItems.add(menuItem);
        }

        languageMenuButton.getItems().addAll(menuItems);
    }

    private void setThemeMenuItems() {
        var themes = List.of(Theme.values());
        var menuItems = new ArrayList<MenuItem>();

        for (var theme : themes) {
            var menuItem = new MenuItem();
            menuItem.setUserData(theme);
            menuItem.setText(I18NService.get(theme.getI18nProperty()));
            if (theme.equals(ThemeService.getCurrentThemeProperty().get())) {
                menuItem.setGraphic(new FontIcon("mdal-check"));
            }

            menuItem.setOnAction(actionEvent -> {
                for (var i : themeMenuButton.getItems()) {
                    i.setGraphic(null);
                }
                var item = (MenuItem)actionEvent.getSource();
                item.setGraphic(new FontIcon("mdal-check"));
                ThemeService.getCurrentThemeProperty().set((Theme) item.getUserData());
            });

            menuItems.add(menuItem);
        }

        themeMenuButton.getItems().addAll(menuItems);
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

        var spreadSheetView = new SpreadsheetView(grid) {
            @Override
            public String getUserAgentStylesheet() {
                var resource = MainSceneController.class.getResource("/css/nord-spreadsheet.css");
                if (resource != null) {
                    return resource.toExternalForm();
                }
                return super.getUserAgentStylesheet();
            }
        };
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
        backgroundOverlay.setVisible(state);
        noDataOverlay.setVisible(state);
    }

    private void showProcessingOverlay(boolean state) {
        backgroundOverlay.setVisible(state);
        processingOverlay.setVisible(state);
        if (state) {
            tabPane.setEffect(boxBlurEffect);
        } else {
            tabPane.setEffect(null);
        }
    }

    private void showLoadingOverlay(boolean state) {
        backgroundOverlay.setVisible(state);
        loadingOverlay.setVisible(state);
        if (state) {
            tabPane.setEffect(boxBlurEffect);
        } else {
            tabPane.setEffect(null);
        }
    }

    @Override
    public void changeLanguage() {
        excelFileLabel.setText(I18NService.get("excel.file.label"));
        chooseFileButton.setText(I18NService.get("choose.file.button"));
        numberOfCellsChosenLabel.setText(I18NService.get("number.cells.chosen.label"));
        processButton.setText(I18NService.get("process.button"));

        loadingOverlayLabel.setText(I18NService.get("loading.overlay.label"));
        noDataOverlayLabel.setText(I18NService.get("no.data.overlay.label"));
        processingOverlayLabel.setText(I18NService.get("processing.overlay.label"));

        themeMenuButton.getItems().forEach(menuItem -> {
            var theme = (Theme) menuItem.getUserData();
            menuItem.setText(I18NService.get(theme.getI18nProperty()));
        });
    }
}
