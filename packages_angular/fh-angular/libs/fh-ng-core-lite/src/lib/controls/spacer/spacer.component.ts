import {
  Component,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
// import {DocumentedComponent} from '@fhng/ng-core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
// import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

// @DocumentedComponent({
//   category: DocumentedComponent.Category.ARRANGEMENT,
//   value: 'Component used to create spaces in application layout',
//   icon: 'fas fa-arrows-alt-h',
// })
@Component({
  selector: 'fhng-spacer',
  templateUrl: './spacer.component.html',
  styleUrls: ['./spacer.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => SpacerComponent)},
  ],
})
export class SpacerComponent extends FhngHTMLElementC implements OnInit {
  public override mb3: boolean = false;

  @HostBinding('class.spacer')
  public classSpacer: boolean = true;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public override ngOnInit() {
    super.ngOnInit();
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
