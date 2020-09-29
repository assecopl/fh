import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";

@injectable()
class RedirectPostEvent extends BaseEvent {

    public fire(data: { url: string, params: Map<string, string> }) {
        let form = '<form action="' + data.url + '" method="post">\n';
        if (data.params) {
            Object.keys(data.params).map( key => {
                form += ' <input type="hidden" name="' + key + '" value="' + data.params[key] + '"/>\n'
            });
        }
        form += '</form>';

        let formElement = $(form);
        $('body').append(formElement);
        $(formElement).trigger('submit');
    }

}

export {RedirectPostEvent};