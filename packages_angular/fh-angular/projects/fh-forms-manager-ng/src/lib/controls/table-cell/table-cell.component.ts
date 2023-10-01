import {
  Component,
  ElementRef, forwardRef, Host,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional, SimpleChanges, SkipSelf,
  ViewContainerRef
} from '@angular/core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from '../../models/componentClasses/EwopComponent';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';

@Component({
  selector: 'ewop-table-cell',
  templateUrl: './table-cell.component.html',
  styleUrls: ['./table-cell.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    EwopAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Ewop.
     */
    {provide: EwopComponent, useExisting: forwardRef(() => TableCellComponent)}
  ]

})
export class TableCellComponent extends EwopHTMLElementC implements OnChanges, OnInit {

  @HostBinding('class')
  class: string = "ewop-table-cell td";

  @Input()
  public width: string = "";

  @Input()
  public label: string = "";

  @Input()
  public value: any;

  constructor(
      public readonly elementRef: ElementRef<HTMLElement>,
      public injector: Injector,
      @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
    super(injector, parentEwopComponent)
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  ngOnInit() {
    super.ngOnInit();
  }


}
