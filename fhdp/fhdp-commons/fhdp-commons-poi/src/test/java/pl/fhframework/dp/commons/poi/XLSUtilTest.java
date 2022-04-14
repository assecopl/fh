package pl.fhframework.dp.commons.poi;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @created 13/04/2022
 */
public class XLSUtilTest {

    @Test
    public void testWorkbook() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        CellStyles styles = new CellStyles(wb);
        XSSFSheet sheet = wb.createSheet("test");
        XLSUtil.writeString(styles.getStringStyleCentered(), sheet, "test string", 0, 0);
        XLSUtil.writeNumber(styles.getNumberStyle(), sheet, new BigInteger("55"), 0, 1);
//        FileOutputStream stream = new FileOutputStream("/tmp/test.xlsx");
//        wb.write(stream);
        wb.close();
    }
}