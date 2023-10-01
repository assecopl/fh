import {
    Component,
    EventEmitter,
    forwardRef,
    Host,
    HostBinding,
    HostListener,
    Injector,
    Input,
    OnInit,
    Optional,
    Output,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";

@Component({
    selector: 'ewop-repeater',
    templateUrl: './repeater.component.html',
    styleUrls: ['./repeater.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => RepeaterComponent)}
    ]
})
export class RepeaterComponent extends EwopHTMLElementC implements OnInit {

    @HostBinding('attr.aria-selected')
    public ariaSelected: boolean = false;

    @HostBinding('class.bg-light')
    hover: boolean = false

    @HostBinding('class.text-white')
    @HostBinding('class.bg-primary')
    highlighted: boolean = false

    @HostBinding('class.text-body')
    @HostBinding('class.bg-white')
    unhighlighted: boolean = false


    @HostBinding('class.pt-2')
    pt2: boolean = true;

    @Input()
    public collection: any[] | any = [];

    @Output()
    public click: EventEmitter<any> = new EventEmitter<any>();

    @Output()
    public dbclick: EventEmitter<any> = new EventEmitter<any>();


    @Input() class: string = ''; // override the standard class attr with a new one.

    @HostBinding('class')
    get hostClasses(): any { // Override host classes. Prevent adding it to Host
        const classArray = this.class.split(" ")
        const classMap = {};
        classArray.forEach(value => {
            if (value) {
                classMap[value.trim()] = false;
            }
        })
        return classMap; //Clear Host classes.
    }

    @Input()
    public selected: any;
    @Output()
    public selectedChange = new EventEmitter<any>();

    @HostBinding('class.repeater-selectable')
    public selectable: boolean = false;

    @HostBinding('class.repeater-clickable')
    public pointer: boolean = false;

    @Input()
    public element: any;

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);
        this.mb3 = false;

    }

    ngOnInit() {
        super.ngOnInit();


        //Przywrócenie działania szerokosci elementów repeatera - teraz repeater czerpie swoją szerokość z pierwszego elementu wewnętrznego
        //i ustawia swoim dzieciom pełną szerokość.
        if (this.childEwopComponents.length == 1) {
            if (this.childEwopComponents[0]["width"] && this.width != this.childEwopComponents[0]["width"])
                // this._width = this.childEwopComponents[0]["width"]
                this.hostWidth = this.childEwopComponents[0]["hostWidth"]
            this.childEwopComponents.forEach(c => {
                c["hostWidth"] = "col-12"
            })
        }

        if (this.selectedChange.observers.length > 0) {
            this.selectable = true;
            this.unhighlighted = true;
            this.tabindex = 0;
            if (this.selected == this.element) {
                this.ariaSelected = true;
                this.highlighted = true;
                this.unhighlighted = false;
                this.hover = false;
            } else {
                this.highlighted = false;
                this.unhighlighted = true;
            }
        }
        // this.click.observers.length > 0 is always bigger then 1 ?? why
        if (this.selectable || this.click.observers.length > 0 || this.dbclick.observers.length > 0) {
            // if (this.selectable || this.dbclick.observers.length > 0) {
            this.pointer = true
        } else {
            this.pointer = false
        }


    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
        if (this.selectedChange.observers.length > 0) {
            this.selectable = true;
            this.unhighlighted = true;
            if (this.selected == this.element) {
                this.ariaSelected = true;
                this.highlighted = true;
                this.unhighlighted = false;
                this.hover = false;
            } else {
                this.highlighted = false;
                this.unhighlighted = true;
                this.ariaSelected = false;
            }
        }
        // this.click.observers.length > 0 is always bigger then 1 ?? why
        if (this.selectable || this.click.observers.length > 0 || this.dbclick.observers.length > 0) {
            // if (this.selectable || this.dbclick.observers.length > 0) {
            this.pointer = true
        } else {
            this.pointer = false
        }
    }

    @HostListener('mouseover', ['$event'])
    private mouseover(event: any) {
        if (this.selected != this.element && this.selectable) {
            this.hover = true;
            this.unhighlighted = false;
        }
    }

    @HostListener('mouseout', ['$event'])
    private mouseout(event: any) {
        if (this.selected != this.element && this.selectable) {
            this.hover = false;
            this.unhighlighted = true;
        }
    }

    select(event: any) {
        if (this.selectable) {
            this.ariaSelected = true;
            this.selectedChange.emit(this.element);
        }
    }


    @HostListener('keydown.enter', ['$event']) onEnterHandler(event: KeyboardEvent) {
        if (this.selectable) {
            event.stopPropagation();
            event.preventDefault();
            this.select(event);
        }
    }

}
