import {Component, forwardRef, HostBinding, Injector, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

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
  public override mb2: boolean = false;

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
