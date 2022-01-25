package pl.fhframework.dp.commons.poi;

import org.apache.poi.ss.usermodel.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;

public class XLSUtil {
    public static Cell writeString(CellStyle style, Sheet sheet, String text, int rowNum , int colNum) {
        Row row = sheet.getRow(rowNum);
        if(row == null) row = sheet.createRow(rowNum);
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
            cell.setCellType(CellType.STRING);
        }  else {
            style = cell.getCellStyle();
        }
        cell.setCellValue(text);
        cell.setCellStyle(style);
        return cell;
    }

    public static  Cell writeCurrency(CellStyle style, Sheet sheet, BigDecimal value, int rowNum , int colNum) {
        if(value == null) return null;
        Row row = sheet.getRow(rowNum);
        if(row == null) row = sheet.createRow(rowNum);
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
            cell.setCellType(CellType.NUMERIC);
        }
        double v = value.doubleValue();
        cell.setCellValue(v);
        cell.setCellStyle(style);
        return cell;
    }

    public static  Cell writeNumber(CellStyle style, Sheet sheet, BigInteger value, int rowNum , int colNum) {
        if(value == null) return null;
        Row row = sheet.getRow(rowNum);
        if(row == null) row = sheet.createRow(rowNum);
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
            cell.setCellType(CellType.NUMERIC);
        }
        double v = value.doubleValue();
        cell.setCellValue(v);
        cell.setCellStyle(style);
        return cell;
    }

    public static   String convertXmlDate(XMLGregorianCalendar dt) {
        if(dt == null) return null;
        String s = dt.toString();
        if(s.contains("T")) return s.substring(0, s.indexOf("T"));
        else return s;
    }

    public static   String convertXmlDateTime(XMLGregorianCalendar dt) {
        return dt.toString().replace("T", " ").replace("Z", "");
    }
}
