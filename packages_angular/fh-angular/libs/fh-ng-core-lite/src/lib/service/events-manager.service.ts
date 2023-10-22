import {Injectable, inject} from '@angular/core';
import {BaseEvent} from "../events/BaseEvent";
import {NotificationEvent} from "../events/NotificationEvent";
import {NotificationService} from "./Notification";

declare var contextRoot: string;
declare var fhBaseUrl: string;
declare const $: any;

@Injectable({
  providedIn: 'root',
})
class EventsManager {

  private notificationService: NotificationService = inject(NotificationService);

  protected events: {
    [index: string]: BaseEvent;
  } = {}

  constructor() {
    this.registerEvent(new NotificationEvent());
  }

  public registerEvent(event: BaseEvent): void {

    this.events[event.constructor.name] = event;
  }

  public getEvent(type: string): BaseEvent {

    if (this.events[type]) {
      return this.events[type];
    } else {
      this.notificationService.showWarning("Event " + type + " is not registered.");
      return null;
    }
  }


}

export {EventsManager};


