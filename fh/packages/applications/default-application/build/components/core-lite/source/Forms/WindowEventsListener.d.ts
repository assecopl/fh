/**
 * Listener that registers handler to Window events. Lets developer to easly register and unregister handler.
 * By default it delays firing event to avoid many subsequent handler calls, e.g. during window resize on FF.
 * Use delay == null to avoid the delay.
 * Constructing a new WindowEventsListener object causes registration.
 */
declare class WindowEventsListener {
    private handler;
    private internalHandler;
    private events;
    private timeoutHandle;
    constructor(events: string, handler: any, delay?: number);
    register(): void;
    unregister(): void;
}
export { WindowEventsListener };
