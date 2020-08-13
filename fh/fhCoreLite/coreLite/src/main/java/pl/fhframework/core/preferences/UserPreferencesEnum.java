package pl.fhframework.core.preferences;

import lombok.Getter;

public enum  UserPreferencesEnum {
    /**
     * Use default prefs
     */
    USE_DEFAULTS_PREF("USE_DEFAULTS_PREF"),
    /**
     * Formata: szerokość projektowanad
     */
    DESIGNER_WIDTH_PREF("DESIGNER_WIDTH_PREF"),
    /**
     * Ukryj wewnętrzne odstępy
     */
    HIDE_INNER_PADDING_PREF("HIDE_INNER_PADDING_PREF"),
    /**
     * Edycja aktywnego elementu
     */
    EDIT_ACTIVE_ELEMENT_PREF("EDIT_ACTIVE_ELEMENT_PREF"),
    /**
     * Odfiltruj Row z Drzewa elementów
     */
    FILTER_OUT_ROW_FROM_TOOLBOX_PREF("FILTER_OUT_ROW_FROM_TOOLBOX_PREF");

    @Getter
    private String key;

    UserPreferencesEnum(String key) {
        this.key = key;
    }
}
