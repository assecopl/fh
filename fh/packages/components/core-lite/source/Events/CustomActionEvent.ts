import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";
import {CustomActions} from "./CustomActions";
import {FhContainer} from "../FhContainer";

@injectable()
class CustomActionEvent extends BaseEvent {
     public fire(data): void {
        let actionName = data.actionName;
        let customActions = FhContainer.get<CustomActions>('CustomActions');

        if (customActions.callbacks[actionName]) {
            customActions.callbacks[actionName](data.data);
        }
    }
}

export { CustomActionEvent};