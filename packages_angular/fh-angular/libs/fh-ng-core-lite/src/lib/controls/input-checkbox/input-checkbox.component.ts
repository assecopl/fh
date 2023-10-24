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
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {DocumentedComponent, IForm} from '@fhng/ng-core';
import {InputTypeEnum} from '../../models/enums/InputTypeEnum';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value:
    'Checkbox allows users select many options from a predefined list of choices',
  icon: 'fa fa-check-square',
})
@Component({
  selector: 'fhng-input-checkbox',
  templateUrl: './input-checkbox.component.html',
  styleUrls: ['./input-checkbox.component.scss'],
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
      useExisting: forwardRef(() => InputCheckboxComponent),
    },
  ],
})
export class InputCheckboxComponent
  extends FhngReactiveInputC
  implements OnInit {
  // @HostBinding('class.form-check')
  // formCheck: boolean = true;

  @Input()
  public checked: boolean = false;

  @Output()
  public selectedCheckbox: EventEmitter<InputCheckboxComponent> =
    new EventEmitter<InputCheckboxComponent>();

  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @SkipSelf() iForm: IForm<any>
  ) {
    super(injector, parentFhngComponent, iForm);
    this.labelPosition = 'RIGHT';
  }

  public width: string = BootstrapWidthEnum.MD12;

  //public class:string = "";

  ngOnInit() {
    super.ngOnInit();

    this.inputType = InputTypeEnum.checkbox;
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
