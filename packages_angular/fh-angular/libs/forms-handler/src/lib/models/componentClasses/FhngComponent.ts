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
  inject, Output
} from '@angular/core';
import * as $ from 'jquery';
import {FormsManager} from "../../Socket/FormsManager";
import {IDataAttributes} from "../interfaces/IDataAttributes";
import {FHNG_CORE_CONFIG, FhNgCoreConfig} from "../../fh-ng-core.config";
import {FhngChangesComponent} from "../abstracts/FhngChangesComponent";
import {HttpClient, HttpErrorResponse, HttpEvent, HttpEventType, HttpHeaders, HttpRequest} from "@angular/common/http";
import {catchError, last, map, tap} from 'rxjs/operators';
import {lastValueFrom, Observable, throwError} from "rxjs";
import {FhmlPortalManager} from "../../service/fh-ml.service";
import {AvailabilityEnum} from "../../availability/enums/AvailabilityEnum";

/**
 * Klasa odpowiedzialna za budowę drzewa komponentów FHNG
 * Każda kontrolka powinna mieć przyporzadkowany elemnt w sekcji providers.{provide: FhngComponent, useExisting: forwardRef(() => RowComponent)}
 */
@Directive()
export class FhngComponent extends FhngChangesComponent implements OnInit, AfterViewInit, AfterContentInit, OnDestroy {

  @Input() subelements: any[] = [];
  //List used wehen components are added and removed form subelements by socket communication.
  public processedSubelements: any[] = null;

  @Output() subelementsChange:EventEmitter<any[]> = new EventEmitter<any[]>();

  @Input() nonVisualComponents: any[] = [];

  public availability: AvailabilityEnum;

  /*
    FhPortal logic
   */
  public hasPortal: boolean = false;

  protected fhmlPortalManager: FhmlPortalManager = inject(FhmlPortalManager);

  public _data: any;
  @Input('data')
  public set data(value: any) {
    this.mapAttributes(value);
  }

  @Input()
  public id: string = '';

  /**
   * Definition of control Type - used for dynamic creation
   *  Type and name of control registered in ComponentManager must match.
   */
  @Input('type')
  public _type: string = null;

  public set type(value: string) {
    this._type = value;
    this.fhdp = value.includes("FhDP");
  }

  public get type() {
    return this._type;
  }

  /**
   * Is current control is variation of FhDP control.
   */
  public fhdp: boolean = false;

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

  protected destroyed: boolean;

  @Input('name')
  public name: string = '';

  public parentFhngComponent: FhngComponent = null;

  public override childFhngComponents: FhngComponent[] = [];

  protected configuration: FhNgCoreConfig = null;

  private _http: HttpClient = null;

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

    this._http = inject(HttpClient);
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
    if (this.hasPortal) {
      this.fhmlPortalManager.handleMutation();
    }
  }

  /* Fire event to backend */

  public fireEvent(eventType, actionName, params = undefined) {
    this.fireEventImpl(eventType, actionName, false, params);
  }

  public fireHttpMultiPartEvent(evnetType, actionName, url, data: FormData): Observable<HttpEvent<any>> {
    return this.fireHttpMultiPartEventImpl(evnetType, actionName, this.formId, this.id, url, data);
  }

  /* Fire event to backend and lock application */

  protected fireEventWithLock(eventType, actionName, params = undefined) {
    this.fireEventImpl(eventType, actionName, true, params);
  }

  protected fireHttpMultiPartEventImpl(eventType, actionName, formId, componentId, url, data: FormData): Observable<HttpEvent<any>> {
    const token = this._getCookieToken();

    let headers = new HttpHeaders();

    if (token) {
      headers.set('X-CSRF-TOKEN', token);
    }

    const req = new HttpRequest('POST', url, data, {
      reportProgress: true,
      withCredentials: true,
      headers: headers
    });

    return this._http.request(req).pipe(
        map(event => this._handleEvent(event)),
        catchError(this.handleError)
    );
  }

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
          this[key] = data[key] || this[key];
        }
      } else {
        if (this.configuration.debug) {
          notFindAttributes.push(key);
        }
      }
    })
    // console.log(this.id, this.constructor.name, notFindAttributes);

    if (data.type) {
      this.type = data.type;
    }

    this._data = data;
  }

  processAddedComponents(addedComponents) {
    if (addedComponents) {
      let newSubelements = []
      if (addedComponents['-']) {
        newSubelements.push(...addedComponents['-'])
      }
      this.subelements.forEach((subelement, index) => {
        newSubelements.push(subelement);
        if (addedComponents[subelement.id]) { //checks if there are components to add after subelement
          newSubelements.push(...addedComponents[subelement.id])
        }
      });
      //Update old list without reference change to prevent change mix of components when change detector fires and components are reattached.
      this.subelements.length = 0;
      this.subelements.push(...newSubelements);
      //Update new list to show data imidietly.
      this.subelements = [...newSubelements];
      // this.processedSubelements = null;
      // this.subelementsChange.emit(this.subelements);
      // this.subelementsChange.emit(this.subelements);
    }
  }

  processRemovedComponents(removedComponents: string[]) {
    if (removedComponents) {
      let newSubelements = []
      this.subelements.forEach((subelement, index) => {
        if (!removedComponents.includes(subelement.id)) { //checks if there are this component should be removed
          newSubelements.push(subelement);
        }
      });
      //Update old list without reference change to prevent change mix of components when change detector fires and components are reattached.

      this.subelements.length = 0;
      this.subelements.push(...newSubelements);
      //Update new list to show data imidietly.
      this.subelements = [...newSubelements];
      // this.processedSubelements = null;
      // this.subelementsChange.emit(this.subelements);
      // this.subelementsChange.emit(this.subelements);
    }
  }

  /**
   * Component Focus logic
   */
  @ViewChild('focusElement', {static: false}) focusElement: ElementRef;


  public focusOnInit() {
    if (this.focusElement && this.formsManager.shouldComponentRefocus(this)) {
      try {
        this.focusElement.nativeElement.focus();
      } catch (e) {
        console.warn('Element ' + this.id + ' is not focusable');
      }
    }
  }

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

  public handleError(error: HttpErrorResponse) {
    let message = `Error during sending request, status is: ${error} `;

    if (error.status == 0) {
      console.error('%c Error during sending request, status is: ',
          'background: #F00; color: #FFF', error.status);
    }

    return throwError(() => new Error(message));
  }

  private _getCookieToken(): string {
    const regExp = /XSRF-TOKEN=([a-zA-Z0-1]*)/i,
        tokenCookie = document.cookie.match(regExp);

    return tokenCookie && tokenCookie[1] ? tokenCookie[1] : null;
  }

  private _handleEvent(event: HttpEvent<any>): HttpEvent<any> {
    switch (event.type) {
      case HttpEventType.Sent:
        break;

      case HttpEventType.UploadProgress:
        break;

      case HttpEventType.Response:
        break;

      default:
    }

    return event;
  }
}

export abstract class FhngGroupComponent {
}

@Directive()
export abstract class FhngButtonGroupComponent extends FhngGroupComponent {
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
