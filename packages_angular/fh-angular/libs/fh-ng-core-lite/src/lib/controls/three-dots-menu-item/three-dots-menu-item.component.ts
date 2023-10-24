import {Component, forwardRef, Host, Injector, OnInit, Optional, SkipSelf,} from '@angular/core';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {DropdownItemComponent} from '../dropdown-item/dropdown-item.component';

@Component({
  selector: 'fhng-three-dots-menu-item',
  templateUrl: '../dropdown-item/dropdown-item.component.html',
  styleUrls: ['../dropdown-item/dropdown-item.component.scss'],
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
      useExisting: forwardRef(() => ThreeDotsMenuItemComponent),
    },
  ],
})
export class ThreeDotsMenuItemComponent
  extends DropdownItemComponent
  implements OnInit {
  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }
}
