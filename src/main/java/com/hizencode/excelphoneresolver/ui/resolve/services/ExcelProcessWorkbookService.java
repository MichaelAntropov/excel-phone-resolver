package com.hizencode.excelphoneresolver.ui.resolve.services;

import com.hizencode.excelphoneresolver.resolver.PhoneResolver;
import com.hizencode.excelphoneresolver.resolver.PhoneResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;

public class ExcelProcessWorkbookService extends Service<Workbook> {


    private final Sheet sheet;
    private final String startRange;
    private final String endRange;

    public ExcelProcessWorkbookService(Sheet sheet, String startRange, String endRange) {
        this.sheet = sheet;
        this.startRange = startRange;
        this.endRange = endRange;
    }

    @Override
    protected Task<Workbook> createTask() {
        return new Task<>() {
            @Override
            protected Workbook call() {
                if(sheet == null) {
                    throw new NullPointerException("The sheet is missing!");
                }

                CellAddress startCellAddress = new CellAddress(startRange);
                CellAddress endCellAddress = new CellAddress(endRange);

                //Iterate over the phones and apply phone resolver
                int startRow = startCellAddress.getRow();
                int endRow = endCellAddress.getRow();
                int column = startCellAddress.getColumn();

                for (int i = startRow; i <= endRow; i++) {
                    Cell cell = sheet.getRow(i).getCell(column);
                    if (cell != null) {
                        Cell nextCell = sheet.getRow(i).createCell(column + 1);

                        PhoneResult<String, String> result =
                                PhoneResolver.resolve(cell.getStringCellValue());

                        cell.setCellValue(result.getMainResult());
                        nextCell.setCellValue(result.getSecondaryResult());
                    }
                }

                return sheet.getWorkbook();
            }
        };
    }
}
