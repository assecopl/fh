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
  SkipSelf
} from '@angular/core';
import {TreeElementComponent} from "../tree-element/tree-element.component";
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {EwopHTMLElementC} from "../../models/componentClasses/EwopHTMLElementC";

@Component({
  selector: 'fh-tree',
    templateUrl: './tree.component.html',
    styleUrls: ['./tree.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
      // EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => TreeComponent)}
    ]
})
export class TreeComponent extends EwopHTMLElementC implements OnInit, OnChanges {

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

    constructor(injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);

        this.width = BootstrapWidthEnum.MD12;
        this.mb3 = false;
    }


  override ngOnInit(): void {
        super.ngOnInit();
    }

  override ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

    public processCollapsed(element: { id: string, collpased: boolean }) {
        this.activeElements[element.id] = element.collpased;
    }

    public setSelected(element: any) {
        this.selectedChange.emit(element);

    }

}
