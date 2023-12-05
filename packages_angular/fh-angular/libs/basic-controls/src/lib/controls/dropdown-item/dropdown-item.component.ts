import {
  Component,
  EventEmitter,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BehaviorSubject, Observable, of} from "rxjs";
import {AvailabilityEnum, FhngComponent, IDataAttributes} from "@fh-ng/forms-handler";

@Component({
  selector: 'fh-dropdown-item',
  templateUrl: './dropdown-item.component.html',
  styleUrls: ['./dropdown-item.component.scss'],
  providers: [

    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => DropdownItemComponent),
    },
  ],
})
export class DropdownItemComponent extends FhngHTMLElementC implements OnInit {
  public override mb2 = false;

  @Output()
  public selectedButton$: Observable<any> = of();

  @Input()
  public url: string;

  @Input()
  public onClickEventName: string = null;

  private _selectedButton: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  @HostBinding('class.fc-editable')
  public get isEditable (): boolean {
    return this.availability == AvailabilityEnum.EDIT;
  }

  @HostBinding('class.d-none')
  public get isHidden (): boolean {
    return this.availability == AvailabilityEnum.HIDDEN;
  }

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.selectedButton$ = this._selectedButton.asObservable();
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  public onClick() {
    if (this.url) {
      window.location.href = this.url;
    }

    if (this.onClickEventName) {
      if (this.onClickEventName) {
        this.fireEventWithLock('onClick', this.onClickEventName);
      }
    }

    this._selectedButton.next(this);
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  override mapAttributes(data: IDataAttributes) {
    super.mapAttributes(data);
    this.label = data.value || this.label;
    this.onClickEventName = data.onClick || this.onClickEventName;
  }
}
