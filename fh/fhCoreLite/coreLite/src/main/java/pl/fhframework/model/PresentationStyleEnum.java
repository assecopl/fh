package pl.fhframework.model;

public enum PresentationStyleEnum {
    /**
     * Everything is ok
     */
    OK,
    /**
     * There is a notice to field. Everything is ok.
     */
    INFO,
    /**
     * Signals some minors problems with field
     */
    WARNING,
    /**
     * Signals non blocking error. Data value from field can be assigned to model.
     */
    ERROR,
    /**
     * Signals blocking error. Data value from field cannot be assigned to model and action cannot be performed
     */
    BLOCKER;
}
