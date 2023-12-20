import {BaseEvent} from "./BaseEvent";
import {Injectable} from "@angular/core";
import {Utils} from "../service/Utils";

@Injectable({providedIn: 'root'})
class ForcedLogoutEvent extends BaseEvent {

  constructor(private util: Utils) {
    super();

  }

    public fire(data) {
        window.location.href = this.util.getPath('autologout?reason=') + data.reason;
    }
}

export { ForcedLogoutEvent };
