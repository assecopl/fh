import {Injectable, inject} from "@angular/core";
import {BaseEvent} from "./BaseEvent";
import {default as Cookies} from "js-cookie"
import {I18nService} from "../service/i18n.service";


@Injectable({providedIn: 'root'})
class LanguageChangeEvent extends BaseEvent {
    protected override i18n: I18nService = inject(I18nService);

    constructor() {
        super();
    }


    public fire(data): void {

        document.querySelector('html')?.setAttribute('lang', data.code);

        Cookies.set('USERLANG', data.code);
        this.i18n.selectLanguage(data.code);
    }
}

export {LanguageChangeEvent};
