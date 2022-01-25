package pl.fhframework.dp.transport.searchtemplate;

public enum OperatorType {
    EQUAL("=", "fa fa-equals"),
    NOT_EQUAL("!=", "fa fa-not-equal"),
    LT("<", "fa fa-less-than"),
    LTE("<=", "fa fa-less-than-equal"),
    GT(">", "fa fa-greater-than"),
    GTE(">=", "fa fa-greater-than-equal"),

    LIKE("LIKE", ""),
    DONT_LIKE("NOT LIKE", ""),
    IN("IN", ""),
    NOT_IN("NOT IN", ""),
    ;
    private String description;
    private String icon;

    OperatorType(String description, String icon) {
        this.description = description;
        this.icon = icon;
    }

    public String getDisplayedValue() {
        //return icon != null && !icon.isEmpty()? "[icon='" + icon + "']" : description;
        //puki co nie działa byt dobrze w lunie wyświetlanie PML jako wartości w Combo czy SelectOne
        // więc na razie wyświetlamy po prostu description, kiedyś można będzie wyświetlać to co było powyżej
        return description;
    }

}
