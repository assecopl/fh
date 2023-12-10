import {
  Component,
  EventEmitter,
  forwardRef,
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
import {FhngComponent} from "@fh-ng/forms-handler";


@Component({
  selector: '[fhng-radio-option]',
  templateUrl: './radio-option.component.html',
  styleUrls: ['./radio-option.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => RadioOptionComponent),
    },
  ],
})
export class RadioOptionComponent extends FhngReactiveInputC implements OnInit {
  @Input()
  public checked: boolean = false;

  @Input()
  public override name: string;

  @Input()
  public targetValue: any;

  @Output()
  public selectedRadio: EventEmitter<RadioOptionComponent> =
    new EventEmitter<RadioOptionComponent>();

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.onChange = "-" // Always fire action
  }

  override ngOnInit() {
    super.ngOnInit();

    this.inputType = InputTypeEnum.radio;

    this.processDisabledOptions();
  }

  public processDisabledOptions() {
    this.disabled = String(this.disabled) === 'true';
  }

  inputSelectEvent(event): void {
    // this.control.setValue(this.targetValue, {
    //   onlySelf: true,
    //   emitEvent: false,
    // });
    this.value = this.targetValue;
    if (!this.disabled) {
      this.changesQueue.queueValueChange(this.value);
    }
    this.selectedRadio.emit(this);
    this.onChangeEvent();
  }





  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
