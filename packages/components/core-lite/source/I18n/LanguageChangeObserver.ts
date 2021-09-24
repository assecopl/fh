/**
 * Komponent nasłuchujący zmiany języka
 */
interface LanguageChangeObserver {
    /**
     * Zdarzenie zmiany języka
     * @param {string} code Kod języka
     */
    languageChanged(code: string);
}

export {LanguageChangeObserver}