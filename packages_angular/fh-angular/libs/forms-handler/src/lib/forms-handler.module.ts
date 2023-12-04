import {NgModule, ModuleWithProviders} from '@angular/core';
import {CommonModule} from '@angular/common';
import '@angular/localize/init';
import {FhNgModule} from './FhNgModule';
import {ComponentManager} from "./service/component-manager.service";
import {EventsManager} from './service/events-manager.service';
import {I18nService} from "./service/i18n.service";
import {ApplicationLockEN} from "./I18n/ApplicationLock.en";
import {ApplicationLockPL} from "./I18n/ApplicationLock.pl";
import {ConnectorEN} from "./I18n/Connector.en";
import {ConnectorPL} from "./I18n/Connector.pl";
import {FormsManagerEN} from "./I18n/FormsManager.en";
import {FormsManagerPL} from "./I18n/FormsManager.pl";
import {TranslationsEn} from "./I18n/translations.en";
import {TranslationsPl} from "./I18n/translations.pl";
import {ShutdownEventPL} from "./I18n/ShutdownEvent.pl";
import {ShutdownEventEN} from "./I18n/ShutdownEvent.en";
import {FhngAvailabilityDirective} from "./availability/directives/fhng-availability-directive";
import {FhngAvailabilityInputDirective} from "./availability/directives/fhng-availability-input-directive";
import {FhngAvailabilityElementDirective} from "./availability/directives/fhng-availability-element-directive";
import {FhMLService} from "./service/fh-ml.service";
import {SafePipe} from "./pipes/safe.pipe";
import {i18nPipe} from "./pipes/i18n.pipe";

import {FormsModule} from '@angular/forms';
import {LanguageChangeEvent} from "./events/LanguageChangeEvent";
import {NotificationEvent} from "./events/NotificationEvent";
import {SessionTimeoutEvent} from "./events/SessionTimeoutEvent";
import {CustomActionEvent} from "./events/CustomActionEvent";
import {RedirectEvent} from "./events/RedirectEvent";
import {CustomActionsManager} from "./service/custom-actions-manager.service";
import {FHNG_CORE_CONFIG, FhNgCoreConfig} from "./fh-ng-core.config";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NotificationsComponent} from "./components/notifications/notifications-component";
import {ApplicationLockComponent} from "./components/backdrop/application-lock.component";
import {ContainerComponent} from "./components/container/container.component";
import {FhFormsManagerNgComponent} from "./fh-forms-manager-ng.component";
import {FormComponent} from "./components/form/form.component";
import {DynamicComponentsDirective} from "./directives/dynamic-components.directive";


const components = [
  DynamicComponentsDirective,
  NotificationsComponent,
  ApplicationLockComponent,
  ContainerComponent,
  FhFormsManagerNgComponent,
  FormComponent,
  FhngAvailabilityDirective,
  FhngAvailabilityInputDirective,
  FhngAvailabilityElementDirective,
  FhMLService,
  SafePipe,
  i18nPipe
];

@NgModule({
  declarations: components,
  providers: [
    CustomActionsManager,
    EventsManager,
    LanguageChangeEvent,
    NotificationEvent,
    SessionTimeoutEvent,
    CustomActionEvent,
    RedirectEvent,
    ComponentManager
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule

  ],
  exports: components,
})
export class FhNgFormsHandlerModule extends FhNgModule {
  constructor(private componentManager: ComponentManager, protected eventManager: EventsManager, protected i18n: I18nService) {
    super(componentManager, eventManager);
    /**
     * Register default translations strings for module.
     */
    i18n.registerStrings('en', ApplicationLockEN);
    i18n.registerStrings('pl', ApplicationLockPL);
    i18n.registerStrings('en', ConnectorEN, true);
    i18n.registerStrings('pl', ConnectorPL, true);
    i18n.registerStrings('en', FormsManagerEN);
    i18n.registerStrings('pl', FormsManagerPL);
    i18n.registerStrings('en', TranslationsEn);
    i18n.registerStrings('pl', TranslationsPl);
    i18n.registerStrings('pl', ShutdownEventPL);
    i18n.registerStrings('en', ShutdownEventEN);

  }

  static forRoot(ngCoreConfig: FhNgCoreConfig): ModuleWithProviders<FhNgFormsHandlerModule> {
    return {
      ngModule: FhNgFormsHandlerModule,
      providers: [
        {
          provide: FHNG_CORE_CONFIG,
          useValue: ngCoreConfig ? ngCoreConfig : {production: true, development: false, debug: true}
        }
      ]
    };
  }

  public registerComponents(componentManager?: ComponentManager): void {


  }

  protected registerCustomActions(customActionsManager?: CustomActionsManager) {
  }

  protected registerEvents(eventManager?: EventsManager) {
  }
}
