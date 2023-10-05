import {
  AfterContentChecked,
  AfterContentInit, AfterViewChecked,
  AfterViewInit,
  Component, Host, Injector,
  Input,
  OnDestroy,
  OnInit, Optional, SkipSelf,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import {FormComponent} from "../../controls/form/form.component";
import {RowComponent} from "../../controls/row/row.component";
import {ButtonComponent} from "projects/fh-forms-manager-ng/src/lib/controls/button/button.component";
import {EwopHTMLElementC} from "projects/fh-forms-manager-ng/src/lib/models/componentClasses/EwopHTMLElementC";
import {EwopComponent} from "projects/fh-forms-manager-ng/src/lib/models/componentClasses/EwopComponent";
import {BootstrapWidthEnum} from "projects/fh-forms-manager-ng/src/lib/models/enums/BootstrapWidthEnum";
import {DropdownComponent} from "projects/fh-forms-manager-ng/src/lib/controls/dropdown/dropdown.component";
import {
  DropdownItemComponent
} from "projects/fh-forms-manager-ng/src/lib/controls/dropdown-item/dropdown-item.component";
import {TreeComponent} from "projects/fh-forms-manager-ng/src/lib/controls/tree/tree.component";
import {TreeElementComponent} from "projects/fh-forms-manager-ng/src/lib/controls/tree-element/tree-element.component";
import {OutputLabelComponent} from "projects/fh-forms-manager-ng/src/lib/controls/output-label/output-label.component";
import {PanelGroupComponent} from "projects/fh-forms-manager-ng/src/lib/controls/panel-group/panel-group.component";

// import {InputTextComponent} from "projects/fh-forms-manager-ng/src/lib/controls/input-text/input-text.component";

@Component({
  selector: 'fh-dynamic-component',
  templateUrl: './dynamic-component.component.html',
  styleUrls: ['./dynamic-component.component.css']
})
export class DynamicComponentComponent extends EwopHTMLElementC implements OnInit, OnDestroy, AfterViewInit, AfterContentInit, AfterViewChecked, AfterContentChecked {

  @Input() data: any = {};

  @ViewChild("adHost", {static: true, read: ViewContainerRef})
  public adHost!: ViewContainerRef;

  constructor(public override injector: Injector, @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
    super(injector, parentEwopComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.mb3 = false;
  }

  public loadComponent() {

    const viewContainerRef = this.adHost;
    viewContainerRef.clear();
    let componentRef = null;


    switch (this.data.type) {
      // case('InputText'):
      //   componentRef = viewContainerRef.createComponent<InputTextComponent>(InputTextComponent)
      //   // componentRef.instance.value = this.data.value;
      //   componentRef.instance.label = this.data.label;
      //   componentRef.instance.value = this.data.value;
      //   break;
      case('PanelGroup'):
        componentRef = viewContainerRef.createComponent<PanelGroupComponent>(PanelGroupComponent)
        // componentRef.instance.value = this.data.value;
        componentRef.instance.label = this.data.label;
        break;

      case('OutputLabel'):
        componentRef = viewContainerRef.createComponent<OutputLabelComponent>(OutputLabelComponent)
        componentRef.instance.value = this.data.value;
        componentRef.instance.label = this.data.label;
        break;
      case('TreeElement'):
        componentRef = viewContainerRef.createComponent<TreeElementComponent>(TreeElementComponent)
        componentRef.instance.label = this.data.value;
        componentRef.instance.bootstrapStyle = this.data.style;


        break;
      case('Tree'):
        componentRef = viewContainerRef.createComponent<TreeComponent>(TreeComponent)
        componentRef.instance.label = this.data.value;
        componentRef.instance.bootstrapStyle = this.data.style;


        break;
      case('DropdownItem'):
        componentRef = viewContainerRef.createComponent<DropdownItemComponent>(DropdownItemComponent)
        componentRef.instance.label = this.data.value;
        componentRef.instance.bootstrapStyle = this.data.style;


        break;
      case('Dropdown'):
        componentRef = viewContainerRef.createComponent<DropdownComponent>(DropdownComponent)

        componentRef.instance.label = this.data.label;
        componentRef.instance.bootstrapStyle = this.data.style;


        break;
      case('Button'):
        componentRef = viewContainerRef.createComponent<ButtonComponent>(ButtonComponent)

        componentRef.instance.label = this.data.label;
        componentRef.instance.bootstrapStyle = this.data.style;


        break;
      case('Row'):
        componentRef = viewContainerRef.createComponent<RowComponent>(RowComponent)
        break;
      case('Form'):
        componentRef = viewContainerRef.createComponent<FormComponent>(FormComponent)
        componentRef.instance.innerId = this.data.id;
        componentRef.instance.label = this.data.label;

        break;
      default:
        componentRef = viewContainerRef.createComponent<FormComponent>(FormComponent)
        componentRef.instance.innerId = this.data.id;
        componentRef.instance.label = this.data.label;

        break;
    }
    componentRef.instance.formId = this.formId;
    componentRef.instance.width = "";
    componentRef.instance.innerId = this.data.id;
    componentRef.instance.accessibility = this.data.accessibility;
    componentRef.instance.subelements = this.data.subelements;


  }

  ngOnDestroy(): void {
  }

  override ngOnInit(): void {
    if (this.data.width) {
      this.width = this.data.width;
    }
    super.ngOnInit();
    this.loadComponent();
  }

  ngAfterContentChecked(): void {
  }

  override ngAfterContentInit(): void {
  }

  ngAfterViewChecked(): void {
  }

  override ngAfterViewInit(): void {
  }
}
