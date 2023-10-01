import {
    AfterContentInit,
    AfterViewInit,
    Directive,
    EventEmitter,
    Host,
    Input,
    OnInit,
    Optional, QueryList,
    SkipSelf,
    ViewChild, ViewChildren,
    ViewContainerRef
} from '@angular/core';
import {FormComponent} from "projects/fh-forms-manager-ng/src/lib/controls/form/form.component";

/**
 * Klasa odpowiedzialna za budowę drzewa komponentów EWOP
 * Każda kontrolka powinna mieć przyporzadkowany elemnt w sekcji providers.{provide: EwopComponent, useExisting: forwardRef(() => RowComponent)}
 */
@Directive()
export class EwopComponent implements OnInit, AfterViewInit, AfterContentInit {

    @Input('id')
    public innerId: string = '';

    public get id() {
        return this.innerId;
    }

    @Input()
    public ariaLabel: string = null;

    public searchId: string = '';

    /**
     * For Input(EwopReactiveInputC) components this parameter is used as modelBinding parameter.
     */
    @Input('name')
    public name: string = "";

    public parentEwopComponent: EwopComponent = null;
    public childEwopComponents: EwopComponent[] = [];

    constructor(@Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        // super();
        this.innerId = this.constructor.name + '_' + ((Math.random() * 10000000000)).toFixed();
        this.name = this.innerId; //Prevent null/empty names - it must be set to prevent getting wrong control value for input-s without names.
        this.parentEwopComponent = parentEwopComponent;
        if (this.parentEwopComponent) {
            this.parentEwopComponent.childEwopComponents.push(this);
        }
    }

    public findEwopComponent(id: string): EwopComponent {
        let c: EwopComponent = null;

        for (const component of this.childEwopComponents) {
            if (id === component.id) {
                c = component;
            } else {
                c = component.findEwopComponent(id);
            }
            if (c) {
                break;
            }
        }

        return c;

    }

    /**
     * Funckja wyszukująca elementy po ich numerach id.
     * @param ids:string[]
     * @return EwopComponent[]
     */
    public findEwopComponents(ids: string[], names: string[], c: EwopComponent[] = []): any[] {
        // TODO: repeater moze powtarzac idki, optymalizacja, że przerywamy przeszukiwanie po znalezieniu ids.length komponentów jest błędna
        // potrzebna jest inna optymalizacja
        // select radio group ma powtorzony name dla wielu kontrolek...
        if (ids.length > 0) {
            if ((ids.includes(this.id) || ids.includes(this.searchId)) && !names.includes(this.name)) {
                names.push(this.name);
                c.push(this);
            }

            for (const component of this.childEwopComponents) {
                component.findEwopComponents(ids, names, c);
            }
        }

        return c;

    }

    public ngOnInit(): void {
        // super.ngOnInit();
        //Ucinamy
        const searchIds = this.id.split("_iteratorIndex_")
        this.searchId = searchIds[0];

    }

    @Input() subelements: any[] = [];

    ngAfterContentInit(): void {

    }

    ngAfterViewInit(): void {

    }

}

@Directive()
export abstract class EwopButtonGroupComponent {
    public initialized: boolean;

    public buttonSubcomponents: EwopComponent[] = [];

    public breadcrumbs: boolean = false;

    public activeButtonComponent: any = null;

    public activeButtonComponentIndex: number;

    public selectedButtonGroup: EventEmitter<any> = new EventEmitter<any>();

    public activeButton: number;

    abstract setActiveButton();

    abstract processActiveButton(selectedButton: any)
}
