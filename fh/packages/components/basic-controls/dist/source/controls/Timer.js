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
var fh_forms_handler_1 = require("fh-forms-handler");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var fh_forms_handler_2 = require("fh-forms-handler");
var fh_forms_handler_3 = require("fh-forms-handler");
var lazyInject = inversify_inject_decorators_1.default(fh_forms_handler_3.FhContainer).lazyInject;
var Timer = /** @class */ (function (_super) {
    __extends(Timer, _super);
    function Timer(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.interval = _this.componentObj.interval;
        ;
        _this.active = _this.componentObj.active;
        _this.onTimer = _this.componentObj.onTimer;
        return _this;
    }
    Timer.prototype.create = function () {
        this.setupTimer();
    };
    Timer.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'active':
                        this.active = newValue;
                        this.setupTimer();
                        break;
                    case 'interval':
                        this.interval = newValue;
                        this.setupTimer();
                        break;
                }
            }.bind(this));
        }
    };
    Timer.prototype.setupTimer = function () {
        if (this.onTimer != null && this.getViewMode() == 'NORMAL') {
            if (this.timer != null) {
                clearTimeout(this.timer);
                this.timer = null;
            }
            if (this.active && this.interval > 0) {
                this.timer = setTimeout(this.timeout.bind(this), 1000 * this.interval);
            }
        }
    };
    Timer.prototype.destroy = function (removeFromParent) {
        _super.prototype.destroy.call(this, removeFromParent);
        if (this.timer != null) {
            clearTimeout(this.timer);
            this.timer = null;
        }
    };
    ;
    Timer.prototype.timeout = function () {
        if (!this.destroyed) {
            if (this.applicationLock.isActive() || !this.connector.isOpen() || !this.isFormActive()) {
                // delay until application lock is taken down or form if activated
                this.timer = setTimeout(this.timeout.bind(this), 200);
            }
            else {
                // set next timeout and fire event
                this.setupTimer();
                this.fireEventWithLock('onTimer', this.onTimer);
            }
        }
    };
    __decorate([
        lazyInject('ApplicationLock'),
        __metadata("design:type", fh_forms_handler_2.ApplicationLock)
    ], Timer.prototype, "applicationLock", void 0);
    __decorate([
        lazyInject('Connector'),
        __metadata("design:type", fh_forms_handler_2.Connector)
    ], Timer.prototype, "connector", void 0);
    return Timer;
}(fh_forms_handler_1.FormComponent));
exports.Timer = Timer;
//# sourceMappingURL=Timer.js.map