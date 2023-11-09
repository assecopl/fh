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
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

@Component({
  selector: '[fhng-table-cell]',
  templateUrl: './table-cell.component.html',
  styleUrls: ['./table-cell.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,
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
  public override width: string = '';

  @Input()
  public override label: string = '';

  constructor(
    public override elementRef: ElementRef<HTMLElement>,
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.wrapperClass = false;
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  override mapAttributes(data: IDataAttributes | any) {
    super.mapAttributes(data);
    this.subelements = data.tableCells;
  }
}
