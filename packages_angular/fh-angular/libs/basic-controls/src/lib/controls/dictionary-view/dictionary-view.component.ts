import {
  AfterViewInit,
  Component,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
  ViewChild,
} from '@angular/core';
import {NgSelectComponent, NgSelectConfig} from '@ng-select/ng-select';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {IDictionaryProvider} from '../dictionary/IDictionaryProvider';
import {OutputLabelComponent} from '../output-label/output-label.component';
import {FhngComponent} from "@fh-ng/forms-handler";


@Component({
  selector: 'fhng-dictionary-view',
  templateUrl: './dictionary-view.component.html',
  styleUrls: ['./dictionary-view.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => DictionaryViewComponent),
    },
  ],
})
export class DictionaryViewComponent
  extends OutputLabelComponent
  implements OnInit, AfterViewInit, OnChanges {
  constructor(
    public injector: Injector,
    private config: NgSelectConfig,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    protected dictionaryProvider: IDictionaryProvider<any, any>
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD3;
  }

  @Input('value')
  public valueSlownik: any;

  @Input()
  public dictionaryCode: string;

  @Input()
  public parentCode: string;

  @Input()
  public effectiveDate: Date;

  @Input()
  public displayExpression: any = 'nazwa'; // TODO Convertery i formatter ??

  @Input('displayFunction')
  public displayFunction(x: any) {
    return this.dictionaryProvider.displayFunction(x);
  }

  @Input('filterFunction')
  public filterFunction: (term: string, value: any) => boolean = (
    term: string,
    value: any
  ) => this.inputFormatter(value).toLowerCase().includes(term.toLowerCase());

  public values: Array<any> = [];

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
    } else if (this.displayExpression) {
      return this.dictionaryProvider.displayExpresion(
        x,
        this.displayExpression
      );
    } else {
      return this.displayFunction(x);
    }
  };

  ngOnInit() {
    this.getData();
    super.ngOnInit();
  }

  ngAfterViewInit() {
    super.ngAfterViewInit();
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (
      changes['dictionaryCode'] ||
      changes['parentCode'] ||
      changes['effectiveDate'] ||
      changes['valueSlownik']
    ) {
      const d = changes['dictionaryCode'];
      const p = changes['parentCode'];
      const e = changes['effectiveDate'];
      const v = changes['valueSlownik'];
      if (
        (p && p.currentValue != p.previousValue && !p.firstChange) ||
        (d && d.previousValue != d.currentValue && !d.firstChange) ||
        (e && e.previousValue != e.currentValue && !e.firstChange) ||
        (v && v.previousValue != v.currentValue && !v.firstChange)
      ) {
        this.getData();
      }
    }
  }

  public getData() {
    if (this.dictionaryProvider && this.dictionaryCode && this.valueSlownik) {
      this.dictionaryProvider
        .dajWartosc(
          this.valueSlownik?.kodSlownika,
          this.valueSlownik?.kodWartosci,
          this.valueSlownik?.oznaczenieWersji
        )
        .then((value) => {
          if (value) {
            this.value = this.inputFormatter(value);
          }
        })
        .catch((reason) => {
          this.value = '-';
        });
    } else {
      this.value = '';
    }
  }
}
