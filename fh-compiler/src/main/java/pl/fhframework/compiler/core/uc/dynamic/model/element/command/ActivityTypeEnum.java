package pl.fhframework.compiler.core.uc.dynamic.model.element.command;

/**
 * Created by pawel.ruta on 2017-03-31.
 */
public enum ActivityTypeEnum {
    RunUseCase("Run use case"),
    RunAction("Run action"),
    GoToExit("Go to exit"),
    ShowForm("Show form", true),
    ShowMessage("Show message", true),
    RunRule("Run rule", true, true),
    RunService("Run service", true, true),
    DataRead("Read data", true, true),
    DataWrite("Write data", true, true),
    DataRefresh("Refresh data", true, true),
    DataDelete("Delete data", true, true),
    AssignValue("Assign value", true, true),
    NewInstance("New Instance", true, true),
    ExpressionEval("Expression evaluation", true, true),
    Validate("Validate", true, true),
    ;

    private String description;

    // unavailable for event links
    private boolean internal;

    private boolean predefinedFunction;

    ActivityTypeEnum(String description) {
        this.description = description;
    }

    ActivityTypeEnum(String description, boolean internal) {
        this(description);

        this.internal = internal;
    }

    ActivityTypeEnum(String description, boolean internal, boolean predefinedFunction) {
        this.description = description;
        this.internal = internal;
        this.predefinedFunction = predefinedFunction;
    }

    @Override
    public String toString() {
        return description;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isPredefinedFunction() {
        return predefinedFunction;
    }
}
