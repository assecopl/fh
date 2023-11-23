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
  ViewChild,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BootstrapStyleEnum} from '../../models/enums/BootstrapStyleEnum';
import {FhngButtonGroupComponent, FhngComponent,} from '../../models/componentClasses/FhngComponent';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {BehaviorSubject, Subject, Observable, of} from "rxjs";

export interface IButtonDataAttributes extends IDataAttributes {
  active?: boolean;
  onClick: string;
  label: string;
  styleClasses?: string;
  mb2?: boolean;
  wrapperClass?: boolean;
}

@Component({
  selector: '[fhng-button]',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,

    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => ButtonComponent)},
  ],
})
export class ButtonComponent
  extends FhngHTMLElementC
  implements OnInit, OnDestroy, OnChanges, AfterViewInit {

  @Input()
  @HostBinding('class.active')
  public active: boolean = false;

  @Input()
  public disabled: boolean;

  public clickEventName = null;

  @Input()
  public override styleClasses = null;

  @HostBinding('class.breadcrumb-item')
  public breadcrumb: boolean = false;

  @HostBinding('class.button')
  public buttonClass: boolean = false;

  @HostBinding('class.form-group')
  public formGroupClass: boolean = true;

  @ViewChild('content')
  public someInput: ElementRef;

  @Output()
  public selectedButton: EventEmitter<ButtonComponent> =
    new EventEmitter<ButtonComponent>();

  @Output()
  public onUpdateButtonEvent$: Observable<any> = of();

  @Output()
  public onClickButtonEvent$: Observable<any> = of();

  private _updateButtonBehavior: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  private _clickButtonBehavior: Subject<any> = new Subject<any>();

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional()
    @SkipSelf()
    public parentButtonGroupComponent: FhngButtonGroupComponent
  ) {
    super(injector, parentFhngComponent);
    this.onUpdateButtonEvent$ = this._updateButtonBehavior.asObservable();
    this.onClickButtonEvent$ = this._clickButtonBehavior.asObservable();

    this.bootstrapStyle = BootstrapStyleEnum.PRIMARY;
  }

  public override ngOnInit() {
    super.ngOnInit();
    this.mb2 = false;
  }

  public override ngAfterViewInit() {
  }

  public override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  @HostListener('click', ['$event'])
  public onSelectedButton(event): void {
    if(this.parentButtonGroupComponent) {
      this.selectedButton.emit(this);
      this.onClickEvent(event);
    }
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  processStyleForButtonGroup() {
    if(this.parentButtonGroupComponent) {
      this.width = "";
      this.hostWidth = "btn btn-"+this.bootstrapStyle+" "+this.styleClasses;
      this.buttonClass = true;
    }
  }

  public override mapAttributes(data: IButtonDataAttributes) {
    super.mapAttributes(data);

    this.label = data.label;
    this.clickEventName = data.onClick;

    this.active = typeof data.active === 'boolean' ? data.active : this.active;
    this.wrapperClass = typeof data.wrapperClass === 'boolean' ? data.wrapperClass : this.wrapperClass;
    this.styleClasses = (data.styleClasses || '').replaceAll(',', ' ') || this.styleClasses || '';
    this.processStyleForButtonGroup();
    this._updateButtonBehavior.next(data);
  }

  public onClickEvent ($event: Event): void {
    if (this.clickEventName) {
      this.fireEvent('onClick', this.clickEventName);
    }

    this._clickButtonBehavior.next(this);
  }
}
