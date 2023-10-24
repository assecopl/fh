import {
  AfterViewInit,
  Component,
  ElementRef,
  forwardRef,
  Host,
  Injector,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {TableComponentRef} from '../table/table.ref';

@Component({
  selector: 'fhng-table-head-row',
  templateUrl: './table-head-row.component.html',
  styleUrls: ['./table-head-row.component.scss'],
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
      useExisting: forwardRef(() => TableHeadRowComponent),
    },
  ],
})
export class TableHeadRowComponent
  extends FhngHTMLElementC
  implements OnInit, AfterViewInit, OnChanges {
  // @HostBinding('class.table-primary')
  highlight: boolean = false;

  @ViewChild(TemplateRef, {read: TemplateRef, static: true})
  public template: TemplateRef<void>;

  constructor(
    public readonly elementRef: ElementRef,
    public injector: Injector,
    @Optional() @SkipSelf() public tableRef: TableComponentRef,
    @Optional() @SkipSelf() public parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  public toggleHighlightAll() {
    this.highlight = !this.highlight;
    this.tableRef.toggleSelectAll();
  }
}
