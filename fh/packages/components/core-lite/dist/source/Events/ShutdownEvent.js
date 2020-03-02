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
var ShutdownEvent_pl_1 = require("./i18n/ShutdownEvent.pl");
var ShutdownEvent_en_1 = require("./i18n/ShutdownEvent.en");
var inversify_1 = require("inversify");
var BaseEvent_1 = require("./BaseEvent");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var Util_1 = require("../Util");
var Connector_1 = require("../Socket/Connector");
var FormsManager_1 = require("../Socket/FormsManager");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var ShutdownEvent = /** @class */ (function (_super) {
    __extends(ShutdownEvent, _super);
    function ShutdownEvent() {
        var _this = _super.call(this) || this;
        _this.i18n.registerStrings('pl', ShutdownEvent_pl_1.ShutdownEventPL);
        _this.i18n.registerStrings('en', ShutdownEvent_en_1.ShutdownEventEN);
        return _this;
    }
    ShutdownEvent.prototype.fire = function (data) {
        this.formsManager.duringShutdown = true;
        if (data.graceful) {
            this.util.showDialog(this.i18n.__('graceful.title'), this.i18n.__('graceful.message'), this.i18n.__('graceful.button'), 'btn-secondary', null);
        }
        else {
            this.connector.close();
            this.util.showDialog(this.i18n.__('title'), this.i18n.__('message'), this.i18n.__('button'), 'btn-danger', function () { location.reload(true); });
        }
    };
    __decorate([
        lazyInject("FormsManager"),
        __metadata("design:type", FormsManager_1.FormsManager)
    ], ShutdownEvent.prototype, "formsManager", void 0);
    __decorate([
        lazyInject("Util"),
        __metadata("design:type", Util_1.Util)
    ], ShutdownEvent.prototype, "util", void 0);
    __decorate([
        lazyInject("Connector"),
        __metadata("design:type", Connector_1.Connector)
    ], ShutdownEvent.prototype, "connector", void 0);
    ShutdownEvent = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [])
    ], ShutdownEvent);
    return ShutdownEvent;
}(BaseEvent_1.BaseEvent));
exports.ShutdownEvent = ShutdownEvent;
//# sourceMappingURL=ShutdownEvent.js.map