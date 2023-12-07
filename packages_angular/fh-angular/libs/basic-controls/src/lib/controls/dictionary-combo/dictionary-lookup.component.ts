import {Component, forwardRef, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {NgSelectConfig} from '@ng-select/ng-select';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {FhngComponent, IDataAttributes} from '@fh-ng/forms-handler';
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";

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
export class DictionaryLookupComponent extends FhngInputWithListC implements OnInit {

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
  public multiselectRawValue: string[] = [];
  protected widthRatio: number;
  protected multiselectOldValue: any;
  protected changeToFired: boolean;

  protected onInputTimer: any;
  protected openOnFocus: boolean = true;
  protected readonly onInputTimeout: number;

  private instance: any;
  private divTooltipId: any;
  private divTooltip: any;

  public dicTitle: string;

  public columns: any[] = [];
  public rows: any[] = [];
  private popupOpen: boolean = false;
  private searchRequested: boolean;
  public page: number = null;
  public pagesCount: number = null;
  public tableIsVisible : boolean = false;
  public orgRawValue: any;
  private onSearchValue:String;
  private valueHasBeenChanged: boolean;

  private isSearch: boolean = true;
  private popupColor?: string;
  private dirty: boolean = false;
  private languageWrapped: any;
  private valueFromChangedBinding: any;
  private displayOnlyCode: boolean;

  private _writingDebaunceTimer: any;
  private pageChangeClicked: boolean = false;
  private clickInPopup: boolean = false;

  public guuid: string;
  public popperId: string;


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


  public cursor: number = 0;
  //@Input()
  //public label:string;
  //
  public override values: Array<any> = [];

  public multiselect: boolean = false;

  @Input()
  public override displayFunction: any = null;

  @Input()
  public override displayExpression: any = 'lastName'; // TODO Convertery i formatter ??

  //@Input('formatter')
  //public resultFormatter = (x: string | {name: string}) => {
  //  if(typeof x === 'string'){
  //    return x
  //  } else {
  //    return x[this.displayExpresion? this.displayExpresion: "name"]
  //  }
  //};

  // public getValuesForCursor(): ComboListElement[] {
  //     let values: ComboListElement[] = [];
  //     const keys = Object.keys(this.filteredValues);
  //     keys.forEach((group, index) => {
  //         let vals = this.filteredValues[group]
  //         vals.forEach((value1, index1) => {
  //             let element: ComboListElement = Object.assign(new ComboListElement, value1);
  //
  //             element.index = index1;
  //             element.group = group;
  //             values.push(element);
  //             this.preselectValue(element);
  //         })
  //     })
  //
  //
  //     return values
  // }

  /**
   * Used to preselect values on init since we got only string data(only targetValu) form backend.
   * @param value
   * @param element
   */
    // public preselectValue(element: ComboListElement) {
    //     element.disabled = false;
    //     element.selected = false;
    //     if (!this.multiselect) {
    //         if (typeof this.rawValue === "string") {
    //             if (element.targetValue == this.rawValue) {
    //                 this.rawValue = element
    //                 element.disabled = true;
    //                 element.selected = true;
    //             }
    //         }
    //         if (this.rawValue instanceof ComboListElement) {
    //             if (this.ngSelectCompareFunction(this.rawValue, element)) {
    //                 this.rawValue = element
    //                 element.disabled = true;
    //                 element.selected = true;
    //             }
    //         }
    //     } else {
    //         if (this.rawValue instanceof Array) {
    //             if (this.multiselectRawValue.includes(element.targetValue) && !this.rawValue.includes(element)) {
    //                 this.rawValue.push(element);
    //                 element.disabled = true;
    //                 element.selected = true;
    //             }
    //         }
    //     }
    // }

  @Input('filterFunction')
  public filterFunction: (term: string, value: any) => boolean = (
    term: string,
    value: any
  ) => this.inputFormatter(value)?.toLowerCase().includes(term?.toLowerCase());

  //@Input('formatter')
  public inputFormatter = (x: ComboListElement) => {

    return x.displayAsTarget ? x.targetValue : x.displayedValue;
    ;

  };

  // protected removeItem(item: ComboListElement, event) {
  //     event.stopPropagation();
  //     event.preventDefault();
  //     const idx = this.rawValue.indexOf(item);
  //     if (idx > -1) {
  //         this.rawValue.splice(idx, 1);
  //     }
  //     this.rawValue = [...this.rawValue];
  //     this.removedIndex = this.multiselectRawValue.indexOf(item.targetValue);
  //     this.multiselectRawValue = this.rawValue.map(a => a.targetValue);
  //     this.valueChanged = true;
  //     this.addedTag = true;
  //     this.values = [...this.values] //Force object reference change to update ng-select view.
  //     this.onChangeEvent();
  // }

  override ngOnInit() {
    super.ngOnInit();
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

  // public override updateModel(selectedValue: ComboListElement | ComboListElement[]) {
  //
  //     if (!this.disabled) {
  //         this.valueChanged = true;
  //         if (selectedValue) {
  //             if (this.multiselect && selectedValue instanceof Array) {
  //                 let difference = selectedValue.filter(x => !this.multiselectRawValue.includes(x.targetValue));
  //                 if (difference.length > 0) {
  //                     this.toggleElementOnList(difference[0])
  //                     this.rawValue = selectedValue
  //                     this.selectedIndex = difference[0].index;
  //                     this.selectedIndexGroup = difference[0].group;
  //                     this.multiselectRawValue = selectedValue.map(a => a.targetValue);
  //                     this.onChangeEvent();
  //                 }
  //             } else if (selectedValue instanceof ComboListElement && this.rawValue != selectedValue) {
  //                 // this.toggleElementOnList(selectedValue)
  //                 selectedValue.disabled = true;
  //                 selectedValue.selected = true;
  //                 this.rawValue = selectedValue
  //                 this.selectedIndex = selectedValue.index;
  //                 this.selectedIndexGroup = selectedValue.group;
  //                 this.forceSendSelectedIndex = true;
  //                 this.cleared = false;
  //                 this.onChangeEvent();
  //             }
  //             this.values = [...this.values] //Force object reference change to update ng-select view.
  //         } else {
  //             this.cleared = true;
  //             this.selectedIndex = null;
  //             this.selectedIndexGroup = null;
  //         }
  //     }
  // };

  // override extractChangedAttributes() {
  //     let attrs = {
  //         blur: undefined,
  //         cleared: undefined,
  //         text: undefined,
  //         addedTag: undefined,
  //         selectedIndexGroup: undefined,
  //         selectedIndex: undefined,
  //         cursor: undefined,
  //         removedIndex: undefined
  //     };
  //     if (this.valueChanged || this.forceSendSelectedIndex) {
  //         if (this.cleared) {
  //             attrs.cleared = this.cleared
  //         } else if (this.selectedIndex != null) {
  //             attrs.selectedIndexGroup = this.selectedIndexGroup;
  //             attrs.selectedIndex = this.selectedIndex;
  //         } else if (this.removedIndex != null) {
  //             attrs.removedIndex = this.removedIndex;
  //         }
  //         attrs.addedTag = this.addedTag;
  //
  //
  //         this.addedTag = false;
  //         // this.oldValue = this.rawValue;
  //         this.multiselectOldValue = this.multiselectRawValue;
  //         this.forceSendSelectedIndex = false;
  //         this.selectedIndexGroup = null;
  //         this.selectedIndex = null;
  //         this.removedIndex = null;
  //         this.cleared = false;
  //     }
  //
  //     return attrs;
  // };

  // override onChangeEvent() {
  //     if (this.onChange && !this.disabled) {
  //         this.fireEventWithLock('onChange', this.onChange);
  //     }
  // }

  override mapAttributes(data: IDataAttributes | any) {
    super.mapAttributes(data);
    // if (data.filteredValues) {
    //     this.values = this.getValuesForCursor();
    // }
    // if (data.multiselectRawValue) {
    //     this.multiselectRawValue = JSON.parse(data.multiselectRawValue);
    //     this.rawValue = [];
    //     this.values = this.getValuesForCursor();
    // }
  }

  // public ngSelectCompareFunction(a: ComboListElement, b: ComboListElement) {
  //     return a.targetValue === b.targetValue;
  // }
  //
  // public toggleElementOnList(element: ComboListElement) {
  //     this.values.forEach(value => {
  //         if (this.ngSelectCompareFunction(element, value)) {
  //             value.disabled = !value.disabled;
  //             value.selected = !value.selected;
  //         }
  //     })
  // }

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
      this.onSelectValue = null;
    } else if (this.onPageChange) {
      attrs["command"] = "changePage";
      attrs["pageChange"] = this.onPageChange;
      this.onPageChange = null;
    } else if (this.onSearchValue != null) {
      attrs["command"] = "search";
      attrs["text"] = this.rawValue || "";
      this.onSearchValue = null;
    } else if (this.onBlurValue != null) {
      console.log("on blur - simply refresh");
    } else {
      //Some other path...
    }
    this.valueChanged = false;
    this.onBlurValue = null;
    return attrs;
  };

  override onChangeEvent() {
    if (this.onChange) {
      this.callRefresh();
    }
  }

  public onBlurValue: string
  onBlurEvent() {
    this.onBlurValue = this.rawValue;

    setTimeout(() => {
      if (this.onBlurValue != null) {
        const needRefresh =  this.tableIsVisible || this.valueHasBeenChanged;
        this.tableIsVisible = false;
        this.valueHasBeenChanged = false;
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
              this.callRefresh();//here we do loopback request, to refresh displayed value
            }
          }
        }
      }
    }, 200);

  }

  public onSelectValue: number
  onSelectedEvent(i: number) {
    this.onSelectValue = i;
    this.tableIsVisible = false;
    this.callRefresh();
  }

  public onPageValue: number
  public onPageChange: string;

  onSearchEvent() {
    if (this.tableIsVisible) {
      this.onExitSearch();
    } else {
      this.tableIsVisible = true;
      this.onSearchValue = this.rawValue || "";
      this.callRefresh();
    }
  }

  onPreviousPageEvent(){
    this.onPageChange =  "previous";
    this.callRefresh();
  }

  onNextPageEvent(){
    this.onPageChange =  "next";
    this.callRefresh();
  }

  onExitSearch(){
    this.tableIsVisible = false;
    if (this.valueHasBeenChanged){
      this.callRefresh();
    }
  }

  callRefresh(){
    this.fireEventWithLock('onChange', this.onChange);
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



