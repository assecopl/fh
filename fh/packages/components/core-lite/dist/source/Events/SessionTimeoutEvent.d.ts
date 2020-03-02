import { BaseEvent } from "./BaseEvent";
import { Util } from "../Util";
export declare class SessionTimeoutEvent extends BaseEvent {
    protected util: Util;
    private readonly timerObject;
    private timerId;
    fire(event: any): void;
    countDownCalc(): void;
    static displayTimer(counterElementId: string, minutes: any, seconds: any): void;
}
