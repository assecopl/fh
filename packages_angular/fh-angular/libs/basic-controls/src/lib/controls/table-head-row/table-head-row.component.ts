import {
  AfterViewInit,
  Component,
  ElementRef,
  forwardRef,
  Injector,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {TableComponentRef} from '../table/table.ref';
import {FhngComponent} from "@fh-ng/forms-handler";

@Component({
  selector: 'fhng-table-head-row',
  templateUrl: './table-head-row.component.html',
  styleUrls: ['./table-head-row.component.scss'],
  providers: [
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
    public override readonly elementRef: ElementRef,
    public override injector: Injector,
    @Optional() @SkipSelf() public tableRef: TableComponentRef,
    @Optional() @SkipSelf() public override parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  override ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  public toggleHighlightAll() {
    this.highlight = !this.highlight;
    // this.tableRef.toggleSelectAll();
  }
}
