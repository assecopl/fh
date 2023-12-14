import {
  AfterViewInit,
  Component,
  EventEmitter,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from "@fh-ng/forms-handler";

@Component({
  selector: '[fh-tree-element]',
  templateUrl: './tree-element.component.html',
  styleUrls: ['./tree-element.component.scss'],
  providers: [

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
export class TreeElementComponent extends FhngHTMLElementC implements OnInit, AfterViewInit {

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

  @HostBinding('id')
  @Input()
  public override id: string = '';

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
  public collapsed: boolean = true;

  @Input()
  expanded: boolean = true;

  @HostBinding('class')
  override styleClasses = null;

  @Input()
  public tree: any = null; //TODO Add Interface/abstract class for TreeComponent to prevent a cycle while injection

  @Output()
  public treeElement: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
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
     event? event.stopPropagation():null;
    if (!this.selectable || this.collapsed) {
      if(!this.fhdp) {
        this.processTreeElementClick();
      }
    }

    // if (this.expandableNode) {
      this.changesQueue.queueAttributeChange('collapsed', this.collapsed);
    // } else {
        this.changesQueue.queueAttributeChange('selected', this.selected);
    // }
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

  /**
   * Fhdp Functionality
   * //TODO This should be removed
   */
  public override ngAfterViewInit(): void {
    this.processFhdDPinit();
  }


  private expandedException:string = null;

  public processFhdDPinit(){
    if (this.expandedException && this.fhdp) {
      const exceptions = this.expandedException.split('|');
      for (const ex of exceptions) {
        if (this.label && this.label.includes(ex)) {
          setTimeout(() => {
            // if(!this.collapsed)
            this.processTreeElementClick();
          }, 0);
        }
      }

      if(this.accessibility !== 'HIDDEN') {
        let regex = /[a-zA-Z_0-9]+\[0\]/;
        if (this.id.endsWith('[0]')) {
          let match = this.id.match(regex);
          if (match && match.length && match[0] === this.id) {
            setTimeout(() => {
              // if(!this.collapsed)
              this.labelClicked(null)
            }, 0);
            // this.setCurrent(true);

            // this.selectBranch(document.getElementById(this.id));
          }
        }
      }
    }
  }

  selectBranch(branchToSelect) {
    branchToSelect.children[0].children[0].click();
    if ((branchToSelect.children[0].children[0].children[0] as HTMLElement).classList.contains('fa-caret-right')) {
      (branchToSelect.children[0].children[0].children[0] as HTMLElement).click();
    }
  }
}
