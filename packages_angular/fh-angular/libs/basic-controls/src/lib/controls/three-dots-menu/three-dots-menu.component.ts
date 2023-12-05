import {
  Component,
  ContentChildren,
  forwardRef,
  Host,
  Injector,
  OnInit,
  Optional,
  QueryList,
  SkipSelf,
} from '@angular/core';
import {DropdownComponent} from '../dropdown/dropdown.component';
import {ThreeDotsMenuItemComponent} from '../three-dots-menu-item/three-dots-menu-item.component';
import {BootstrapStyleEnum} from '../../models/enums/BootstrapStyleEnum';
import {FhngComponent} from "@fh-ng/forms-handler";

@Component({
  selector: 'fhng-three-dots-menu',
  templateUrl: './three-dots-menu.component.html',
  styles: [
    `
      :host-context(.table-primary) .dropdown-toggle,
      :host-context(.bg-primary) .dropdown-toggle {
        color: white !important;
      }
    `,
  ],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => ThreeDotsMenuComponent),
    },
  ],
})
export class ThreeDotsMenuComponent
  extends DropdownComponent
  implements OnInit {
  @ContentChildren(ThreeDotsMenuItemComponent)
  public subcomponents: QueryList<ThreeDotsMenuItemComponent> =
    new QueryList<ThreeDotsMenuItemComponent>();

  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.bootstrapStyle = BootstrapStyleEnum.LINK;
    this.label = "[icon='fas fa-ellipsis-v']";
  }
}
