package pl.fhframework.compiler.core.services.dynamic.model;

public enum RestParameterTypeEnum {
    Query("Query"),
    Body("Body"),
    Template("Template"),
    Header("Header"),
    ;

    private String opis;

    RestParameterTypeEnum(String opis) {
        this.opis = opis;
    }

    @Override
    public String toString() {
        return opis;
    }
}
