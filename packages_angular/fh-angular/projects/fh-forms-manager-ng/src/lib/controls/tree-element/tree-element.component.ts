import {
    Component,
    EventEmitter,
    forwardRef,
    Host, HostBinding,
    Injector,
    Input,
    OnInit,
    Optional,
    Output,
    SkipSelf, TemplateRef, ViewChild
} from '@angular/core';
import {EwopHTMLElementC} from "../../models/componentClasses/EwopHTMLElementC";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";

@Component({
  selector: 'fh-tree-element',
    templateUrl: './tree-element.component.html',
    styleUrls: ['./tree-element.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
      // EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => TreeElementComponent)}
    ]
})
export class TreeElementComponent extends EwopHTMLElementC implements OnInit {


    @HostBinding('class.pl-3')
    public pl3: boolean = true;

    //@Override bottom margin.
  override mb3 = false;

    @Input()
    icon: string;

    @Input()
    children: any;

    @Input()
    expandableNode: boolean = false;

    @Input()
    public selected: boolean = false;

    collapsed: boolean;

    @Output()
    public treeElement: EventEmitter<any> = new EventEmitter<any>();

    constructor(injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);

        this.icon = this.icon ? this.icon : 'fa fa-caret-right';
        this.collapsed = true;
    }

  override ngOnInit(): void {
        super.ngOnInit();
        //Override width, this component shuld't have width set.
        this.hostWidth = "";
    }

    processTreeElementClick(): void {
        if (this.expandableNode && this.collapsed) {
            this.collapsed = false;
        } else if (this.expandableNode && !this.collapsed) {
            this.collapsed = true;
        }

        this.treeElement.emit({id: this.id, collpased: this.collapsed});
      this.fireEventWithLock('onLabelClick', "onLabelClick");
    }

  labelClicked(event) {
    event.stopPropagation();

    // if (this.nextLevelExpandable) {
    // this.changesQueue.queueAttributeChange('collapsed', this.collapsed);
    // } else {
    this.selected = !this.selected;
    // this.changesQueue.queueAttributeChange('selected', this.selected);
    // }

    // if (this.onLabelClick) {
    this.fireEventWithLock('onLabelClick', "onLabelClick");
    // }
    return false;
  };

}
