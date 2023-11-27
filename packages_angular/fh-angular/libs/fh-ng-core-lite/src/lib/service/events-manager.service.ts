import {Injectable, inject} from '@angular/core';
import {BaseEvent} from "../events/BaseEvent";
import {NotificationEvent} from "../events/NotificationEvent";
import {NotificationService} from "./Notification";
import {SessionTimeoutEvent} from "../events/SessionTimeoutEvent";
import {CustomActionEvent} from '../events/CustomActionEvent';
import {LanguageChangeEvent} from "../events/LanguageChangeEvent";
import {RedirectEvent} from "../events/RedirectEvent";

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

  constructor(private sessionTimeoutEvent: SessionTimeoutEvent,
              private notificationEvent: NotificationEvent,
              private customActionEvent: CustomActionEvent,
              private languageChangeEvent: LanguageChangeEvent,
              private redirectEvent:RedirectEvent) {
    this.registerEvent(sessionTimeoutEvent);
    this.registerEvent(notificationEvent);
    this.registerEvent(customActionEvent);
    this.registerEvent(languageChangeEvent);
    this.registerEvent(redirectEvent);
  }

  public registerEvent(event: BaseEvent): void {
    this.events[event.constructor.name] = event;
  }

  public registerEventWithName(name: string, event: BaseEvent): void {
    this.events[name] = event;
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


