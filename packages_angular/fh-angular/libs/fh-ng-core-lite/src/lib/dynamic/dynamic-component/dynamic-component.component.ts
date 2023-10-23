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
// import {TableComponent} from "../../controls/table/table.component";

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

    this.data.formId = this.formId;

    if (this.data.type === 'PanelGroup') {
      componentRef =
        viewContainerRef.createComponent<PanelGroupComponent>(PanelGroupComponent);
    } else if (this.data.type !== 'OutputLabel') {
      if (this.data.type === 'TreeElement') {
        componentRef =
          viewContainerRef.createComponent<TreeElementComponent>(TreeElementComponent);
        console.log('treeElement:data', this.data);
      } else if (this.data.type === 'Tree') {
        componentRef =
          viewContainerRef.createComponent<TreeComponent>(TreeComponent);
        console.log('tree:data', this.data);
      } else if (this.data.type === 'DropdownItem') {
        componentRef =
          viewContainerRef.createComponent<DropdownItemComponent>(DropdownItemComponent);
      } else if (this.data.type === 'Dropdown') {
        componentRef =
          viewContainerRef.createComponent<DropdownComponent>(DropdownComponent);
      } else if (this.data.type === 'Button') {
        console.log('Dynamic:Button', this.data);
        componentRef =
          viewContainerRef.createComponent<ButtonComponent>(ButtonComponent);
      } else if (this.data.type === 'Row') {
        componentRef =
          viewContainerRef.createComponent<RowComponent>(RowComponent);
      } else if (this.data.type === 'Table') {
        // componentRef =
        //   viewContainerRef.createComponent<FormComponent>(TableComponent);
      } else if (this.data.type === 'Form') {
        componentRef =
          viewContainerRef.createComponent<FormComponent>(FormComponent);
      } else {
        componentRef =
          viewContainerRef.createComponent<FormComponent>(FormComponent);
      }
    } else if (this.data.type === 'OutputLabel') {
      componentRef =
        viewContainerRef.createComponent<OutputLabelComponent>(OutputLabelComponent);
    } else {
      componentRef =
        viewContainerRef.createComponent<OutputLabelComponent>(
          OutputLabelComponent
        );
    }

    if (componentRef && componentRef.instance && componentRef.instance.mapAttributes) {
      componentRef.instance.mapAttributes(this.data);
    }
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
