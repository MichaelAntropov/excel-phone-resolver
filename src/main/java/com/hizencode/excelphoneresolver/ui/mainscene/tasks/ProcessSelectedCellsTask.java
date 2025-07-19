package com.hizencode.excelphoneresolver.ui.mainscene.tasks;

import com.hizencode.excelphoneresolver.resolver.PhoneResolver;
import javafx.concurrent.Task;
import javafx.scene.control.TablePosition;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.util.List;

public class ProcessSelectedCellsTask extends Task<Workbook> {

    private final Sheet selectedSheet;

    @SuppressWarnings("rawtypes") // Until new versions of controlsfx will use generics
    private final List<TablePosition> selectedCells;

    @SuppressWarnings("rawtypes") // Until new versions of controlsfx will use generics
    public ProcessSelectedCellsTask(Sheet selectedSheet, List<TablePosition> selectedCells) {
        this.selectedSheet = selectedSheet;
        this.selectedCells = selectedCells;
    }

    @Override
    protected Workbook call() {
        if(selectedSheet == null) {
            throw new NullPointerException("The sheet is missing!");
        }

        var cellEvaluator = selectedSheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        //Iterate over the selected cells(phones) and apply phone resolver
        for (var selectedCell : selectedCells) {
            var cell = selectedSheet.getRow(selectedCell.getRow()).getCell(selectedCell.getColumn());
            if (cell != null && cellEvaluator.evaluate(cell) != null) {
                var cellValue = cellEvaluator.evaluate(cell);
                var phoneString = switch (cellValue.getCellType()) {
                    case NUMERIC -> NumberToTextConverter.toText(cellValue.getNumberValue());
                    case STRING -> cellValue.getStringValue();
                    default -> "";
                };
                var formattedResult = PhoneResolver.resolve(phoneString);
                cell.setCellFormula(null);
                cell.setCellValue(formattedResult.mainResult());
            }
        }

        return selectedSheet.getWorkbook();
    }
}
