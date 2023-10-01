import {NgModule} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FhFormsManagerNgComponent} from './fh-forms-manager-ng.component';
import {FhMLService} from "projects/fh-forms-manager-ng/src/lib/service/fh-ml.service";
import {AdDirective} from './directive/ad.directive';
import {FormComponent} from './controls/form/form.component';
import {DynamicComponentComponent} from './dynamic/dynamic-component/dynamic-component.component';
import {RowComponent} from "./controls/row/row.component";
import {ButtonComponent} from "./controls/button/button.component";
import {EwopButtonGroupComponent} from "./models/componentClasses/EwopComponent";


@NgModule({
    declarations: [
        FhMLService,
        AdDirective,
        FormComponent,
        FhFormsManagerNgComponent,
        DynamicComponentComponent,
        RowComponent,
        ButtonComponent
    ],
    imports: [
        CommonModule
    ],
    exports: [
        FhFormsManagerNgComponent, FhMLService, FormComponent, AdDirective, CommonModule, RowComponent,
        ButtonComponent
    ]
})
export class FhFormsManagerNgModule {
}
