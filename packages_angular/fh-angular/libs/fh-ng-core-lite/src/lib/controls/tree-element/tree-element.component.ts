import {
  Component,
  EventEmitter,
  forwardRef,
  Host,
  HostBinding, HostListener,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@Component({
  selector: '[fh-tree-element]',
  templateUrl: './tree-element.component.html',
  styleUrls: ['./tree-element.component.scss'],
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
      useExisting: forwardRef(() => TreeElementComponent),
    },
  ],
})
export class TreeElementComponent extends FhngHTMLElementC implements OnInit {

  public onIconClick: any = null;
  // @HostBinding('class.isLeaf')
  // public nextLevelExpandable: boolean = true;
  private techIconElement: HTMLSpanElement;
  private iconElement: HTMLSpanElement;
  private subTree: any;
  public onLabelClick: any = null;
  private currentTechIconClasses: any;
  private currentIconClasses: any;
  private icons: any;
  private selectable: any;
  private spanWithLabel: any;
  private ul: any;

  public customHighlightColor:string = null;



  @HostBinding('class.pl-3')
  public pl3: boolean = true;

  //@Override bottom margin.
  override mb2 = false;

  @HostBinding('class')
  get defaultClasses() {
    return {
      'list-group-item-custom': true,
      'node-treeview1': true,
      'treeNode': true
    }
  }

  @Input()
  children: any;

  @HostBinding('class.isLeaf')
  @HostBinding('class.text-underline')
  @Input()
  expandableNode: boolean = false;

  @Input()
  public selected: boolean = false;

  @Input()
  public selectedElement: any = null;

  @Input()
  collapsed: boolean = true;

  @Input()
  expanded: boolean = true;

  @HostBinding('class')
  override styleClasses = null;

  @Output()
  public treeElement: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.icon = this.icon ? this.icon : 'fa fa-caret-right';
    this.collapsed = true;
  }

  override ngOnInit(): void {
    super.ngOnInit();
    // if(!this.expanded) {
    //   this.treeElement.emit({id: this.id, collpased: this.collapsed});
    // }
  }

  public processTreeElementClick(): void {
    if (this.expandableNode && this.collapsed) {
      this.collapsed = false;
    } else if (this.expandableNode && !this.collapsed) {
      this.collapsed = true;
    }

    this.treeElement.emit({id: this.id, collpased: this.collapsed});
    if(this.onIconClick) {
      this.fireEventWithLock('onIconClick', 'onIconClick');
    }
  }

  labelClicked(event) {
    event.stopPropagation();

    if (this.expandableNode) {
      this.changesQueue.queueAttributeChange('collapsed', this.collapsed);
    } else {
        this.changesQueue.queueAttributeChange('selected', this.selected);
    }
    this.treeElement.emit({id: this.id, selected: this});
    if (this.onLabelClick) {
    this.fireEventWithLock('onLabelClick', 'onLabelClick');
    }
    return false;
  }

  public override mapAttributes(data: any) {
    super.mapAttributes(data);
  }

  getTextColor() {
    if (this.selectedElement == this) {
      return  'var(--color-main)';
    } else {
      return 'var(--color-tree-element-selected)';
    }
  }
}
