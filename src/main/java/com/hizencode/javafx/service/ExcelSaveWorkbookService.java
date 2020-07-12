package com.hizencode.javafx.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ExcelSaveWorkbookService extends Service<Workbook> {

    private final File file;

    private final Workbook workbook;


    public ExcelSaveWorkbookService(File file, Workbook workbook) {
        this.file = file;
        this.workbook = workbook;
    }

    @Override
    protected Task<Workbook> createTask() {
        return new Task<>() {
            @Override
            protected Workbook call() throws Exception {
                OutputStream outputStream = new FileOutputStream(file);
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();

                return workbook;
            }
        };
    }
}
