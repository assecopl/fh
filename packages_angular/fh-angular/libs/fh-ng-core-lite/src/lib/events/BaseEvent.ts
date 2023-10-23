import {Directive, inject} from "@angular/core";
import {EventsManager} from "../service/events-manager.service";

abstract class BaseEvent {
  protected i18n: any;

  protected constructor() {
  }

  public abstract fire(data: any);
}

export {BaseEvent};
