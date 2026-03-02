package pl.fhframework.dp.commons.prints.printservice;

/**
 * Print exception.
 *
 * 1 Operation executed successfully. -1 Unsupported output format. -2
 * Unsupported template name. -99 Unknown system error.
 *
 * @author <a href="mailto:pawel_kasprzak@skg.pl">Paweł Kasprzak</a>
 * @version $Revision: $, $Date: $
 */
public class PrintException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final int ERROR_UNSUPPORTED_OUTPUT_FORMAT = -1;
    public static final int ERROR_UNSUPPORTED_TEMPLATE_NAME = -2;
    public static final int ERROR_UNKNOWN = -99;
    private int errorCode = ERROR_UNKNOWN;
    private boolean recoverable = false;

    public PrintException(int errorCode) {
        this.errorCode = errorCode;
    }

    public PrintException(String msg) {
        super(msg);
    }

    public PrintException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public PrintException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public PrintException(int errorCode, String msg, Throwable cause) {
        super(msg, cause);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int code) {
        this.errorCode = code;
    }

    public boolean getRecoverable() {
        return this.recoverable;
    }

    public void setRecoverable(boolean recoverable) {
        this.recoverable = recoverable;
    }
}
