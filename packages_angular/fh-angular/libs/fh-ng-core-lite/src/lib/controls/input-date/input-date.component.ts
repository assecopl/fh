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
  NgbDatepickerConfig,
  NgbInputDatepickerConfig,
} from '@ng-bootstrap/ng-bootstrap';
import {FhngDateAdapter} from '../../models/FhngDateTimeAdapter';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import Inputmask from 'inputmask';
import {FORM_VALUE_ATTRIBUTE_NAME, IconAligmentType} from '../../models/CommonTypes';
import {CustomNgbDateService, CustomNgbDatetimeService} from "../../service/custom-ngb-date-parser-formatter.service";
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

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
  public format: string = 'DD-MM-YYYY';

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

  public override icon: string = 'fa-calendar-alt';

  constructor(
    public override injector: Injector,
    public config: NgbDatepickerConfig,
    public calendar: NgbCalendar,
    private _customNgbDateService: CustomNgbDateService,
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

  override ngAfterViewInit() {
    super.ngAfterViewInit();

    // if (this.maskEnabled) {
    //   Inputmask({
    //     clearMaskOnLostFocus: false,
    //     greedy: false,
    //     jitMasking: this.maskDynamic,
    //     placeholder: this.format,
    //     definitions: {
    //       R: {
    //         validator: '[0-9]',
    //         cardinality: 1,
    //         definitionSymbol: '*',
    //       },
    //       Y: {
    //         validator: '[0-9]',
    //         cardinality: 1,
    //         definitionSymbol: '*',
    //       },
    //       M: {
    //         validator: '[0-9]',
    //         cardinality: 1,
    //         definitionSymbol: '*',
    //       },
    //       D: {
    //         validator: '[0-9]',
    //         cardinality: 1,
    //         definitionSymbol: '*',
    //       },
    //     },
    //     insertMode: true,
    //     shiftPositions: false,
    //     alias: 'date',
    //     mask: this.format,
    //   }).mask(this.inputRef.nativeElement);
    // }
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

    if (CustomNgbDatetimeService.isDateValid($event.target.value, this._customNgbDateService.frontendFormat)) {
      this.updateModel($event.target.value);
      this.onChangeEvent();
    }
  }

  public override updateModel(date: string) {
    this.valueChanged = true;
    this.rawValue = date;
  };

  public override mapAttributes(data: IInputDateDataAttributes): void {
    super.mapAttributes(data);

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
}
