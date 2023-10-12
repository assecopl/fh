import {Component, forwardRef, Host, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {DocumentedComponent} from '@fhng/ng-core';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value: 'Image component is responsible for displaying images.',
  icon: 'fa fa-font',
})
@Component({
  selector: 'fhng-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => ImageComponent)},
  ],
})
export class ImageComponent extends FhngHTMLElementC implements OnInit {
  public width: string = BootstrapWidthEnum.MD2;

  @Input()
  public src: any;
  @Input()
  public alt: any;

  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
