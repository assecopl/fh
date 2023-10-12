import {
  Component,
  ContentChild,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {TreeElementComponent} from '../tree-element/tree-element.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';

@Component({
  selector: 'fh-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => TreeComponent)},
  ],
})
export class TreeComponent
  extends FhngHTMLElementC
  implements OnInit, OnChanges {
  @Input()
  public collection: any[];

  @Input()
  public relation: string;

  @Input()
  public iterator: string;

  public activeElements: any = {};

  @Input()
  public collapsedTree: boolean;

  @Input()
  public treeElementTemplate: TreeElementComponent = null;

  @ContentChild(TreeElementComponent)
  treeElement: TreeElementComponent;

  @Input()
  public selected: any;

  @Output()
  public selectedChange: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.mb3 = false;
  }

  override ngOnInit(): void {
    super.ngOnInit();
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public processCollapsed(element: { id: string; collpased: boolean }) {
    this.activeElements[element.id] = element.collpased;
  }

  public setSelected(element: any) {
    this.selectedChange.emit(element);
  }
}
