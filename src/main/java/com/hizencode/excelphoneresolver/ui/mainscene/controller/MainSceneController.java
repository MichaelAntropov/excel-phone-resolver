package com.hizencode.excelphoneresolver.ui.mainscene.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
    private TableView<?> excelTableView;

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


}
