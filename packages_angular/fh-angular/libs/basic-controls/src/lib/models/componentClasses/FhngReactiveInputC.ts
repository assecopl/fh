import {ValidatorFn,} from '@angular/forms';
import {
  Directive,
  ElementRef,
  EventEmitter,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from './FhngHTMLElementC';
import {InputTypeEnum} from '../enums/InputTypeEnum';
import {
  AvailabilityEnum,
  AvailabilityUtils,
  FhngComponent,
  IconAligmentType,
  IDataAttributes
} from "@fh-ng/forms-handler";

/**
 * TODO Rewrite
 *
 *
 */
@Directive()
export class FhngReactiveInputC
  extends FhngHTMLElementC
  implements OnInit, OnChanges {


  public inputType: InputTypeEnum = InputTypeEnum.text;
  // Store a reference to the enum form html compare purpose
  public inputTypeEnum: any = InputTypeEnum;

  @Input()
  protected onInput: any = null;
  @Input()
  protected onChange: any = null;
  protected valueChanged: boolean = false;

  @Input()
  public placeholder: string = '';

  //Used for static values mappings
  @Input()
  public value: any = null;
  @Output()
  public valueChange: EventEmitter<any> = new EventEmitter<any>();

  @Input()
  public disabled: boolean = false;

  public override set accessibility(value: AvailabilityEnum | string) {
    this.availability = AvailabilityUtils.stringToEnum(value);
    if (this.availability == AvailabilityEnum.EDIT) {
      this.disabled = false;
    } else {
      this.disabled = true;
    }

  }

  @Input()
  public iconText: string = null;

  @Input()
  public override iconAlignment: IconAligmentType = 'AFTER';

  @Input()
  public inputSize: number = 60;

  @Input('labelSize')
  public _labelSize: string = null;

  public get labelSize(): string {
    if (this._labelSize == null) {
      return (100 - this.inputSize).toString();
    }
    return this._labelSize;
  }

  @HostBinding('class.input-required')
  @Input()
  public required: boolean = false; // todo:

  @Input()
  minValue: number = null;

  @Input()
  maxValue: number = null;

  @Input()
  maxLength: number = null; // todo:

  @HostBinding('class.form-group')
  public formGroupClass: boolean = true;

  /**
   * Konstruktor wymaga przekazania klasy Injector. Z kalsy tej w ręczny sposób pobieramy dodatkowe wymagane serwisy.
   * Wykorzystujemy pobieranie z Injector-a aby ograniczyć przekazywanie parametrów z konstruktorów rozszerzanych komponentów.
   * @param injector
   * @param parentFhngComponent
   */
  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  addCustomValidators(): ValidatorFn[] {
    return [() => null];
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.processAriaLabel();
  }

  /**
   * -------------------------------------------------
   * -------- REACTIVE FORM CONTROL LOGIC ------------
   * -------------------------------------------------
   *
   * TODO: Simplify
   * TODO: Move Form control create logic to ng-core
   */

    //FIXME Nie musi być @Input().;
  @Input()
  public updateOn: 'change' | 'blur' | 'submit' = 'submit'; //Default value must be "submit"  as it makes default behaviour of model update logic works



  /**
   * Get selector of component - for loging purpose
   */
  public getSelector() {
    const selector: ElementRef = this.injector.get(ElementRef, null);
    return selector
      ? selector.nativeElement.tagName.toLowerCase()
      : 'selector not found';
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  /**
   * WithValidationInterface
   */
  public validate(): void {
  }

  public processAriaLabel() {
    if (!this.ariaLabel) {
      if (!this.label) {
        this.ariaLabel = this.placeholder ? this.placeholder : null;
      }
    }
  }

  public updateModel(event) {
    if (!this.disabled) {
      this.rawValue = event.target.value;
    }
  };

  onInputEvent(event) {
    this.updateModel(event);
    if (this.onInput && !this.disabled) {
      this.fireEvent('onInput', this.onInput);
    }
  }

  onChangeEvent() {
    if (this.onChange && !this.disabled) {
      this.fireEventWithLock('onChange', this.onChange);
    }
  }

  override mapAttributes(data: IDataAttributes) {
    super.mapAttributes(data);
    if (data.rawValue) {
      this.value = data.rawValue;
    }
  }
}
