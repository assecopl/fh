import {
    AfterViewInit,
    Component,
    ComponentFactoryResolver,
    ElementRef,
    forwardRef,
    Host,
    HostBinding,
    HostListener,
    Injector,
    Input,
    OnChanges,
    OnInit,
    Optional,
    SimpleChanges,
    SkipSelf,
    TemplateRef,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {TableComponentRef} from '../table/table.ref';
import {TypeUtils} from "@ewop/ng-core";

@Component({
    selector: 'ewop-table-head-row',
    templateUrl: './table-head-row.component.html',
    styleUrls: ['./table-head-row.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => TableHeadRowComponent)}
    ]
})
export class TableHeadRowComponent extends EwopHTMLElementC implements OnInit, AfterViewInit, OnChanges {
    // @HostBinding('class.table-primary')
    highlight: boolean = false

    @ViewChild(TemplateRef, {read: TemplateRef, static: true})
    public template: TemplateRef<void>;

    constructor(public readonly elementRef: ElementRef,
                public injector: Injector,
                @Optional() @Host() @SkipSelf() public tableRef: TableComponentRef,
                @Optional() @Host() @SkipSelf() public parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent)
    }


    ngOnInit() {
        super.ngOnInit()
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit()
    }

    public toggleHighlightAll() {
        this.highlight = !this.highlight;
        this.tableRef.toggleSelectAll();
    }
}
