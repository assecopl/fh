package pl.fhframework.docs.i18n;

public enum Language {
    PL("pl"), ENG("en");

    private String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
