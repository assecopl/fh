import {
    AfterContentInit,
    Component,
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
    SkipSelf
} from '@angular/core';
import {ButtonComponent} from '../button/button.component';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {DocumentedComponent} from '@ewop/ng-core';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopButtonGroupComponent, EwopComponent} from "../../models/componentClasses/EwopComponent";

@DocumentedComponent({
    category: DocumentedComponent.Category.BUTTONS_AND_OTHER,
    value: "ButtonGroup component responsible for the grouping of buttons.",
    icon: "fa fa-square"
})
@Component({
    selector: 'ewop-button-group',
    templateUrl: './button-group.component.html',
    styleUrls: ['./button-group.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => ButtonGroupComponent)},
        {provide: EwopButtonGroupComponent, useExisting: forwardRef(() => ButtonGroupComponent)}
    ]
})
export class ButtonGroupComponent extends GroupingComponentC<ButtonComponent> implements OnChanges, OnInit, AfterContentInit {

    @Input()
    public activeButton: number;

    @HostBinding('class.breadcrumbs')
    @Input()
    public breadcrumbs: boolean = false;

    public initialized: boolean = false;

    @Input()
    public selectedButton: ButtonComponent;

    public activeButtonComponent: ButtonComponent = null;

    public activeButtonComponentIndex: number;

    @Output()
    public selectedButtonGroup: EventEmitter<ButtonGroupComponent> = new EventEmitter<ButtonGroupComponent>();

    @HostBinding('class')
    public class: string;

    @HostBinding('attr.role')
    public role: string;

    public buttonSubcomponents: ButtonComponent[] = [];

    public updateSubcomponent: (subcomponent: ButtonComponent, index: number) => null;

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);

        this.width = BootstrapWidthEnum.MD12;
        this.class = 'btn-group';
        this.role = 'group';
        this.mb3 = false;
    }

    public getSubcomponentInstance(): new (...args: any[]) => ButtonComponent {
        return ButtonComponent
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
        if (changes.activeButton && this.activeButton !== changes.activeButton.currentValue) {
            this.activeButton = changes.activeButton.currentValue;
            this.setActiveButton();
        }
    }

    ngOnInit() {
        super.ngOnInit();

        //Listen to each button selected event emiter and process selection in group.
        if (this.buttonSubcomponents.length > 0) {
            if (this.breadcrumbs) {
                this.activeButton = this.buttonSubcomponents.length - 1;
            }
            this.buttonSubcomponents.forEach((button) => {
                if (this.breadcrumbs) {
                    button.bootstrapStyle = "btn-link"
                    button.breadcrumb = true;
                } else {
                    button.selectedButton.subscribe(val => {
                        this.processActiveButton(val);
                    });
                }
            });
            this.setActiveButton();
        }
        this.initialized = true;
    }

    ngAfterContentInit(): void {
        this.processButtonsWidth();
    }

    private processButtonsWidth() {
        this.buttonSubcomponents.forEach((button) => {
            button.hostWidth = button.hostWidth.replace("col-md", "md");
        })
    }

    setActiveButton() {
        if (this.activeButton < this.buttonSubcomponents.length) {
            this.activeButtonComponentIndex = this.activeButton;
            this.activeButtonComponent = this.buttonSubcomponents[this.activeButton];
            this.activeButtonComponent.active = true;
            this.buttonSubcomponents.filter(button => button != this.activeButtonComponent).forEach(button => button.active = false);
        }
    }

    public processActiveButton(selectedButton: ButtonComponent) {
        this.buttonSubcomponents.forEach((button, index) => {
            if (button.innerId === selectedButton.innerId) {
                button.active = !button.active;
                this.activeButtonComponent = button;
                this.activeButtonComponentIndex = index;
            } else {
                button.active = false;
            }
        });

        this.selectedButtonGroup.emit(this);
    }
}
