import {
  Component,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {DocumentedComponent} from '@fhng/ng-core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {InputTypeEnum} from '../../models/enums/InputTypeEnum';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FormComponent} from '../form/form.component';

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value: 'Radio Option represents a single radio component',
  icon: 'fa fa-circle',
})
@Component({
  selector: 'fhng-radio-option',
  templateUrl: './radio-option.component.html',
  styleUrls: ['./radio-option.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
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
  public name: string;

  @Input()
  public targetValue: any;

  @Output()
  public selectedRadio: EventEmitter<RadioOptionComponent> =
    new EventEmitter<RadioOptionComponent>();

  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @Host() @SkipSelf() iForm: FormComponent
  ) {
    super(injector, parentFhngComponent, iForm);
  }

  ngOnInit() {
    super.ngOnInit();

    this.inputType = InputTypeEnum.radio;

    this.processDisabledOptions();
  }

  public processDisabledOptions() {
    this.disabled = String(this.disabled) === 'true';
  }

  updateControlValue(event): void {
    this.control.setValue(this.targetValue, {
      onlySelf: true,
      emitEvent: false,
    });
    this.selectedRadio.emit(this);
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
