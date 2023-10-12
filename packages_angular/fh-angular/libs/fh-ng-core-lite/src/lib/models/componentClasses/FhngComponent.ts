import {
  AfterContentInit,
  AfterViewInit,
  Directive,
  EventEmitter,
  Host,
  Injector,
  Input,
  OnInit,
  Optional,
  SkipSelf,
} from '@angular/core';
import {FormsManagerService} from '../../forms-manager.service';
import * as $ from 'jquery';

/**
 * Klasa odpowiedzialna za budowę drzewa komponentów FHNG
 * Każda kontrolka powinna mieć przyporzadkowany elemnt w sekcji providers.{provide: FhngComponent, useExisting: forwardRef(() => RowComponent)}
 */
@Directive()
export class FhngComponent implements OnInit, AfterViewInit, AfterContentInit {
  @Input('id')
  public innerId: string = '';

  public get id() {
    return this.innerId;
  }

  @Input()
  public formId = null;

  @Input()
  public ariaLabel: string = null;

  public searchId: string = '';

  protected formsManager: FormsManagerService;

  /**
   * For Input(FhngReactiveInputC) components this parameter is used as modelBinding parameter.
   */
  @Input('name')
  public name: string = '';

  public parentFhngComponent: FhngComponent = null;
  public childFhngComponents: FhngComponent[] = [];

  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    // super();
    this.innerId =
      this.constructor.name + '_' + (Math.random() * 10000000000).toFixed();
    this.name = this.innerId; //Prevent null/empty names - it must be set to prevent getting wrong control value for input-s without names.
    this.parentFhngComponent = parentFhngComponent;
    if (this.parentFhngComponent) {
      this.parentFhngComponent.childFhngComponents.push(this);
    }
    this.formsManager = this.injector.get(FormsManagerService, null);
  }

  public findFhngComponent(id: string): FhngComponent {
    let c: FhngComponent = null;

    for (const component of this.childFhngComponents) {
      if (id === component.id) {
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
    // super.ngOnInit();
    //Ucinamy
    // const searchIds = this.id.split("_iteratorIndex_")
    // this.searchId = searchIds[0];
  }

  @Input() subelements: any[] = [];

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
