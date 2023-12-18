import {
  AfterContentInit,
  Component,
  forwardRef,
  HostBinding,
  Injector,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BehaviorSubject, Observable, of} from "rxjs";
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";
import {AvailabilityEnum, FhngComponent, IDataAttributes} from "@fh-ng/forms-handler";


@Component({
  selector: 'fhng-tab',
  templateUrl: './tab.component.html',
  styleUrls: ['./tab.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => TabComponent)},
  ],
})
export class TabComponent
  extends FhngHTMLElementC
  implements OnInit, OnChanges, AfterContentInit {

  public override mb2 = false;

  public override wrapperClass = false;

  @HostBinding('id')
  public tabId: string;

  @HostBinding('class.tab-pane')
  public classTabPane: boolean = true;

  @HostBinding('class.fc-editable')
  public classFcEditable: boolean = false;

  @HostBinding('class.d-none')
  public classDNone: boolean = false;

  @HostBinding('class.active')
  public selected: boolean = false;

  @Output()
  public update$: Observable<any> = of();

  public override width: BootstrapWidthEnum = BootstrapWidthEnum.MD12;

  private _updateBehavior: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.update$ = this._updateBehavior.asObservable();
  }

  public override ngOnInit() {
    super.ngOnInit();

    if (this.id) {
      this.tabId = this.id;
    } else {
      this.tabId =
        this.constructor.name + '_' + (Math.random() * 100000000).toFixed();
    }

    this.classFcEditable = this.availability === AvailabilityEnum.EDIT;
    this.classDNone = this.availability === AvailabilityEnum.HIDDEN;
  }

  public override ngAfterContentInit(): void {
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    this.classFcEditable = this.availability === AvailabilityEnum.EDIT;
    this.classDNone = this.availability === AvailabilityEnum.HIDDEN;
  }

  public override mapAttributes(data: IDataAttributes & { selected: boolean }) {
    super.mapAttributes(data);

    if(data.selected) {
      this.selected = data.selected;
    }
    this._updateBehavior.next(data);
    this.classFcEditable = this.availability === AvailabilityEnum.EDIT;
    this.classDNone = this.availability === AvailabilityEnum.HIDDEN;
  }
}
