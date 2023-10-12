import {
  Component,
  forwardRef,
  Host,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {DocumentedComponent, FhngFormatter} from '@fhng/ng-core';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FormComponent} from '../form/form.component';

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value:
    'Component responsible for displaying field, where use can set only number.',
  icon: 'fa fa-edit',
})
@Component({
  selector: 'fhng-input-number',
  templateUrl: './input-number.component.html',
  styleUrls: ['./input-number.component.scss'],
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
      useExisting: forwardRef(() => InputNumberComponent),
    },
  ],
})
export class InputNumberComponent
  extends FhngReactiveInputC
  implements OnInit, OnChanges {
  public width = 'md-3';

  public pattern: string = 'separator';

  @Input()
  public maxFractionDigits: any = null;
  @Input()
  public maxIntigerDigits: any = null;
  @Input()
  public allowNegativeNumbers: boolean = false;
  @Input()
  public textAlign: 'LEFT' | 'CENTER' | 'RIGHT';

  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @Host() @SkipSelf() iForm: FormComponent
  ) {
    super(injector, parentFhngComponent, iForm);
  }

  ngOnInit() {
    super.ngOnInit();
    this.processPattern();
  }

  public processPattern() {
    if (!this.formatterName) {
      // this.customPatterns = {'N': {pattern: new RegExp("^([-]?" + integerMark + "" + separatorMark + "" + fractionMark + ")$")}};
      let pattern = 'separator';
      if (this.maxFractionDigits) {
        pattern = 'separator.' + this.maxFractionDigits;
      }
      this.formatter = new FhngFormatter(pattern, null);
      this.formatter.thousandSeparator = '';
      if (this.maxIntigerDigits) {
        let ints = '1';
        this.maxIntigerDigits--;
        while (this.maxIntigerDigits) {
          ints = ints + '0';
          this.maxIntigerDigits--;
        }
        this.formatter.separatorLimit = ints;
      }
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
