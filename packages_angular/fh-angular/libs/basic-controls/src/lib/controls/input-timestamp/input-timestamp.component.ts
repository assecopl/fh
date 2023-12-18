import {
  Component,
  EventEmitter,
  forwardRef,
  Host,
  HostBinding,
  Inject,
  Injector,
  Input,
  LOCALE_ID,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
  ViewChild,
} from '@angular/core';
import {
  NgbCalendar,
  NgbDate,
  NgbDateAdapter,
  NgbDateParserFormatter, NgbDatepickerConfig,
  NgbInputDatepicker,
  NgbInputDatepickerConfig,
  NgbTimeAdapter,
} from '@ng-bootstrap/ng-bootstrap';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {
  CustomNgbDatetimeService,
  FhngComponent, FhngDateUtils,
  FORM_VALUE_ATTRIBUTE_NAME,
  IDataAttributes
} from '@fh-ng/forms-handler';
import moment from "moment";

interface IInputTimestampDataAttributes extends IDataAttributes {
  format?: string;
  onChange?: string;
}

@Component({
  selector: '[fhng-input-timestamp]',
  templateUrl: './input-timestamp.component.html',
  styleUrls: ['./input-timestamp.component.scss'],
  providers: [
    CustomNgbDatetimeService,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => InputTimestampComponent),
    },
    NgbInputDatepickerConfig,
    {provide: NgbDateAdapter, useExisting: CustomNgbDatetimeService},
    {provide: NgbTimeAdapter, useExisting: CustomNgbDatetimeService},
    {provide: NgbDateParserFormatter, useExisting: CustomNgbDatetimeService}
  ],
})
export class InputTimestampComponent
  extends FhngReactiveInputC
  implements OnInit {

  public override width: BootstrapWidthEnum = BootstrapWidthEnum.MD3;

  public override mb2: boolean = false;

  @HostBinding('class.highlightToday')
  @Input()
  public highlightToday: boolean = false;

  @Input()
  public format: string = FhngDateUtils.FRONTEND_DATETIME_FORMAT;

  @ViewChild('d', {read: NgbInputDatepicker})
  public d: NgbInputDatepicker & any;

  @Output()
  public change = new EventEmitter<any>();

  public c: NgbDate;

  /**
   * @Override
   */
  public override icon: string = 'fa-calendar';

  public get hasTime () {
    return !!this.format.match(/HH?|mm?/gm);
  }

  public get hasSeconds() {
    return !!this.format.match(/ss?/gmi);
  }

  constructor(
    @Inject(LOCALE_ID) private _locale: string,
    public override injector: Injector,
    public calendar: NgbCalendar,
    private customNgbDatetimeService: CustomNgbDatetimeService,
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

  public timeChangeEvent(value: string) {
    if (this.d) {
      this.updateModel(value);
    }
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
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

    if (CustomNgbDatetimeService.isDateValid($event.target.value, this.customNgbDatetimeService.frontendFormat)) {
      this.updateModel($event.target.value);
      this.onChangeEvent();
    }
  }

  public override updateModel(date: string) {
    this.valueChanged = true;
    this.rawValue = moment.utc(date, this.customNgbDatetimeService.backendFormat, true).format('YYYY-MM-DDTHH:mm:ss.SSS');
  };

  public override mapAttributes(data: IInputTimestampDataAttributes): void {
    super.mapAttributes(data);

    this.value = data.rawValue;

    this.onChange = data.onChange || this.onChange;
    this.customNgbDatetimeService.frontendFormat = this.format?.replace('RRRR', 'YYYY');
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
