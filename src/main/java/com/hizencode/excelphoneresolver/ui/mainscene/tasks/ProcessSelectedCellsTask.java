package com.hizencode.excelphoneresolver.ui.mainscene.tasks;

import com.hizencode.excelphoneresolver.resolver.PhoneResolver;
import javafx.concurrent.Task;
import javafx.scene.control.TablePosition;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public class ProcessSelectedCellsTask extends Task<Workbook> {

    private final Sheet selectedSheet;

    private final List<TablePosition> selectedCells;

    public ProcessSelectedCellsTask(Sheet selectedSheet, List<TablePosition> selectedCells) {
        this.selectedSheet = selectedSheet;
        this.selectedCells = selectedCells;
    }

    @Override
    protected Workbook call() {
        if(selectedSheet == null) {
            throw new NullPointerException("The sheet is missing!");
        }

        //Iterate over the selected cells(phones) and apply phone resolver
        for (var selectedCell : selectedCells) {
            var cell = selectedSheet.getRow(selectedCell.getRow()).getCell(selectedCell.getColumn());
            if (cell != null) {
                var formattedResult = PhoneResolver.resolve(cell.getStringCellValue());
                cell.setCellValue(formattedResult.mainResult());
            }
        }

        return selectedSheet.getWorkbook();
    }
}
