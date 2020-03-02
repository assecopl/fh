import { BaseEvent } from "./BaseEvent";
import { I18n } from "../I18n/I18n";
declare class LanguageChangeEvent extends BaseEvent {
    protected i18n: I18n;
    fire(data: any): void;
}
export { LanguageChangeEvent };
