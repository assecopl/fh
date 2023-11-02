import {ModuleWithProviders, NgModule, inject} from '@angular/core';
import {CommonModule, JsonPipe} from '@angular/common';
import {FhFormsManagerNgComponent} from './fh-forms-manager-ng.component';
import {FhMLService} from './service/fh-ml.service';
import {AdDirective} from './directive/ad.directive';
import {FormComponent} from './controls/form/form.component';
import {RowComponent} from './controls/row/row.component';
import {ButtonComponent} from './controls/button/button.component';
import {DropdownComponent} from './controls/dropdown/dropdown.component';
import {DropdownItemComponent} from './controls/dropdown-item/dropdown-item.component';
import {TreeComponent} from './controls/tree/tree.component';
import {TreeElementComponent} from './controls/tree-element/tree-element.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {GroupComponent} from './controls/group/group.component';
import {RepeaterComponent} from './controls/repeater/repeater.component';
import {OutputLabelComponent} from './controls/output-label/output-label.component';
import {PanelGroupComponent} from "./controls/panel-group/panel-group.component";
import {ApplicationLockComponent} from "./components/backdrop/application-lock.component";
import {NotificationsComponent} from "./components/notifications/notifications-component";
import {LanguageChangeEvent} from "./events/LanguageChangeEvent";
import {NotificationEvent} from "./events/NotificationEvent";
import {SessionTimeoutEvent} from "./events/SessionTimeoutEvent";
import {CustomActionEvent} from "./events/CustomActionEvent";
import {CustomActionsManager} from "./service/custom-actions-manager.service";
import {EventsManager} from "./service/events-manager.service";
import {DynamicComponentsDirective} from "./directives/dynamic-components.directive";
import {FHNG_CORE_CONFIG, FhNgCoreConfig} from "./fh-ng-core.config";
import {DebuggerComponent} from "./debug/debuger/debugger.component";
import {ComponentManager} from './service/component-manager.service';
import {TabContainerComponent} from "./controls/tab-container/tab-container.component";
import {TabComponent} from "./controls/tab/tab.component";
import {FhNgModule} from "./FhNgModule";
import {TableComponent} from "./controls/table/table.component";
import {TableCellComponent} from "./controls/table-cell/table-cell.component";
import {TableColumnComponent} from "./controls/table-column/table-column.component";
import {TableHeadRowComponent} from "./controls/table-head-row/table-head-row.component";
import {TableRowComponent} from "./controls/table-row/table-row.component";
import {TablePagedComponent} from "./controls/table-paged/table-paged.component";
import {IConfig, NgxMaskDirective, NgxMaskPipe, provideEnvironmentNgxMask} from 'ngx-mask'
import {AccordionComponent} from "./controls/accordion/accordion.component";
import {SpacerComponent} from "./controls/spacer/spacer.component";
import {ValidateMessagesComponent} from "./controls/validation-messages/validation-messages.component";
import {ContainerComponent} from "./controls/container/container.component";
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
import {I18nService} from "./service/i18n.service";
import {InputTextComponent} from "./controls/input-text/input-text.component";
import {InputGroupComponent} from "./components/input-group/input-group.component";
import {InputLabelComponent} from "./components/input-label/input-label.component";
import {FhngAvailabilityDirective} from "./availability/directives/fhng-availability-directive";
import {FhngAvailabilityInputDirective} from "./availability/directives/fhng-availability-input-directive";
import {FhngAvailabilityElementDirective} from "./availability/directives/fhng-availability-element-directive";
import {PanelHeaderFhDPComponent} from "./controls/panel-header/panel-header.component";
import {InputNumberComponent} from "./controls/input-number/input-number.component";


const maskConfigFunction: () => Partial<IConfig> = () => {
  return {
    validation: false,
    thousandSeparator: "",
    decimalMarker: ",",
    clearIfNotMatch: false,
    patterns: {
      '0': {pattern: new RegExp("[0-9]")},
      '9': {pattern: new RegExp("[0-9]")},
      'A': {pattern: new RegExp("[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]")},
      'a': {pattern: new RegExp("[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]")},
      'P': {pattern: new RegExp("[A-Z0-9]")}, //For NIP.
      'L': {pattern: new RegExp("[A-Za-z]")},
      'l': {pattern: new RegExp("[A-Za-z]")},
      'M': {pattern: new RegExp('[0-9a-zA-Z._-]')},
      'G': {pattern: new RegExp('[0-9a-zA-Z._-]')}, //Use G instead of M mark - there is somthing wrong with M (as 4.x) mark
      'N': {pattern: new RegExp("[0-9a-zA-Z ąćęłńóśźżĄĆĘŁŃÓŚŹŻ._-]")}
    }
  };
};

const components = [
  ContainerComponent,
  AccordionComponent,
  FhngAvailabilityDirective,
  FhngAvailabilityInputDirective,
  FhngAvailabilityElementDirective,
  FhMLService,
  AdDirective,
  FormComponent,
  FhFormsManagerNgComponent,
  RowComponent,
  ButtonComponent,
  DropdownComponent,
  DropdownItemComponent,
  TreeComponent,
  TreeElementComponent,
  GroupComponent,
  RepeaterComponent,
  OutputLabelComponent,
  PanelGroupComponent,
  ApplicationLockComponent,
  NotificationsComponent,
  DynamicComponentsDirective,
  DebuggerComponent,
  TabContainerComponent,
  TabComponent,
  TableComponent,
  TableCellComponent,
  TableColumnComponent,
  TableHeadRowComponent,
  TableRowComponent,
  TablePagedComponent,
  TabComponent,
  SpacerComponent,
  RepeaterComponent,
  InputGroupComponent,
  InputLabelComponent,
  InputTextComponent,
  InputNumberComponent,
  ValidateMessagesComponent,
  PanelHeaderFhDPComponent
]

@NgModule({
  declarations: components,
  providers: [
    CustomActionsManager,
    EventsManager,
    LanguageChangeEvent,
    NotificationEvent,
    SessionTimeoutEvent,
    CustomActionEvent,
    ComponentManager,
    provideEnvironmentNgxMask(maskConfigFunction)
  ],
  imports: [CommonModule, JsonPipe, NgbModule, NgxMaskDirective, NgxMaskPipe],
  exports: components,
})
export class FhNgCoreLiteModule extends FhNgModule {
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


  static forRoot(ngCoreConfig: FhNgCoreConfig): ModuleWithProviders<FhNgCoreLiteModule> {
    return {
      ngModule: FhNgCoreLiteModule,
      providers: [
        {
          provide: FHNG_CORE_CONFIG,
          useValue: ngCoreConfig ? ngCoreConfig : {production: true, development: false, debug: true}
        }
      ]
    };
  }

  public override registerComponents(componentManager?: ComponentManager): void {
    componentManager.registerComponent(AccordionComponent);
    componentManager.registerComponent(ButtonComponent);
    componentManager.registerComponent(DropdownComponent);
    componentManager.registerComponent(DropdownItemComponent);
    componentManager.registerComponent(FormComponent);
    componentManager.registerComponent(GroupComponent);
    componentManager.registerComponent(InputTextComponent);
    componentManager.registerComponent(InputNumberComponent);
    componentManager.registerComponent(OutputLabelComponent);
    componentManager.registerComponent(PanelGroupComponent);
    componentManager.registerComponent(RepeaterComponent);
    componentManager.registerComponent(RowComponent);
    componentManager.registerComponent(TabComponent);
    componentManager.registerComponent(TabContainerComponent);
    componentManager.registerComponent(TreeComponent);
    componentManager.registerComponent(TreeElementComponent);
    componentManager.registerComponent(TableComponent);
    componentManager.registerComponent(TablePagedComponent);
    componentManager.registerComponent(TableColumnComponent);
    componentManager.registerComponent(TableRowComponent);
    componentManager.registerComponent(TableHeadRowComponent);
    componentManager.registerComponent(TableCellComponent);
    componentManager.registerComponent(SpacerComponent);
    componentManager.registerComponent(ValidateMessagesComponent);
    componentManager.registerComponent(PanelHeaderFhDPComponent);
  }

  protected registerCustomActions(customActionsManager?: CustomActionsManager) {
  }

  protected registerEvents(eventManager?: EventsManager) {
  }

}


