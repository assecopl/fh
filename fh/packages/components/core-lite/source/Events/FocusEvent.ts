import {injectable} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {BaseEvent} from "./BaseEvent";
import {FormsManager} from "../Socket/FormsManager";
import {FhContainer} from "../FhContainer";

let { lazyInject } = getDecorators(FhContainer);

@injectable()
class FocusEvent extends BaseEvent {
    @lazyInject('FormsManager')
    private formsManager: FormsManager;

    constructor() {
        super();
    }

    public fire(data) {
        this.formsManager.focusComponent(data.formElementId, data.containerId);
    }
}

export {FocusEvent};