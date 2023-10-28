import {
  AfterContentInit,
  AfterViewInit,
  Component,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {TableComponent} from '../table/table.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {TableComponentRef} from '../table/table.ref';
import {Page} from '../../Base';

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
   * @override
   */
  @Input()
  public override collection: Page<any> = null;

  /**
   * pageable variables and eventEmiters
   */
  @Input()
  public pageable: boolean = true;
  @Output()
  public pageChange = new EventEmitter<any>();
  @Output()
  public sortChange = new EventEmitter<any>();
  @Output()
  public pageSizeChange = new EventEmitter<any>();

  @Input()
  public pageSize = 0;

  public pageNumber: number = 1;

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
  }

  override ngAfterContentInit(): void {
    super.ngAfterContentInit();
  }

  public restorePageNumber() {
    if (this.collection && this.collection.pageNumber > 0) {
      this.pageNumber = this.collection.pageNumber + 1;
      this.pageChange.emit(this.collection);
    }
  }

  public onPageChange() {
    if (this.pageNumber - 1 != this.collection.pageNumber) {
      this.collection.pageNumber = this.pageNumber - 1;
      this.pageChange.emit(this.collection);
    }
  }
}
