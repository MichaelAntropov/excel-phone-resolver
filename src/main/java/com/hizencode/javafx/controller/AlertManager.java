package com.hizencode.javafx.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public final class AlertManager {

    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static void showWarning(String title, String header) {
        showWarning(title, header, null);
    }

    public static void showErrorWithTrace(Throwable throwable) {
        showErrorWithTrace("Error!",throwable.getMessage(), throwable);
    }

    public static void showErrorWithTrace(String title, String header, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);

        VBox dialogPaneContent = new VBox();
        Label label = new Label("Stack Trace:");
        TextArea textArea = new TextArea();

        //Get stacktrace into a string
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));

        textArea.setText(writer.toString());

        dialogPaneContent.getChildren().addAll(label, textArea);

        // Set content for Dialog Pane
        alert.getDialogPane().setContent(dialogPaneContent);

        alert.showAndWait();
    }

    public static Optional<ButtonType> showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert.showAndWait();
    }
}
