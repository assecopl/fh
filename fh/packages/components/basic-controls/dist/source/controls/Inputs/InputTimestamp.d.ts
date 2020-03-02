import 'pc-bootstrap4-datetimepicker/build/js/bootstrap-datetimepicker.min';
import { InputDate } from "./InputDate";
import { HTMLFormComponent } from "fh-forms-handler";
import { LanguageChangeObserver } from 'fh-forms-handler';
declare class InputTimestamp extends InputDate implements LanguageChangeObserver {
    private tooltipsI18n;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    applyDatepicker(): void;
    disableDatepicker(): void;
    onInputEvent(): void;
    addStyles(): void;
    languageChanged(code: string): void;
    wrap(skipLabel: any, isInputElement: any): void;
    destroy(removeFromParent: boolean): void;
}
export { InputTimestamp };
