import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";
import {I18n} from "../I18n/I18n";
import getDecorators from "inversify-inject-decorators";
import {FhContainer} from "../FhContainer";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class LanguageChangeEvent extends BaseEvent {
    @lazyInject("I18n")
    protected i18n: I18n;

    public fire(data): void {
        let head = $('html');
        head.attr('lang', data.code);

        this.i18n.selectLanguage(data.code);
    }
}

export { LanguageChangeEvent};