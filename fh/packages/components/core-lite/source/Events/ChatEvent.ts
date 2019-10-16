import {injectable} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {FormsManager} from "../Socket/FormsManager";
import {FhContainer} from "../FhContainer";
import {BaseEvent} from "./BaseEvent";

let { lazyInject } = getDecorators(FhContainer);

@injectable()
class ChatEvent extends BaseEvent {

    @lazyInject('FormsManager')
    private formsManager: FormsManager;

    public fire(data) {
        let form = this.formsManager.findForm('ChatForm');
        if(form != null) {
            let component = form.findComponent('messagesTable', false, false, true);
            if (component != null) {
                component.fireEvent('onRowDoubleClick', 'onChatRefresh');
            }
        }
    }

}

export { ChatEvent };