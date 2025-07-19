package com.hizencode.excelphoneresolver.main;

import com.hizencode.excelphoneresolver.data.ExcelData;
import com.hizencode.excelphoneresolver.i18n.I18NService;
import com.hizencode.excelphoneresolver.settings.SettingsService;
import com.hizencode.excelphoneresolver.ui.alertmanager.AlertManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX App
 */
public class App extends Application {
    public static final String CLIENT_VERSION = "1.2.2";

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        try {
            SettingsService.loadSettings();

            FXMLLoader fxmlLoader = new FXMLLoader(
                    App.class.getResource("/fxml/main-scene.fxml"), I18NService.getCurrentResourceBundle()
            );
            scene = new Scene(fxmlLoader.load());
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/icon512.png"))));
            stage.setTitle("Excel Phone Resolver");
            stage.setScene(scene);
            stage.show();
        } catch (Exception exception) {
            AlertManager.showErrorWithTrace(exception);
        }
    }

    @Override
    public void stop() {
        //Clean up if any temp files are created/close books
        try {
            ExcelData.clearData();
        } catch (IOException exception) {
            AlertManager.showErrorWithTrace(exception);
        }

        //Save settings
        try {
            SettingsService.saveSettings();
        } catch (IOException exception) {
            AlertManager.showErrorWithTrace(exception);
        }
    }

    public static Window getWindow() {
        return scene.getWindow();
    }

    public static void main(String[] args) {
        launch();
    }

}