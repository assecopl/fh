"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var BaseEvent_1 = require("./BaseEvent");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var FhContainer_1 = require("../FhContainer");
var Util_1 = require("../Util");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var SessionTimeoutEvent = /** @class */ (function (_super) {
    __extends(SessionTimeoutEvent, _super);
    function SessionTimeoutEvent() {
        var _this_1 = _super !== null && _super.apply(this, arguments) || this;
        _this_1.timerObject = 'logoutTimer';
        return _this_1;
    }
    SessionTimeoutEvent_1 = SessionTimeoutEvent;
    SessionTimeoutEvent.prototype.fire = function (event) {
        var timerObject = new TimerProps();
        timerObject.mainTimerId = event.data.now.epochSecond;
        timerObject.counterElementId = event.data.counterElementId;
        timerObject.countDownDate = new Date().getTime() + event.data.maxInactivityMinutes * 60 * 1000;
        window[this.timerObject] = timerObject;
        this.timerId = timerObject.mainTimerId;
        clearTimeout(window[this.timerObject].timerId);
        var _this = this;
        window[this.timerObject].timerId = setTimeout(function () {
            _this.countDownCalc();
        }, 1000);
        SessionTimeoutEvent_1.displayTimer(timerObject.counterElementId, event.data.maxInactivityMinutes, 0);
    };
    SessionTimeoutEvent.prototype.countDownCalc = function () {
        var now = new Date().getTime();
        var timerObject = window[this.timerObject];
        var distance = timerObject.countDownDate - now;
        var seconds = 0;
        var minutes = 0;
        if (distance > 0) {
            var distanceInSeconds = Math.ceil(distance / 1000.0);
            seconds = distanceInSeconds % 60;
            minutes = Math.floor(distanceInSeconds / 60) % 60;
        }
        if (timerObject.counterElementId) {
            SessionTimeoutEvent_1.displayTimer(timerObject.counterElementId, minutes, seconds);
        }
        if (distance > 0 && timerObject.mainTimerId == this.timerId) {
            var _this_2 = this;
            timerObject.timerId = setTimeout(function () {
                _this_2.countDownCalc();
            }, distance % 1000);
        }
        if (distance <= 0) {
            clearTimeout(timerObject.timerId);
            window.location.href = this.util.getPath('autologout?reason=timeout');
        }
    };
    SessionTimeoutEvent.displayTimer = function (counterElementId, minutes, seconds) {
        if (counterElementId) {
            var text = minutes + ":" + seconds.toLocaleString(undefined, { minimumIntegerDigits: 2 });
            var counterElement = document.getElementById(counterElementId);
            if (counterElement) {
                counterElement.innerText = text;
            }
        }
    };
    var SessionTimeoutEvent_1;
    __decorate([
        lazyInject("Util"),
        __metadata("design:type", Util_1.Util)
    ], SessionTimeoutEvent.prototype, "util", void 0);
    SessionTimeoutEvent = SessionTimeoutEvent_1 = __decorate([
        inversify_1.injectable()
    ], SessionTimeoutEvent);
    return SessionTimeoutEvent;
}(BaseEvent_1.BaseEvent));
exports.SessionTimeoutEvent = SessionTimeoutEvent;
var TimerProps = /** @class */ (function () {
    function TimerProps() {
    }
    return TimerProps;
}());
//# sourceMappingURL=SessionTimeoutEvent.js.map