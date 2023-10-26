import {ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule, JsonPipe} from '@angular/common';
import {FhFormsManagerNgComponent} from './fh-forms-manager-ng.component';
import {FhMLService} from './service/fh-ml.service';
import {AdDirective} from './directive/ad.directive';
import {FormComponent} from './controls/form/form.component';
import {DynamicComponent} from './dynamic/dynamic-component/dynamic.component';
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


@NgModule({
  declarations: [
    FhMLService,
    AdDirective,
    FormComponent,
    FhFormsManagerNgComponent,
    DynamicComponent,
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
    DebuggerComponent
  ],
  providers: [
    CustomActionsManager,
    EventsManager,
    LanguageChangeEvent,
    NotificationEvent,
    SessionTimeoutEvent,
    CustomActionEvent],
  imports: [CommonModule, JsonPipe, NgbModule],
  exports: [
    FhFormsManagerNgComponent,
    FhMLService,
    FormComponent,
    AdDirective,
    RowComponent,
    ButtonComponent,
    DropdownComponent,
    DropdownItemComponent,
    TreeElementComponent,
    OutputLabelComponent,
      PanelGroupComponent,
    NotificationsComponent,
    DynamicComponentsDirective,
    DebuggerComponent
  ],
})
export class FhNgCoreLiteModule {

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

}


