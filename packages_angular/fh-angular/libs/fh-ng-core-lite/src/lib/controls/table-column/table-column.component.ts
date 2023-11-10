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
import {TableComponentRef} from '../table/table.ref';
import {TableCellComponent} from '../table-cell/table-cell.component';
import {TablePagedComponent} from '../table-paged/table-paged.component';
import {Direction, Order, Page} from '../../Base';
import {isNumber} from 'util';
import {STYLE_UNIT} from "../../models/enums/StyleUnitEnum";
import {DomSanitizer, SafeStyle} from '@angular/platform-browser';

@Component({
  selector: '[fhng-table-column]',
  templateUrl: './table-column.component.html',
  styleUrls: ['./table-column.component.scss'],
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
      useExisting: forwardRef(() => TableColumnComponent),
    },
  ],
})
export class TableColumnComponent
  extends TableCellComponent
  implements OnChanges, OnInit {
  @HostBinding('class')
  override class: string = 'fhng-table-column th';

  @Input()
  public sortBy: string = null;

  @Input('data')
  public override set data(value: any) {
    this.mapAttributes(value);
  }

  @HostBinding('style.width')
  public hostStyleWidth: SafeStyle & any = {};

  @HostBinding('class.sortable')
  public sortable: boolean = false;
  // private sorter: any;
  public rowspan: number = null;
  public subColumnsExists: boolean = false;
  public colspan: number = null;

  public direction: Direction = null;

  constructor(
    public override readonly elementRef: ElementRef<HTMLElement>,
    public override injector: Injector,
    private table: TableComponentRef,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    private sanitizer: DomSanitizer
  ) {
    super(elementRef, injector, parentFhngComponent);
    this.wrapperClass = false

    if (table) {
      table.registerColumn(this, parentFhngComponent);
    }
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (changes['sortedBy'] && changes['sortedBy'].currentValue != this.sortBy) {
      this.direction == null;
    }
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  public sort() {
    if (this.sortable && this.table instanceof TablePagedComponent) {

      if (this.direction == null || this.direction == Direction.DESC) {
        this.direction = Direction.ASC;
      } else {
        this.direction = Direction.DESC;
      }


      this.table.sortChange(this.id, Direction[this.direction]);
    }
  }

  /**
   * Zmiana zarzadzania szerokoscia w kolumnie, moze wystepowac tylko %, px i vw - domyslnie %
   * @param value
   * @param force
   */
  public override processWidth(value: string, force: boolean = false): void {
    // if (this.hostWidth.length === 0 || force) {
    if (!value) {
      value = this.width;
    }

    if (value) {
      this.width = value;
      let v = null;
      if (value) {
        v = isNumber(value) ? value : value.replace(/px|%|em|rem|pt/gi, '');
        let unit = value.replace(v, "");
        unit = unit ? unit : '%';
        // this.hostStyle[key] = v;
        // this.hostStyle['width'] = {};
        this.hostStyle['width'] = v + unit;
      }
    }
  }




}
