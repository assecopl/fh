import { LanguageChangeObserver } from "./LanguageChangeObserver";
declare class I18n {
    supportedLanguages: string[];
    strings: any;
    defaultStrings: any;
    selectedLanguage: string;
    /**
     * Components observing language change event
     * @type {LanguageChangeObserver[]}
     */
    private observers;
    subscribe(component: LanguageChangeObserver): void;
    unsubscribe(component: LanguageChangeObserver): void;
    private isLanguageSupported;
    registerStrings(code: string, strings: any, isCustom?: boolean): void;
    private overrideStrings;
    selectLanguage(code: string): void;
    translateString(string: string, args: any, code: string): string;
    __(string: string, args?: any, code?: string): string;
}
export { I18n };
