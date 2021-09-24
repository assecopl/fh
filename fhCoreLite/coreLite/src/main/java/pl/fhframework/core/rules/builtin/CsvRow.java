package pl.fhframework.core.rules.builtin;

import lombok.Data;
import pl.fhframework.core.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV-like row data. May be used for filling tables in fast prototyping.
 */
@Model
@Data
public class CsvRow {

    private List<String> values = new ArrayList<>();

    public String getColumn1() {
        return getColumn(0);
    }

    public String getColumn2() {
        return getColumn(1);
    }

    public String getColumn3() {
        return getColumn(2);
    }

    public String getColumn4() {
        return getColumn(3);
    }

    public String getColumn5() {
        return getColumn(4);
    }

    public String getColumn6() {
        return getColumn(5);
    }

    public String getColumn7() {
        return getColumn(6);
    }

    public String getColumn8() {
        return getColumn(7);
    }

    public String getColumn9() {
        return getColumn(8);
    }

    public String getColumn10() {
        return getColumn(9);
    }

    public String getColumn11() {
        return getColumn(10);
    }

    public String getColumn12() {
        return getColumn(11);
    }

    public String getColumn13() {
        return getColumn(12);
    }

    public String getColumn14() {
        return getColumn(13);
    }

    public String getColumn15() {
        return getColumn(14);
    }

    public String getColumn16() {
        return getColumn(15);
    }

    public String getColumn17() {
        return getColumn(16);
    }

    public String getColumn18() {
        return getColumn(17);
    }

    public String getColumn19() {
        return getColumn(18);
    }

    public String getColumn20() {
        return getColumn(19);
    }

    public void setColumn1(String value) {
        setColumn(0, value);
    }

    public void setColumn2(String value) {
        setColumn(1, value);
    }

    public void setColumn3(String value) {
        setColumn(2, value);
    }

    public void setColumn4(String value) {
        setColumn(3, value);
    }

    public void setColumn5(String value) {
        setColumn(4, value);
    }

    public void setColumn6(String value) {
        setColumn(5, value);
    }

    public void setColumn7(String value) {
        setColumn(6, value);
    }

    public void setColumn8(String value) {
        setColumn(7, value);
    }

    public void setColumn9(String value) {
        setColumn(7, value);
    }

    public void setColumn10(String value) {
        setColumn(9, value);
    }

    public void setColumn11(String value) {
        setColumn(10, value);
    }

    public void setColumn12(String value) {
        setColumn(11, value);
    }

    public void setColumn13(String value) {
        setColumn(12, value);
    }

    public void setColumn14(String value) {
        setColumn(13, value);
    }

    public void setColumn15(String value) {
        setColumn(14, value);
    }

    public void setColumn16(String value) {
        setColumn(15, value);
    }

    public void setColumn17(String value) {
        setColumn(16, value);
    }

    public void setColumn18(String value) {
        setColumn(17, value);
    }

    public void setColumn19(String value) {
        setColumn(18, value);
    }

    public void setColumn20(String value) {
        setColumn(19, value);
    }

    private String getColumn(int index) {
        if (values.size() > index) {
            return values.get(index);
        } else {
            return "";
        }
    }

    private void setColumn(int index, String value) {
        if (values.size() > index) {
            values.set(index, value);
        }
    }
}
