import {
  Component,
  EventEmitter,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {InputTypeEnum} from '../../models/enums/InputTypeEnum';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent, IDataAttributes} from "@fh-ng/forms-handler";

export interface ICheckboxDataAttributes extends IDataAttributes {
  horizontalAlign?: "LEFT" | "CENTER" | "RIGHT" | null;
  verticalAlign?: "TOP" | "MIDDLE" | "BOTTOM" | null;
}

@Component({
  selector: '[fhng-input-checkbox]',
  templateUrl: './checkbox.component.html',
  styleUrls: ['./checkbox.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => CheckboxComponent),
    },
  ],
})
export class CheckboxComponent
  extends FhngReactiveInputC
  implements OnInit {
  public override width: string = BootstrapWidthEnum.MD2;

  public override rawValue: boolean = false;

  @HostBinding('class.form-check')
  public formCheckClass: boolean = true;

  @HostBinding('class.form-switch')
  public formSwitchClass: boolean = false;

  @HostBinding('class.form-group')
  public formCheck: boolean = true;

  @HostBinding('class.stickedLabel')
  public stickyLabelClass: boolean = true;


  @HostBinding('class.justify-content-start')
  public justifyContentStartClass: boolean = true;

  @HostBinding('class.justify-content-center')
  public justifyContentCenterClass: boolean = false;

  @HostBinding('class.justify-content-end')
  public justifyContentEndClass: boolean = false;


  @HostBinding('class.align-items-start')
  public alignItemsStartClass: boolean = true;

  @HostBinding('class.align-items-center')
  public alignItemsCenterClass: boolean = false;

  @HostBinding('class.align-items-end')
  public alignItemsEndClass: boolean = false;


  @Input()
  public checked: boolean = false;

  @Output()
  public selectedCheckbox: EventEmitter<CheckboxComponent> =
    new EventEmitter<CheckboxComponent>();

  @HostBinding('class')
  public get additionalClasses() {
    let _additionalClasses = ['positioned'];

    if (this.labelPosition) {
      _additionalClasses.push(this.labelPosition.toLowerCase());
    } else {
      _additionalClasses.push('right');
    }

    return _additionalClasses.join(' ');
  }

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.labelPosition = 'RIGHT';

    if (!this.fhdp) {
      this.formSwitchClass = true;
    }
  }

  //public class:string = "";

  override ngOnInit() {
    super.ngOnInit();

    this.inputType = InputTypeEnum.checkbox;
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  inputCheckEvent(checked) {
    if (!this.disabled) {
      this.changesQueue.queueValueChange(checked);
    }
  }

  override extractChangedAttributes() {
    return this.changesQueue.extractChangedAttributes();
  };

  override mapAttributes(data: ICheckboxDataAttributes) {
    super.mapAttributes(data);

    this.horizontalAlign = data.horizontalAlign || this.horizontalAlign;
    this.verticalAlign = data.verticalAlign || this.verticalAlign;

    this._updateAlignItemAlign();
    this._updateJustifyContentAlign();
  }

  private _updateJustifyContentAlign() {
    switch (this.horizontalAlign) {
      case "CENTER":
        this.justifyContentStartClass = false;
        this.justifyContentEndClass = false;
        this.justifyContentCenterClass = true;
        break;
      case "RIGHT":
        this.justifyContentStartClass = false;
        this.justifyContentEndClass = true;
        this.justifyContentCenterClass = false;
        break;
      case "LEFT":
      default:
        this.justifyContentStartClass = true;
        this.justifyContentEndClass = false;
        this.justifyContentCenterClass = false;
    }
  }

  private _updateAlignItemAlign() {
    switch (this.verticalAlign) {
      case "MIDDLE":
        this.justifyContentStartClass = false;
        this.justifyContentEndClass = false;
        this.justifyContentCenterClass = true;
        break;
      case "BOTTOM":
        this.justifyContentStartClass = false;
        this.justifyContentEndClass = true;
        this.justifyContentCenterClass = false;
        break;
      case "TOP":
      default:
        this.justifyContentStartClass = true;
        this.justifyContentEndClass = false;
        this.justifyContentCenterClass = false;
    }
  }
}
