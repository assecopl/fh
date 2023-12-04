import {
    Pipe,
    PipeTransform
} from "@angular/core";
import {I18nService} from "../service/i18n.service";

@Pipe({
    name: 'i18n'
})
export class i18nPipe implements PipeTransform {
    constructor(private _i18n: I18nService) {
    }

    public transform(value: string, args: any = null, code: string = null): string {
        return this._i18n.__(value, args, code);
    }
}
