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
import {IConfig, NgxMaskDirective, NgxMaskPipe, provideNgxMask} from 'ngx-mask'
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
import {SelectOneMenuComponent} from "./controls/select-one-menu/select-one-menu.component";
import {InputNumberComponent} from "./controls/input-number/input-number.component";
import {InputDateComponent} from "./controls/input-date/input-date.component";
import {InputTimestampComponent} from "./controls/input-timestamp/input-timestamp.component";
import {FormsModule} from '@angular/forms';
import {CheckboxComponent} from "./controls/input-checkbox/checkbox.component";
import {RadioOptionComponent} from "./controls/radio-option/radio-option.component";
import {RadioOptionsGroupComponent} from "./controls/radio-options-group/radio-options-group.component";
import {ComboComponent} from "./controls/combo/combo.component";
import {NgSelectModule} from '@ng-select/ng-select';
import {AutosizeModule} from '@techiediaries/ngx-textarea-autosize';
import {ImageComponent} from "./controls/image/image.component";
import {TimerComponent} from "./controls/timer/timer.component";
import {FhngHasHeightDirective} from "../styles/directives/fhng-has-height.directive";
import {EmbedPageComponent} from "./controls/embedded-view/embedded-view.component";
import {SafePipe} from "./pipes/safe.pipe";
import {i18nPipe} from "./pipes/i18n.pipe";
import {HtmlViewComponent} from "./controls/html-view/html-view.component";



const maskConfigFunction: () => Partial<IConfig> = () => {
  //TODO Zastanowic sie co dalej z maskami i jak je dostosowac - najprawdopodobniej zaakceptowac nowe definicje wynikające ze zaktualizowanego pluginu.
  return {
    validation: false,
    thousandSeparator: "",
    // decimalMarker: (Intl.NumberFormat() as any).formatToParts(1.1).find(part => part.type === 'decimal').value,
    decimalMarker: ".",
    // clearIfNotMatch: true,
    // patterns: {
    //   '0': {pattern: new RegExp("[0-9]"),optional:true},
    //   '9': {pattern: new RegExp("[0-9]")},
    //   'A': {pattern: new RegExp("[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]")},
    //   'a': {pattern: new RegExp("[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż]"),optional:true},
    //   'P': {pattern: new RegExp("[A-Z0-9]")}, //For NIP.
    //   'p': {pattern: new RegExp("[A-Z0-9]"),optional:true}, //For NIP.
    //   'L': {pattern: new RegExp("[A-Za-z]")},
    //   'l': {pattern: new RegExp("[A-Za-z]"),optional:true},
    //   'R': {pattern: new RegExp("[0-9A-Za-z]")},
    //   'r': {pattern: new RegExp("[0-9A-Za-z]"),optional:true},
    //   'M': {pattern: new RegExp('[0-9a-zA-Z._-]')},
    //   'm': {pattern: new RegExp('[0-9a-zA-Z._-]'), optional:true},
    //   'G': {pattern: new RegExp('[0-9a-zA-Z._-]')}, //Use G instead of M mark - there is somthing wrong with M (as 4.x) mark
    //   'g': {pattern: new RegExp('[0-9a-zA-Z._-]'),optional:true}, //Use G instead of M mark - there is somthing wrong with M (as 4.x) mark
    //   'N': {pattern: new RegExp("[0-9a-zA-Z ąćęłńóśźżĄĆĘŁŃÓŚŹŻ._-]")},
    //   'n': {pattern: new RegExp("[0-9a-zA-Z ąćęłńóśźżĄĆĘŁŃÓŚŹŻ._-]"),optional:true}
    // }
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
  CheckboxComponent,
  RadioOptionComponent,
  RadioOptionsGroupComponent,
  InputTextComponent,
  InputNumberComponent,
  InputDateComponent,
  InputTimestampComponent,
  ValidateMessagesComponent,
  PanelHeaderFhDPComponent,
  ComboComponent,
  SelectOneMenuComponent,
  ImageComponent,
  TimerComponent,
  FhngHasHeightDirective,
  EmbedPageComponent,
  HtmlViewComponent,
  SafePipe,
  i18nPipe
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
    provideNgxMask(maskConfigFunction)
  ],
  imports: [CommonModule, JsonPipe, NgbModule, NgxMaskDirective, NgxMaskPipe, FormsModule, NgSelectModule, AutosizeModule],
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
    componentManager?.registerComponentWithName("CheckBox", CheckboxComponent);
    componentManager.registerComponent(ComboComponent);
    componentManager.registerComponent(DropdownComponent);
    componentManager.registerComponent(DropdownItemComponent);
    componentManager.registerComponent(FormComponent);
    componentManager.registerComponent(GroupComponent);
    componentManager.registerComponent(InputDateComponent);
    componentManager.registerComponent(InputNumberComponent);
    componentManager.registerComponent(InputTextComponent);
    componentManager.registerComponent(InputTimestampComponent);
    componentManager.registerComponent(OutputLabelComponent);
    componentManager.registerComponent(PanelGroupComponent);
    componentManager.registerComponent(RadioOptionComponent);
    componentManager.registerComponent(RadioOptionsGroupComponent);
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
    componentManager.registerComponent(SelectOneMenuComponent);
    componentManager.registerComponent(ImageComponent);
    componentManager.registerComponent(TimerComponent);
    componentManager.registerComponent(EmbedPageComponent);
    componentManager.registerComponent(HtmlViewComponent)
  }

  protected registerCustomActions(customActionsManager?: CustomActionsManager) {
  }

  protected registerEvents(eventManager?: EventsManager) {
  }

}


