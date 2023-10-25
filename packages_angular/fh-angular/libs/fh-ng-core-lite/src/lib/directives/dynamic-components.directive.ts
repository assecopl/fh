import {Directive, Input, OnChanges, OnInit, SimpleChanges, ViewContainerRef,} from '@angular/core';
import {PanelGroupComponent} from "../controls/panel-group/panel-group.component";
import {TreeElementComponent} from "../controls/tree-element/tree-element.component";
import {TreeComponent} from "../controls/tree/tree.component";
import {DropdownItemComponent} from "../controls/dropdown-item/dropdown-item.component";
import {DropdownComponent} from "../controls/dropdown/dropdown.component";
import {ButtonComponent} from "../controls/button/button.component";
import {RowComponent} from "../controls/row/row.component";
import {FormComponent} from "../controls/form/form.component";
import {OutputLabelComponent} from "../controls/output-label/output-label.component";

// import {TableComponent} from "../../controls/table/table.component";

// import {InputTextComponent} from "projects/fh-forms-manager-ng/src/lib/controls/input-text/input-text.component";

@Directive({
  selector: '[fhDynamicComponents]',
})
export class DynamicComponentsDirective
  implements OnInit, OnChanges {

  // @ViewChild('adHost', {static: true, read: ViewContainerRef})
  // public adHost!: ViewContainerRef;

  @Input() fhDynamicComponents: any[] = [];

  constructor(private adHost: ViewContainerRef, private form: FormComponent
  ) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    // throw new Error('Method not implemented.');
    if (changes["fhDynamicComponents"]) {
      this.loadOrUpdateComponent();
    }
  }


  public loadOrUpdateComponent() {
    if (this.fhDynamicComponents) {
      const viewContainerRef = this.adHost;
      viewContainerRef.clear();
      let componentRef = null;

      this.fhDynamicComponents.forEach((data, index) => {
        if (data.type === 'PanelGroup') {
          componentRef =
            viewContainerRef.createComponent<PanelGroupComponent>(PanelGroupComponent, {index: index});
        } else if (data.type !== 'OutputLabel') {
          if (data.type === 'TreeElement') {
            componentRef =
              viewContainerRef.createComponent<TreeElementComponent>(TreeElementComponent, {index: index});
          } else if (data.type === 'Tree') {
            componentRef =
              viewContainerRef.createComponent<TreeComponent>(TreeComponent, {index: index});
          } else if (data.type === 'DropdownItem') {
            componentRef =
              viewContainerRef.createComponent<DropdownItemComponent>(DropdownItemComponent, {index: index});
          } else if (data.type === 'Dropdown') {
            componentRef =
              viewContainerRef.createComponent<DropdownComponent>(DropdownComponent, {index: index});
          } else if (data.type === 'Button') {
            componentRef =
              viewContainerRef.createComponent<ButtonComponent>(ButtonComponent, {index: index});
          } else if (data.type === 'Row') {
            componentRef =
              viewContainerRef.createComponent<RowComponent>(RowComponent, {index: index});
          } else if (data.type === 'Table') {
            // componentRef =
            //   viewContainerRef.createComponent<FormComponent>(TableComponent);
          } else if (data.type === 'Form') {
            componentRef =
              viewContainerRef.createComponent<FormComponent>(FormComponent, {index: index});
          } else {
            componentRef =
              viewContainerRef.createComponent<FormComponent>(FormComponent, {index: index});
          }
        } else if (data.type === 'OutputLabel') {
          componentRef =
            viewContainerRef.createComponent<OutputLabelComponent>(OutputLabelComponent, {index: index});
        } else {
          componentRef =
            viewContainerRef.createComponent<OutputLabelComponent>(
              OutputLabelComponent, {index: index}
            );
        }

        if (componentRef && componentRef.instance && componentRef.instance.mapAttributes) {
          componentRef.instance.data = data;
          componentRef.instance.formId = this.form.id;
        }
      })


    }
  }

  ngOnInit(): void {
    this.loadOrUpdateComponent();
  }


}
