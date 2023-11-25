import {
  AfterContentInit, AfterViewInit,
  Component,
  EventEmitter,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnChanges, OnDestroy,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {ButtonComponent} from '../button/button.component';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngButtonGroupComponent, FhngComponent,} from '../../models/componentClasses/FhngComponent';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

export interface IGroupButtonDataAttributes extends IDataAttributes {
  activeButton?: number;
  onButtonChange?: string;
}

@Component({
  selector: 'fhng-button-group',
  templateUrl: './button-group.component.html',
  styleUrls: ['./button-group.component.scss'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => ButtonGroupComponent),
    },
    {
      provide: FhngButtonGroupComponent,
      useExisting: forwardRef(() => ButtonGroupComponent),
    },
  ],
})
export class ButtonGroupComponent
  extends GroupingComponentC<ButtonComponent>
  implements OnChanges, OnInit, AfterContentInit, AfterViewInit, OnDestroy {

  public override mb2 = false;

  public override width = BootstrapWidthEnum.MD12;

  @Input()
  public activeButton: number;

  @HostBinding('class.breadcrumbs')
  @Input()
  public breadcrumbs: boolean = false;

  @HostBinding('class')
  public class: string = 'btn-group';

  @HostBinding('attr.role')
  public role: string = 'group';

  public onButtonChange: string;

  private _subscriptions = [];

  private valueChanged = false;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
  ) {
    super(injector, parentFhngComponent);
  }

  public updateSubcomponent: (
    subcomponent: ButtonComponent,
    index: number
  ) => null;

  public getSubcomponentInstance(): new (...args: any[]) => ButtonComponent {
    return ButtonComponent;
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override ngOnInit() {
    super.ngOnInit();
  }

  public override ngAfterContentInit(): void {
    super.ngAfterContentInit();
  }

  public override ngAfterViewInit() {
    super.ngAfterViewInit();
    this._mapSubscriptions();
  }

  public override ngOnDestroy(): void {
    this._unsubscribe();
  }

  public override mapAttributes(data: IGroupButtonDataAttributes) {
    super.mapAttributes(this._prepareButton(data));

    (this.subelements.filter(element => element.type = 'Button') || []).forEach((element, index) => {
      element.active = index === data.activeButton;
    });

    this.subelements = JSON.parse(JSON.stringify(this.subelements));

    this.onButtonChange = data.onButtonChange || this.onButtonChange;
  }

  public override extractChangedAttributes() {
    let attr = {};
    if (this.valueChanged) {
      attr['value'] = this.activeButton;
      this.valueChanged = false;
    }

    return attr;
  };

  public onButtonClick (element): void {
    this._onClickSubscribeEvent(element);
  }

  public isDropdown (element: FhngComponent): boolean {
    return element.type === "Dropdown" || !!element.subelements?.length
  }

  private _prepareButton(data: IGroupButtonDataAttributes ): IGroupButtonDataAttributes {
    data.subelements?.forEach((element) => {
      if (element.type === 'Button') {
        element.mb2 = false;
        element.width = null;
        element.wrapperClass = false;
      }
    });

    this._setActiveButton(data);

    return data;
  }

  private _setActiveButton(data: IGroupButtonDataAttributes): void {
    if (data.subelements instanceof Array) {
      (data.subelements.filter(element => element.type = 'Button') || []).forEach((element, index) => {
        element.active = index === data.activeButton;
      });
    }
    this.childFhngComponents.forEach((element: any, index) => {
      element.active = index === data.activeButton;
    });
  }

  private _mapSubscriptions(): void {
    this._unsubscribe();

    this.childFhngComponents.forEach((element: any, index: number) => {
      if (element.onUpdateButtonEvent$) {
        this._subscriptions.push(element.onUpdateButtonEvent$.subscribe((data: ButtonComponent) => this._updateSubscribeEvent(data)));
      }

      if (element.onClickButtonEvent$) {
        this._subscriptions.push(element.onClickButtonEvent$.subscribe((data: ButtonComponent) => this._onClickSubscribeEvent(data)));
      }
    });
  }

  private _unsubscribe(): void {
    this._subscriptions.forEach(element => element.unsubscribe());
    this._subscriptions = [];
  }

  private _updateSubscribeEvent(data: ButtonComponent): void {
    let index = this.subelements.findIndex(element => element.id === data.id);

    this.subelements[index] = {...this.subelements[index], label: data.label, active: data.active}
  }

  private _onClickSubscribeEvent(data: ButtonComponent): void {
    let index = this.subelements.findIndex(element => element.id === data.id);

    this.activeButton = index;
    console.log('buttonGroupClick', index)

    if (this.onButtonChange && index > -1) {
      this.valueChanged = true;
      this.fireEventWithLock('onButtonChange', this.onButtonChange);
    }
  }
}
