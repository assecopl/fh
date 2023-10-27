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
import {AccordionComponent} from "./controls/accordion/accordion.component";
import {SpacerComponent} from "./controls/spacer/spacer.component";


@NgModule({
  declarations: [
    AccordionComponent,
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
    SpacerComponent
  ],
  providers: [
    CustomActionsManager,
    EventsManager,
    LanguageChangeEvent,
    NotificationEvent,
    SessionTimeoutEvent,
    CustomActionEvent,
    ComponentManager],
  imports: [CommonModule, JsonPipe, NgbModule],
  exports: [
    AccordionComponent,
    FhFormsManagerNgComponent,
    FhMLService,
    FormComponent,
    AdDirective,
    CommonModule,
    RowComponent,
    ButtonComponent,
    DropdownComponent,
    DropdownItemComponent,
    TreeElementComponent,
    OutputLabelComponent,
    PanelGroupComponent,
    NotificationsComponent,
    DynamicComponentsDirective,
    DebuggerComponent,
    TabContainerComponent,
    TabComponent,
    SpacerComponent
  ],
})
export class FhNgCoreLiteModule extends FhNgModule {
  constructor(private componentManager: ComponentManager, protected eventManager: EventsManager) {
    super(componentManager, eventManager);
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

  public override registerComponents(componentManager?: ComponentManager, eventManager?: EventsManager): void {
    componentManager.registerComponent(ButtonComponent);
    componentManager.registerComponent(DropdownComponent);
    componentManager.registerComponent(DropdownItemComponent);
    componentManager.registerComponent(FormComponent);
    componentManager.registerComponent(GroupComponent);
    componentManager.registerComponent(OutputLabelComponent);
    componentManager.registerComponent(PanelGroupComponent);
    componentManager.registerComponent(RowComponent);
    componentManager.registerComponent(TabComponent);
    componentManager.registerComponent(TabContainerComponent);
    componentManager.registerComponent(TreeComponent);
    componentManager.registerComponent(TreeElementComponent);
  }

  protected registerCustomActions(customActionsManager?: CustomActionsManager) {
  }

  protected registerEvents(eventManager?: EventsManager) {
  }

}


