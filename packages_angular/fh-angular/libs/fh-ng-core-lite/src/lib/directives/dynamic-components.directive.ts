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
import {GroupComponent} from "../controls/group/group.component";
import {TabContainerComponent} from "../controls/tab-container/tab-container.component";
import {TabComponent} from "../controls/tab/tab.component";
import {AccordionComponent} from "../controls/accordion/accordion.component";
import {SpacerComponent} from "../controls/spacer/spacer.component";

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
        } else if (data.type === 'Accordion') {
          componentRef =
              viewContainerRef.createComponent<AccordionComponent>(AccordionComponent);
        } else  if (data.type !== 'OutputLabel') {
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
          } else if (data.type === 'Group') {
            componentRef =
              viewContainerRef.createComponent<GroupComponent>(GroupComponent, {index: index});
          } else if (data.type === 'Table') {
            // componentRef =
            //   viewContainerRef.createComponent<FormComponent>(TableComponent);TabComponent
          } else if (data.type === 'TabContainer') {
            componentRef =
              viewContainerRef.createComponent<TabContainerComponent>(TabContainerComponent);
          } else if (data.type === 'Tab') {
            componentRef =
                viewContainerRef.createComponent<TabComponent>(TabComponent);
          } else if (data.type === 'Form') {
            componentRef =
              viewContainerRef.createComponent<FormComponent>(FormComponent, {index: index});
          } else if (data.type === 'Spacer') {
            componentRef =
              viewContainerRef.createComponent<SpacerComponent>(SpacerComponent, {index: index});
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
