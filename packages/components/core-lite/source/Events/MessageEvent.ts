import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";
import getDecorators from "inversify-inject-decorators";
import {Util} from "../Util";
import {FhContainer} from "../FhContainer";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class MessageEvent extends BaseEvent {

    @lazyInject("Util")
    protected util: Util;

    public fire(data) {
        this.util.showDialog(
            this.escapeHtml(data.title),
            this.escapeHtml(data.message),
            'OK',
            'btn-secondary',
            null);
    }

    private escapedMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
        '/': '&#x2F;',
        '`': '&#x60;',
        '=': '&#x3D;',
        '\n': '<br>'
    };

    private escapeHtml(string) {
        return String(string).replace(/[&<>"'\/]|[\n]/g, function (s) {
            return this.escapedMap[s];
        }.bind(this));
    }
}

export { MessageEvent };