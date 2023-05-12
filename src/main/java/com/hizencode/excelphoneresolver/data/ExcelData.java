package com.hizencode.excelphoneresolver.data;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class ExcelData {

    private static File originalFile;

    private static File tempFile;

    private static Workbook workbook;

    private static Sheet sheet;

    public static boolean isExcelFilePresent() {
        return getTempFile() != null;
    }

    public static boolean isWorkbookPresent() {
        return getWorkbook() != null;
    }

    public static boolean isSheetPresent() {
        return getSheet() != null;
    }

    public static void setTempFile(File tempFile) {
        ExcelData.tempFile = tempFile;
    }

    public static File getTempFile() {
        return tempFile;
    }

    public static Workbook getWorkbook() {
        return workbook;
    }

    public static void setWorkbook(Workbook workbook) {
        ExcelData.workbook = workbook;
    }

    public static Sheet getSheet() {
        return sheet;
    }

    public static void setSheet(Sheet sheet) {
        ExcelData.sheet = sheet;
    }

    public static File getOriginalFile() {
        return originalFile;
    }

    public static void setOriginalFile(File originalFile) {
        ExcelData.originalFile = originalFile;
    }

    public static void clearData() throws IOException {
        //Clean up if any temp files are created/close books
        setSheet(null);
        if(ExcelData.isWorkbookPresent()) {
            ExcelData.getWorkbook().close();
        }
        setWorkbook(null);
        if(ExcelData.isExcelFilePresent()) {
            Files.deleteIfExists(ExcelData.getTempFile().toPath());
        }
        setTempFile(null);
        setOriginalFile(null);

    }
}
