import './source/Module.css';

import {ApplicationLock} from './source/Socket/ApplicationLock';
import {Connector} from './source/Socket/Connector';
import {SocketHandler} from './source/Socket/SocketHandler';
import {FormsManager} from './source/Socket/FormsManager';
import {FH} from './source/Socket/FH';
import {I18n} from './source/I18n/I18n';
import {Util} from "./source/Util";
import {FHML} from "./source/FHML";

import {DataToClientEvent} from './source/Events/DataToClientEvent';
import {FileDownloadEvent} from './source/Events/FileDownloadEvent';
import {NotificationEvent} from './source/Events/NotificationEvent';
import {CloseTabEvent} from './source/Events/CloseTabEvent';
import {FocusEvent} from './source/Events/FocusEvent';
import {StylesheetChangeEvent} from './source/Events/StylesheetChangeEvent';
import {LanguageChangeEvent} from './source/Events/LanguageChangeEvent';
import {RedirectEvent} from './source/Events/RedirectEvent';
import {RedirectHomeEvent} from './source/Events/RedirectHomeEvent';
import {RedirectPostEvent} from './source/Events/RedirectPostEvent';
import {SessionTimeoutEvent} from './source/Events/SessionTimeoutEvent';
import {ShutdownEvent} from './source/Events/ShutdownEvent';
import {MessageEvent} from './source/Events/MessageEvent';
import {CustomActionEvent} from './source/Events/CustomActionEvent';
import {CustomActions} from './source/Events/CustomActions';
import {BaseEvent} from "./source/Events/BaseEvent";
import {ForcedLogoutEvent} from "./source/Events/ForcedLogoutEvent";
import {LanguageChangeObserver} from './source/I18n/LanguageChangeObserver';
import {FormComponentKeySupport} from "./source/Forms/FormComponentKeySupport";
import {FormComponentChangesQueue} from "./source/Forms/FormComponentChangesQueue";
import {Form} from "./source/Forms/Form";
import {HTMLFormComponent} from './source/Forms/HTMLFormComponent';
import {ComponentExtender} from './source/Forms/ComponentExtender';
import {AdditionalButton} from './source/Forms/AdditionalButton';
import {FormComponent} from './source/Forms/FormComponent';
import {WindowEventsListener} from './source/Forms/WindowEventsListener';
import {FhModule} from "./source/FhModule";
import {FhContainer} from "./source/FhContainer";
import {ServiceManagerUtil} from "./source/Devices/ServiceManagerUtil";
import {ClientDataHandler} from './source/Events/Handlers/ClientDataHandler';
import {ServiceManager} from './source/Devices/ServiceManager';
import {LayoutHandler} from "./source/LayoutHandler";
import {ChatEvent} from "./source/Events/ChatEvent"
import {ScrollEvent} from "./source/Events/ScrollEvent";
import {ChatListEvent} from "./source/Events/ChatListEvent";
import "@stardazed/streams-polyfill";

class FormsHandler extends FhModule {
    protected registerComponents() {
        FhContainer.bind<I18n>('I18n').to(I18n).inSingletonScope();

        FhContainer.bind<Util>('Util').to(Util).inSingletonScope();
        FhContainer.bind<LayoutHandler>('LayoutHandler').to(LayoutHandler).inSingletonScope();
        FhContainer.bind<FormsManager>('FormsManager').to(FormsManager).inSingletonScope();
        FhContainer.bind<SocketHandler>('SocketHandler').to(SocketHandler).inSingletonScope();
        FhContainer.bind<ApplicationLock>('ApplicationLock').to(ApplicationLock).inSingletonScope();
        FhContainer.bind<FH>('FH').to(FH).inSingletonScope();

        FhContainer.bind<(target: string, reconnectCallback: () => void, openCallback: () => void) => Connector>("Connector")
            .toFactory<Connector>(() => {
                return (target: string, reconnectCallback: () => void, openCallback: () => void) => {
                    return new Connector(target, reconnectCallback, openCallback);
                };
            });
        FhContainer.bind<BaseEvent>('Events.DataToClientEvent').to(DataToClientEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.FileDownloadEvent').to(FileDownloadEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.NotificationEvent').to(NotificationEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.FocusEvent').to(FocusEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.ScrollEvent').to(ScrollEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.StylesheetChangeEvent').to(StylesheetChangeEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.LanguageChangeEvent').to(LanguageChangeEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.RedirectEvent').to(RedirectEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.RedirectHomeEvent').to(RedirectHomeEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.RedirectPostEvent').to(RedirectPostEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.CloseTabEvent').to(CloseTabEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.ShutdownEvent').to(ShutdownEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.MessageEvent').to(MessageEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.ForcedLogoutEvent').to(ForcedLogoutEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.CustomActionEvent').to(CustomActionEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.SessionTimeoutEvent').to(SessionTimeoutEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.ChatEvent').to(ChatEvent).inRequestScope();
        FhContainer.bind<BaseEvent>('Events.ChatListEvent').to(ChatListEvent).inRequestScope();
        FhContainer.bind<CustomActions>('CustomActions').to(CustomActions).inSingletonScope();
        FhContainer.bind<FHML>('FHML').to(FHML).inSingletonScope();
        FhContainer.bind<FormComponentChangesQueue>('FormComponentChangesQueue').to(FormComponentChangesQueue).inRequestScope();

        FhContainer.bind<(componentObj: any, formElement: any) => FormComponentKeySupport>("FormComponentKeySupport")
            .toFactory<FormComponentKeySupport>(() => {
                return (componentObj: any, formElement: any) => {
                    return new FormComponentKeySupport(componentObj, formElement);
                };
            });

        FhContainer.bind<(componentObj: any) => Form>("Form")
            .toFactory<Form>(() => {
                return (componentObj: any) => {
                    return new Form(componentObj);
                };
            });

        FhContainer.bind<ServiceManagerUtil>('ServiceManagerUtil').to(ServiceManagerUtil).inSingletonScope();
    }
}

export {
    I18n,
    FormsManager,
    Util,
    FH,
    ApplicationLock,
    Connector,
    NotificationEvent,
    SocketHandler,
    CustomActions,
    AdditionalButton,
    FormComponentKeySupport,
    FormComponent,
    HTMLFormComponent,
    ComponentExtender,
    FHML,
    LanguageChangeObserver,
    WindowEventsListener,
    FormsHandler,
    BaseEvent,
    FhModule,
    ShutdownEvent,
    CustomActionEvent,
    DataToClientEvent,
    FileDownloadEvent,
    CloseTabEvent,
    FocusEvent,
    ScrollEvent,
    StylesheetChangeEvent,
    LanguageChangeEvent,
    RedirectEvent,
    RedirectHomeEvent,
    SessionTimeoutEvent,
    MessageEvent,
    FhContainer,
    ServiceManagerUtil,
    ClientDataHandler,
    ServiceManager,
    ChatEvent,
    ChatListEvent
};
