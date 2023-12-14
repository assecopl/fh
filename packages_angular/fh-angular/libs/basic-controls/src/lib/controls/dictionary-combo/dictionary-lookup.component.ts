import {
  AfterViewInit,
  Component,
  ElementRef,
  forwardRef,
  Injector,
  Input,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf, ViewChild,
} from '@angular/core';
import {NgSelectConfig} from '@ng-select/ng-select';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {FhngComponent, IDataAttributes} from '@fh-ng/forms-handler';
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";
import {NgbDropdown} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: '[dictionary-lookup]',
  templateUrl: './dictionary-lookup.component.html',
  styleUrls: ['./dictionary-lookup.component.scss'],
  providers: [

    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => DictionaryLookupComponent)},
  ],
})
export class DictionaryLookupComponent extends FhngInputWithListC implements OnInit, AfterViewInit {
  public override values: Array<any> = [];

  public cursor: number = 0;
  public multiselect: boolean = false;
  public multiselectRawValue: string[] = [];
  public dicTitle: string;
  public columns: any[] = [];
  public rows: any[] = [];
  public page: number = null;
  public pagesCount: number = null;
  public orgRawValue: any;
  public guuid: string;
  public popperId: string;
  public onBlurValue: string;
  public onPageValue: number;
  public onPageChange: string;
  public onSelectValue: number;

  @ViewChild('myDrop', { read: NgbDropdown })
  public myDrop: NgbDropdown = null;

  @Input()
  public override displayFunction: any = null;

  @Input()
  public override displayExpression: any = 'lastName'; // TODO Convertery i formatter ??

  protected autocompleter: any;
  protected selectedIndexGroup: any;
  protected selectedIndex: any;
  protected removedIndex: any;
  protected highlighted: any;
  protected forceSendSelectedIndex: any;
  protected cleared: any;
  protected addedTag: boolean = false;
  protected lastCursorPosition: any;
  protected blurEvent: any;
  protected blurEventWithoutChange: boolean;
  protected onSpecialKey: any;
  protected onDblSpecialKey: any;
  protected freeTyping: boolean;
  protected tagslist: Array<string> = [];
  protected tagsInputData: any;
  protected widthRatio: number;
  protected multiselectOldValue: any;
  protected changeToFired: boolean;
  protected onInputTimer: any;
  protected openOnFocus: boolean = true;
  protected readonly onInputTimeout: number;

  private instance: any;
  private divTooltipId: any;
  private divTooltip: any;
  private popupOpen: boolean = false;
  private searchRequested: boolean;
  private onSearchValue:String;
  private valueHasBeenChanged: boolean;
  private onRefreshValue: boolean;
  private isSearch: boolean = true;
  private popupColor?: string;
  private dirty: boolean = false;
  private languageWrapped: any;
  private valueFromChangedBinding: any;
  private displayOnlyCode: boolean;
  private _writingDebaunceTimer: any;
  private pageChangeClicked: boolean = false;
  private clickInPopup: boolean = false;
  private _tableIsVisible : boolean = false;

  public set tableIsVisible (value: boolean) {
    this._tableIsVisible = value;

    console.log('mydrop', this.myDrop)

    if (this.myDrop && value) {
      this.myDrop.open();
    } else if (this.myDrop && !value && this.myDrop.isOpen()) {
      this.myDrop.close();
    }
  }

  public get tableIsVisible (): boolean {
    return this._tableIsVisible;
  }

  constructor(
    public override injector: Injector,
    private config: NgSelectConfig,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD3;
  }

  @Input()
  public filteredValues: {
    [key: string]: []
  } = null;


  @Input('filterFunction')
  public filterFunction: (term: string, value: any) => boolean = (
    term: string,
    value: any
  ) => this.inputFormatter(value)?.toLowerCase().includes(term?.toLowerCase());

  public inputFormatter = (x: ComboListElement) => {
    return x.displayAsTarget ? x.targetValue : x.displayedValue;
  };

  public override ngOnInit() {
    super.ngOnInit();
  }

  public override ngAfterViewInit() {
    super.ngAfterViewInit();
    console.log('ngAfterViewInit', this.myDrop);
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public onClear(data: any) {
    this.cleared = true;
    this.selectedIndex = -1;
    this.selectedIndexGroup = "";
    this.rawValue = null;
    this.multiselectRawValue = null;
    this.forceSendSelectedIndex = true;
    this.onChangeEvent();
  }

  override mapAttributes(data: IDataAttributes | any) {
    super.mapAttributes(data);
    if (data.rawValue===""){
      this.rawValue = "";
    }
    // if (data.filteredValues) {
    //     this.values = this.getValuesForCursor();
    // }
    // if (data.multiselectRawValue) {
    //     this.multiselectRawValue = JSON.parse(data.multiselectRawValue);
    //     this.rawValue = [];
    //     this.values = this.getValuesForCursor();
    // }
  }

  public override updateModel(event) {
    // if (!this.disabled) {
    this.rawValue = event.target.value;
    // }
  };

  override onInputEvent(event) {
    this.updateModel(event);
    this.onSearchValue = this.rawValue;
    this.tableIsVisible = true;
    this.valueHasBeenChanged = true;
    this.fireEvent('onInput', this.onInput);
  }

  override extractChangedAttributes() {
    let attrs = {};
    if (this.onSelectValue !== null && this.onSelectValue !== undefined) {
      attrs["command"] = "selectItem";
      attrs["select"] = this.onSelectValue;
      this._resetValueChanged();
      this.onSelectValue = null;
    } else if (this.onPageChange) {
      attrs["command"] = "changePage";
      attrs["pageChange"] = this.onPageChange;
      this.onPageChange = null;
    } else if (this.onSearchValue != null) {
      attrs["command"] = "search";
      attrs["text"] = this.rawValue || "";
      this.onSearchValue = null;
    } else if (this.onRefreshValue){
      attrs["command"] = "refreshValue";
      this.onRefreshValue = false;
      this._resetValueChanged();
    } else if (this.onBlurValue != null) {
      //console.log("on blur - simply refresh");
    } else {
      //Some other path...
    }
    this.onBlurValue = null;
    return attrs;
  };

  public override onChangeEvent() {  }

  public onBlurEvent() {
    this.onBlurValue = this.rawValue;

    setTimeout(() => {
      if (this.onBlurValue != null) {
        const needRefresh =  this.tableIsVisible || this.valueHasBeenChanged;
        this.tableIsVisible = false;
        this._resetValueChanged()
        if (needRefresh) {
          if (this.rows.length == 1) {
            this.onSelectedEvent(0);
          } else {
            let index = this.rows.findIndex(row =>
              Object.values(row).some(valueInRow => valueInRow === this.rawValue) //We are looking for first elemet which has value equals to this.rawValue - usualy dictionary code will match
            );
            if (index >= 0) {
              this.onSelectedEvent(index);
            } else {
              this.onRefreshValue = true;
              this.refreshPresentedValue();//here we do loopback request, to refresh displayed value
            }
          }
        }
      }
    }, 200);

  }


  public onSelectedEvent(i: number, myDrop?: NgbDropdown): void {
    this.onSelectValue = i;
    this.tableIsVisible = false;
    this.refreshPresentedValue();
  }

  public onClearEvent(myDrop?: NgbDropdown){
    this.rawValue = "";
    this.onSelectValue = -1;
    this.refreshPresentedValue();
  }

  onSwitchSearchModeEvent(myDrop?: NgbDropdown) {
    if (this.tableIsVisible) {
      this.onCancelSearch(myDrop);
    } else {
      this.tableIsVisible = true;
      this.onSearchValue = this.rawValue || "";
      this.refreshPresentedValue();
    }
  }

  onPreviousPageEvent(){
    this.onPageChange =  "previous";
    this.fireEventWithLock('onChange', this.onChange);
  }

  onNextPageEvent(){
    this.onPageChange =  "next";
    this.fireEventWithLock('onChange', this.onChange);
  }

  public onCancelSearch(myDrop?:NgbDropdown): void {
    this.tableIsVisible = false;

    if (this.valueHasBeenChanged){
      this.onRefreshValue = true;
      this.refreshPresentedValue();
    }
  }

  public refreshPresentedValue(): void{
    this.fireEventWithLock('onChange', this.onChange);
    this._resetValueChanged();
  }

  /**
   * @param event
   * true - dla otwartego
   * false - dla zamkniętego
   */
  public onOpenChange(event: boolean ): void {
    if (!event) this.tableIsVisible = false;
  }

  private _resetValueChanged(): void {
    this.valueHasBeenChanged = false;
  }
}

export class ComboListElement {
  displayAsTarget: boolean
  targetValue: string
  targetId: number
  displayedValue: string
  index: number
  group: string
  disabled: boolean
  selected: boolean
}



