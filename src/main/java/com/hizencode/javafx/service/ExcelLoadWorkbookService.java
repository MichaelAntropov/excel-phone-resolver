package com.hizencode.javafx.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;


public class ExcelLoadWorkbookService extends Service<Workbook> {

    private final File file;

    public ExcelLoadWorkbookService(File file) {
        this.file = file;
    }

    @Override
    protected Task<Workbook> createTask() {
        return new Task<>() {
            @Override
            protected Workbook call() throws Exception {
                return WorkbookFactory.create(file);
            }
        };
    }
}
