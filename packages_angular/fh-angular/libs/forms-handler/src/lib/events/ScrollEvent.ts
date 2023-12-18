import {BaseEvent} from "./BaseEvent";
import {inject, Injectable} from "@angular/core";
import {Utils} from "@fh-ng/forms-handler";


@Injectable({providedIn: 'root'})
class ScrollEvent extends BaseEvent {

    protected util: Utils = inject(Utils);

    constructor() {
        super();
    }

    public fire(data) {
        // this.util.scrollToComponent(data.formElementId, data.animateDuration);
      console.log("ScrollEvent" , "not implemented" , data)
    }

}

export {ScrollEvent};
