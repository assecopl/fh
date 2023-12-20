import {BaseEvent} from "./BaseEvent";
import {Injectable} from "@angular/core";
import {Subject} from "rxjs";


@Injectable({providedIn: 'root'})
class FocusEvent extends BaseEvent {


  public focusSubject: Subject<{formElementId:string, containerId:string}> = new Subject<any>();

    constructor() {
        super();
    }

    public fire(data) {
        this.focusSubject.next({formElementId:data.formElementId, containerId:data.containerId});
    }
}

export {FocusEvent};
