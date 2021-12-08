
package pl.fhframework.dp.transport.prints;

public enum FormatType {

    JASPER,
    DOCX,
    HTML,
    PDF,
    XLS,
    XLSX;

    public String value() {
        return name();
    }

    public static FormatType fromValue(String v) {
        return valueOf(v);
    }

}
