import {
  AfterContentChecked,
  AfterContentInit,
  AfterViewChecked,
  AfterViewInit,
  Component,
  Host,
  HostBinding,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  SkipSelf,
  ViewChild,
  ViewContainerRef,
  forwardRef,
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
  templateUrl: './dynamic.component.html',
  styleUrls: ['./dynamic.component.css'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => DynamicComponent),
    },
  ],
})
export class DynamicComponent
  extends FhngHTMLElementC
  implements OnInit,
    OnDestroy {
  @Input() data: any = {};

  @ViewChild('adHost', {static: true, read: ViewContainerRef})
  public adHost!: ViewContainerRef;


  // @HostBinding('class')
  // @Input()
  // public hostWidth: string = '';
  //
  // @Input('width')
  // public set setWidth(value: string) {
  //   this.processWidth(value, true);
  // }

  @HostBinding('class.form-group')
  @Input()
  public cssFormGroup: boolean = false;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    // this.w100 = false;
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
      } else if (this.data.type === 'Tree') {
        componentRef =
          viewContainerRef.createComponent<TreeComponent>(TreeComponent);
      } else if (this.data.type === 'DropdownItem') {
        componentRef =
          viewContainerRef.createComponent<DropdownItemComponent>(DropdownItemComponent);
      } else if (this.data.type === 'Dropdown') {
        componentRef =
          viewContainerRef.createComponent<DropdownComponent>(DropdownComponent);
      } else if (this.data.type === 'Button') {
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

    //TODO Czesc atrybutuow bedzie mapowana bezposrednio na dynamic-component
    this.width = this.data.width ? this.data.width : componentRef.instance.width;
    // this.processWidth(this.width);
  }

  ngOnDestroy(): void {
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.loadComponent();
  }

  // public processWidth(value: string, force: boolean = false) {
  //   if (this.hostWidth.length === 0 || force) {
  //     if (!value) {
  //       value = this.width;
  //     }
  //
  //     if (value) {
  //       this.width = value;
  //
  //       if (
  //         value.indexOf('px') >= 0 || //pixel width
  //         value.indexOf('%') >= 0 || //procent widths
  //         value.indexOf('vw') >= 0 || //width Relative width of the viewport
  //         value == 'fit' //width Relative width of the viewport
  //       ) {
  //         //Set host element width to auto to fit its content.
  //         this.hostWidth += 'col-auto exactWidth';
  //         //Set inner element styles to exact width;
  //         if (value != 'fit') {
  //           this.processStyleWithUnit('width', value);
  //         }
  //       } else if (value == 'auto') {
  //         this.hostWidth += 'col';
  //       } else {
  //         //Host works with bootstrap width classes.
  //         const widths = value.replace(/ /g, '').split(',');
  //         this.hostWidth += ' col-' + widths.join(' col-');
  //       }
  //     }
  //   }
  // }


}
