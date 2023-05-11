package com.hizencode.excelphoneresolver.data;

import java.io.File;

public record ExcelFileChooserResult(File originalFile, File tmpFile) {
}
