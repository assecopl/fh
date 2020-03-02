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
        this.fireUpdate("ChatForm");
        this.fireUpdate("ChatSystemForm");
    }

    private fireUpdate(formId: string) {
        let form = this.formsManager.findForm(formId);
        if(form != null && form.state == 'ACTIVE') {
            let component = form.findComponent('refreshBtn', false, false, true);
            if (component != null) {
                component.fireEvent('onClick', 'onChatRefresh');
            }
        }
    }
}

export { ChatEvent };
