import {
  AfterViewInit,
  Component,
  EventEmitter,
  forwardRef,
  Host,
  HostListener,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
  ViewChild,
} from '@angular/core';
import {DocumentedComponent, FhngFormatter, IForm} from '@fhng/ng-core';
import {NgSelectComponent, NgSelectConfig} from '@ng-select/ng-select';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {IDictionaryProvider} from './IDictionaryProvider';
import {KodWartosciSlownika} from 'src/app/fhng/slowniki/KodWartosciSlownika';
import {compile, eval as expEval, parse} from 'expression-eval';
import {of, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value:
    'Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.',
  icon: 'fa fa-outdent',
})
@Component({
  selector: 'fhng-dictionary',
  templateUrl: './dictionary.component.html',
  styleUrls: ['./dictionary.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => DictionaryComponent),
    },
  ],
})
export class DictionaryComponent
  extends FhngReactiveInputC
  implements OnInit, AfterViewInit {
  constructor(
    public injector: Injector,
    private config: NgSelectConfig,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @SkipSelf() iForm: IForm<any>,
    protected dictionaryProvider: IDictionaryProvider<any, any>
  ) {
    super(injector, parentFhngComponent, iForm);

    this.width = BootstrapWidthEnum.MD3;
  }

  @Input()
  public updateOn: 'change' | 'blur' | 'submit' = 'change'; //Domyślnie upload plików musi aktualizować/odpisywać się od razu.

  @Input()
  public dictionaryCode: string;

  @Input()
  public parentCode: string;

  @Input()
  public effectiveDate: Date;

  @Input()
  public displayExpression: any = 'nazwa'; // TODO Convertery i formatter ??
  private displayExpressionParsed: any = null;

  @Input()
  public displayFunction: (x: any) => string;

  @Input('filterFunction')
  public filterFunction: (term: string, value: any) => boolean = (
    term: string,
    value: any
  ) => this.inputFormatter(value).toLowerCase().includes(term?.toLowerCase());

  public values: Array<any> = [];
  public valuesBuffer: Array<any> = [];

  public bufferSize = 200;
  public numberOfItemsFromEndBeforeFetchingMore = 10;
  public recordsCount: number = 0;

  public virtualScroll: boolean = false;
  public loading: boolean = false;

  public input$ = new Subject<string>();

  public model: any;

  public isOpen: boolean = false;

  @Output()
  public change: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild('ngselect') ngselect: NgSelectComponent = null;

  protected selectedIdx = null;

  @Input()
  public freeTyping: boolean = false;

  //@Input('formatter')
  public inputFormatter = (x: string | { label: string }) => {
    if (typeof x === 'string') {
      return x;
    } else if (this.displayFunction) {
      return this.displayFunction(x);
    } else {
      //Sprawdzamy czy mamy doczynienia z expression a nie pojedynczą wartoscią.
      if (this.displayExpression.toString().includes('+')) {
        return expEval(this.displayExpressionParsed, x);
      }
      return this.dictionaryProvider.displayExpresion(
        x,
        this.displayExpression
      );
    }
  };

  ngOnInit() {
    this.prepareDisplayExpresion();
    this.getData();
    this.formatter = new DictionaryFormatter(this);

    super.ngOnInit();
  }

  ngAfterViewInit() {
    super.ngAfterViewInit();
  }

  @HostListener('document:click', ['$event'])
  clickout(event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }

  toggleOptionsViewOnClick(eventTarget): void {
    if (eventTarget.classList.contains('ng-arrow-wrapper') && !this.isOpen) {
      this.isOpen = true;
    } else if (
      (eventTarget.classList.contains('ng-arrow-wrapper') ||
        eventTarget.classList.contains('ng-option')) &&
      this.isOpen
    ) {
      this.isOpen = false;
    }
  }

  toggleOptionsViewOnInput(event): void {
    if (event.term.length) {
      this.isOpen = true;
    } else {
      this.isOpen = false;
    }
  }

  closeOptionsView(): void {
    // this.change.emit(this.value);
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (
      changes['dictionaryCode'] ||
      changes['parentCode'] ||
      changes['effectiveDate']
    ) {
      const d = changes['dictionaryCode'];
      const p = changes['parentCode'];
      const e = changes['effectiveDate'];
      if (
        (p && p.currentValue != p.previousValue && !p.firstChange) ||
        (d && d.previousValue != d.currentValue && !d.firstChange) ||
        (e && e.previousValue != e.currentValue && !e.firstChange)
      ) {
        this.getData();
      }
    }
  }

  public getData() {
    if (this.dictionaryProvider && this.dictionaryCode) {
      this.loading = true;
      let response = null;
      if (this.dictionaryCode == 'SL02100') {
        response = this.dictionaryProvider.dajWartosciLight(
          this.dictionaryCode,
          this.parentCode,
          this.effectiveDate
        );
      } else {
        response = this.dictionaryProvider.dajWartosci(
          this.dictionaryCode,
          this.parentCode,
          this.effectiveDate
        );
      }
      response
        .then((value) => {
          this.values = value;
          this.recordsCount = this.values?.length;
          if (this.value) {
            /**
             * Jeżeli obiekt value jest nie pełny a na liście znalziony został element o tym samym kodzie wartości
             * to ustawiamy jako wartość startową.
             *
             */
            const val = this.values.filter((value1, index) => {
              if (value1.kodWartosci == this.value.kodWartosci) {
                this.selectedIdx = index;
                value1['selected'] = true;
              }
              return value1.kodWartosci == this.value.kodWartosci;
            });

            if (
              this.ngselect &&
              (this.value instanceof KodWartosciSlownika ||
                this.inputFormatter(this.values[this.selectedIdx]) !=
                this.inputFormatter(this.value))
            ) {
              this.ngselect.select(this.values[this.selectedIdx]);
              this.control.setValue(this.values[this.selectedIdx], {
                onlySelf: true,
              });
            }
          }
          if (this.values.length > this.bufferSize) {
            this.valuesBuffer = this.values.slice(0, this.bufferSize);
            this.virtualScroll = true;
          } else {
            this.valuesBuffer = this.values;
            this.virtualScroll = false;
          }

          this.loading = false;
        })
        .catch((reason) => {
          this.placeholder = 'Nie udało się pobrać wartosci słownika';
          this.loading = false;
          this.recordsCount = 0;
        });
      this.onSearch();
    } else {
      this.placeholder = 'Brak usługi zasilającej ';
      this.loading = false;
      this.recordsCount = 0;
    }
  }

  public freeTypingFn(v: string) {
    if (v == null) {
      return null;
    }
    return {
      kodWartosci: v,
      nazwa: v,
    };
  }

  public prepareDisplayExpresion() {
    if (this.displayExpression) {
      const de = this.displayExpression
        .replace(/{/g, '')
        .replace(/}/g, '')
        .replace(new RegExp(/\\/g), '');
      this.displayExpressionParsed = parse(de);
      this.displayFunction = compile(de);
    }
  }

  onScroll({end}, term) {
    if (this.loading || this.values.length <= this.valuesBuffer.length) {
      return;
    }

    if (
      end + this.numberOfItemsFromEndBeforeFetchingMore >=
      this.valuesBuffer.length
    ) {
      this.fetchMore(term);
    }
  }

  onSearch() {
    this.input$
      .pipe(
        debounceTime(200),
        distinctUntilChanged(),
        switchMap((term: string) => {
          let data = this.values.filter((x) =>
            term ? this.filterFunction(term, x) : x
          );
          return of(data);
        })
      )
      .subscribe((data) => {
        this.recordsCount = data.length;
        this.valuesBuffer = data.slice(0, this.bufferSize);
      });
  }

  public fetchMore(term) {
    if (this.recordsCount > this.valuesBuffer.length) {
      const len = this.valuesBuffer.length;
      // const more = this.values.slice(len, this.bufferSize + len);
      const more = this.values
        .filter((x) => (term ? this.filterFunction(term, x) : x))
        .slice(len, this.bufferSize + len);
      this.loading = true;
      // using timeout here to simulate backend API delay
      setTimeout(() => {
        this.loading = false;
        this.valuesBuffer = this.valuesBuffer.concat(more);
      }, 200);
    }
  }
}

class DictionaryFormatter extends FhngFormatter {
  private component: DictionaryComponent;

  constructor(component: DictionaryComponent) {
    super();
    this.component = component;
  }

  fromModel(v: any): string {
    if (v == null) {
      return null;
    }
    const value = this.component.values.find(
      (value) => value.kodWartosci == v.kodWartosci
    );
    if (value == null) {
      return {
        kodWartosci: v.kodWartosci,
        slownik: {
          kodSlownika: v.kodSlownika,
        },
        wersjaSlownika: v.oznaczenieWersji
          ? {oznaczenieWersji: v.oznaczenieWersji}
          : null,
        nazwa: v.nazwa,
      } as any;
    }
    return value;
  }

  toModel(v: any): any {
    if (v == null) {
      return null;
    }
    return new KodWartosciSlownika({
      kodWartosci: v.kodWartosci,
      kodSlownika: v.slownik ? this.component.dictionaryCode : null,
      oznaczenieWersji: v.wersjaSlownika?.oznaczenieWersji,
      nazwa: v.nazwa,
    });
  }
}
