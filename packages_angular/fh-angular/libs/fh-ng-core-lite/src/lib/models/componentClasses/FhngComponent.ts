import {
  AfterContentInit,
  AfterViewInit,
  Directive,
  ElementRef,
  EventEmitter,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  SkipSelf,
  ViewChild,
} from '@angular/core';
import * as $ from 'jquery';
import {FormsManager} from "../../Socket/FormsManager";
import {IDataAttributes} from "../interfaces/IDataAttributes";
import {FHNG_CORE_CONFIG, FhNgCoreConfig} from "../../fh-ng-core.config";
import {FhngChangesComponent} from "../abstracts/FhngChangesComponent";

/**
 * Klasa odpowiedzialna za budowę drzewa komponentów FHNG
 * Każda kontrolka powinna mieć przyporzadkowany elemnt w sekcji providers.{provide: FhngComponent, useExisting: forwardRef(() => RowComponent)}
 */
@Directive()
export class FhngComponent extends FhngChangesComponent implements OnInit, AfterViewInit, AfterContentInit, OnDestroy {

  @Input() subelements: any[] = [];

  @Input() nonVisualComponents: any[] = [];

  public _data: any;
  @Input('data')
  public set data(value: any) {
    this.mapAttributes(value);
  }

  @Input()
  public id: string = '';

  public get innerId() {
    return this.id;
  }

  public set innerId(value) {
    this.id = value;
  }

  @Input()
  public formId = null;

  @Input()
  public ariaLabel: string = null;

  public searchId: string = '';

  protected formsManager: FormsManager;

  /**
   * For Input(FhngReactiveInputC) components this parameter is used as modelBinding parameter.
   */
  @Input('name')
  public name: string = '';

  public parentFhngComponent: FhngComponent = null;
  public override childFhngComponents: FhngComponent[] = [];

  protected configuration: FhNgCoreConfig = null;

  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super();
    this.innerId = this.constructor.name + '_' + (Math.random() * 10000000000).toFixed();
    this.name = this.innerId; //Prevent null/empty names - it must be set to prevent getting wrong control value for input-s without names.
    this.parentFhngComponent = parentFhngComponent;

    this.formsManager = this.injector.get(FormsManager, null);
    this.configuration = this.injector.get(FHNG_CORE_CONFIG, null) as FhNgCoreConfig;

  }

  public findFhngComponent(id: string): FhngComponent {
    let c: FhngComponent = null;

    for (const component of this.childFhngComponents) {
      if (id === component?.id) {
        c = component;
      } else {
        c = component.findFhngComponent(id);
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
   * @return FhngComponent[]
   */
  public findFhngComponents(
    ids: string[],
    names: string[],
    c: FhngComponent[] = []
  ): any[] {
    // TODO: repeater moze powtarzac idki, optymalizacja, że przerywamy przeszukiwanie po znalezieniu ids.length komponentów jest błędna
    // potrzebna jest inna optymalizacja
    // select radio group ma powtorzony name dla wielu kontrolek...
    if (ids.length > 0) {
      if (
        (ids.includes(this.id) || ids.includes(this.searchId)) &&
        !names.includes(this.name)
      ) {
        names.push(this.name);
        c.push(this);
      }

      for (const component of this.childFhngComponents) {
        component.findFhngComponents(ids, names, c);
      }
    }

    return c;
  }

  public ngOnInit(): void {
    //rejestrujemy component na liscie elementów rodzica jeżeli nie istnieje na niej.
    if (this.parentFhngComponent) {
      const idx = this.parentFhngComponent.childFhngComponents.indexOf(this);
      if (idx == -1) {
        this.parentFhngComponent.childFhngComponents.push(this);
      }
    }
    // super.ngOnInit();
    //Ucinamy
    // const searchIds = this.id.split("_iteratorIndex_")
    // this.searchId = searchIds[0];
  }

  ngOnDestroy(): void {
    if (this.parentFhngComponent) {
      const idx = this.parentFhngComponent.childFhngComponents.indexOf(this);
      if (idx > -1) {
        this.parentFhngComponent.childFhngComponents.splice(idx, 1);
      }
    }
  }

  ngAfterContentInit(): void {
  }

  ngAfterViewInit(): void {
  }

  /* Fire event to backend */

  public fireEvent(eventType, actionName, params = undefined) {
    this.fireEventImpl(eventType, actionName, false, params);
  }

  /* Fire event to backend and lock application */

  protected fireEventWithLock(eventType, actionName, params = undefined) {
    this.fireEventImpl(eventType, actionName, true, params);
  }

  protected destroyed: boolean;

  /* Fire event to backend */

  protected fireEventImpl(eventType, actionName, doLock, params = undefined) {
    if (this.destroyed) {
      return;
    }

    var deferedEvent = {
      component: this,
      deferred: $.Deferred(),
    };

    this.formsManager.eventQueue.push(deferedEvent);
    if (this.formsManager.eventQueue.length == 1) {
      deferedEvent.deferred.resolve();
    }

    var success = this.formsManager.fireEvent(
      eventType,
      actionName,
      this.formId,
      this.id,
      deferedEvent,
      doLock,
      params
    );
    if (!success) {
      this.formsManager.eventQueue.pop();
    }
  }

  public mapAttributes(data: IDataAttributes | any): void {
    //Przepisujemy wszystkie typowe parametry obiektu jeżeli istnieją na naszym obiekcie.
    //W metodach w obiektach możemy sobono przepisywać parametry których nazwy się nie pokrywają.
    let dataKeys = Object.keys(data);
    let notFindAttributes: string[] = []
    dataKeys.forEach(key => {
      if (Object.hasOwn(this, key)) {
        if (typeof this[key] == "boolean") {
          this[key] = data[key] == true || data[key] == 'true';
        } else {
          this[key] = data[key];
        }
      } else {
        if (this.configuration.debug) {
          notFindAttributes.push(key);
        }
      }
    })
    // console.log(this.id, this.constructor.name, notFindAttributes);

    this._data = data;
  }

  /**
   * Component Focus logic
   */
  @ViewChild('focusElement', {static: false}) focusElement: ElementRef;

  public focus() {
    if (this.focusElement) {
      try {
        this.focusElement.nativeElement.focus();
      } catch (e) {
        console.warn('Element ' + this.id + ' is not focusable');
      }
    }
  }

  public focusComponent(componentId: string) {
    this.childFhngComponents.forEach(component => {
      if (component.id == componentId) {
        component.focus();
      } else {
        component.focusComponent(componentId);
      }
    })
  }


  // protected fireHttpMultiPartEvent(eventType, actionName, url, data: FormData) {
  //     return this.formsManager.fireHttpMultiPartEvent(eventType, actionName, this.formId, this.id, url, data);
  // };
}

@Directive()
export abstract class FhngButtonGroupComponent {
  public initialized: boolean;

  public buttonSubcomponents: FhngComponent[] = [];

  public breadcrumbs: boolean = false;

  public activeButtonComponent: any = null;

  public activeButtonComponentIndex: number;

  public selectedButtonGroup: EventEmitter<any> = new EventEmitter<any>();

  public activeButton: number;

  abstract setActiveButton();

  abstract processActiveButton(selectedButton: any);
}
