import {
    AfterContentInit,
    Component,
    ContentChildren, forwardRef, Host,
    HostBinding,
    Injector,
    Input, OnChanges,
    OnInit, Optional,
    QueryList, SimpleChanges, SkipSelf,
} from '@angular/core';
import {GroupingComponentC} from "../../models/componentClasses/GroupingComponentC";
import {DropdownItemComponent} from "../dropdown-item/dropdown-item.component";
import {BootstrapStyleEnum} from "../../models/enums/BootstrapStyleEnum";
import {DocumentedComponent} from "@ewop/ng-core";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {BootstrapWidthEnum} from "./../../models/enums/BootstrapWidthEnum";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Dropdown aggregates single DropdownItem components",
    icon: "fa fa-caret-down",
})
@Component({
    selector: 'ewop-dropdown',
    templateUrl: './dropdown.component.html',
    styleUrls: ['./dropdown.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => DropdownComponent)}
    ]
})
export class DropdownComponent extends GroupingComponentC<DropdownItemComponent> implements OnInit, AfterContentInit, OnChanges {
    public width: string = BootstrapWidthEnum.MD3;

    public updateSubcomponent = null;

    public showItems: boolean;

    @ContentChildren(DropdownItemComponent)
    public subcomponents: QueryList<DropdownItemComponent> = new QueryList<DropdownItemComponent>();

    @HostBinding('class.dropdown')
    public dropdown: boolean;

    @Input()
    public bootstrapStyle: string;

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);
    }

    ngOnInit(): void {
        super.ngOnInit();

        this.width = BootstrapWidthEnum.MD2;
        this.bootstrapStyle = this.bootstrapStyle ? this.bootstrapStyle : BootstrapStyleEnum.PRIMARY;
        this.dropdown = true;
        this.showItems = false;
    }

    public getSubcomponentInstance(): new (...args: any[]) => DropdownItemComponent {
        return DropdownItemComponent
    }

    ngAfterContentInit(): void {
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }
}
