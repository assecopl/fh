import {BaseEvent} from "./BaseEvent";
import {Injectable} from "@angular/core";
import {CustomActionsManager} from "../service/custom-actions-manager.service";
import {EventsManager} from "../service/events-manager.service";

@Injectable({
  providedIn: 'root',
})
class CustomActionEvent extends BaseEvent {


  constructor(private customActionsManager: CustomActionsManager) {
    super();
  }

  public fire(data): void {
    let actionName = data.actionName;
    //
    if (this.customActionsManager.callbacks[actionName]) {
      this.customActionsManager.callbacks[actionName](data.data);
    }
  }
}

export {CustomActionEvent};
