import { HTMLFormComponent } from "fh-forms-handler";
import 'summernote/dist/summernote-bs4';
import 'summernote/lang/summernote-pl-PL';
import { LanguageChangeObserver } from "fh-forms-handler";
declare class HtmlEditor extends HTMLFormComponent implements LanguageChangeObserver {
    protected onChange: string;
    protected editor: string;
    protected code: string;
    protected displayLanguage: string;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    initEditor(): void;
    update(change: any): void;
    onChangeEvent(content: any): void;
    getDisplayLanguageCode(code: string): string;
    languageChanged(code: string): void;
    destroy(removeFromParent: any): void;
}
export { HtmlEditor };
