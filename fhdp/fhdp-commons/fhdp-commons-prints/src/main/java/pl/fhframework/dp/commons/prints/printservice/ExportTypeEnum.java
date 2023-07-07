package pl.fhframework.dp.commons.prints.printservice;

public enum ExportTypeEnum {
    HTML("text/html", "html", "HTML"),
    PDF("application/pdf", "pdf", "PDF"),
    XLS("application/xls", "xls", "XLS"),
    XLSX("application/xlsx", "xlsx", "XLSX"),
    RTF("application/msword", "rtf", "RTF"),
    DOCX("application/msword", "docx", "DOCX"),
    XML("text/xml", "xml", "XML");

    private final String contentType;
    private final String extension;
    private final String description;

    private ExportTypeEnum(String contentType, String extension,
            String description) {
        this.contentType = contentType;
        this.extension = extension;
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getDescription() {
        return description;
    }
}
