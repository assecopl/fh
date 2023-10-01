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
  ViewChild
} from '@angular/core';
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';
import {DocumentedComponent} from '@ewop/ng-core';
import {
  NgbCalendar,
  NgbDateAdapter,
  NgbDateParserFormatter,
  NgbDatepickerConfig,
  NgbInputDatepickerConfig,
  NgbTimeAdapter
} from '@ng-bootstrap/ng-bootstrap';
import {EwopDateAdapter} from '../../models/EwopDateTimeAdapter';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {BootstrapWidthEnum} from "./../../models/enums/BootstrapWidthEnum";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {FormComponent} from "../form/form.component";
import Inputmask from "inputmask";
import {IconAligmentType} from "libs/ng-basic-controls/src/models/CommonTypes";

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value: "Component responsible for displaying field, where user can set only date.",
  icon: "fa fa-calendar"
})
@Component({
  selector: 'ewop-input-date',
  templateUrl: './input-date.component.html',
  styleUrls: ['./input-date.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    EwopAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Ewop.
     */
    {provide: EwopComponent, useExisting: forwardRef(() => InputDateComponent)}
    , NgbInputDatepickerConfig, EwopDateAdapter, NgbDatepickerConfig,
    {provide: NgbDateAdapter, useExisting: EwopDateAdapter},
    {provide: NgbTimeAdapter, useExisting: EwopDateAdapter},
    {provide: NgbDateParserFormatter, useExisting: EwopDateAdapter},
  ] // add config to the component providers
})
export class InputDateComponent extends EwopReactiveInputC implements OnInit {
  public width = BootstrapWidthEnum.MD3;

  @HostBinding('class.highlightToday') @Input() public highlightToday: boolean = false;

  @Input() public format: string = 'DD-MM-YYYY';

  @Input() public maskEnabled: boolean = true;
  @Input() public maskDynamic: boolean = false;

  @ViewChild('element') inputRef: ElementRef;


  /**
   * @Override
   */
  @Input()
  public iconAlignment: IconAligmentType = "AFTER";

  @Output() public change = new EventEmitter<any>();
  /**
   * @Override
   */
  public icon: string = "fa-calendar-alt text-primary";

  constructor(public injector: Injector, public config: NgbInputDatepickerConfig,
              public ngbDatepickerConfig: NgbDatepickerConfig,
              public calendar: NgbCalendar, public ewopDateTimeAdapter: EwopDateAdapter,
              @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
              @Optional() @Host() @SkipSelf() iForm: FormComponent
  ) {
    super(injector, parentEwopComponent, iForm);
    //TODO formatters , config params

    const curentYear = new Date().getFullYear();
    this.ngbDatepickerConfig.minDate = {year: curentYear - 100, day: 1, month: 1};
    this.config.minDate = {year: curentYear - 100, day: 1, month: 1};
    this.ngbDatepickerConfig.maxDate = {year: curentYear + 100, day: 1, month: 1};
    this.config.maxDate = {year: curentYear + 100, day: 1, month: 1};
  }

  ngOnInit() {
    super.ngOnInit();
    this.ewopDateTimeAdapter.dateFormat = this.format.replace("RRRR", "YYYY");
    if (!this.control && this.value) {
      this.value = new Date(Date.parse(this.value))
    }
    if (this.iconAlignment === "BEFORE") {
      this.config.placement = "bottom-left"
    }
    if (this.iconAlignment == "AFTER") {
      this.config.placement = 'bottom-right';
    }

    const curentYear = new Date().getFullYear();
    this.config.minDate = {year: curentYear - 100, day: 1, month: 1};
    this.config.maxDate = {year: curentYear + 100, day: 1, month: 1};
  }

  ngAfterViewInit() {
    super.ngAfterViewInit();

    if (this.maskEnabled) {
      Inputmask({
        clearMaskOnLostFocus: false,
        greedy: false,
        jitMasking: this.maskDynamic,
        placeholder: this.format,
        definitions: {
          "R": {
            validator: "[0-9]",
            cardinality: 1,
            definitionSymbol: "*"
          },
          "Y": {
            validator: "[0-9]",
            cardinality: 1,
            definitionSymbol: "*"
          },
          "M": {
            validator: "[0-9]",
            cardinality: 1,
            definitionSymbol: "*"
          },
          "D": {
            validator: "[0-9]",
            cardinality: 1,
            definitionSymbol: "*"
          },
        },
        insertMode: true,
        shiftPositions: false,
        alias: "date",
        mask: this.format
      }).mask(this.inputRef.nativeElement);
    }
  }

  onDateSelect(date: any) {
    this.change.emit();
  }
}
