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
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var ApplicationLock_1 = require("./ApplicationLock");
var SocketHandler_1 = require("./SocketHandler");
var FormsManager_1 = require("./FormsManager");
var $ = require("jquery");
var SocketOutputCommands_1 = require("./SocketOutputCommands");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var FH = /** @class */ (function () {
    function FH() {
        this.applicationLocked = false;
        this.ignoreNextHashChange = false;
    }
    FH.prototype.init = function () {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connect(function (connectionIdJson) {
            this.socketHandler.connectionId = connectionIdJson.sessionId;
            var requestId = this.socketHandler.activeConnector.getSubsystemMetadata(function (requestId, data) {
                var _this = this;
                this.formsManager.handleEvent(requestId, data);
                for (var _i = 0, _a = this.serviceManagers; _i < _a.length; _i++) {
                    var deviceManager = _a[_i];
                    deviceManager.init(function (config) {
                        return _this.socketHandler.activeConnector.run(SocketOutputCommands_1.SocketOutputCommands.IN_CLIENT_DATA, config);
                    });
                }
            }.bind(this));
            this.applicationLock.enable(requestId);
        }.bind(this));
        $(function () {
            $(window).on('hashchange', function () {
                if (!this.ignoreNextHashChange) {
                    var json = { 'url': location.pathname + this.formsManager.getLocationHash() };
                    var requestId = this.socketHandler.activeConnector.run(SocketOutputCommands_1.SocketOutputCommands.URL_CHANGE, json);
                    this.applicationLock.enable(requestId);
                }
                else {
                    this.ignoreNextHashChange = false;
                }
            }.bind(this));
        }.bind(this));
    };
    FH.prototype.initExternal = function (socketUrl) {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connectExternal(function (connectionIdJson) {
            this.socketHandler.connectionId = connectionIdJson.sessionId;
            var requestId = this.socketHandler.activeConnector.getSubsystemMetadata(function (requestId, data) {
                var _this = this;
                this.formsManager.handleEvent(requestId, data);
                for (var _i = 0, _a = this.serviceManagers; _i < _a.length; _i++) {
                    var deviceManager = _a[_i];
                    deviceManager.init(function (config) {
                        return _this.socketHandler.activeConnector.run(SocketOutputCommands_1.SocketOutputCommands.IN_CLIENT_DATA, config);
                    });
                }
            }.bind(this));
            this.applicationLock.enable(requestId);
        }.bind(this), socketUrl);
        $(function () {
            $(window).on('hashchange', function () {
                if (!this.ignoreNextHashChange) {
                    var json = { 'url': location.pathname + this.formsManager.getLocationHash() };
                    var requestId = this.socketHandler.activeConnector.run(SocketOutputCommands_1.SocketOutputCommands.URL_CHANGE, json);
                    this.applicationLock.enable(requestId);
                }
                else {
                    this.ignoreNextHashChange = false;
                }
            }.bind(this));
        }.bind(this));
    };
    /**
     * @deprecated since version 2.0
     */
    FH.prototype.startUseCase = function (subsystemId, useCaseId, callback) {
        var requestId = this.socketHandler.activeConnector.startUseCase(subsystemId, useCaseId, function (requestId, runUCEventHandleResult) {
            console.log("Use case was started with result:");
            console.log(requestId, runUCEventHandleResult);
            this.formsManager.handleEvent(requestId, runUCEventHandleResult);
            if (typeof callback === 'function') {
                callback();
            }
        }.bind(this));
        this.applicationLock.enable(requestId);
    };
    FH.prototype.createComponent = function (componentObj, parent) {
        var type = componentObj.type;
        var factory = FhContainer_1.FhContainer.get(type);
        if (!factory) {
            throw new Error('Component "' + type + '" is not available');
        }
        return factory(componentObj, parent);
    };
    FH.prototype.changeHash = function (newHash) {
        if (this.formsManager.getLocationHash() != newHash) {
            this.ignoreNextHashChange = true;
            location.hash = newHash;
        }
    };
    FH.prototype.isIE = function () {
        var ua = window.navigator.userAgent;
        var msie = ua.indexOf('MSIE ');
        if (msie > 0) {
            // IE 10 or older => return version number
            return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
        }
        var trident = ua.indexOf('Trident/');
        if (trident > 0) {
            // IE 11 => return version number
            var rv = ua.indexOf('rv:');
            return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
        }
        var edge = ua.indexOf('Edge/');
        if (edge > 0) {
            // Edge (IE 12+) => return version number
            return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
        }
        // other browser
        return false;
    };
    __decorate([
        lazyInject('ApplicationLock'),
        __metadata("design:type", ApplicationLock_1.ApplicationLock)
    ], FH.prototype, "applicationLock", void 0);
    __decorate([
        lazyInject('FormsManager'),
        __metadata("design:type", FormsManager_1.FormsManager)
    ], FH.prototype, "formsManager", void 0);
    __decorate([
        lazyInject('SocketHandler'),
        __metadata("design:type", SocketHandler_1.SocketHandler)
    ], FH.prototype, "socketHandler", void 0);
    __decorate([
        inversify_1.multiInject("ServiceManager"), inversify_1.optional(),
        __metadata("design:type", Array)
    ], FH.prototype, "serviceManagers", void 0);
    FH = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [])
    ], FH);
    return FH;
}());
exports.FH = FH;
//# sourceMappingURL=FH.js.map