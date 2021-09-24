import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";

@injectable()
class StylesheetChangeEvent extends BaseEvent {
    public fire(data) {
        var head = $('head');
        var stylesheet = head.find("link[title='alternate']");
        if (stylesheet.length > 0) {
            stylesheet.remove();
        }

        head.append($("<link>").attr({
            type: 'text/css',
            rel: 'stylesheet',
            title: 'alternate',
            href: data.name
        }));
    }
}

export { StylesheetChangeEvent };