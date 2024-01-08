import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule, JsonPipe} from '@angular/common';
import {IConfig, NgxMaskDirective, NgxMaskPipe, provideNgxMask} from "ngx-mask";
import {NgbModule, NgbTooltipConfig} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {NgSelectModule} from "@ng-select/ng-select";
import {HttpClientModule} from "@angular/common/http";
import {AutosizeModule} from '@techiediaries/ngx-textarea-autosize';
import {
    ComponentManager,
    CustomActionsManager,
    EventsManager,
    FhNgFormsHandlerModule,
    FhNgModule,
    I18nService
} from "@fh-ng/forms-handler";
import { AccordionComponent } from './controls/accordion/accordion.component';
import { RowComponent } from './controls/row/row.component';
import { ButtonComponent } from './controls/button/button.component';
import { DropdownComponent } from './controls/dropdown/dropdown.component';
import { DropdownItemComponent } from './controls/dropdown-item/dropdown-item.component';
import { TreeComponent } from './controls/tree/tree.component';
import { TreeElementComponent } from './controls/tree-element/tree-element.component';
import { GroupComponent } from './controls/group/group.component';
import { RepeaterComponent } from './controls/repeater/repeater.component';
import { OutputLabelComponent } from './controls/output-label/output-label.component';
import { PanelGroupComponent } from './controls/panel-group/panel-group.component';
import { DebuggerComponent } from './debug/debuger/debugger.component';
import { TabContainerComponent } from './controls/tab-container/tab-container.component';
import { TabComponent } from './controls/tab/tab.component';
import { TableComponent } from './controls/table/table.component';
import { TableCellComponent } from './controls/table-cell/table-cell.component';
import { TableColumnComponent } from './controls/table-column/table-column.component';
import { TableHeadRowComponent } from './controls/table-head-row/table-head-row.component';
import { TableRowComponent } from './controls/table-row/table-row.component';
import { TablePagedComponent } from './controls/table-paged/table-paged.component';
import { SpacerComponent } from './controls/spacer/spacer.component';
import { InputGroupComponent } from './components/input-group/input-group.component';
import { InputLabelComponent } from './components/input-label/input-label.component';
import {CheckboxComponent } from './controls/input-checkbox/checkbox.component';
import { RadioOptionsGroupComponent } from './controls/radio-options-group/radio-options-group.component';
import { RadioOptionComponent } from './controls/radio-option/radio-option.component';
import { InputTextComponent } from './controls/input-text/input-text.component';
import { InputNumberComponent } from './controls/input-number/input-number.component';
import { InputDateComponent } from './controls/input-date/input-date.component';
import { InputTimestampComponent } from './controls/input-timestamp/input-timestamp.component';
import { ValidateMessagesComponent } from './controls/validation-messages/validation-messages.component';
import { PanelHeaderFhDPComponent } from './controls/panel-header/panel-header.component';
import { ComboComponent } from './controls/combo/combo.component';
import { SelectOneMenuComponent } from './controls/select-one-menu/select-one-menu.component';
import { ImageComponent } from './controls/image/image.component';
import { TimerComponent } from './controls/timer/timer.component';
import { FhngHasHeightDirective } from '../styles/directives/fhng-has-height.directive';
import { EmbedPageComponent } from './controls/embed-page/embed-page.component';
import {HtmlViewComponent } from './controls/html-view/html-view.component';
import { FileUploadComponent } from './controls/file-upload/file-upload.component';
import { XMLViewerFhDPComponent } from './controls/xml-viewer/xml-viewer.component';
import { FileUploadAccessorComponent } from './components/file-upload-accessor/file-upload.component';
import { DictionaryLookupComponent } from './controls/dictionary-combo/dictionary-lookup.component';
import { ButtonGroupComponent } from './controls/button-group/button-group.component';
import { HintComponent } from './components/hint/hint.component';
import { ComboPL } from './controls/combo/i18n/combo.pl';
import { UploadPL } from './controls/file-upload/i18n/upload.pl';
import { ComboEN } from './controls/combo/i18n/combo.en';
import { UploadEN } from './controls/file-upload/i18n/upload.en';
import { InputDatePL } from './controls/input-date/i18n/input-date.pl';
import { InputDateEN } from './controls/input-date/i18n/input-date.en';
import { NumberPL } from './controls/input-number/i18n/number.pl';
import { NumberEN } from './controls/input-number/i18n/number.en';
import { TextPL } from './controls/input-text/i18n/text.pl';
import { TextEN } from './controls/input-text/i18n/text.en';
import {TimestampPl} from './controls/input-timestamp/i18n/timestamp.pl';
import {TimestampEn} from './controls/input-timestamp/i18n/timestamp.en';
import {CanvasComponent} from "./controls/canvas/canvas.component";
import {DictionaryLookupPl} from "./controls/dictionary-combo/i18n/dictionary-lookup.pl";
import {DictionaryLookupEn} from "./controls/dictionary-combo/i18n/dictionary-lookup.en";
import {DictionaryLookupLt} from "./controls/dictionary-combo/i18n/dictionary-lookup.lt";
import {TablePagedPl} from "./controls/table-paged/i18n/table-paged.pl";
import {TablePagedEn} from "./controls/table-paged/i18n/table-paged.en";
import {TimestampLt} from "./controls/input-timestamp/i18n/timestamp.lt";
import {TablePagedLt} from "./controls/table-paged/i18n/table-paged.lt";
import {ComboLt} from "./controls/combo/i18n/combo.lt";
import {UploadLt} from "./controls/file-upload/i18n/upload.lt";
import {InputDateLt} from "./controls/input-date/i18n/input-date.lt";
import {NumberLt} from "./controls/input-number/i18n/number.lt";
import {TextLt} from "./controls/input-text/i18n/text.lt";
import Cookies from "js-cookie";
import {CookieInfoComponent} from "./controls/cookie-info/cookie-info.component";
import {CookieInfoPl} from "./controls/cookie-info/i18n/cookie-info.pl";
import {CookieInfoEn} from "./controls/cookie-info/i18n/cookie-info.en";
import {CookieInfoLt} from "./controls/cookie-info/i18n/cookie-info.lt";


const maskConfigFunction: () => Partial<IConfig> = () => {
  //TODO Zastanowic sie co dalej z maskami i jak je dostosowac - najprawdopodobniej zaakceptowac nowe definicje wynikające ze zaktualizowanego pluginu.
  return {
    validation: false,
    thousandSeparator: "",
    decimalMarker: ".",

  };
};

const components = [
  AccordionComponent,
  CanvasComponent,
  RowComponent,
  ButtonComponent,
  DropdownComponent,
  DropdownItemComponent,
  TreeComponent,
  TreeElementComponent,
  GroupComponent,
  RepeaterComponent,
  OutputLabelComponent,
  PanelGroupComponent,
  DebuggerComponent,
  TabContainerComponent,
  TabComponent,
  TableComponent,
  TableCellComponent,
  TableColumnComponent,
  TableHeadRowComponent,
  TableRowComponent,
  TablePagedComponent,
  SpacerComponent,
  InputGroupComponent,
  InputLabelComponent,
  CheckboxComponent,
  RadioOptionComponent,
  RadioOptionsGroupComponent,
  InputTextComponent,
  InputNumberComponent,
  InputDateComponent,
  InputTimestampComponent,
  ValidateMessagesComponent,
  PanelHeaderFhDPComponent,
  ComboComponent,
  SelectOneMenuComponent,
  ImageComponent,
  TimerComponent,
  FhngHasHeightDirective,
  EmbedPageComponent,
  HtmlViewComponent,
  XMLViewerFhDPComponent,
  FileUploadComponent,
  FileUploadAccessorComponent,
  DictionaryLookupComponent,
  ButtonGroupComponent,
  HintComponent,
  CookieInfoComponent
]




@NgModule({
    declarations: [
        components,
    ],
  schemas:[CUSTOM_ELEMENTS_SCHEMA],
  providers:[provideNgxMask(maskConfigFunction),{provide: NgbTooltipConfig}],
  imports: [FhNgFormsHandlerModule,CommonModule,
    JsonPipe,
    NgbModule,
    NgxMaskDirective,
    NgxMaskPipe,
    FormsModule,
    NgSelectModule,
    AutosizeModule,
    HttpClientModule],
  exports: components,
})
export class FhNgBasicControlsModule extends FhNgModule {


  constructor(tooltipConfig: NgbTooltipConfig, private componentManager: ComponentManager, protected eventManager: EventsManager, protected i18n: I18nService) {
    super(componentManager, eventManager);

    tooltipConfig.container = "body";
    tooltipConfig.autoClose = false;

    /**
     * Register default translations strings for components.
     */
    i18n.registerStrings('pl', ComboPL);
    i18n.registerStrings('en', ComboEN);
    i18n.registerStrings('lt', ComboLt);
    // FileUpload
    i18n.registerStrings('pl', UploadPL);
    i18n.registerStrings('en', UploadEN);
    i18n.registerStrings('lt', UploadLt);
    // InputDate
    i18n.registerStrings('pl', InputDatePL);
    i18n.registerStrings('en', InputDateEN);
    i18n.registerStrings('lt', InputDateLt);
    // InputNumber
    i18n.registerStrings('pl', NumberPL);
    i18n.registerStrings('en', NumberEN);
    i18n.registerStrings('lt', NumberLt);
    // InputText
    i18n.registerStrings('pl', TextPL);
    i18n.registerStrings('en', TextEN);
    i18n.registerStrings('lt', TextLt);
    // InputTimestamp
    i18n.registerStrings('pl', TimestampPl);
    i18n.registerStrings('en', TimestampEn);
    i18n.registerStrings('lt', TimestampLt);
    //DictionaryLookup
    i18n.registerStrings('pl', DictionaryLookupPl);
    i18n.registerStrings('en', DictionaryLookupEn);
    i18n.registerStrings('lt', DictionaryLookupLt);
    //TablePaged
    i18n.registerStrings('pl', TablePagedPl);
    i18n.registerStrings('en', TablePagedEn);
    i18n.registerStrings('lt', TablePagedLt);
    //CookieInfo
    i18n.registerStrings('pl', CookieInfoPl);
    i18n.registerStrings('en', CookieInfoEn);
    i18n.registerStrings('lt', CookieInfoLt);

    if (Cookies.get('USERLANG')) {
      i18n.selectLanguage(Cookies.get('USERLANG'));
    }
  }

  public override registerComponents(componentManager?: ComponentManager): void {
    componentManager.registerComponent(AccordionComponent);
    componentManager.registerComponent(ButtonComponent);
    componentManager?.registerComponent(CanvasComponent);
    componentManager?.registerComponentWithName("CheckBox", CheckboxComponent);
    componentManager.registerComponent(ComboComponent);
    componentManager.registerComponent(DictionaryLookupComponent);
    componentManager.registerComponentWithName("DictionaryComboFhDP", DictionaryLookupComponent);
    componentManager.registerComponent(DropdownComponent);
    componentManager.registerComponent(DropdownItemComponent);
    componentManager.registerComponent(GroupComponent);
    componentManager.registerComponent(InputDateComponent);
    componentManager.registerComponent(InputNumberComponent);
    componentManager.registerComponent(InputTextComponent);
    componentManager.registerComponent(InputTimestampComponent);
    componentManager.registerComponent(OutputLabelComponent);
    componentManager.registerComponent(PanelGroupComponent);
    componentManager.registerComponent(RadioOptionComponent);
    componentManager.registerComponent(RadioOptionsGroupComponent);
    componentManager.registerComponent(RepeaterComponent);
    componentManager.registerComponent(RowComponent);
    componentManager.registerComponent(TabComponent);
    componentManager.registerComponent(TabContainerComponent);
    componentManager.registerComponent(TreeComponent);
    componentManager.registerComponent(TreeElementComponent);
    componentManager.registerComponent(TableComponent);
    componentManager.registerComponent(TablePagedComponent);
    componentManager.registerComponent(TableColumnComponent);
    componentManager.registerComponent(TableRowComponent);
    componentManager.registerComponent(TableHeadRowComponent);
    componentManager.registerComponent(TableCellComponent);
    componentManager.registerComponent(SpacerComponent);
    componentManager.registerComponent(ValidateMessagesComponent);
    componentManager.registerComponent(PanelHeaderFhDPComponent);
    componentManager.registerComponent(SelectOneMenuComponent);
    componentManager.registerComponent(ImageComponent);
    componentManager.registerComponent(TimerComponent);
    componentManager.registerComponent(EmbedPageComponent);
    componentManager.registerComponent(HtmlViewComponent);
    componentManager.registerComponent(XMLViewerFhDPComponent);
    componentManager.registerComponent(FileUploadComponent);
    componentManager.registerComponent(ButtonGroupComponent);

    //FHDP Specific
    componentManager?.registerComponentWithName("InputTextFhDP", InputTextComponent);
    componentManager?.registerComponentWithName("InputNumberFhDP", InputNumberComponent);
    componentManager?.registerComponentWithName("TreeFhDP", TreeComponent);
    componentManager?.registerComponentWithName("TabContainerFhDP", TabContainerComponent);
    componentManager?.registerComponentWithName("InputTimestampFhDP", InputTimestampComponent);
    componentManager?.registerComponentWithName("InputDateFhDP", InputDateComponent);
    componentManager?.registerComponentWithName('CheckBoxFhDP', CheckboxComponent);
    //componentManager?.registerComponentWithName("DictionaryComboFhDP", DictionaryLookupComponent);

  }

  protected registerCustomActions(customActionsManager?: CustomActionsManager) {
  }

  protected registerEvents(eventManager?: EventsManager) {
  }
}
