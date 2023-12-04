import {Component, forwardRef, Injector, OnInit, Optional, SkipSelf,} from '@angular/core';
import {DropdownItemComponent} from '../dropdown-item/dropdown-item.component';
import {FhngComponent} from '@fh-ng/forms-handler';

@Component({
  selector: 'fhng-three-dots-menu-item',
  templateUrl: '../dropdown-item/dropdown-item.component.html',
  styleUrls: ['../dropdown-item/dropdown-item.component.scss'],
  providers: [

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
