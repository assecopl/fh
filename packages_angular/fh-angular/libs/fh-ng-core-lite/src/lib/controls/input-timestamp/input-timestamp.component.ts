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
  NgbDateParserFormatter, NgbDateStruct,
  NgbInputDatepicker,
  NgbInputDatepickerConfig,
  NgbTimeAdapter, NgbTimeStruct,
} from '@ng-bootstrap/ng-bootstrap';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {CustomNgbDatetimeService} from "../../service/custom-ngb-date-parser-formatter.service";

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

  @HostBinding('class.form-group')
  public formGroupClass: boolean = true;

  @HostBinding('class.highlightToday')
  @Input()
  public highlightToday: boolean = false;

  @Input()
  public format: string = 'DD-MM-YYYY HH:mm:ss';

  @ViewChild('d', {read: NgbInputDatepicker})
  public d: NgbInputDatepicker & any;

  @Output()
  public change = new EventEmitter<any>();

  public c: NgbDate;

  /**
   * @Override
   */
  public override icon: string = 'fa-calendar';

  constructor(
    @Inject(LOCALE_ID) private _locale: string,
    public override injector: Injector,
    public config: NgbInputDatepickerConfig,
    public calendar: NgbCalendar,
    public custom: CustomNgbDatetimeService,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    const curentYear = new Date().getFullYear();

    this.config.minDate = {year: curentYear - 100, day: 1, month: 1};
    this.config.maxDate = {year: curentYear + 100, day: 1, month: 1};
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

  public override updateModel(date: string) {
    this.valueChanged = true;
    this.rawValue = date;
  };

  public override mapAttributes(data: IInputTimestampDataAttributes): void {
    super.mapAttributes(data);

    this.onChange = data.onChange;
    this.custom.frontendFormat = this.format?.replace('RRRR', 'YYYY');

    console.log('data', this)
  }
}
