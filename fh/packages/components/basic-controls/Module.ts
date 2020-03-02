import './source/Module.css';
import {Tree} from "./source/controls/Tree/Tree";
import {TreeElement} from "./source/controls/Tree/TreeElement";
import {Group} from "./source/controls/Group";
import {InputText} from "./source/controls/Inputs/InputText";
import {SelectOneMenu} from "./source/controls/SelectOneMenu";
import {Button} from "./source/controls/Button";
import {Timer} from "./source/controls/Timer";
import {PanelGroup} from "./source/controls/PanelGroup";
import {OutputLabel} from "./source/controls/OutputLabel";
import {Spacer} from "./source/controls/Spacer";
import {Dropdown} from "./source/controls/Dropdown/Dropdown";
import {DropdownItem} from "./source/controls/Dropdown/DropdownItem";
import {TableRow} from "./source/controls/Table/TableRow";
import {Table} from "./source/controls/Table/Table";
import {Column} from "./source/controls/Table/Column";
import {TableCell} from "./source/controls/Table/TableCell";
import {TabContainer} from "./source/controls/Tabs/TabContainer";
import {Tab} from "./source/controls/Tabs/Tab";
import {Footer} from "./source/controls/Footer";
import {RadioOptionsGroup} from "./source/controls/RadioOptionsGroup";
import {TablePaged} from "./source/controls/Table/TablePaged";
import {ColumnPaged} from "./source/controls/Table/ColumnPaged";
import {FileUpload} from "./source/controls/FileUpload/FileUpload";
import {ValidateMessages} from "./source/controls/ValidateMessages";
import {FloatingGroup} from "./source/controls/FloatingGroup";
import {Canvas} from "./source/controls/Canvas";
import {Repeater} from "./source/controls/Repeater";
import {CheckBox} from "./source/controls/CheckBox";
import {Accordion} from "./source/controls/Accordion";
import {Link} from "./source/controls/Link";
import {InputNumber} from "./source/controls/Inputs/InputNumber";
import {Wizard} from "./source/controls/Tabs/Wizard";
import {Widget} from "./source/controls/Widget";
import {SplitContainer} from "./source/controls/SplitContainer";
import {OptionsList} from "./source/controls/OptionsList";
import {OptionsListElement} from "./source/controls/OptionsListElement";
import {Combo} from "./source/controls/Inputs/Combo";
import {InputDate} from "./source/controls/Inputs/InputDate";
import {InputTimestamp} from "./source/controls/Inputs/InputTimestamp";
import {Image} from "./source/controls/Image";
import {Calendar} from "./source/controls/Calendar";
import {ImageGrid} from "./source/controls/ImageGrid";
import {ButtonGroup} from "./source/controls/ButtonGroup";
import {Row} from './source/controls/Row';
import {RadioOption} from "./source/controls/RadioOption";
import {KeyboardEvent} from "./source/controls/KeyboardEvent";
import {SelectComboMenu} from "./source/controls/Inputs/SelectComboMenu";
import {FhContainer} from "fh-forms-handler";
import {MdFileViewer} from "./source/controls/MdFileViewer";
import {HtmlEditor} from "./source/controls/HtmlEditor";
import {FhModule} from "fh-forms-handler";
import {MarkdownGrid} from "./source/controls/MarkdownGrid";
import {HtmlView} from "./source/controls/HtmlView";
import {TableLazy} from "./source/controls/Table/TableLazy";
import {ColumnLazy} from "./source/controls/Table/ColumnLazy";
import {TableOptimized} from "./source/controls/Table/Optimized/TableOptimized";
import {ColumnOptimized} from "./source/controls/Table/Optimized/ColumnOptimized";
import {TableRowOptimized} from "./source/controls/Table/Optimized/TableRowOptimized";
import {TableCellOptimized} from "./source/controls/Table/Optimized/TableCellOptimized";
import {InputDateOptimized} from "./source/controls/Inputs/Optimized/InputDateOptimized";
import {SelectComboMenuOptimized} from "./source/controls/Inputs/Optimized/SelectComboMenuOptimized";
import {ThreeDotsMenuItem} from "./source/controls/Dropdown/ThreeDotsMenuItem";
import {ThreeDotsMenu} from "./source/controls/Dropdown/ThreeDotsMenu";
import { DictionaryCombo } from './source/controls/Inputs/DictionaryCombo';
import {Anchor} from "./source/controls/Anchor";
import {DropdownDivider} from "./source/controls/Dropdown/DropdownDivider";

class BasicControls extends FhModule {
    protected registerComponents() {
        FhContainer.bind<(componentObj: any, parent: any) => Tree>("Tree")
            .toFactory<Tree>(() => {
                return (componentObj: any, parent: any) => {
                    return new Tree(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TreeElement>("TreeElement")
            .toFactory<TreeElement>(() => {
                return (componentObj: any, parent: any) => {
                    return new TreeElement(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Group>("Group")
            .toFactory<Group>(() => {
                return (componentObj: any, parent: any) => {
                    return new Group(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputText>("InputText")
            .toFactory<InputText>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputText(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => SelectOneMenu>("SelectOneMenu")
            .toFactory<SelectOneMenu>(() => {
                return (componentObj: any, parent: any) => {
                    return new SelectOneMenu(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Button>("Button")
            .toFactory<Button>(() => {
                return (componentObj: any, parent: any) => {
                    return new Button(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Timer>("Timer")
            .toFactory<Timer>(() => {
                return (componentObj: any, parent: any) => {
                    return new Timer(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => PanelGroup>("PanelGroup")
            .toFactory<PanelGroup>(() => {
                return (componentObj: any, parent: any) => {
                    return new PanelGroup(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => OutputLabel>("OutputLabel")
            .toFactory<OutputLabel>(() => {
                return (componentObj: any, parent: any) => {
                    return new OutputLabel(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Spacer>("Spacer")
            .toFactory<Spacer>(() => {
                return (componentObj: any, parent: any) => {
                    return new Spacer(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Dropdown>("Dropdown")
            .toFactory<Dropdown>(() => {
                return (componentObj: any, parent: any) => {
                    return new Dropdown(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => DropdownItem>("DropdownItem")
            .toFactory<DropdownItem>(() => {
                return (componentObj: any, parent: any) => {
                    return new DropdownItem(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TableRow>("TableRow")
            .toFactory<TableRow>(() => {
                return (componentObj: any, parent: any) => {
                    return new TableRow(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Table>("Table")
            .toFactory<Table>(() => {
                return (componentObj: any, parent: any) => {
                    return new Table(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Column>("Column")
            .toFactory<Column>(() => {
                return (componentObj: any, parent: any) => {
                    return new Column(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TableCell>("TableCell")
            .toFactory<TableCell>(() => {
                return (componentObj: any, parent: any) => {
                    return new TableCell(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TabContainer>("TabContainer")
            .toFactory<TabContainer>(() => {
                return (componentObj: any, parent: any) => {
                    return new TabContainer(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Tab>("Tab")
            .toFactory<Tab>(() => {
                return (componentObj: any, parent: any) => {
                    return new Tab(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Footer>("Footer")
            .toFactory<Footer>(() => {
                return (componentObj: any, parent: any) => {
                    return new Footer(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => RadioOptionsGroup>("RadioOption")
            .toFactory<RadioOption>(() => {
                return (componentObj: any, parent: any) => {
                    return new RadioOption(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => RadioOptionsGroup>("RadioOptionsGroup")
            .toFactory<RadioOptionsGroup>(() => {
                return (componentObj: any, parent: any) => {
                    return new RadioOptionsGroup(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TablePaged>("TablePaged")
            .toFactory<TablePaged>(() => {
                return (componentObj: any, parent: any) => {
                    return new TablePaged(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TableLazy>("TableLazy")
            .toFactory<TableLazy>(() => {
                return (componentObj: any, parent: any) => {
                    return new TableLazy(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ColumnPaged>("ColumnPaged")
            .toFactory<ColumnPaged>(() => {
                return (componentObj: any, parent: any) => {
                    return new ColumnPaged(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ColumnLazy>("ColumnLazy")
            .toFactory<ColumnLazy>(() => {
                return (componentObj: any, parent: any) => {
                    return new ColumnLazy(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => FileUpload>("FileUpload")
            .toFactory<FileUpload>(() => {
                return (componentObj: any, parent: any) => {
                    return new FileUpload(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ValidateMessages>("ValidateMessages")
            .toFactory<ValidateMessages>(() => {
                return (componentObj: any, parent: any) => {
                    return new ValidateMessages(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => FloatingGroup>("FloatingGroup")
            .toFactory<FloatingGroup>(() => {
                return (componentObj: any, parent: any) => {
                    return new FloatingGroup(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Canvas>("Canvas")
            .toFactory<Canvas>(() => {
                return (componentObj: any, parent: any) => {
                    return new Canvas(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Repeater>("Repeater")
            .toFactory<Repeater>(() => {
                return (componentObj: any, parent: any) => {
                    return new Repeater(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => CheckBox>("CheckBox")
            .toFactory<CheckBox>(() => {
                return (componentObj: any, parent: any) => {
                    return new CheckBox(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Accordion>("Accordion")
            .toFactory<Accordion>(() => {
                return (componentObj: any, parent: any) => {
                    return new Accordion(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Link>("Link")
            .toFactory<Link>(() => {
                return (componentObj: any, parent: any) => {
                    return new Link(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputNumber>("InputNumber")
            .toFactory<InputNumber>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputNumber(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Wizard>("Wizard")
            .toFactory<Wizard>(() => {
                return (componentObj: any, parent: any) => {
                    return new Wizard(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Widget>("Widget")
            .toFactory<Widget>(() => {
                return (componentObj: any, parent: any) => {
                    return new Widget(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => SplitContainer>("SplitContainer")
            .toFactory<SplitContainer>(() => {
                return (componentObj: any, parent: any) => {
                    return new SplitContainer(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => OptionsList>("OptionsList")
            .toFactory<OptionsList>(() => {
                return (componentObj: any, parent: any) => {
                    return new OptionsList(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => OptionsListElement>("OptionsListElement")
            .toFactory<OptionsListElement>(() => {
                return (componentObj: any, parent: any) => {
                    return new OptionsListElement(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => DictionaryCombo>("DictionaryCombo")
            .toFactory<DictionaryCombo>(() => {
                return (componentObj: any, parent: any) => {
                    return new DictionaryCombo(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Combo>("Combo")
            .toFactory<Combo>(() => {
                return (componentObj: any, parent: any) => {
                    return new Combo(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => SelectComboMenu>("SelectComboMenu")
            .toFactory<SelectComboMenu>(() => {
                return (componentObj: any, parent: any) => {
                    return new SelectComboMenu(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputDate>("InputDate")
            .toFactory<InputDate>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputDate(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputTimestamp>("InputTimestamp")
            .toFactory<InputTimestamp>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputTimestamp(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Calendar>("Calendar")
            .toFactory<Calendar>(() => {
                return (componentObj: any, parent: any) => {
                    return new Calendar(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Image>("Image")
            .toFactory<Image>(() => {
                return (componentObj: any, parent: any) => {
                    return new Image(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ImageGrid>("ImageGrid")
            .toFactory<ImageGrid>(() => {
                return (componentObj: any, parent: any) => {
                    return new ImageGrid(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => MarkdownGrid>("MarkdownGrid")
            .toFactory<MarkdownGrid>(() => {
                return (componentObj: any, parent: any) => {
                    return new MarkdownGrid(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ButtonGroup>("ButtonGroup")
            .toFactory<ButtonGroup>(() => {
                return (componentObj: any, parent: any) => {
                    return new ButtonGroup(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ButtonGroup>("Row")
            .toFactory<Row>(() => {
                return (componentObj: any, parent: any) => {
                    return new Row(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ButtonGroup>("KeyboardEvent")
            .toFactory<KeyboardEvent>(() => {
                return (componentObj: any, parent: any) => {
                    return new KeyboardEvent(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => MdFileViewer>("MdFileViewer")
            .toFactory<MdFileViewer>(() => {
                return (componentObj: any, parent: any) => {
                    return new MdFileViewer(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => HtmlEditor>("HtmlEditor")
            .toFactory<HtmlEditor>(() => {
                return (componentObj: any, parent: any) => {
                    return new HtmlEditor(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => HtmlView>("HtmlView")
            .toFactory<HtmlView>(() => {
                return (componentObj: any, parent: any) => {
                    return new HtmlView(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => HtmlView>("ThreeDotsMenu")
            .toFactory<ThreeDotsMenu>(() => {
                return (componentObj: any, parent: any) => {
                    return new ThreeDotsMenu(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => HtmlView>("ThreeDotsMenuItem")
            .toFactory<ThreeDotsMenuItem>(() => {
                return (componentObj: any, parent: any) => {
                    return new ThreeDotsMenuItem(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => Anchor>("Anchor")
            .toFactory<Anchor>(() => {
                return (componentObj: any, parent: any) => {
                    return new Anchor(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => DropdownDivider>("DropdownDivider")
            .toFactory<DropdownDivider>(() => {
                return (componentObj: any, parent: any) => {
                    return new DropdownDivider(componentObj, parent);
                };
            });

        /**
         * --------------------------------------------------------------------------
         * ------------------ Optimized components for table in IE ------------------
         * --------------------------------------------------------------------------
         */
        FhContainer.bind<(componentObj: any, parent: any) => TableOptimized>("TableOptimized")
            .toFactory<TableOptimized>(() => {
                return (componentObj: any, parent: any) => {
                    return new TableOptimized(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TableCellOptimized>("TableCellOptimized")
            .toFactory<TableCellOptimized>(() => {
                return (componentObj: any, parent: any) => {
                    return new TableCellOptimized(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => TableRowOptimized>("TableRowOptimized")
            .toFactory<TableRowOptimized>(() => {
                return (componentObj: any, parent: any) => {
                    return new TableRowOptimized(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => InputDateOptimized>("InputDateOptimized")
            .toFactory<InputDateOptimized>(() => {
                return (componentObj: any, parent: any) => {
                    return new InputDateOptimized(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => SelectComboMenuOptimized>("SelectComboMenuOptimized")
            .toFactory<SelectComboMenuOptimized>(() => {
                return (componentObj: any, parent: any) => {
                    return new SelectComboMenuOptimized(componentObj, parent);
                };
            });
        FhContainer.bind<(componentObj: any, parent: any) => ColumnOptimized>("ColumnOptimized")
            .toFactory<ColumnOptimized>(() => {
                return (componentObj: any, parent: any) => {
                    return new ColumnOptimized(componentObj, parent);
                };
            });
    }
}

export {
    BasicControls,
    Tree,
    TreeElement,
    Group,
    InputText,
    SelectOneMenu,
    Button,
    Timer,
    PanelGroup,
    OutputLabel,
    Spacer,
    Dropdown,
    DropdownItem,
    TableRow,
    TableRowOptimized,
    Table,
    TableOptimized,
    Column,
    ColumnOptimized,
    TableCell,
    TableCellOptimized,
    TabContainer,
    Tab,
    Footer,
    RadioOptionsGroup,
    TablePaged,
    ColumnPaged,
    FileUpload,
    ValidateMessages,
    FloatingGroup,
    Canvas,
    Repeater,
    CheckBox,
    Accordion,
    Link,
    InputNumber,
    Wizard,
    Widget,
    SplitContainer,
    OptionsList,
    OptionsListElement,
    Combo,
    InputDate,
    InputTimestamp,
    Image,
    Calendar,
    ImageGrid,
    ButtonGroup,
    Row,
    RadioOption,
    KeyboardEvent,
    SelectComboMenu,
    MdFileViewer,
    HtmlEditor,
    FhModule,
    MarkdownGrid,
    HtmlView,
    DropdownDivider
};
