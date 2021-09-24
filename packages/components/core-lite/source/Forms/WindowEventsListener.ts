/**
 * Listener that registers handler to Window events. Lets developer to easly register and unregister handler.
 * By default it delays firing event to avoid many subsequent handler calls, e.g. during window resize on FF.
 * Use delay == null to avoid the delay.
 * Constructing a new WindowEventsListener object causes registration.
 */
class WindowEventsListener {
    private handler: any;
    private internalHandler: any;
    private events: string;
    private timeoutHandle: any;

    constructor(events: string, handler: any, delay: number = 100) {
        this.events = events;
        this.handler = handler;
        this.internalHandler = function (event: any) {
            if (delay) {
                if (this.timeoutHandle != null) {
                    clearTimeout(this.timeoutHandle);
                }
                this.timeoutHandle = setTimeout(function () {
                    this.timeoutHandle = null;
                    this.handler(event);
                }.bind(this), delay);
            } else {
                this.handler(event);
            }
        }.bind(this);
        this.register();
    }

    public register(): void {
        $(window).on(this.events, null, null, this.internalHandler);
    }

    public unregister(): void {
        $(window).off(this.events, null, this.internalHandler);
    }
}

export {WindowEventsListener};