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
  public active: boolean;

  @HostBinding('class.breadcrumb-item')
  public breadcrumb: boolean = false;

  @Input()
  public disabled: boolean;

  @ViewChild('content')
  public someInput: ElementRef;

  @Output()
  public selectedButton: EventEmitter<ButtonComponent> =
    new EventEmitter<ButtonComponent>();

  public override parentFhngComponent: FhngComponent | any = null;

  public clickEventName = null;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional()
    @Host()
    @SkipSelf()
    public parentButtonGroupComponent: FhngButtonGroupComponent
  ) {
    super(injector, parentFhngComponent);

    this.bootstrapStyle = BootstrapStyleEnum.PRIMARY;
    if (this.parentButtonGroupComponent) {
      this.parentButtonGroupComponent.buttonSubcomponents.push(this);

      if (this.parentButtonGroupComponent.initialized) {
        if (this.parentButtonGroupComponent.breadcrumbs) {
          this.bootstrapStyle = 'btn-link';
          this.breadcrumb = true;
          this.parentButtonGroupComponent.activeButton =
            this.parentButtonGroupComponent.buttonSubcomponents.length - 1;
        } else {
          this.selectedButton.subscribe((val) => {
            this.parentButtonGroupComponent.processActiveButton(val);
          });
        }
        if (this.parentButtonGroupComponent.activeButton) {
          this.parentButtonGroupComponent.setActiveButton();
        }
      }
    }
  }

  public override ngOnInit() {
    super.ngOnInit();
  }

  public override ngAfterViewInit() {
  }

  public override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  @HostListener('click')
  public onSelectedButton(): void {
    this.selectedButton.emit(this);
  }

  ngOnDestroy(): void {
    if (
      this.parentButtonGroupComponent &&
      this.parentButtonGroupComponent.buttonSubcomponents
    ) {
      // this.parentButtonGroupComponent.buttonSubcomponents.removeElement(this);
    }
  }

  public override mapAttributes(data: IDataAttributes & {onClick: string, label: string}) {
    super.mapAttributes(data);

    this.label = data.label;
    this.clickEventName = data.onClick;
  }

  public onClickEvent ($event: Event): void {
    console.log('onClickEvent', $event);
    if (this.clickEventName) {
      this.fireEvent('onClick', this.clickEventName);
    }
  }
}
