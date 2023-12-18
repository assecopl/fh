import {BaseEvent} from "./BaseEvent";
import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
class StylesheetChangeEvent extends BaseEvent {
    public fire(data) {
        // var head = $('head');
        // var stylesheet = head.find("link[title='alternate']");
        // if (stylesheet.length > 0) {
        //     stylesheet.remove();
        // }
        //
        // head.append($("<link>").attr({
        //     type: 'text/css',
        //     rel: 'stylesheet',
        //     title: 'alternate',
        //     href: data.name
        // }));
      console.log("StylesheetChangeEvent" , "not implemented" , data)
    }
}

export { StylesheetChangeEvent };
