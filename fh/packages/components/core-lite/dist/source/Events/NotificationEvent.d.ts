import { BaseEvent } from "./BaseEvent";
import 'bootstrap/js/dist/toast';
declare class NotificationEvent extends BaseEvent {
    constructor();
    fire(data: any): void;
    private createToast;
}
export { NotificationEvent };
