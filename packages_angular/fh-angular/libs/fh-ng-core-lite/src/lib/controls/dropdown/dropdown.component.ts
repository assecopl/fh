import {
  AfterContentInit,
  Component,
  ContentChildren,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  QueryList,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {DropdownItemComponent} from '../dropdown-item/dropdown-item.component';
import {BootstrapStyleEnum} from '../../models/enums/BootstrapStyleEnum';
import {FhngButtonGroupComponent, FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';

@Component({
  selector: '[fh-dropdown]',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => DropdownComponent),
    },
  ],
})
export class DropdownComponent
  extends GroupingComponentC<DropdownItemComponent>
  implements OnInit, AfterContentInit, OnChanges {
  public override width: string = BootstrapWidthEnum.MD2;

  @HostBinding('class')
  public override styleClasses: string = '';

  public updateSubcomponent = null;

  public showItems: boolean;

  @ContentChildren(DropdownItemComponent)
  public override subcomponents: QueryList<DropdownItemComponent> =
    new QueryList<DropdownItemComponent>();

  @HostBinding('class.dropdown')
  public dropdown: boolean;

  @HostBinding('class.btn-group')
  public btnGroupClass: boolean = false;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    public parentButtonGroupComponent: FhngButtonGroupComponent
  ) {
    super(injector, parentFhngComponent);

    this.btnGroupClass = !!this.parentButtonGroupComponent;
  }

  override ngOnInit(): void {
    super.ngOnInit();

    this.width = BootstrapWidthEnum.MD2;
    this.bootstrapStyle = this.bootstrapStyle
      ? this.bootstrapStyle
      : BootstrapStyleEnum.PRIMARY;
    this.dropdown = true;
    this.showItems = false;


  }

  public getSubcomponentInstance(): new (
    ...args: any[]
  ) => DropdownItemComponent {
    return DropdownItemComponent;
  }

  override ngAfterContentInit(): void {
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: any) {
    super.mapAttributes(data);
    this.label = data.label;
    this.styleClasses = data.styleClasses || this.styleClasses;

    if (!!this.parentButtonGroupComponent) {
      this.fcClass = false;
      this.width = BootstrapWidthEnum.NONE;
      this.mb2 = false;
    }
  }
}
