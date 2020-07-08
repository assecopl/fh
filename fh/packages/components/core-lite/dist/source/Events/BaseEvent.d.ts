import { I18n } from "../I18n/I18n";
declare abstract class BaseEvent {
    protected i18n: I18n;
    abstract fire(data: any): any;
}
export { BaseEvent };
