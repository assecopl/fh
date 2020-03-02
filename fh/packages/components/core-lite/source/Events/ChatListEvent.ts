import {injectable} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {FormsManager} from "../Socket/FormsManager";
import {FhContainer} from "../FhContainer";
import {BaseEvent} from "./BaseEvent";

let { lazyInject } = getDecorators(FhContainer);

@injectable()
class ChatListEvent extends BaseEvent {

    @lazyInject('FormsManager')
    private formsManager: FormsManager;

    public fire(data: { show: boolean }) {
        let chatFgWidth, chatFgDimWidth, chatGroupWidth;
        if (data.show) {
            chatFgWidth = '800px';
            chatFgDimWidth = 800;
            chatGroupWidth = '67%';
        } else{
            chatFgWidth = '600px';
            chatFgDimWidth = 600;
            chatGroupWidth = '100%';
        }

        let form = this.formsManager.findForm('ChatSystemForm');
        if(form != null) {
            let component = form.findComponent('chatFG', false, false, true);
            if (component != null) {
                console.log(component);
                component.dimensions.width = chatFgDimWidth;
                component.htmlElement.style.width = chatFgWidth;
            }

            let chatGroup = form.findComponent('chatGroup', false, false, true);
            if (chatGroup != null) {
                chatGroup.htmlElement.style.width = chatGroupWidth;
            }
        }
    }
}

export { ChatListEvent };
