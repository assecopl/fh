"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var SocketHandler = /** @class */ (function () {
    function SocketHandler() {
        this.connectors = [];
    }
    SocketHandler.prototype.addConnector = function (connector) {
        this.connectors.push(connector);
    };
    ;
    SocketHandler.prototype.selectConnector = function (index) {
        var connector = this.connectors[index];
        if (connector) {
            this.activeConnector = this.connectors[index];
        }
        else {
            console.log('There is no connector at selected index');
        }
    };
    ;
    SocketHandler.prototype.selectBestConnector = function () {
        // TODO: We should do better logic to choose best connector.
        this.activeConnector = this.connectors[0];
    };
    ;
    SocketHandler = __decorate([
        inversify_1.injectable()
    ], SocketHandler);
    return SocketHandler;
}());
exports.SocketHandler = SocketHandler;
//# sourceMappingURL=SocketHandler.js.map