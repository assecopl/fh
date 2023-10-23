import {BaseEvent} from "./BaseEvent";
import {Injectable, inject} from "@angular/core";
import {Util} from "../service/Util";

@Injectable({providedIn: 'root'})
export class SessionTimeoutEvent extends BaseEvent {
  protected util: Util = inject(Util);

  private readonly timerObject: string = 'logoutTimer';
  private timerId: number;

  constructor() {
    super();
  }

  public fire(event) {
    let timerObject = new TimerProps();
    timerObject.mainTimerId = event.data.now.epochSecond;
    timerObject.counterElementId = event.data.counterElementId;
    timerObject.countDownDate = new Date().getTime() + event.data.maxInactivityMinutes * 60 * 1000;
    window[this.timerObject] = timerObject;
    this.timerId = timerObject.mainTimerId;

    clearTimeout(window[this.timerObject].timerId);

    let _this = this;
    window[this.timerObject].timerId = setTimeout(function () {
      _this.countDownCalc();
    }, 1000);

    SessionTimeoutEvent.displayTimer(timerObject.counterElementId, event.data.maxInactivityMinutes, 0);
  }

  countDownCalc() {
    let now = new Date().getTime();
    let timerObject = window[this.timerObject]

    let distance = timerObject.countDownDate - now;

    let seconds = 0;
    let minutes = 0;

    if (distance > 0) {
      let distanceInSeconds = Math.ceil(distance / 1000.0);
      seconds = distanceInSeconds % 60;
      minutes = Math.floor(distanceInSeconds / 60) % 60;
    }

    if (timerObject.counterElementId) {
      SessionTimeoutEvent.displayTimer(timerObject.counterElementId, minutes, seconds);
    }

    if (distance > 0 && timerObject.mainTimerId == this.timerId) {
      let _this = this;
      timerObject.timerId = setTimeout(function () {
        _this.countDownCalc();
      }, distance % 1000);
    }
    if (distance <= 0) {
      clearTimeout(timerObject.timerId);
      window.location.href = this.util.getPath('autologout?reason=timeout');
    }
  }

  static displayTimer(counterElementId: string, minutes, seconds) {
    if (counterElementId) {
      let text = minutes + ":" + seconds.toLocaleString(undefined, {minimumIntegerDigits: 2});
      let counterElement = document.getElementById(counterElementId);
      if (counterElement) {
        counterElement.innerText = text;
      }
    }
  }
}

class TimerProps {
  timerId: number;
  mainTimerId: number;
  countDownDate: number;
  counterElementId: string;
}
