"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
require("./source/Module.css");
var Tree_1 = require("./source/controls/Tree/Tree");
exports.Tree = Tree_1.Tree;
var TreeElement_1 = require("./source/controls/Tree/TreeElement");
exports.TreeElement = TreeElement_1.TreeElement;
var Group_1 = require("./source/controls/Group");
exports.Group = Group_1.Group;
var InputText_1 = require("./source/controls/Inputs/InputText");
exports.InputText = InputText_1.InputText;
var SelectOneMenu_1 = require("./source/controls/SelectOneMenu");
exports.SelectOneMenu = SelectOneMenu_1.SelectOneMenu;
var Button_1 = require("./source/controls/Button");
exports.Button = Button_1.Button;
var Timer_1 = require("./source/controls/Timer");
exports.Timer = Timer_1.Timer;
var PanelGroup_1 = require("./source/controls/PanelGroup");
exports.PanelGroup = PanelGroup_1.PanelGroup;
var OutputLabel_1 = require("./source/controls/OutputLabel");
exports.OutputLabel = OutputLabel_1.OutputLabel;
var Spacer_1 = require("./source/controls/Spacer");
exports.Spacer = Spacer_1.Spacer;
var Dropdown_1 = require("./source/controls/Dropdown/Dropdown");
exports.Dropdown = Dropdown_1.Dropdown;
var DropdownItem_1 = require("./source/controls/Dropdown/DropdownItem");
exports.DropdownItem = DropdownItem_1.DropdownItem;
var TableRow_1 = require("./source/controls/Table/TableRow");
exports.TableRow = TableRow_1.TableRow;
var Table_1 = require("./source/controls/Table/Table");
exports.Table = Table_1.Table;
var Column_1 = require("./source/controls/Table/Column");
exports.Column = Column_1.Column;
var TableCell_1 = require("./source/controls/Table/TableCell");
exports.TableCell = TableCell_1.TableCell;
var TabContainer_1 = require("./source/controls/Tabs/TabContainer");
exports.TabContainer = TabContainer_1.TabContainer;
var Tab_1 = require("./source/controls/Tabs/Tab");
exports.Tab = Tab_1.Tab;
var Footer_1 = require("./source/controls/Footer");
exports.Footer = Footer_1.Footer;
var RadioOptionsGroup_1 = require("./source/controls/RadioOptionsGroup");
exports.RadioOptionsGroup = RadioOptionsGroup_1.RadioOptionsGroup;
var TablePaged_1 = require("./source/controls/Table/TablePaged");
exports.TablePaged = TablePaged_1.TablePaged;
var ColumnPaged_1 = require("./source/controls/Table/ColumnPaged");
exports.ColumnPaged = ColumnPaged_1.ColumnPaged;
var FileUpload_1 = require("./source/controls/FileUpload/FileUpload");
exports.FileUpload = FileUpload_1.FileUpload;
var ValidateMessages_1 = require("./source/controls/ValidateMessages");
exports.ValidateMessages = ValidateMessages_1.ValidateMessages;
var FloatingGroup_1 = require("./source/controls/FloatingGroup");
exports.FloatingGroup = FloatingGroup_1.FloatingGroup;
var Canvas_1 = require("./source/controls/Canvas");
exports.Canvas = Canvas_1.Canvas;
var Repeater_1 = require("./source/controls/Repeater");
exports.Repeater = Repeater_1.Repeater;
var CheckBox_1 = require("./source/controls/CheckBox");
exports.CheckBox = CheckBox_1.CheckBox;
var Accordion_1 = require("./source/controls/Accordion");
exports.Accordion = Accordion_1.Accordion;
var Link_1 = require("./source/controls/Link");
exports.Link = Link_1.Link;
var InputNumber_1 = require("./source/controls/Inputs/InputNumber");
exports.InputNumber = InputNumber_1.InputNumber;
var Wizard_1 = require("./source/controls/Tabs/Wizard");
exports.Wizard = Wizard_1.Wizard;
var Widget_1 = require("./source/controls/Widget");
exports.Widget = Widget_1.Widget;
var SplitContainer_1 = require("./source/controls/SplitContainer");
exports.SplitContainer = SplitContainer_1.SplitContainer;
var OptionsList_1 = require("./source/controls/OptionsList");
exports.OptionsList = OptionsList_1.OptionsList;
var OptionsListElement_1 = require("./source/controls/OptionsListElement");
exports.OptionsListElement = OptionsListElement_1.OptionsListElement;
var Combo_1 = require("./source/controls/Inputs/Combo");
exports.Combo = Combo_1.Combo;
var InputDate_1 = require("./source/controls/Inputs/InputDate");
exports.InputDate = InputDate_1.InputDate;
var InputTimestamp_1 = require("./source/controls/Inputs/InputTimestamp");
exports.InputTimestamp = InputTimestamp_1.InputTimestamp;
var Image_1 = require("./source/controls/Image");
exports.Image = Image_1.Image;
var Calendar_1 = require("./source/controls/Calendar");
exports.Calendar = Calendar_1.Calendar;
var ImageGrid_1 = require("./source/controls/ImageGrid");
exports.ImageGrid = ImageGrid_1.ImageGrid;
var ButtonGroup_1 = require("./source/controls/ButtonGroup");
exports.ButtonGroup = ButtonGroup_1.ButtonGroup;
var Row_1 = require("./source/controls/Row");
exports.Row = Row_1.Row;
var RadioOption_1 = require("./source/controls/RadioOption");
exports.RadioOption = RadioOption_1.RadioOption;
var KeyboardEvent_1 = require("./source/controls/KeyboardEvent");
exports.KeyboardEvent = KeyboardEvent_1.KeyboardEvent;
var SelectComboMenu_1 = require("./source/controls/Inputs/SelectComboMenu");
exports.SelectComboMenu = SelectComboMenu_1.SelectComboMenu;
var fh_forms_handler_1 = require("fh-forms-handler");
var MdFileViewer_1 = require("./source/controls/MdFileViewer");
exports.MdFileViewer = MdFileViewer_1.MdFileViewer;
var HtmlEditor_1 = require("./source/controls/HtmlEditor");
exports.HtmlEditor = HtmlEditor_1.HtmlEditor;
var fh_forms_handler_2 = require("fh-forms-handler");
exports.FhModule = fh_forms_handler_2.FhModule;
var MarkdownGrid_1 = require("./source/controls/MarkdownGrid");
exports.MarkdownGrid = MarkdownGrid_1.MarkdownGrid;
var HtmlView_1 = require("./source/controls/HtmlView");
exports.HtmlView = HtmlView_1.HtmlView;
var TableLazy_1 = require("./source/controls/Table/TableLazy");
var ColumnLazy_1 = require("./source/controls/Table/ColumnLazy");
var TableOptimized_1 = require("./source/controls/Table/Optimized/TableOptimized");
var ColumnOptimized_1 = require("./source/controls/Table/Optimized/ColumnOptimized");
var TableRowOptimized_1 = require("./source/controls/Table/Optimized/TableRowOptimized");
var TableCellOptimized_1 = require("./source/controls/Table/Optimized/TableCellOptimized");
var InputDateOptimized_1 = require("./source/controls/Inputs/Optimized/InputDateOptimized");
var SelectComboMenuOptimized_1 = require("./source/controls/Inputs/Optimized/SelectComboMenuOptimized");
var RowDropdownItem_1 = require("./source/controls/Dropdown/RowDropdownItem");
var RowDropdown_1 = require("./source/controls/Dropdown/RowDropdown");
var DictionaryCombo_1 = require("./source/controls/Inputs/DictionaryCombo");
var BasicControls = /** @class */ (function (_super) {
    __extends(BasicControls, _super);
    function BasicControls() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    BasicControls.prototype.registerComponents = function () {
        fh_forms_handler_1.FhContainer.bind("Tree")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Tree_1.Tree(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TreeElement")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TreeElement_1.TreeElement(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Group")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Group_1.Group(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("InputText")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new InputText_1.InputText(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("SelectOneMenu")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new SelectOneMenu_1.SelectOneMenu(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Button")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Button_1.Button(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Timer")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Timer_1.Timer(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("PanelGroup")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new PanelGroup_1.PanelGroup(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("OutputLabel")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new OutputLabel_1.OutputLabel(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Spacer")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Spacer_1.Spacer(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Dropdown")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Dropdown_1.Dropdown(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("DropdownItem")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new DropdownItem_1.DropdownItem(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TableRow")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TableRow_1.TableRow(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Table")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Table_1.Table(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Column")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Column_1.Column(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TableCell")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TableCell_1.TableCell(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TabContainer")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TabContainer_1.TabContainer(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Tab")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Tab_1.Tab(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Footer")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Footer_1.Footer(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("RadioOption")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new RadioOption_1.RadioOption(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("RadioOptionsGroup")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new RadioOptionsGroup_1.RadioOptionsGroup(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TablePaged")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TablePaged_1.TablePaged(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TableLazy")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TableLazy_1.TableLazy(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("ColumnPaged")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new ColumnPaged_1.ColumnPaged(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("ColumnLazy")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new ColumnLazy_1.ColumnLazy(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("FileUpload")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new FileUpload_1.FileUpload(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("ValidateMessages")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new ValidateMessages_1.ValidateMessages(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("FloatingGroup")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new FloatingGroup_1.FloatingGroup(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Canvas")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Canvas_1.Canvas(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Repeater")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Repeater_1.Repeater(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("CheckBox")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new CheckBox_1.CheckBox(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Accordion")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Accordion_1.Accordion(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Link")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Link_1.Link(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("InputNumber")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new InputNumber_1.InputNumber(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Wizard")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Wizard_1.Wizard(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Widget")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Widget_1.Widget(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("SplitContainer")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new SplitContainer_1.SplitContainer(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("OptionsList")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new OptionsList_1.OptionsList(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("OptionsListElement")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new OptionsListElement_1.OptionsListElement(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("DictionaryCombo")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new DictionaryCombo_1.DictionaryCombo(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Combo")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Combo_1.Combo(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("SelectComboMenu")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new SelectComboMenu_1.SelectComboMenu(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("InputDate")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new InputDate_1.InputDate(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("InputTimestamp")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new InputTimestamp_1.InputTimestamp(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Calendar")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Calendar_1.Calendar(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Image")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Image_1.Image(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("ImageGrid")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new ImageGrid_1.ImageGrid(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("MarkdownGrid")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new MarkdownGrid_1.MarkdownGrid(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("ButtonGroup")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new ButtonGroup_1.ButtonGroup(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("Row")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new Row_1.Row(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("KeyboardEvent")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new KeyboardEvent_1.KeyboardEvent(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("MdFileViewer")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new MdFileViewer_1.MdFileViewer(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("HtmlEditor")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new HtmlEditor_1.HtmlEditor(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("HtmlView")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new HtmlView_1.HtmlView(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("RowDropdown")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new RowDropdown_1.RowDropdown(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("RowDropdownItem")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new RowDropdownItem_1.RowDropdownItem(componentObj, parent);
            };
        });
        /**
         * --------------------------------------------------------------------------
         * ------------------ Optimized components for table in IE ------------------
         * --------------------------------------------------------------------------
         */
        fh_forms_handler_1.FhContainer.bind("TableOptimized")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TableOptimized_1.TableOptimized(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TableCellOptimized")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TableCellOptimized_1.TableCellOptimized(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("TableRowOptimized")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new TableRowOptimized_1.TableRowOptimized(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("InputDateOptimized")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new InputDateOptimized_1.InputDateOptimized(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("SelectComboMenuOptimized")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new SelectComboMenuOptimized_1.SelectComboMenuOptimized(componentObj, parent);
            };
        });
        fh_forms_handler_1.FhContainer.bind("ColumnOptimized")
            .toFactory(function () {
            return function (componentObj, parent) {
                return new ColumnOptimized_1.ColumnOptimized(componentObj, parent);
            };
        });
    };
    return BasicControls;
}(fh_forms_handler_2.FhModule));
exports.BasicControls = BasicControls;
//# sourceMappingURL=Module.js.map