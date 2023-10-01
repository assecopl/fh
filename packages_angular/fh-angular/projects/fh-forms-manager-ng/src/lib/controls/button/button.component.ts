import {
    AfterViewInit,
    Component,
    ElementRef,
    EventEmitter,
    forwardRef,
    Host,
    HostBinding,
    HostListener,
    Injector,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Optional,
    Output,
    SimpleChanges,
    SkipSelf,
    ViewChild
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {BootstrapStyleEnum} from '../../models/enums/BootstrapStyleEnum';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {EwopButtonGroupComponent, EwopComponent} from "../../models/componentClasses/EwopComponent";
import {FhMLService} from "../../service/fh-ml.service";

@Component({
    selector: 'ewop-button',
    templateUrl: './button.component.html',
    styleUrls: ['./button.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        // EwopAvailabilityDirective,

        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => ButtonComponent)}
    ]
})
export class ButtonComponent extends EwopHTMLElementC implements OnInit, OnDestroy, OnChanges, AfterViewInit {

    @Input() // 'style' has conflict with html attribute
    public bootstrapStyle: string; // Wyniesc na interferjs / klase abstrakcyjną lub coś jeszcze wykmbinować

    @Input()
    public active: boolean;

    @HostBinding('class.breadcrumb-item')
    public breadcrumb: boolean = false;

    @Input()
    public disabled: boolean;

    @ViewChild('content') someInput: ElementRef;

    @Output()
    public selectedButton: EventEmitter<ButtonComponent> = new EventEmitter<ButtonComponent>();

    public override parentEwopComponent: EwopComponent | any = null;

    constructor(public override injector: Injector, private ewopml: FhMLService,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() public parentButtonGroupComponent: EwopButtonGroupComponent) {
        super(injector, parentEwopComponent);


        this.width = BootstrapWidthEnum.MD2;
        this.bootstrapStyle = BootstrapStyleEnum.PRIMARY;
        if (this.parentButtonGroupComponent) {
            this.parentButtonGroupComponent.buttonSubcomponents.push(this);

            if (this.parentButtonGroupComponent.initialized) {
                if (this.parentButtonGroupComponent.breadcrumbs) {
                    this.bootstrapStyle = "btn-link"
                    this.breadcrumb = true;
                    this.parentButtonGroupComponent.activeButton = this.parentButtonGroupComponent.buttonSubcomponents.length - 1;
                } else {
                    this.selectedButton.subscribe(val => {
                        this.parentButtonGroupComponent.processActiveButton(val);
                    });
                }
                if (this.parentButtonGroupComponent.activeButton) {
                    this.parentButtonGroupComponent.setActiveButton();
                }
            }
        }
    }

    override ngOnInit() {
        super.ngOnInit();
    }

    override ngAfterViewInit() {
    }

    override ngOnChanges(changes: SimpleChanges): void {
        super.ngOnChanges(changes);
    }

    @HostListener('click')
    onSelectedButton() {
        this.selectedButton.emit(this);
    }

    ngOn

    ngOnDestroy(): void {
        if (this.parentButtonGroupComponent && this.parentButtonGroupComponent.buttonSubcomponents) {
            // this.parentButtonGroupComponent.buttonSubcomponents.removeElement(this);
        }
    }
}
