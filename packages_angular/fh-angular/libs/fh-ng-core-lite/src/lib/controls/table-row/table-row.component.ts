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
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {TableComponentRef} from '../table/table.ref';
import {TypeUtils} from '@fhng/ng-core';

@Component({
  selector: 'fhng-table-row',
  templateUrl: './table-row.component.html',
  styleUrls: ['./table-row.component.scss'],
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
      useExisting: forwardRef(() => TableRowComponent),
    },
  ],
})
export class TableRowComponent
  extends FhngHTMLElementC
  implements OnInit, AfterViewInit, OnChanges {
  @HostBinding('attr.aria-selected')
  public ariaSelected: boolean = false;

  @HostBinding('class.table-primary')
  highlight: boolean = false;

  @HostBinding('class.table-secondary')
  hover: boolean = false;

  @Input()
  public row: any;

  @ViewChild(TemplateRef, {read: TemplateRef, static: true})
  public template: TemplateRef<void>;

  constructor(
    public readonly elementRef: ElementRef,
    public injector: Injector,
    @Optional() @SkipSelf() public tableRef: TableComponentRef,
    @Optional() @SkipSelf() public parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    if (tableRef) {
      tableRef.rowsArray.push(this);
    }
  }

  @ViewChild('tr', {static: true, read: ViewContainerRef})
  public tr: ViewContainerRef = null;

  ngOnInit() {
    super.ngOnInit();
    if (
      this.tableRef &&
      (this.tableRef.selectable ||
        this.tableRef.selectedChange.observers.length > 0)
    ) {
      this.pointer = true;
      this.tabindex = 0;
    } else {
      this.pointer = false;
      this.tabindex = null;
    }
    this.toggleHighlight(this.tableRef.selected);
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  ngAfterViewInit(): void {
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
      this.tableRef.select(this.row);
    }
  }

  public toggleHighlight(selected: any) {
    if (selected === this.row) {
      this.highlight = true;
      this.ariaSelected = true;
    } else if (TypeUtils.isArray(selected)) {
      this.highlight = (selected as Array<any>).includes(this.row);
      this.ariaSelected = this.highlight;
    } else {
      this.highlight = false;
      this.ariaSelected = false;
    }
  }

  @HostListener('keydown.enter', ['$event']) onEnterHandler(
    event: KeyboardEvent
  ) {
    if (!this.tableRef?.selectionCheckboxes) {
      event.stopPropagation();
      event.preventDefault();
      this.tableRef.select(this.row);
    }
  }
}
