package com.hizencode.javafx.controller;

import com.hizencode.javafx.data.ExcelData;
import com.hizencode.javafx.data.ExcelFileChooser;
import com.hizencode.javafx.main.App;
import com.hizencode.phoneresolver.PhoneResolver;
import com.hizencode.phoneresolver.PhoneResult;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

        //Task for loading the file
        Task<Void> loadFile = new Task<>() {
            @Override
            protected Void call() throws IOException {
                ExcelData.setWorkbook(WorkbookFactory.create(ExcelData.getFile()));
                return null;
            }
        };

        loadFile.setOnSucceeded(event -> {
            System.out.println("Task succeeded");
            setSheetsInMenu(sheetMenu, ExcelData.getWorkbook());
            showLoadingIndicator(false);
            disableInput(false);
        });

        loadFile.setOnFailed(event -> {
            System.out.println("Task failed");
            loadFile.getException().printStackTrace();
        });

        loadFile.setOnCancelled(event -> System.out.println("Task cancelled"));

        //Start the task
        new Thread(loadFile).start();
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

        Task<Void> processFile = new Task<>() {
            @Override
            protected Void call() {

                Workbook workbook = ExcelData.getWorkbook();
                Sheet sheet = ExcelData.getSheet();

                CellAddress startCellAddress = new CellAddress(startRange.getText());
                CellAddress endCellAddress = new CellAddress(endRange.getText());

                //Iterate over the phones and apply phone resolver
                int startRow = startCellAddress.getRow();
                int endRow = endCellAddress.getRow();
                int column = startCellAddress.getColumn();

                for (int i = startRow; i <= endRow; i++) {
                    Cell cell = sheet.getRow(i).getCell(column);
                    if (cell != null) {
                        Cell nextCell = sheet.getRow(i).createCell(column + 1);

                        PhoneResult<String, String> result =
                                PhoneResolver.resolve(cell.getStringCellValue());

                        cell.setCellValue(result.getMainResult());
                        nextCell.setCellValue(result.getSecondaryResult());
                    }
                }

                //Save file after finishing
                Platform.runLater(() -> {
                    showProcessingIndicator(false);
                    showSavingIndicator(true);

                    File file = ExcelFileChooser.saveExcelFile(App.getWindow());

                    Task<Void> saveFile = new Task<>() {
                        @Override
                        protected Void call() {
                            if (file != null) {
                                try (OutputStream outputStream = new FileOutputStream(file)) {
                                    workbook.write(outputStream);
                                    workbook.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println(e.getMessage());
                                }
                            }

                            try {
                                Files.delete(ExcelData.getFile().toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println(e.getMessage());
                            }

                            Platform.runLater(() -> {
                                disableInput(false);
                                showSavingIndicator(false);
                            });
                            return null;
                        }
                    };

                    new Thread(saveFile).start();

                });
                return null;
            }
        };

        new Thread(processFile).start();
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


}
