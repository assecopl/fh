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
Object.defineProperty(exports, "__esModule", { value: true });
require("./source/Module.css");
var ApplicationLock_1 = require("./source/Socket/ApplicationLock");
exports.ApplicationLock = ApplicationLock_1.ApplicationLock;
var Connector_1 = require("./source/Socket/Connector");
exports.Connector = Connector_1.Connector;
var SocketHandler_1 = require("./source/Socket/SocketHandler");
exports.SocketHandler = SocketHandler_1.SocketHandler;
var FormsManager_1 = require("./source/Socket/FormsManager");
exports.FormsManager = FormsManager_1.FormsManager;
var FH_1 = require("./source/Socket/FH");
exports.FH = FH_1.FH;
var I18n_1 = require("./source/I18n/I18n");
exports.I18n = I18n_1.I18n;
var Util_1 = require("./source/Util");
exports.Util = Util_1.Util;
var FHML_1 = require("./source/FHML");
exports.FHML = FHML_1.FHML;
var DataToClientEvent_1 = require("./source/Events/DataToClientEvent");
exports.DataToClientEvent = DataToClientEvent_1.DataToClientEvent;
var FileDownloadEvent_1 = require("./source/Events/FileDownloadEvent");
exports.FileDownloadEvent = FileDownloadEvent_1.FileDownloadEvent;
var NotificationEvent_1 = require("./source/Events/NotificationEvent");
exports.NotificationEvent = NotificationEvent_1.NotificationEvent;
var CloseTabEvent_1 = require("./source/Events/CloseTabEvent");
exports.CloseTabEvent = CloseTabEvent_1.CloseTabEvent;
var FocusEvent_1 = require("./source/Events/FocusEvent");
exports.FocusEvent = FocusEvent_1.FocusEvent;
var StylesheetChangeEvent_1 = require("./source/Events/StylesheetChangeEvent");
exports.StylesheetChangeEvent = StylesheetChangeEvent_1.StylesheetChangeEvent;
var LanguageChangeEvent_1 = require("./source/Events/LanguageChangeEvent");
exports.LanguageChangeEvent = LanguageChangeEvent_1.LanguageChangeEvent;
var RedirectEvent_1 = require("./source/Events/RedirectEvent");
exports.RedirectEvent = RedirectEvent_1.RedirectEvent;
var RedirectHomeEvent_1 = require("./source/Events/RedirectHomeEvent");
exports.RedirectHomeEvent = RedirectHomeEvent_1.RedirectHomeEvent;
var SessionTimeoutEvent_1 = require("./source/Events/SessionTimeoutEvent");
exports.SessionTimeoutEvent = SessionTimeoutEvent_1.SessionTimeoutEvent;
var ShutdownEvent_1 = require("./source/Events/ShutdownEvent");
exports.ShutdownEvent = ShutdownEvent_1.ShutdownEvent;
var MessageEvent_1 = require("./source/Events/MessageEvent");
exports.MessageEvent = MessageEvent_1.MessageEvent;
var CustomActionEvent_1 = require("./source/Events/CustomActionEvent");
exports.CustomActionEvent = CustomActionEvent_1.CustomActionEvent;
var CustomActions_1 = require("./source/Events/CustomActions");
exports.CustomActions = CustomActions_1.CustomActions;
var BaseEvent_1 = require("./source/Events/BaseEvent");
exports.BaseEvent = BaseEvent_1.BaseEvent;
var ForcedLogoutEvent_1 = require("./source/Events/ForcedLogoutEvent");
var FormComponentKeySupport_1 = require("./source/Forms/FormComponentKeySupport");
exports.FormComponentKeySupport = FormComponentKeySupport_1.FormComponentKeySupport;
var FormComponentChangesQueue_1 = require("./source/Forms/FormComponentChangesQueue");
var Form_1 = require("./source/Forms/Form");
var HTMLFormComponent_1 = require("./source/Forms/HTMLFormComponent");
exports.HTMLFormComponent = HTMLFormComponent_1.HTMLFormComponent;
var AdditionalButton_1 = require("./source/Forms/AdditionalButton");
exports.AdditionalButton = AdditionalButton_1.AdditionalButton;
var FormComponent_1 = require("./source/Forms/FormComponent");
exports.FormComponent = FormComponent_1.FormComponent;
var WindowEventsListener_1 = require("./source/Forms/WindowEventsListener");
exports.WindowEventsListener = WindowEventsListener_1.WindowEventsListener;
var FhModule_1 = require("./source/FhModule");
exports.FhModule = FhModule_1.FhModule;
var FhContainer_1 = require("./source/FhContainer");
exports.FhContainer = FhContainer_1.FhContainer;
var ServiceManagerUtil_1 = require("./source/Devices/ServiceManagerUtil");
exports.ServiceManagerUtil = ServiceManagerUtil_1.ServiceManagerUtil;
var LayoutHandler_1 = require("./source/LayoutHandler");
var ChatEvent_1 = require("./source/Events/ChatEvent");
exports.ChatEvent = ChatEvent_1.ChatEvent;
var ScrollEvent_1 = require("./source/Events/ScrollEvent");
exports.ScrollEvent = ScrollEvent_1.ScrollEvent;
var ChatListEvent_1 = require("./source/Events/ChatListEvent");
exports.ChatListEvent = ChatListEvent_1.ChatListEvent;
var FormsHandler = /** @class */ (function (_super) {
    __extends(FormsHandler, _super);
    function FormsHandler() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    FormsHandler.prototype.registerComponents = function () {
        FhContainer_1.FhContainer.bind('I18n').to(I18n_1.I18n).inSingletonScope();
        FhContainer_1.FhContainer.bind('Util').to(Util_1.Util).inSingletonScope();
        FhContainer_1.FhContainer.bind('LayoutHandler').to(LayoutHandler_1.LayoutHandler).inSingletonScope();
        FhContainer_1.FhContainer.bind('FormsManager').to(FormsManager_1.FormsManager).inSingletonScope();
        FhContainer_1.FhContainer.bind('SocketHandler').to(SocketHandler_1.SocketHandler).inSingletonScope();
        FhContainer_1.FhContainer.bind('ApplicationLock').to(ApplicationLock_1.ApplicationLock).inSingletonScope();
        FhContainer_1.FhContainer.bind('FH').to(FH_1.FH).inSingletonScope();
        FhContainer_1.FhContainer.bind("Connector")
            .toFactory(function () {
            return function (target, reconnectCallback, openCallback) {
                return new Connector_1.Connector(target, reconnectCallback, openCallback);
            };
        });
        FhContainer_1.FhContainer.bind('Events.DataToClientEvent').to(DataToClientEvent_1.DataToClientEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.FileDownloadEvent').to(FileDownloadEvent_1.FileDownloadEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.NotificationEvent').to(NotificationEvent_1.NotificationEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.FocusEvent').to(FocusEvent_1.FocusEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.ScrollEvent').to(ScrollEvent_1.ScrollEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.StylesheetChangeEvent').to(StylesheetChangeEvent_1.StylesheetChangeEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.LanguageChangeEvent').to(LanguageChangeEvent_1.LanguageChangeEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.RedirectEvent').to(RedirectEvent_1.RedirectEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.RedirectHomeEvent').to(RedirectHomeEvent_1.RedirectHomeEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.CloseTabEvent').to(CloseTabEvent_1.CloseTabEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.ShutdownEvent').to(ShutdownEvent_1.ShutdownEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.MessageEvent').to(MessageEvent_1.MessageEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.ForcedLogoutEvent').to(ForcedLogoutEvent_1.ForcedLogoutEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.CustomActionEvent').to(CustomActionEvent_1.CustomActionEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.SessionTimeoutEvent').to(SessionTimeoutEvent_1.SessionTimeoutEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.ChatEvent').to(ChatEvent_1.ChatEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('Events.ChatListEvent').to(ChatListEvent_1.ChatListEvent).inRequestScope();
        FhContainer_1.FhContainer.bind('CustomActions').to(CustomActions_1.CustomActions).inSingletonScope();
        FhContainer_1.FhContainer.bind('FHML').to(FHML_1.FHML).inSingletonScope();
        FhContainer_1.FhContainer.bind('FormComponentChangesQueue').to(FormComponentChangesQueue_1.FormComponentChangesQueue).inRequestScope();
        FhContainer_1.FhContainer.bind("FormComponentKeySupport")
            .toFactory(function () {
            return function (componentObj, formElement) {
                return new FormComponentKeySupport_1.FormComponentKeySupport(componentObj, formElement);
            };
        });
        FhContainer_1.FhContainer.bind("Form")
            .toFactory(function () {
            return function (componentObj) {
                return new Form_1.Form(componentObj);
            };
        });
        FhContainer_1.FhContainer.bind('ServiceManagerUtil').to(ServiceManagerUtil_1.ServiceManagerUtil).inSingletonScope();
    };
    return FormsHandler;
}(FhModule_1.FhModule));
exports.FormsHandler = FormsHandler;
//# sourceMappingURL=Module.js.map