package pl.fhframework.model.forms.optimized;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.core.forms.IterationContext;
import pl.fhframework.core.forms.iterators.IMultipleIteratorComponentFactory;
import pl.fhframework.core.forms.iterators.IMultipleIteratorRepeatable;
import pl.fhframework.core.forms.iterators.IRepeatableIteratorInfo;
import pl.fhframework.forms.ICompilerAwareComponent;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.table.AdHocTableColumnOptimizedComponentFactory;
import pl.fhframework.model.forms.table.LowLevelRowMetadata;
import pl.fhframework.model.forms.table.RowIteratorMetadata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * ColumnOptimized is component grouping together other components wrapped as TableCellOptimized component. It is used
 * on server side to construct columns of TableOptimized components and has is not processed by javascript
 * directly.
 */
@DesignerControl(defaultWidth = -1)
@Control(parents = {TableOptimized.class, ColumnOptimized.class}, invalidParents = {TablePaged.class, ColumnPaged.class})
@OverridenPropertyAnnotations(
        designerXmlProperty = @DesignerXMLProperty(readOnlyInDesigner = false, functionalArea = LOOK_AND_STYLE, priority = 100),
        property = "width"
)
@OverridenPropertyAnnotations(
        designerXmlProperty = @DesignerXMLProperty(skip = true),
        property = "height"
)
@DocumentedComponent(documentationExample = true, value = "It is used to construct columns of TableOptimized components.", icon = "fa fa-columns")
public class ColumnOptimized extends GroupingComponent<FormElement> implements CompactLayout, IMultipleIteratorRepeatable<TableOptimized>, ICompilerAwareComponent, IHasBoundableLabel {

    public static final String ATTR_LABEL = "label";
    public static final String ATTR_VALUE = "value";
    public static final String AUTOMATIC_LABEL_ID_SUFFIX = "_value_based_label";

    @JsonIgnore
    @XMLMetadataSubelementParent
    protected TableOptimized table;

    @Getter
    private String label;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_LABEL)
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT, priority = 30, allowedTypes = String.class,
            previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    @DocumentedComponentAttribute(boundable = true, value = "Component label. Supports FHML - FH Markup Language.")
    private ModelBinding labelModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Id of formatter which will format object to String. It must be consistend with value of pl.fhframework.formatter.FhFormatter annotation.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 90)
    private String formatter;

    /**
     * This is value binding
     */
    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty(value = ATTR_VALUE, skipCompiler = true)
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT, priority = 20, previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    @DocumentedComponentAttribute("Represents text value for created component.")
    private ModelBinding value;

    @Getter
    @Setter
    private Integer rowspan;

    @Getter
    @Setter
    private Boolean fixedHeader;

    @JsonIgnore
    @Getter
    @Setter
    private Integer level;

    @Getter
    @Setter
    private boolean subColumnsExists;

    @Getter
    @Setter
    @XMLProperty
    private String iterationRef;

    @JsonIgnore
    private List<IRepeatableIteratorInfo> iteratorInfos;

    /**
     * Prototype cell, which will be repeated for rows
     */
    @JsonIgnore
    @Getter
    @Setter
    private TableCellOptimized prototype;

    @JsonIgnore
    @Setter
    protected IMultipleIteratorComponentFactory<TableOptimized> interatorComponentFactory;

    @JsonIgnore
    private OutputLabel implicitOutputLabel;

    public ColumnOptimized(Form form) {
        super(form);
        prototype = new TableCellOptimized(form);
        prototype.setArtificial(true);
        prototype.setGroupingParentComponent(this);
        interatorComponentFactory = new AdHocTableColumnOptimizedComponentFactory(this);
    }

    public void init() {
        // TableOptimized does this for every column while repeater runs init for every component - doubles init on columns
        if (isInitDone()) {
            return;
        }

        if (getWidth() != null && getWidth().endsWith("%")) {
            setWidth(getWidth().substring(0, getWidth().length() - 1));
        }

        // subcolumns exist
        subColumnsExists = false;
        if (getSubcomponents().stream().anyMatch(c -> c instanceof ColumnOptimized)) {
            subColumnsExists = true;
        }

        super.init();
        if (getGroupingParentComponent() instanceof ColumnOptimized) {
            setLevel(((ColumnOptimized) getGroupingParentComponent()).getLevel() + 1);
        } else {
            setLevel(1);
        }
        refreshImplicitOutputLabel();
        if (labelModelBinding != null) {
            BindingResult bindingResult = labelModelBinding.getBindingResult();
            if (bindingResult != null) {
                label = (String) bindingResult.getValue();
            }
        }
        prototype.init();
        prototype.doActionForEverySubcomponent(c -> c.init());
        this.fixedHeader = this.table.fixedHeader;
    }

    @Override
    public void beforeCompilation() {
        refreshImplicitOutputLabel();
    }

    private void refreshImplicitOutputLabel() {
        if (implicitOutputLabel != null) {
            getPrototype().removeSubcomponent(implicitOutputLabel);
        }
        if (value != null) {
            implicitOutputLabel = new OutputLabel(getForm());
            implicitOutputLabel.setValueBinding(value.clone(implicitOutputLabel));
            if (getId() != null) {
                implicitOutputLabel.setId(getId() + AUTOMATIC_LABEL_ID_SUFFIX);
            }
            implicitOutputLabel.setArtificial(true);
            implicitOutputLabel.setWidth("md-12");

            getPrototype().getSubcomponents().add(implicitOutputLabel);
            implicitOutputLabel.setGroupingParentComponent(getPrototype());
            implicitOutputLabel.setFormatter(formatter);
        }
    }

    @Override
    public void addSubcomponent(FormElement formElement) {
        if (formElement instanceof ColumnOptimized) {
            if (!getPrototype().getSubcomponents().isEmpty() || getValue() != null) {
                throw new FhFormException("ColumnOptimized cannot have both form elements (or value) and subcolumns");
            }
            super.addSubcomponent(formElement);
        } else {
            if (!getSubcolumns().isEmpty()) {
                throw new FhFormException("ColumnOptimized cannot have both form elements and subcolumns");
            }
            getPrototype().getSubcomponents().add(formElement);
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        BindingResult bindingResult = labelModelBinding != null ? labelModelBinding.getBindingResult() : null;
        if (bindingResult != null) {
            String newLabelValue = this.convertBindingValueToString(bindingResult);

            if (!areValuesTheSame(newLabelValue, label)) {
                this.refreshView();
                this.label = newLabelValue;
                elementChanges.addChange(ATTR_LABEL, this.label);
            }
        }

        return elementChanges;
    }

    @Override
    public List<IRepeatableIteratorInfo> getIteratorInfos() {
        // init once
        if (iteratorInfos == null || getForm().getViewMode() != Form.ViewMode.NORMAL) {
            List<IRepeatableIteratorInfo> iterators = new ArrayList<>();
            for (IRepeatableIteratorInfo iterator : getTable().getAllIterators()) {
                iterators.add(iterator);

                // stop on column's iterator
                if (iterationRef == null // no iterationRef -> main interator (add first only)
                        || iterator.getName().equals(iterationRef)) {
                    break;
                }
            }
            iteratorInfos = iterators;
        }
        return iteratorInfos;
    }

    @Override
    public List<Component> getRepeatedComponents() {
        return (List) getPrototype().getSubcomponents();
    }

    @Override
    public boolean isComponentFactorySupported() {
        // only leaf columns can have original subcomponents and use component factory
        return getSubcolumns().isEmpty();
    }

    @Override
    public TableOptimized getGroupingComponentForNewComponents() {
        return getTable();
    }

    @Override
    public Component getIteratorDefiningComponent() {
        return getTable();
    }

    @JsonIgnore
    public List<ColumnOptimized> getSubcolumns() {
        return getSubcomponents().stream().filter(subComponent -> subComponent instanceof ColumnOptimized).map(comp -> (ColumnOptimized) comp).collect(Collectors.toList());
    }

    public TableOptimized getTable() {
        if (table == null) {
            table = ((ColumnOptimized) getGroupingParentComponent()).getTable();
        }
        return table;
    }

    public void setTable(TableOptimized table) {
        this.table = table;
        this.iteratorInfos = null;
    }

    private TableCellOptimized createCell(LowLevelRowMetadata lowLevelRowMetadata) {
        TableCellOptimized tableCell = createEmptyCell();

        RowIteratorMetadata mainTableIteratorMetadata = lowLevelRowMetadata.getIteratorData().get(getTable().getIterator());
        final int mainTableIteratorIndex = mainTableIteratorMetadata.getIndex();

        // call fatory to produce cell components
        List<FormElement> cellComponents = interatorComponentFactory.createComponentsForIterators(
                getTable(), getTable(), lowLevelRowMetadata.getIteratorsIndices());

        for (FormElement cellComponent : cellComponents) {
            getForm().addToElementIdToFormElement(cellComponent);
            getTable().getBindedSubcomponents().add(new IterationContext(mainTableIteratorIndex, cellComponent));
            tableCell.getSubcomponents().add(cellComponent);
            cellComponent.setGroupingParentComponent(tableCell);
        }
        return tableCell;
    }

    public List<FormElement> createTableCells(LowLevelRowMetadata lowLevelRowMetadata) {
        return createTableCells(this, lowLevelRowMetadata);
    }

    private List<FormElement> createTableCells(ColumnOptimized columnOrSubcolumn, LowLevelRowMetadata lowLevelRowMetadata) {
        List<FormElement> tableCells = new ArrayList<>();
        List<ColumnOptimized> subColumns = columnOrSubcolumn.getSubcolumns();

        if (subColumns.size() == 0) {
            if (table.getForm().getViewMode() == Form.ViewMode.NORMAL) {
                createTableCell(columnOrSubcolumn, lowLevelRowMetadata, getTable().getColumns()).ifPresent(tableCells::add);
            } else if (table.getForm().getForm().getViewMode() == Form.ViewMode.PREVIEW) { // designer edit mode not supported yet
                createTableCellForDesigner(columnOrSubcolumn, lowLevelRowMetadata, getTable().getColumns()).ifPresent(tableCells::add);
            }
        } else {
            for (ColumnOptimized subColumnElement : subColumns) {
                tableCells.addAll(createTableCells(subColumnElement, lowLevelRowMetadata));
            }
        }
        return tableCells;
    }

    private Optional<FormElement> createTableCell(ColumnOptimized column, LowLevelRowMetadata lowLevelRowMetadata, List<ColumnOptimized> columns) {
        column.setRowspan(getMaxColumnDepthForLevel(column.getLevel(), columns));
        // get row iterator
        RowIteratorMetadata rowIteratorMetadata = getRowIteratorMetadata(lowLevelRowMetadata, column.getIterationRef());

        TableCellOptimized tableCell = null;
        if (rowIteratorMetadata != null) {
            // if it is first occurrence of TableCellOptimized (used in case of rowSpan)
            if (rowIteratorMetadata.isFirstOccurrence()) {
                tableCell = column.createCell(lowLevelRowMetadata);
                tableCell.setRowspan(rowIteratorMetadata.getRowSpan().get());
            }
        } else {
            // if there's no iterator for that row, it means that there's no value for bindings (there's no mapped binding)
            // therefore you have to create empty OutputLabel form component to emulate displaying empty data
            tableCell = createEmptyCell();
            OutputLabel emptyLabel = createEmptyOutputLabel();
            emptyLabel.setGroupingParentComponent(tableCell);
            tableCell.addSubcomponent(emptyLabel);
        }

        if (tableCell != null) {
            tableCell.init();
            tableCell.setProcessComponentStateChange(false);
            tableCell.setGroupingParentComponent(column);
        }

        return Optional.ofNullable(tableCell);
    }

    private Optional<FormElement> createTableCellForDesigner(ColumnOptimized column, LowLevelRowMetadata lowLevelRowMetadata, List<ColumnOptimized> columns) {
        TableCellOptimized tableCell = createEmptyCell();
        tableCell.setRowspan(1);
        tableCell.setProcessComponentStateChange(false);
        tableCell.setGroupingParentComponent(this);

        // designer preview mode
        for (FormElement prototypeCellComponent : prototype.getSubcomponents()) {
            getForm().addToElementIdToFormElement(prototypeCellComponent);
            getTable().getBindedSubcomponents().add(new IterationContext(0, prototypeCellComponent));
            prototypeCellComponent.setGroupingParentComponent(tableCell); // normally done by factory
            tableCell.getSubcomponents().add(prototypeCellComponent);
        }

        return Optional.of(tableCell);
    }

    protected OutputLabel createEmptyOutputLabel() {
        OutputLabel emptyLabel = new OutputLabel(getForm());
        return emptyLabel;
    }

    private RowIteratorMetadata getRowIteratorMetadata(LowLevelRowMetadata lowLevelRowMetadata, String columnIterationRef) {
        if (columnIterationRef == null) {
            columnIterationRef = getTable().getIterator();
        }

        return lowLevelRowMetadata.getIteratorData().get(columnIterationRef);
    }

    private void collectSubColumns(int level, List<ColumnOptimized> columns, List<ColumnOptimized> collectedColumns) {
        columns.stream().filter(component -> component instanceof ColumnOptimized).forEach(column -> {
            if (column.getLevel() == level) {
                collectedColumns.add(column);
            } else if (column.getLevel() < level) {
                collectSubColumns(level, column.getSubcomponents().stream().filter(subComponent -> subComponent instanceof ColumnOptimized).map(comp -> (ColumnOptimized) comp).collect(Collectors.toList()), collectedColumns);
            }
        });
    }

    protected int getMaxColumnDepthForLevel(int level, List<ColumnOptimized> components) {
        int initialDepth = 0;
        int maxDepth = 1;

        LinkedList<ColumnOptimized> columns = new LinkedList<>();
        collectSubColumns(level, components, columns);
        for (ColumnOptimized column : columns) {
            maxDepth = Integer.max(maxDepth, getMaxColumnDepth(column, initialDepth));
        }

        return maxDepth;
    }

    private int getMaxColumnDepth(ColumnOptimized column, int initialDepth) {
        List<ColumnOptimized> subcolumns = column.getSubcolumns();
        initialDepth++;

        if (subcolumns.size() == 0) {
            return initialDepth;
        } else {
            int maxDepth = initialDepth;
            for (ColumnOptimized subColumnElement : subcolumns) {
                maxDepth = Integer.max(getMaxColumnDepth(subColumnElement, initialDepth), maxDepth);
            }

            return maxDepth;
        }
    }

    protected TableCellOptimized createEmptyCell() {
        TableCellOptimized tableCell = new TableCellOptimized(getForm());
        tableCell.setHorizontalAlign(getHorizontalAlign());
        tableCell.setVerticalAlign(getVerticalAlign());
        return tableCell;
    }
}
