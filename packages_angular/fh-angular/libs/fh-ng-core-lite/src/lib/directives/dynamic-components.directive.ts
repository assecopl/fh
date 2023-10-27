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
import {ComponentManager} from "../service/component-manager.service";
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

  constructor(private adHost: ViewContainerRef,
              private form: FormComponent,
              private componentManager: ComponentManager
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
        let componentType = this.componentManager.getComponentFactory(data.type);
        if (componentType) {
          componentRef =
            viewContainerRef.createComponent(componentType, {index: index});
          if (componentRef && componentRef.instance && componentRef.instance.mapAttributes) {
            componentRef.instance.data = data;
            componentRef.instance.formId = this.form.id;
          }
        } else {
          componentRef =
            viewContainerRef.createComponent<OutputLabelComponent>(
              OutputLabelComponent, {index: index}
            );
          componentRef.instance.data = data;
          componentRef.instance.formId = this.form.id;
          componentRef.instance.value = "Component " + data.type + " does not exist";
        }


      })


    }
  }

  ngOnInit(): void {
    this.loadOrUpdateComponent();
  }


}
