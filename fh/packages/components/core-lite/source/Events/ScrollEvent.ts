import {injectable} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {BaseEvent} from "./BaseEvent";
import {FormsManager} from "../Socket/FormsManager";
import {FhContainer} from "../FhContainer";
import * as $ from "jquery";
import {Util} from "../Util";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class ScrollEvent extends BaseEvent {
    @lazyInject("Util")
    protected util: Util;

    constructor() {
        super();
    }

    public fire(data) {
        this.util.scrollToComponent(data.formElementId, data.animateDuration);
    }

}

export {ScrollEvent};