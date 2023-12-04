import {
  Component,
  ContentChild,
  EventEmitter,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from "@fh-ng/forms-handler";

@Component({
  selector: '[fh-tree]',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss'],
  providers: [

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
  public treeElementTemplate: any = null;

  @Input()
  public child: boolean = false;

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
