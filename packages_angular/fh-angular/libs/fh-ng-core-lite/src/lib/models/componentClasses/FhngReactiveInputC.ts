import {
  AbstractControl,
  ControlContainer,
  FormArray,
  FormControl,
  FormGroup,
  FormGroupDirective,
  ValidatorFn,
} from '@angular/forms';
import {
  Directive,
  ElementRef,
  EventEmitter,
  Host,
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
import {IconAligmentType} from '../CommonTypes'; //  Unused becouse of compiler problem
import {FhngComponent} from './FhngComponent';
import {FormComponent} from '../../controls/form/form.component';

/**
 * TODO Rewrite
 *
 *
 */
@Directive()
export class FhngReactiveInputC
  extends FhngHTMLElementC
  implements OnInit, OnChanges {
  public controlContainer: ControlContainer; //FormGroupDirective used to register control.
  // public fhngNotificationService: FhngNotificationService; //FormGroupDirective used to register control.
  // public validationHandler: ValidationResults;

  @Input()
  public control: any = null;

  public form: FormGroup | any;
  public iForm: FormComponent;

  public inputType: InputTypeEnum = InputTypeEnum.text;
  // Store a reference to the enum form html compare purpose
  public inputTypeEnum: any = InputTypeEnum;

  @Input('inputType')
  public set type(value: InputTypeEnum | string) {
    if (typeof value === 'string') {
      const a: any = InputTypeEnum[value];
      this.inputType = a;
    } else {
      this.inputType = value;
    }
  }

  //https://www.npmjs.com/package/ngx-mask

  @Input()
  public placeholder: string = '';

  //Used for static values mappings
  @Input()
  public value: any = null;
  @Output()
  public valueChange: EventEmitter<any> = new EventEmitter<any>();

  @Input()
  public disabled: boolean = false;

  //TODO Move Icon to mixin class ??
  @Input()
  public icon: string = null;

  @Input()
  public iconText: string = null;

  @Input()
  public iconAlignment: IconAligmentType = 'AFTER';

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

  /**
   * Konstruktor wymaga przekazania klasy Injector. Z kalsy tej w ręczny sposób pobieramy dodatkowe wymagane serwisy.
   * Wykorzystujemy pobieranie z Injector-a aby ograniczyć przekazywanie parametrów z konstruktorów rozszerzanych komponentów.
   * @param injector
   * @param parentFhngComponent
   */
  constructor(
    public override injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    iForm: any
  ) {
    super(injector, parentFhngComponent);
    this.iForm = iForm;
  }

  addCustomValidators(): ValidatorFn[] {
    return [() => null];
  }

  override ngOnInit(): void {
    super.ngOnInit();
    /**
     * Pobieram nadrzędny kontener kontrolek, wstrzyknięty z elementu hosta-a
     */
    this.controlContainer = this.injector.get(ControlContainer, null);
    // this.fhngNotificationService = this.injector.get(FhngNotificationService, null);
    // this.validationHandler = this.injector.get(ValidationResults, null);

    // if (this.controlContainer) {
    //     //Control can be pass as input.
    //     this.control = this.control ? this.control : this.controlContainer.control.get(this.name) as FhngFormControl;
    //     this.resolveFormControl();
    // }

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

  public resolveFormControl() {
    //Budujemy dinamicznie kontrolkę jeżeli nie została przekazana programowo
    this.control = this.control
      ? this.control
      : this.buildFormControl(this.updateOn);

    let groupingControl = this.controlContainer.control as AbstractControl;

    //Add it to FormGroup if its was set on fhng-form.
    if (groupingControl) {
      const controlName = this.name
        ? this.name.toString().replace(/\[/g, '.').replace(/\]/g, '')
        : this.innerId;
      const fg = this.name
        ? controlName.split('.')
        : controlName.split('!!!!!!!');
      if (fg.length == 1) {
        (groupingControl as FormGroup).addControl(fg[0], this.control); // should be FormGroup, there is no access to internal Array fields
      } else {
        const last = fg.pop();
        FhngReactiveInputC.fillFormControlsByPath(
          groupingControl.get('_model').value,
          new Array(...fg),
          groupingControl
        );
        const targetFormGroup: FormGroup = groupingControl.get(
          fg.join('.')
        ) as FormGroup;
        if (targetFormGroup) {
          targetFormGroup.addControl(last, this.control);
        } else {
          (groupingControl as FormGroup).addControl(this.name, this.control);
        }
      }
    }
    //Emitujemy wartosc do modelu - działa odpowiednio dla ustwieniem "updateOn" w danej kontrolce.
    //Wysyła wwartość spowrotem do modelu przekazanego w [(value)]
    this.control.valueChanges.subscribe((value1) => {
      let val = value1 === '' ? null : value1;
      if (this.valueChange) {
        if (this.formatter) {
          val = this.formatter.toModel(val);
        }
        if (this.value != val) {
          this.valueChange.emit(val);
        }
      }
    });

    //Used for validation purpose. Easy we to know to which component control belongs.
    //TODO Same control inside multiple fhngcomponents is not supported.
    this.control.componentId = this.id;
    this.control.componentReference = this;
  }

  /**
   * Get selector of component - for loging purpose
   */
  public getSelector() {
    const selector: ElementRef = this.injector.get(ElementRef, null);
    return selector
      ? selector.nativeElement.tagName.toLowerCase()
      : 'selector not found';
  }

  protected buildFormControl(updateOn: 'change' | 'blur' | 'submit') {
    /**
     * Zawsze tworzymy kontrolkę dynamicznie.
     */

      //Create new FormControl for this input
    let v = this.value;

    if (this.formatter) {
      v = this.formatter.fromModel(v);
    }

    let disabled = false;
    // if (this.availabilityDirective) {
    //     if (this.availabilityDirective.calculateAvailability() != AvailabilityEnum.EDIT) {
    //         disabled = true;
    //     }
    // }

    // const formControl: FhngFormControl = new FhngFormControl({value: v, disabled: disabled}, {
    //     // modelToControlValidator musi być zawsze pierwszy!!
    //     validators: [FhngValidators.modelToControlValidator(this),
    //         ...ValidationUtils.resolveStandradValidators(this),
    //         ...this.addCustomValidators()],
    //     updateOn: updateOn
    // });

    // formControl.componentReference = this;
    // formControl.componentId = this.id;

    return formControl;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['value']) {
      const val = changes['value'];
      if (val.previousValue != val.currentValue && this.control != null) {
        this.control.setValue(
          this.formatter
            ? this.formatter.fromModel(val.currentValue)
            : val.currentValue,
          {onlySelf: true, emitEvent: false}
        );
      }
    }
    super.ngOnChanges(changes);
  }

  /**
   * WithValidationInterface
   */
  public validate(): void {
  }

  @Input()
  public validationLabel: string = null;

  public getValidationLabel(): string {
    return this.validationLabel
      ? this.validationLabel
      : this.label
        ? this.label
        : this.id;
  }

  static fillFormControlsByPath(
    model: any,
    path: string[],
    abstractControl: AbstractControl,
    circularMap: Map<any, FormGroup | FormArray> = new Map<
      any,
      FormGroup | FormArray
    >()
  ) {
    if (path.length == 0) {
      return;
    }
    const key = path.shift();
    const value = model[key];
    if (!(value == null) && !circularMap.has(value)) {
      if (
        value instanceof Object &&
        !key.startsWith('$') &&
        !(value instanceof Date)
      ) {
        const notExists = abstractControl.get(key) == null;
        if (TypeUtils.isArray(value)) {
          const fa: FormArray = notExists
            ? FhngReactiveInputC.createFormArray(value)
            : (abstractControl.get(key) as FormArray);
          circularMap.set(value, fa);
          value.forEach((arrayObject, index) => {
            const fg: FormGroup = notExists
              ? FhngReactiveInputC.createFormGroup(arrayObject)
              : (fa.get(index.toString()) as FormGroup);
            circularMap.set(arrayObject, fg);
            if (notExists) {
              fa.push(fg);
            }
          });
          if (notExists && abstractControl instanceof FormGroup) {
            abstractControl.addControl(key, fa);
          }
          if (path.length == 0) {
            return;
          }
          const index = path.shift();
          FhngReactiveInputC.fillFormControlsByPath(
            value[index],
            path,
            fa.get(index),
            new Map(circularMap)
          );
        } else {
          const fg: FormGroup = notExists
            ? FhngReactiveInputC.createFormGroup(value)
            : (abstractControl.get(key) as FormGroup);
          circularMap.set(value, fg);
          if (notExists && abstractControl instanceof FormGroup) {
            abstractControl.addControl(key, fg);
          }
          FhngReactiveInputC.fillFormControlsByPath(
            value,
            path,
            fg,
            new Map(circularMap)
          );
        }
      }
    }
  }

  static createFormGroup(obj: any): FormGroup {
    const fc = new FormControl(obj);
    fc.disable({onlySelf: true, emitEvent: false});
    return new FormGroup({
      _model: fc,
    });
  }

  //TODO Sprawdzić FormArray
  static createFormArray(obj: any): FormArray {
    return new FormArray([]);
    //TODO Czy będziemy potrzebować modelu rodzica dla Listy obiektów?? Narazie go nie dodaje

    // return new FormArray([
    //   new FormControl(obj, control => {
    //     control.disable({onlySelf: true, emitEvent: false});
    //     return control
    //   })
    // ])
  }

  public processAriaLabel() {
    if (!this.ariaLabel) {
      if (!this.label) {
        this.ariaLabel = this.placeholder ? this.placeholder : null;
      }
    }
  }
}

export namespace FhngReactiveInputC {
  export const viewProviders = [
    {
      provide: ControlContainer,
      useExisting: FormGroupDirective,
    },
  ];
}
