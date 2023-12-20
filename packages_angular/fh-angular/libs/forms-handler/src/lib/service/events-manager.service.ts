import {Injectable, inject} from '@angular/core';
import {BaseEvent} from "../events/BaseEvent";
import {NotificationEvent} from "../events/NotificationEvent";
import {NotificationService} from "./Notification";
import {SessionTimeoutEvent} from "../events/SessionTimeoutEvent";
import {CustomActionEvent} from '../events/CustomActionEvent';
import {LanguageChangeEvent} from "../events/LanguageChangeEvent";
import {RedirectEvent} from "../events/RedirectEvent";
import {MessageEvent} from "../events/MessageEvent";
import {ShutdownEvent} from "../events/ShutdownEvent";
import {StylesheetChangeEvent} from "../events/StylesheetChangeEvent";
import {ScrollEvent} from "../events/ScrollEvent";
import {FileDownloadEvent} from "../events/FileDownloadEvent";
import {FocusEvent} from "../events/FocusEvent";
import {ForcedLogoutEvent} from "../events/ForcedLogoutEvent";
@Injectable({
  providedIn: 'root',
})
class EventsManager {

  private notificationService: NotificationService = inject(NotificationService);



  protected events: {
    [index: string]: BaseEvent;
  } = {}

  constructor(public sessionTimeoutEvent: SessionTimeoutEvent,
              public notificationEvent: NotificationEvent,
              public customActionEvent: CustomActionEvent,
              public languageChangeEvent: LanguageChangeEvent,
              public redirectEvent: RedirectEvent,
              public shutdownEvent: ShutdownEvent,
              public stylesheetChangeEvent: StylesheetChangeEvent,
              public scrollEvent: ScrollEvent,
              public fileDownloadEvent: FileDownloadEvent,
              public focusEvent: FocusEvent,
              public forcedLogoutEvent: ForcedLogoutEvent,
              public messageEvent: MessageEvent) {
    this.registerEvent(sessionTimeoutEvent);
    this.registerEvent(notificationEvent);
    this.registerEvent(customActionEvent);
    this.registerEvent(languageChangeEvent);
    this.registerEvent(redirectEvent);
    this.registerEvent(shutdownEvent);
    this.registerEvent(messageEvent);
    this.registerEvent(stylesheetChangeEvent);
    this.registerEvent(scrollEvent);
    this.registerEvent(fileDownloadEvent);
    this.registerEvent(forcedLogoutEvent);
    this.registerEvent(focusEvent);
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


