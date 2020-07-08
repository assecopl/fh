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
var FormsManager_1 = require("../Socket/FormsManager");
var Util_1 = require("../Util");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var FileDownloadEvent = /** @class */ (function (_super) {
    __extends(FileDownloadEvent, _super);
    function FileDownloadEvent() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    FileDownloadEvent.prototype.fire = function (data) {
        if (this.formsManager.ensureFunctionalityUnavailableDuringShutdown()) {
            var link = document.createElement('a');
            link.href = this.util.getPath(data.url);
            window.open(link.href, '_blank');
        }
    };
    __decorate([
        lazyInject("FormsManager"),
        __metadata("design:type", FormsManager_1.FormsManager)
    ], FileDownloadEvent.prototype, "formsManager", void 0);
    __decorate([
        lazyInject("Util"),
        __metadata("design:type", Util_1.Util)
    ], FileDownloadEvent.prototype, "util", void 0);
    FileDownloadEvent = __decorate([
        inversify_1.injectable()
    ], FileDownloadEvent);
    return FileDownloadEvent;
}(BaseEvent_1.BaseEvent));
exports.FileDownloadEvent = FileDownloadEvent;
//# sourceMappingURL=FileDownloadEvent.js.map