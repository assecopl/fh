export enum PresentationStyleEnum {
    /**
     * Everything is ok
     */
    OK = "OK",
    /**
     * There is a notice to field. Everything is ok.
     */
    INFO = "INFO",
    /**
     * Signals some minors problems with field
     */
    WARNING = "WARNING",
    /**
     * Signals non blocking error. Data value from field can be assigned to model.
     */
    ERROR = "ERROR",
    /**
     * Signals blocking error. Data value from field cannot be assigned to model and action cannot be performed
     */
    BLOCKER = "BLOCKER"
}
