package com.hizencode.javafx.main;

import com.hizencode.javafx.data.ExcelData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.nio.file.Files;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("chooseFile"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        //Clean up if any temp files are created/close books
        if(ExcelData.isWorkbookPresent()) {
            try {
                ExcelData.getWorkbook().close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        if(ExcelData.isExcelFilePresent()) {
            try {
                Files.deleteIfExists(ExcelData.getFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    public static void setRoot(String fxml) {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));

        return fxmlLoader.load();
    }

    public static Window getWindow() {
        return scene.getWindow();
    }

    public static void main(String[] args) {
        launch();
    }

}