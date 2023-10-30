import {Component, forwardRef, Host, Injector, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {DocumentedComponent, IForm} from '@fhng/ng-core';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';

@Component({
  selector: 'fhng-select-one-menu',
  templateUrl: './select-one-menu.component.html',
  styleUrls: ['./select-one-menu.component.scss'],
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
      useExisting: forwardRef(() => SelectOneMenuComponent),
    },
  ],
})
@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value:
    'Component responsible for displaying list of values with possibility of selecting only one value.',
  icon: 'fa fa-caret-down',
})
export class SelectOneMenuComponent
  extends FhngInputWithListC
  implements OnInit {
  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @SkipSelf() iForm: IForm<any>
  ) {
    super(injector, parentFhngComponent, iForm);
    this.width = BootstrapWidthEnum.MD3;
  }

  ngOnInit() {
    super.ngOnInit();
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}