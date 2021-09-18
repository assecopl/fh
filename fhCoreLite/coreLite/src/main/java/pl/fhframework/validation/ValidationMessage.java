package pl.fhframework.validation;

// working progress
public class ValidationMessage {

    enum Type { CUSTOM, FH }

    private Object model;
    private String attribute;
    private Type type;

}
