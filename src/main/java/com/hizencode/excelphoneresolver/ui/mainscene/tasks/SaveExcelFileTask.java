package com.hizencode.excelphoneresolver.ui.mainscene.tasks;

import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SaveExcelFileTask extends Task<Void> {

    private final Workbook workbook;

    private final File file;

    public SaveExcelFileTask(Workbook workbook, File file) {
        this.workbook = workbook;
        this.file = file;
    }

    @Override
    protected Void call() throws Exception {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }
        return null;
    }
}
