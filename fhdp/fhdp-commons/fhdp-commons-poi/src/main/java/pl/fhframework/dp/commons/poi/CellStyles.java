package pl.fhframework.dp.commons.poi;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * User: jacekborowiec
 * Date: 03.04.2017
 * Time: 13:09
 *
 * @version $Revision:  $, $Date:  $
 */
@Getter
public class CellStyles {
    private CellStyle currencyStyle;
    private CellStyle numberStyle;
    private CellStyle stringStyle;
    private CellStyle stringStyleCentered;


    public CellStyles(XSSFWorkbook wb) {
        DataFormat format = wb.createDataFormat();
        currencyStyle = wb.createCellStyle();
        currencyStyle.setDataFormat(format.getFormat("#,##0.00"));
        numberStyle = wb.createCellStyle();
        numberStyle.setDataFormat(format.getFormat("0"));
        stringStyle = wb.createCellStyle();
        stringStyleCentered = wb.createCellStyle();
        stringStyleCentered.setAlignment(HorizontalAlignment.CENTER);
    }

    public CellStyles(XSSFWorkbook wb, Cell baseCell) {
        DataFormat format = wb.createDataFormat();
        currencyStyle = wb.createCellStyle();
        currencyStyle.cloneStyleFrom(baseCell.getCellStyle());
        currencyStyle.setDataFormat(format.getFormat("#,##0.00"));
        numberStyle = wb.createCellStyle();
        numberStyle.cloneStyleFrom(baseCell.getCellStyle());
        numberStyle.setDataFormat(format.getFormat("0"));
        stringStyle = wb.createCellStyle();
        stringStyle.cloneStyleFrom(baseCell.getCellStyle());
        stringStyle.setAlignment(HorizontalAlignment.LEFT);
        stringStyleCentered = wb.createCellStyle();
        stringStyleCentered.cloneStyleFrom(baseCell.getCellStyle());
        stringStyleCentered.setAlignment(HorizontalAlignment.CENTER);
    }
}
