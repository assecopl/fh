"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Listener that registers handler to Window events. Lets developer to easly register and unregister handler.
 * By default it delays firing event to avoid many subsequent handler calls, e.g. during window resize on FF.
 * Use delay == null to avoid the delay.
 * Constructing a new WindowEventsListener object causes registration.
 */
var WindowEventsListener = /** @class */ (function () {
    function WindowEventsListener(events, handler, delay) {
        if (delay === void 0) { delay = 100; }
        this.events = events;
        this.handler = handler;
        this.internalHandler = function (event) {
            if (delay) {
                if (this.timeoutHandle != null) {
                    clearTimeout(this.timeoutHandle);
                }
                this.timeoutHandle = setTimeout(function () {
                    this.timeoutHandle = null;
                    this.handler(event);
                }.bind(this), delay);
            }
            else {
                this.handler(event);
            }
        }.bind(this);
        this.register();
    }
    WindowEventsListener.prototype.register = function () {
        $(window).on(this.events, null, null, this.internalHandler);
    };
    WindowEventsListener.prototype.unregister = function () {
        $(window).off(this.events, null, this.internalHandler);
    };
    return WindowEventsListener;
}());
exports.WindowEventsListener = WindowEventsListener;
//# sourceMappingURL=WindowEventsListener.js.map