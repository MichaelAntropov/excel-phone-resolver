package com.hizencode.excelphoneresolver.ui.mainscene.tasks;

import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;

public class LoadExcelFileTask extends Task<Workbook> {

    private final File excelFile;

    public LoadExcelFileTask(File excelFile) {
        this.excelFile = excelFile;
    }

    @Override
    protected Workbook call() throws Exception {
        return WorkbookFactory.create(excelFile);
    }

}
