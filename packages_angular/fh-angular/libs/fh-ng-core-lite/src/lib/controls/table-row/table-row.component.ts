import {
  AfterViewInit,
  Component,
  ElementRef,
  forwardRef,
  Host,
  HostBinding,
  HostListener,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
  TemplateRef,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {TableComponentRef} from '../table/table.ref';
import {TypeUtils} from '../../Base';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

@Component({
  selector: '[fhng-table-row]',
  templateUrl: './table-row.component.html',
  styleUrls: ['./table-row.component.scss'],
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
      useExisting: forwardRef(() => TableRowComponent),
    },
  ],
})
export class TableRowComponent
  extends FhngHTMLElementC
  implements OnInit, AfterViewInit, OnChanges {
  @HostBinding('attr.aria-selected')
  public ariaSelected: boolean = false;

  @Input()
  @HostBinding('class.table-primary')
  highlight: boolean = false;

  @HostBinding('class.table-secondary')
  hover: boolean = false;

  @Input()
  public rowIndex: any = null;

  @ViewChild(TemplateRef, {read: TemplateRef, static: true})
  public template: TemplateRef<void>;

  constructor(
    public override elementRef: ElementRef,
    public override injector: Injector,
    @Optional() @SkipSelf() public tableRef: TableComponentRef,
    @Optional() @SkipSelf() public override parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.wrapperClass = false;
    if (tableRef) {
      tableRef.rowsArray.push(this);
    }
  }

  @ViewChild('tr', {static: true, read: ViewContainerRef})
  public tr: ViewContainerRef = null;

  override ngOnInit() {
    super.ngOnInit();
    // if (
    //   this.tableRef &&
    //   (this.tableRef.selectable ||
    //     this.tableRef.selectedChange.observers.length > 0)
    // ) {
    //   this.pointer = true;
    //   this.tabindex = 0;
    // } else {
    //   this.pointer = false;
    //   this.tabindex = null;
    // }
    // this.toggleHighlight(this.tableRef.selected);
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  override ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  @HostListener('mouseover', ['$event'])
  private mouseover(event: any) {
    this.hover = true;
  }

  @HostListener('mouseout', ['$event'])
  private mouseout(event: any) {
    this.hover = false;
  }

  @HostListener('click', ['$event'])
  private clickRow(event: any) {
    if (!this.tableRef?.selectionCheckboxes) {
      this.tableRef.onRowClickEvent(event, this.rowIndex, false);
    }
  }

  public toggleHighlight(selected: any) {
    // if (selected === this) {
    //   this.highlight = true;
    //   this.ariaSelected = true;
    // } else if (TypeUtils.isArray(selected)) {
    //   this.highlight = (selected as Array<any>).includes(this.row);
    //   this.ariaSelected = this.highlight;
    // } else {
    //   this.highlight = false;
    //   this.ariaSelected = false;
    // }
  }

  @HostListener('keydown.enter', ['$event']) onEnterHandler(
    event: KeyboardEvent
  ) {
    if (!this.tableRef?.selectionCheckboxes) {
      event.stopPropagation();
      event.preventDefault();
      this.tableRef.select(event, this);
    }
  }

  override mapAttributes(data: IDataAttributes | any) {
    super.mapAttributes(data);
    this.subelements = data.tableCells;
  }
}
