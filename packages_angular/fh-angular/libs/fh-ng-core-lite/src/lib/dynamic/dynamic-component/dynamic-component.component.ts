import {
  AfterContentChecked,
  AfterContentInit,
  AfterViewChecked,
  AfterViewInit,
  Component,
  Host,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  SkipSelf,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import {FormComponent} from '../../controls/form/form.component';
import {RowComponent} from '../../controls/row/row.component';
import {FhngHTMLElementC} from "../../models/componentClasses/FhngHTMLElementC";
import {FhngComponent} from "../../models/componentClasses/FhngComponent";
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {ButtonComponent} from "../../controls/button/button.component";
import {PanelGroupComponent} from "libs/fh-ng-core-lite/src/lib/controls/panel-group/panel-group.component";
import {OutputLabelComponent} from "libs/fh-ng-core-lite/src/lib/controls/output-label/output-label.component";
import {TreeElementComponent} from '../../controls/tree-element/tree-element.component';
import {TreeComponent} from '../../controls/tree/tree.component';
import {DropdownItemComponent} from '../../controls/dropdown-item/dropdown-item.component';
import {DropdownComponent} from '../../controls/dropdown/dropdown.component';

// import {InputTextComponent} from "projects/fh-forms-manager-ng/src/lib/controls/input-text/input-text.component";

@Component({
  selector: 'fh-dynamic-component',
  templateUrl: './dynamic-component.component.html',
  styleUrls: ['./dynamic-component.component.css'],
})
export class DynamicComponentComponent
  extends FhngHTMLElementC
  implements OnInit,
    OnDestroy,
    AfterViewInit,
    AfterContentInit,
    AfterViewChecked,
    AfterContentChecked {
  @Input() data: any = {};

  @ViewChild('adHost', {static: true, read: ViewContainerRef})
  public adHost!: ViewContainerRef;

  constructor(
    public override injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.mb3 = false;
  }

  public loadComponent() {
    const viewContainerRef = this.adHost;
    viewContainerRef.clear();
    let componentRef = null;

    if (this.data.type === 'PanelGroup') {
      componentRef =
        viewContainerRef.createComponent<PanelGroupComponent>(
          PanelGroupComponent
        );
      // componentRef.instance.value = this.data.value;
      componentRef.instance.label = this.data.label;
    } else if (this.data.type !== 'OutputLabel') {
      if (this.data.type === 'TreeElement') {
        componentRef =
          viewContainerRef.createComponent<TreeElementComponent>(
            TreeElementComponent
          );
        componentRef.instance.label = this.data.value;
        componentRef.instance.bootstrapStyle = this.data.style;

      } else if (this.data.type === 'Tree') {
        componentRef =
          viewContainerRef.createComponent<TreeComponent>(TreeComponent);
        componentRef.instance.label = this.data.value;
        componentRef.instance.bootstrapStyle = this.data.style;

      } else if (this.data.type === 'DropdownItem') {
        componentRef = viewContainerRef.createComponent<DropdownItemComponent>(
          DropdownItemComponent
        );
        componentRef.instance.label = this.data.value;
        componentRef.instance.bootstrapStyle = this.data.style;

      } else if (this.data.type === 'Dropdown') {
        componentRef =
          viewContainerRef.createComponent<DropdownComponent>(
            DropdownComponent
          );

        componentRef.instance.label = this.data.label;
        componentRef.instance.bootstrapStyle = this.data.style;

      } else if (this.data.type === 'Button') {
        componentRef =
          viewContainerRef.createComponent<ButtonComponent>(ButtonComponent);

        componentRef.instance.label = this.data.label;
        componentRef.instance.bootstrapStyle = this.data.style;

      } else if (this.data.type === 'Row') {
        componentRef =
          viewContainerRef.createComponent<RowComponent>(RowComponent);
      } else if (this.data.type === 'Form') {
        componentRef =
          viewContainerRef.createComponent<FormComponent>(FormComponent);
        componentRef.instance.innerId = this.data.id;
        componentRef.instance.label = this.data.label;

      } else {
        componentRef =
          viewContainerRef.createComponent<FormComponent>(FormComponent);
        componentRef.instance.innerId = this.data.id;
        componentRef.instance.label = this.data.label;

      }
    } else {
      componentRef =
        viewContainerRef.createComponent<OutputLabelComponent>(
          OutputLabelComponent
        );
      componentRef.instance.value = this.data.value;
      componentRef.instance.label = this.data.label;
    }
    componentRef.instance.formId = this.formId;
    componentRef.instance.width = '';
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
