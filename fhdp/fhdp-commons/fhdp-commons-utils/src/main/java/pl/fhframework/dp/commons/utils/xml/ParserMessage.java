
package pl.fhframework.dp.commons.utils.xml;

public class ParserMessage {

    private String message;
    private String pointer;

    ParserMessage(String message, int lineNumber, int columnNumber) {
        this.message = formatMessage(message);
        this.pointer = "Linia: " + lineNumber + " Kolumna: " + columnNumber;
    }

    ParserMessage(String message, String pointer) {
        this.message = formatMessage(message);
        this.pointer = pointer;
    }

    private String formatMessage(String message) {
        if (message != null) {
            if (message.indexOf("#AnonType_") != -1) {
                message = message.replaceAll("#AnonType_", "");
            }
            if (message.indexOf("typ, 'null'") != -1) {
                message = message.replaceAll("typ, 'null'", "typ");
            }
        }
        return message;
    }

    public String getMessage() {
        return message;
    }

    public String getPointer() {
        return pointer;
    }

    public String toString() {
        return new StringBuilder().append(message).append("; ").append(pointer).toString();
    }
}
