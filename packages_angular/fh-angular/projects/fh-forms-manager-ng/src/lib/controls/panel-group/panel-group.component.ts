import {
    Component,
    EventEmitter,
    forwardRef,
    Host,
    HostBinding,
    Injector,
    Input,
    OnInit,
    Optional,
    Output,
    SecurityContext,
    SimpleChanges,
    SkipSelf,
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {DocumentedComponent, EwopML} from "@ewop/ng-core";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {DomSanitizer} from "@angular/platform-browser";

@DocumentedComponent({
    category: DocumentedComponent.Category.ARRANGEMENT,
    value: "PanelGroup component is responsible for grouping subelements with optional panel-header",
    icon: "fa fa-object-group"
})
@Component({
    selector: 'ewop-panel-group',
    templateUrl: './panel-group.component.html',
    styleUrls: ['./panel-group.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => PanelGroupComponent)}
    ]
})
export class PanelGroupComponent extends EwopHTMLElementC implements OnInit {

    width: string = BootstrapWidthEnum.MD12;

    public accordion: boolean = false;

    @Input() collapsed: boolean = false;
    @Input() collapsible: boolean = false;
    @Input() borderVisible: boolean = false;
    @Input() public iconOpened: string = "fa-chevron-down";
    @Input() public iconClosed: string = "fa-chevron-up";


    @Output()
    public headerClick: EventEmitter<boolean> = new EventEmitter<boolean>();
    public isHeaderCLickUsed: boolean = false;

    /**
     * If set to true panel-group will fill its header with specific component from its content.
     * Simply add css class ("panel-header") to component which you want to be placed inside header.
     */
    @Input() public customHeader: boolean = false;

    @HostBinding('class.mb-3')
    public mb3: boolean = true;
    @HostBinding('class.card')
    public hostCard: boolean = false;


    @Output() panelToggle = new EventEmitter<{ id: string, collapsed: boolean }>();

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent, private sanitizer: DomSanitizer) {
        super(injector, parentEwopComponent);

    }

    ngOnInit() {
        super.ngOnInit();
        let ewopml = this.injector.get(EwopML);
        this.label = ewopml.transform(this.label);
        this.processCollapsible();


        /**
         * Since EventEmiter extends Subject wchich extends Observable
         * we can check if there is any observer/subsribers.
         */
        this.isHeaderCLickUsed = this.headerClick.observers.length > 0;
    }

    processCollapsible(): void {
        if (String(this.collapsible) === 'false') {
            this.collapsible = false;
        } else if (String(this.collapsible) === 'true') {
            this.collapsible = true;
        }
    }

    collapseToggle(): void {
        this.panelToggle.emit({id: this.innerId, collapsed: !this.collapsed});
        if (this.collapsed) {
            this.expandPanelGroup();
        } else {
            this.collapsePanelGroup();
        }
    }

    collapsePanelGroup(): void {
        // podmiana ikonek
        this.collapsed = true;
    }

    expandPanelGroup(): void {
        // podmiana ikonek
        this.collapsed = false;
    }


    public ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
