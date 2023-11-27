import {
  Component,
  ContentChild,
  EventEmitter,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
  ViewEncapsulation,
} from '@angular/core';
import {TreeElementComponent} from '../tree-element/tree-element.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';

@Component({
  selector: '[fh-tree]',
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
  ]
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

  @Input()
  public child: boolean = false;

  @ContentChild(TreeElementComponent)
  treeElement: TreeElementComponent;

  public lines:boolean = false;

  @Input()
  public selected: any;

  @Output()
  public selectedChange: EventEmitter<any> = new EventEmitter<any>();

  @HostBinding('class.d-none')
  private get hostDNoneClassDisplay () {
    return this.collapsedTree && this.treeElementTemplate !== null;
  }

  constructor(
    injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.mb2 = false;
  }

  override ngOnInit(): void {
    super.ngOnInit();
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public processCollapsed(element: any) {
    this.activeElements[element.id] = element.collpased;
    if(element.selected){
      this.selected = element.selected
      // this.subelements = [...this.subelements]
    }
    // this.selectedChange.emit(element);
  }

  public setSelected(element: any) {
    this.processCollapsed(element)

  }

  public override mapAttributes(data: any) {
    super.mapAttributes(data);

    if (data.expanded == 'true' || data.expanded == 'false' || data.expanded == true) {
      this.collapsedTree =  !(data.expanded == true || data.expanded == 'true');
    }
  }





}
