import {
  Component,
  ElementRef,
  EventEmitter,
  forwardRef,
  Host,
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
  NgbDatepickerConfig,
  NgbInputDatepickerConfig,
  NgbTimeAdapter,
} from '@ng-bootstrap/ng-bootstrap';
import {FhngDateAdapter} from '../../models/FhngDateTimeAdapter';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FormComponent} from '../form/form.component';
import Inputmask from 'inputmask';
import {IconAligmentType} from '../../models/CommonTypes';

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
    FhngDateAdapter,
    NgbDatepickerConfig,
    {provide: NgbDateAdapter, useExisting: FhngDateAdapter},
    {provide: NgbTimeAdapter, useExisting: FhngDateAdapter},
    {provide: NgbDateParserFormatter, useExisting: FhngDateAdapter},
  ], // add config to the component providers
})
export class InputDateComponent extends FhngReactiveInputC implements OnInit {
  public override width = BootstrapWidthEnum.MD3;

  @HostBinding('class.highlightToday') @Input() public highlightToday: boolean =
    false;

  @Input() public format: string = 'DD-MM-YYYY';

  @Input() public maskEnabled: boolean = true;
  @Input() public maskDynamic: boolean = false;

  @ViewChild('element') inputRef: ElementRef;

  /**
   * @Override
   */
  @Input()
  public override iconAlignment: IconAligmentType = 'AFTER';

  @Output() public change = new EventEmitter<any>();
  /**
   * @Override
   */
  public override icon: string = 'fa-calendar-alt';

  constructor(
    public override injector: Injector,
    public config: NgbInputDatepickerConfig,
    public ngbDatepickerConfig: NgbDatepickerConfig,
    public calendar: NgbCalendar,
    public fhngDateTimeAdapter: FhngDateAdapter,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    //TODO formatters , config params

    const curentYear = new Date().getFullYear();
    this.ngbDatepickerConfig.minDate = {
      year: curentYear - 100,
      day: 1,
      month: 1,
    };
    this.config.minDate = {year: curentYear - 100, day: 1, month: 1};
    this.ngbDatepickerConfig.maxDate = {
      year: curentYear + 100,
      day: 1,
      month: 1,
    };
    this.config.maxDate = {year: curentYear + 100, day: 1, month: 1};
  }

  override ngOnInit() {
    super.ngOnInit();
    this.fhngDateTimeAdapter.dateFormat = this.format.replace('RRRR', 'YYYY');
    if (this.value) {
      this.value = new Date(Date.parse(this.value));
    }
    if (this.iconAlignment === 'BEFORE') {
      this.config.placement = 'bottom-left';
    }
    if (this.iconAlignment == 'AFTER') {
      this.config.placement = 'bottom-right';
    }

    const curentYear = new Date().getFullYear();
    this.config.minDate = {year: curentYear - 100, day: 1, month: 1};
    this.config.maxDate = {year: curentYear + 100, day: 1, month: 1};
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();

    if (this.maskEnabled) {
      Inputmask({
        clearMaskOnLostFocus: false,
        greedy: false,
        jitMasking: this.maskDynamic,
        placeholder: this.format,
        definitions: {
          R: {
            validator: '[0-9]',
            cardinality: 1,
            definitionSymbol: '*',
          },
          Y: {
            validator: '[0-9]',
            cardinality: 1,
            definitionSymbol: '*',
          },
          M: {
            validator: '[0-9]',
            cardinality: 1,
            definitionSymbol: '*',
          },
          D: {
            validator: '[0-9]',
            cardinality: 1,
            definitionSymbol: '*',
          },
        },
        insertMode: true,
        shiftPositions: false,
        alias: 'date',
        mask: this.format,
      }).mask(this.inputRef.nativeElement);
    }
  }

  onDateSelect(date: any) {
    this.change.emit();
  }
}
