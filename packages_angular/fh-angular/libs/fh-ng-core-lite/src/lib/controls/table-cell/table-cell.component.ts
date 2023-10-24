import {
  Component,
  ElementRef,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';

@Component({
  selector: 'fhng-table-cell',
  templateUrl: './table-cell.component.html',
  styleUrls: ['./table-cell.component.scss'],
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
      useExisting: forwardRef(() => TableCellComponent),
    },
  ],
})
export class TableCellComponent
  extends FhngHTMLElementC
  implements OnChanges, OnInit {
  @HostBinding('class')
  class: string = 'fhng-table-cell td';

  @Input()
  public width: string = '';

  @Input()
  public label: string = '';

  @Input()
  public value: any;

  constructor(
    public readonly elementRef: ElementRef<HTMLElement>,
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  ngOnInit() {
    super.ngOnInit();
  }
}
