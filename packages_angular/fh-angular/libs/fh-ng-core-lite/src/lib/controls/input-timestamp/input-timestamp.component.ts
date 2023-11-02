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
  NgbDateParserFormatter,
  NgbInputDatepicker,
  NgbInputDatepickerConfig,
  NgbTimeAdapter,
} from '@ng-bootstrap/ng-bootstrap';
import {FhngDateTimeAdapter} from '../../models/FhngDateTimeAdapter';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';

@Component({
  selector: '[fhng-input-timestamp]',
  templateUrl: './input-timestamp.component.html',
  styleUrls: ['./input-timestamp.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => InputTimestampComponent),
    },
    NgbInputDatepickerConfig,
    FhngDateTimeAdapter,
    {provide: NgbDateAdapter, useExisting: FhngDateTimeAdapter},
    {provide: NgbTimeAdapter, useExisting: FhngDateTimeAdapter},
    {provide: NgbDateParserFormatter, useExisting: FhngDateTimeAdapter},
  ],
})
export class InputTimestampComponent
  extends FhngReactiveInputC
  implements OnInit {
  @HostBinding('class.highlightToday') @Input() public highlightToday: boolean =
    false;

  @Input() public format: string = 'DD-MM-YYYY HH:mm:ss';

  @ViewChild('d', {read: NgbInputDatepicker}) d: NgbInputDatepicker & any;

  @Output() public change = new EventEmitter<any>();

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
    public fhngDateTimeAdapter: FhngDateTimeAdapter,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.width = BootstrapWidthEnum.MD3;
    //TODO formatters , config params

    const curentYear = new Date().getFullYear();
    this.config.minDate = {year: curentYear - 100, day: 1, month: 1};
    this.config.maxDate = {year: curentYear + 100, day: 1, month: 1};
  }

  override ngOnInit() {
    super.ngOnInit();
    this.fhngDateTimeAdapter.dateFormat = this.format.replace('RRRR', 'YYYY');
  }

  public timeChangeEvent(event) {
    //Use NgbInputDatepicker internal function to update input value dynamicly.
    // console.log("timeChangeEvent", event);
    if (this.d) {
      this.d.manualDateChange(event, true);
    }
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  changeHour(event) {
  }

  onDateSelect(date: any) {
    this.change.emit();
  }
}
