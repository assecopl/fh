import {
  AfterContentInit,
  AfterViewInit,
  Component,
  EventEmitter,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {TableColumnComponent} from '../table-column/table-column.component';
import {TableRowComponent} from '../table-row/table-row.component';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {TableComponentRef} from './table.ref';
import {TableCellComponent} from '../table-cell/table-cell.component';
import {TableHeadRowComponent} from '../table-head-row/table-head-row.component';

/**
 * FIXME Przebudować Tabelę!!!!! Między innymi tak aby nie korzystać z ViewCHild i ContentCHild - działają z opuźnieniem.
 */

@Component({
  selector: 'fhng-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => TableComponent)},
    {
      provide: TableComponentRef,
      useExisting: forwardRef(() => TableComponent),
    },
  ],
})
export class TableComponent
  extends TableComponentRef
  implements OnInit, AfterViewInit, OnChanges, OnDestroy, AfterContentInit {
  @HostBinding('class')
  hostClass: string;

  @Input()
  public minRows: number = 1; // todo:

  @HostBinding('class.table-selectable')
  @Input()
  public selectable: boolean = false;

  @Input()
  public multiselect: boolean = false;

  @Input()
  public selectionCheckboxes: boolean = false;

  @Input()
  public selectAllChceckbox: boolean = true;

  @Input()
  public csvExport: boolean;

  public columns: Map<string, TableColumnComponent> = new Map();
  public columnsArray: TableColumnComponent[] = [];
  /**
   * Used to calculate columns. Register columns only from first row.
   * @private
   */
  private firstRow: TableRowComponent = null;

  public rowsArray: TableRowComponent[] = [];

  @ViewChild('tbody', {static: true, read: ViewContainerRef})
  public tbody: ViewContainerRef = null;

  @ViewChild('thead', {static: true, read: ViewContainerRef})
  private thead: ViewContainerRef = null;

  @ViewChild('tfoot', {static: true, read: ViewContainerRef})
  private tfoot: ViewContainerRef = null;

  @Input()
  public collection: any[] | any = [];

  @Input()
  public selected: any;
  @Output()
  public selectedChange = new EventEmitter<any>();

  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.hostClass = `col-${this.width}`;
  }

  ngOnInit() {
    super.ngOnInit();
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
    if (changes['selected'] && changes['selected'].currentValue == null) {
      this.rowsArray.forEach((row) => {
        row.toggleHighlight(null);
      });
    }
  }

  ngOnDestroy(): void {
  }

  ngAfterContentInit(): void {
    super.ngAfterContentInit();
  }

  onExportCsv(): void {
    let content = '';
    if (this.rowsArray && this.rowsArray.length > 0) {
      this.rowsArray[0].childFhngComponents.forEach((component) => {
        if (component instanceof TableCellComponent) {
          content = content.concat(component.label ? component.label : '', ';');
        }
      });
    }
    content = content.concat('\n');

    this.rowsArray.forEach((row) => {
      row.childFhngComponents.forEach((component) => {
        if (component instanceof TableCellComponent) {
          content = content.concat(component.value ? component.value : '', ';');
        }
      });
      content = content.concat('\n');
    });

    let filename = (this.label ? this.label : 'table_export') + '.csv';
    this.downloadFile(filename, content);
  }

  /** Downloads file with given content */
  protected downloadFile(filename: string, content: string) {
    let element = document.createElement('a');
    element.href =
      'data:text/plain;charset=utf-8,' + encodeURIComponent(content);
    element.download = filename;
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  }

  /**
   * Founstion to register TH columns.
   * We register only columns of first row to create header elements
   * @param column
   * @param row
   */
  public registerColumn(column: TableColumnComponent): void {
    if (!this.columns[column.id]) {
      this.columns.set(column.id, column);
      this.columnsArray.push(column);
    }
  }

  public select(selected: any) {
    if (this.selectedChange?.observers?.length > 0) {
      if (this.multiselect) {
        const array = (this.selected as Array<any>) || [];
        if (array.includes(selected)) {
          array.removeElement(selected);
        } else {
          array.push(selected);
        }
        this.selectedChange.emit(array);
        this.rowsArray.forEach((row) => {
          row.toggleHighlight(array);
        });
        const header = this.childFhngComponents.find(
          (element) => element instanceof TableHeadRowComponent
        ) as TableHeadRowComponent;
        if (header) {
          header.highlight = array?.length == this.collection?.length;
        }
      } else {
        this.selectedChange.emit(selected);
        this.rowsArray.forEach((row) => {
          row.toggleHighlight(selected);
        });
      }
    }
  }

  public toggleSelectAll() {
    if (
      this.selected &&
      this.selected.length > 0 &&
      this.selected.length == this.collection.length
    ) {
      this.selected = [];
      this.selectedChange.emit([]);
      this.rowsArray.forEach((row) => {
        row.highlight = false;
      });
    } else {
      this.selected = [...this.collection];
      this.selectedChange.emit(this.selected);
      this.rowsArray.forEach((row) => {
        row.highlight = true;
      });
    }
  }
}
