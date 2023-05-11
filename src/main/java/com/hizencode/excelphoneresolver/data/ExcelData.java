package com.hizencode.excelphoneresolver.data;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;

public final class ExcelData {

    private static String originalFileName;

    private static File file;

    private static Workbook workbook;

    private static Sheet sheet;

    public static boolean isExcelFilePresent() {
        return getFile() != null;
    }

    public static boolean isWorkbookPresent() {
        return getWorkbook() != null;
    }

    public static boolean isSheetPresent() {
        return getSheet() != null;
    }

    public static void setFile(File file) {
        ExcelData.file = file;
    }

    public static File getFile() {
        return file;
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

    public static String getOriginalFileName() {
        return originalFileName;
    }

    public static void setOriginalFileName(String originalFileName) {
        ExcelData.originalFileName = originalFileName;
    }

    public static void clearData() throws IOException {
        //Clean up if any temp files are created/close books
        setSheet(null);
        if(ExcelData.isWorkbookPresent()) {
            ExcelData.getWorkbook().close();
        }
        setWorkbook(null);
        setFile(null);
        setOriginalFileName(null);

    }
}
