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
import {TableComponentRef} from '../table/table.ref';
import {TableCellComponent} from '../table-cell/table-cell.component';
import {TablePagedComponent} from '../table-paged/table-paged.component';
import {Direction, Order, Page} from '@fhng/ng-core';

@Component({
  selector: 'fhng-table-column',
  templateUrl: './table-column.component.html',
  styleUrls: ['./table-column.component.scss'],
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
      useExisting: forwardRef(() => TableColumnComponent),
    },
  ],
})
export class TableColumnComponent
  extends TableCellComponent
  implements OnChanges, OnInit {
  @HostBinding('class')
  class: string = 'fhng-table-column th';

  @Input()
  public sortBy: string = null;

  constructor(
    public readonly elementRef: ElementRef<HTMLElement>,
    public injector: Injector,
    private table: TableComponentRef,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(elementRef, injector, parentFhngComponent);
    if (table) {
      table.registerColumn(this, parentFhngComponent);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  public sort() {
    if (this.sortBy && this.table instanceof TablePagedComponent) {
      let direction = this.getDirection();
      if (direction == null || direction == Direction.DESC) {
        direction = Direction.ASC;
      } else {
        direction = Direction.DESC;
      }

      (this.table.collection as Page<any>).orders = [
        new Order(this.sortBy, direction),
      ];
      this.table.sortChange.emit(this.table.collection);
    }
  }

  public getDirection() {
    if (
      this.table instanceof TablePagedComponent &&
      this.table.collection instanceof Page
    ) {
      return (this.table.collection as Page<any>).orders.find(
        (order) => order.property == this.sortBy
      )?.direction;
    }
    return undefined;
  }
}
