import {
  Component,
  ElementRef,
  EventEmitter,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SkipSelf,
  ViewChild,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {
  NgbCalendar,
  NgbDateAdapter,
  NgbDateParserFormatter,
  NgbDatepickerConfig, NgbInputDatepicker,
  NgbInputDatepickerConfig,
} from '@ng-bootstrap/ng-bootstrap';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {
  CustomNgbDateService,
  CustomNgbDatetimeService,
  FhngComponent, FhngDateUtils, FORM_VALUE_ATTRIBUTE_NAME,
  IconAligmentType,
  IDataAttributes
} from '@fh-ng/forms-handler';
import moment from "moment/moment";

interface IInputDateDataAttributes extends IDataAttributes {
  format?: string;
  onChange?: string;
}

@Component({
  selector: '[fhng-input-date]',
  templateUrl: './input-date.component.html',
  styleUrls: ['./input-date.component.scss'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => InputDateComponent),
    },
    NgbInputDatepickerConfig,
    NgbDatepickerConfig,
    CustomNgbDateService,
    {provide: NgbDateAdapter, useExisting: CustomNgbDateService},
    {provide: NgbDateParserFormatter, useExisting: CustomNgbDateService},
  ], // add config to the component providers
})
export class InputDateComponent extends FhngReactiveInputC implements OnInit {
  public override width = BootstrapWidthEnum.MD3;

  @HostBinding('class.highlightToday')
  @Input()
  public highlightToday: boolean = false;

  @Input()
  public format: string = 'YYYY-MM-DD';

  @Input()
  public maskEnabled: boolean = true;

  @Input()
  public maskDynamic: boolean = false;

  @ViewChild('element')
  public inputRef: ElementRef;

  @Output()
  public change = new EventEmitter<any>();

  /**
   * @Override
   */
  @Input()
  public override iconAlignment: IconAligmentType = 'AFTER';

  public override icon: string = 'fa-calendar';

  constructor(
    public override injector: Injector,
    public calendar: NgbCalendar,
    private _customNgbDateService: CustomNgbDateService,
    private _dateConfig: NgbDatepickerConfig,
    private _inputDateConfig: NgbInputDatepickerConfig,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    const curentYear = new Date().getFullYear();

    this._dateConfig.minDate = {year: curentYear - 100, day: 1, month: 1};
    this._dateConfig.maxDate = {year: curentYear + 100, day: 1, month: 1};

    this._inputDateConfig.container = 'body';
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();
  }

  public onDateSelect(value: string): void {
    this.updateModel(value);
  }

  public onClosed(): void {
    this.value = this.rawValue;
    this.onChangeEvent();
  }

  public onChangeInputEvent ($event: any): void {
    $event.preventDefault();

    //Check if date is new date is Valid or if input vale was deleted
    if (CustomNgbDatetimeService.isDateValid($event.target.value, this._customNgbDateService.frontendFormat) || (!$event.target.value && this.value)) {
      this.updateModel($event.target.value);
      this.onChangeEvent();
    }
  }

  public override updateModel(date: string) {
    this.valueChanged = true;
    // Here date should be always valid date or null;
    this.rawValue = date ? date : "";
  };

  public override mapAttributes(data: IInputDateDataAttributes): void {
    super.mapAttributes(data);

    this.value = data.rawValue;

    this.onChange = data.onChange;
    this._customNgbDateService.frontendFormat = this.format?.replace('RRRR', 'YYYY');
  }

  public override extractChangedAttributes() {
    let attrs = {};
    if (this.valueChanged) {
      attrs[FORM_VALUE_ATTRIBUTE_NAME] = this.rawValue;
      this.valueChanged = false;
    }

    return attrs;
  };

  public onClickButton(d: NgbInputDatepicker): void {
    if (!this.disabled) {
      d.toggle();
    }
  }
}
