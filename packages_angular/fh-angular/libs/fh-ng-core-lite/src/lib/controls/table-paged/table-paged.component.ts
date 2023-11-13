import {
  AfterContentInit,
  AfterViewInit,
  Component,
  forwardRef,
  Injector,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {TableComponent} from '../table/table.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {TableComponentRef} from '../table/table.ref';
import {AvailabilityEnum} from "../../availability/enums/AvailabilityEnum";

@Component({
  selector: 'fhng-table-paged',
  templateUrl: './table-paged.component.html',
  styleUrls: ['./table-paged.component.scss'],
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
      useExisting: forwardRef(() => TablePagedComponent),
    },
    {
      provide: TableComponentRef,
      useExisting: forwardRef(() => TablePagedComponent),
    },
  ],
})
export class TablePagedComponent
  extends TableComponent
  implements OnInit, AfterViewInit, OnChanges, OnDestroy, AfterContentInit {
  /**
   * pageable variables and eventEmiters
   */
  @Input()
  public pageable: boolean = true;
  // @Output()
  // public pageChange = new EventEmitter<any>();
  // @Output()
  // public sortChange = new EventEmitter<any>();
  // @Output()
  // public pageSizeChange = new EventEmitter<any>();

  @Input()
  public pageSize: number = 0;

  public pageNumber: number = 1;
  public nextPageNumber: number = 1;

  // private pageSizeSelect: any;
  // private pageSizeSelect_clone: any;
  public sortedBy: any = null;
  // private paginator: HTMLElement;
  // private paginator_second: HTMLElement;
  // private pageInfo: HTMLElement;
  // private pageInfo_second: HTMLElement;
  public paginationAboveTable: boolean = false;
  // private pageSize: number;
  public sortDirection: any;

  public onPageChange: string = null;
  public onPageSizeChange: string = null;
  public onSortChange: string = null;
  public defaultSortByAsc: string = null;
  public totalPages: number = null;
  public totalRows: number = null;
  public currentPage: number = 1;
  // public readonly paginatorOffset: any;
  public pageChangeListeners: HTMLElement[];

  public pageSizes: Number[] = [5, 10, 15, 25];
  public pageSizeAsButtons: boolean = false;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.hostClass = `col-${this.width}`;
  }

  override ngOnInit() {
    super.ngOnInit();
    this.restorePageNumber();
  }

  override ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  override ngAfterContentInit(): void {
    super.ngAfterContentInit();
  }

  public restorePageNumber() {
    if (this.pageNumber > 0) {
      // this.pageNumber = this.collection.pageNumber + 1;
      // this.pageChange.emit(this.collection);
    }
  }

  public pageChange(pageNumber: number) {
    // event.preventDefault();
    // event.stopPropagation();
    if (this.accessibility != AvailabilityEnum.EDIT) return;
    if (this.pageNumber != pageNumber - 1) {
      this.pageNumber = pageNumber - 1;
      this.currentPage = pageNumber - 1;
      this.changesQueue.queueAttributeChange('currentPage', this.pageNumber);
      this.fireEventWithLock('onPageChange', this.onPageChange);
    }
  }

  public sortChange(newSortedBy, newSortDirection) {
    this.sortedBy = newSortedBy;
    this.changesQueue.queueAttributeChange('sortBy', newSortedBy);
    if (newSortDirection !== undefined) { // undefined -> not changed
      this.sortDirection = newSortDirection;
    }
    // always send sortDirection with sortedBy change
    this.changesQueue.queueAttributeChange('direction', this.sortDirection);
    // this.cleanUpSortableComponents();

    this.columnsMap.forEach(column => {
      if (column.id != this.sortedBy) {
        column.direction = null;
      }
    })

    this.fireEventWithLock('onSortChange', null);
  }

  public pageSizeSelectEvent(size: any) {
    this.pageSize = size;
    this.pageNumber = 0;
    this.nextPageNumber = 0;
    this.currentPage = 0;
    this.changesQueue.queueAttributeChange('pageSize', Number(size));
    this.fireEventWithLock('onPageSizeChange', null);
  }

  override mapAttributes(data: any) {
    super.mapAttributes(data);
    if (data.pageNumber) {
      this.nextPageNumber = data.pageNumber + 1;
    }
  }
}
