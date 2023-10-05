import {NgModule} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FhFormsManagerNgComponent} from './fh-forms-manager-ng.component';
import {FhMLService} from "./service/fh-ml.service";
import {AdDirective} from './directive/ad.directive';
import {FormComponent} from './controls/form/form.component';
import {DynamicComponentComponent} from './dynamic/dynamic-component/dynamic-component.component';
import {RowComponent} from "./controls/row/row.component";
import {ButtonComponent} from "./controls/button/button.component";
import {DropdownComponent} from "./controls/dropdown/dropdown.component";
import {DropdownItemComponent} from "./controls/dropdown-item/dropdown-item.component";
import {TreeComponent} from "./controls/tree/tree.component";
import {TreeElementComponent} from "./controls/tree-element/tree-element.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {GroupComponent} from "./controls/group/group.component";
import {RepeaterComponent} from "./controls/repeater/repeater.component";
import {OutputLabelComponent} from "projects/fh-forms-manager-ng/src/lib/controls/output-label/output-label.component";
import {PanelGroupComponent} from "projects/fh-forms-manager-ng/src/lib/controls/panel-group/panel-group.component";


@NgModule({
    declarations: [
        FhMLService,
        AdDirective,
        FormComponent,
        FhFormsManagerNgComponent,
        DynamicComponentComponent,
        RowComponent,
      ButtonComponent,
      DropdownComponent,
      DropdownItemComponent,
      TreeComponent,
      TreeElementComponent,
      GroupComponent,
      RepeaterComponent,
      OutputLabelComponent,
      PanelGroupComponent
    ],
    imports: [
      CommonModule,
      NgbModule
    ],
    exports: [
        FhFormsManagerNgComponent, FhMLService, FormComponent, AdDirective, CommonModule, RowComponent,
      ButtonComponent, DropdownComponent, DropdownItemComponent, TreeElementComponent, OutputLabelComponent, PanelGroupComponent
    ]
})
export class FhFormsManagerNgModule {
}
