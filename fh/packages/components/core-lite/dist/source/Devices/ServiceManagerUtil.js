"use strict";
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
var FormsManager_1 = require("../Socket/FormsManager");
var SocketOutputCommands_1 = require("../Socket/SocketOutputCommands");
var SocketHandler_1 = require("../Socket/SocketHandler");
var ServiceManagerUtil = /** @class */ (function () {
    function ServiceManagerUtil() {
    }
    ServiceManagerUtil.prototype.callAction = function (serviceId, actionName, params, doLock) {
        if (params === void 0) { params = undefined; }
        if (doLock === void 0) { doLock = true; }
        var deferedEvent = {
            component: this,
            serviceId: serviceId,
            deferred: $.Deferred()
        };
        this.formsManager.eventQueue.push(deferedEvent);
        if (this.formsManager.eventQueue.length == 1) {
            deferedEvent.deferred.resolve();
        }
        var success = this.formsManager.fireEvent(null, actionName, null, serviceId, deferedEvent, doLock, params);
        if (!success) {
            this.formsManager.eventQueue.pop();
        }
    };
    ServiceManagerUtil.prototype.cancelServiceEvents = function (serviceId) {
        this.formsManager.eventQueue = this.formsManager.eventQueue.filter(function (event) { return event.serviceId !== serviceId; });
    };
    ServiceManagerUtil.prototype.sendData = function (serviceId, data) {
        data.serviceId = serviceId;
        this.socketHandler.activeConnector.run(SocketOutputCommands_1.SocketOutputCommands.IN_CLIENT_DATA, {
            "clientMessage": data
        });
    };
    __decorate([
        inversify_1.inject("FormsManager"),
        __metadata("design:type", FormsManager_1.FormsManager)
    ], ServiceManagerUtil.prototype, "formsManager", void 0);
    __decorate([
        inversify_1.inject('SocketHandler'),
        __metadata("design:type", SocketHandler_1.SocketHandler)
    ], ServiceManagerUtil.prototype, "socketHandler", void 0);
    ServiceManagerUtil = __decorate([
        inversify_1.injectable()
    ], ServiceManagerUtil);
    return ServiceManagerUtil;
}());
exports.ServiceManagerUtil = ServiceManagerUtil;
//# sourceMappingURL=ServiceManagerUtil.js.map