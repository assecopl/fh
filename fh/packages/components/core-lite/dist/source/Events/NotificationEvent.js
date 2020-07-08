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
require("bootstrap/js/dist/toast");
var NotificationEvent = /** @class */ (function (_super) {
    __extends(NotificationEvent, _super);
    function NotificationEvent() {
        return _super.call(this) || this;
    }
    NotificationEvent.prototype.fire = function (data) {
        this.createToast(data);
    };
    NotificationEvent.prototype.createToast = function (data) {
        if (data.level === 'error') {
            data.level = 'danger';
        }
        var container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.style.position = 'fixed';
            container.style.top = '15px';
            container.style.right = '30px';
            container.style.zIndex = '1061';
            document.body.appendChild(container);
        }
        var toast = document.createElement('div');
        toast.classList.add('toast');
        toast.classList.add('fade');
        toast.classList.add('bg-' + data.level);
        toast.classList.add('text-light');
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        var body = document.createElement('div');
        body.classList.add('toast-body');
        body.innerText = data.message;
        toast.appendChild(body);
        // @ts-ignore
        $(toast).toast({
            autohide: true,
            animation: true,
            delay: 5000
        }).toast('show');
        container.appendChild(toast);
        $(toast).on('click', function () {
            // @ts-ignore
            $(this).off('click').toast('hide');
        });
        $(toast).on('hidden.bs.toast', function () {
            // @ts-ignore
            $(this).toast('dispose').remove();
        });
    };
    ;
    NotificationEvent = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [])
    ], NotificationEvent);
    return NotificationEvent;
}(BaseEvent_1.BaseEvent));
exports.NotificationEvent = NotificationEvent;
//# sourceMappingURL=NotificationEvent.js.map