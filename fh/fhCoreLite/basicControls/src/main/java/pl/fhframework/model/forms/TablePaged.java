package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.utils.LanguageResolver;

import java.util.Iterator;
import java.util.*;

import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Order;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

@Control(parents = {GroupingComponent.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "collection",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = {Page.class, PageModel.class}, commonUse = true, bindingOnly = true, functionalArea = CONTENT, priority = 15))
@DocumentedComponent(category = DocumentedComponent.Category.TABLE_AND_TREE, documentationExample = true, value = "Table that allows to arrange data like text, images, links, other tables, etc. into rows and columns of cells inside of page.", icon = "fa fa-table")
public class TablePaged extends Table {

    private static final String SORT_BY_ATTRIBUTE = "sortBy";
    private static final String DIRECTION_ATTRIBUTE = "direction";
    private static final String CURRENT_PAGE_ATTRIBUTE = "currentPage";
    private static final String PAGE_SIZE_ATTRIBUTE = "pageSize";

    @Getter
    @Setter
    private PageRequest pageable = null;

    @Getter
    private int totalPages;

    @Getter
    private long totalRows;

    @Getter
    private int pageNumber;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "10")
    @DocumentedComponentAttribute(value = "Amount of records displayed on single page")
    private int pageSize;

    @JsonIgnore
    private Page currentPage;

    @JsonIgnore
    private PageModel pageModel;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(defaultValue = "-")
    @DocumentedComponentAttribute(defaultValue = "-", value = "If the table page is changed that method will be executed")
    private ActionBinding onPageChange;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(defaultValue = "-")
    @DocumentedComponentAttribute(defaultValue = "-", value = "If the table page size is changed that method will be executed")
    private ActionBinding onPageSizeChange;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(defaultValue = "-")
    @DocumentedComponentAttribute(defaultValue = "-", value = "If the table page sorting is changed that method will be executed")
    private ActionBinding onSortChange;

    @Getter
    @Setter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty
    @DocumentedComponentAttribute(value = "Property name by default passed in the Pageable object to be interpreted in a data source (eg. DAO)")
    private String defaultSortBy;

    @Getter
    @Setter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(defaultValue = "true")
    @DocumentedComponentAttribute(value = "If defaultSortBy is set this property decides if default order is ascending ", defaultValue = "true")
    private boolean defaultSortByAsc = true;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "false")
    @DocumentedComponentAttribute(value = "Add additional pagination and page size select above the table")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 16)
    private boolean paginationAboveTable = false;

    @Getter
    private String language;

    private static final int PAGE_START = 0;

    private static final String ON_PAGE_CHANGE = "onPageChange";

    private static final String ON_SORT_CHANGE = "onSortChange";

    private static final String ON_PAGE_SIZE_CHANGE = "onPageSizeChange";

    private static final String DISPLAYED_PAGE_NUMBER = "pageNumber";

    private static final String DISPLAYED_PAGE_SIZE = "pageSize";

    private static final String DISPLAYED_TOTAL_PAGES = "totalPages";

    private static final String DISPLAYED_TOTAL_ROWS = "totalRows";

    public TablePaged(Form form) {
        super(form);
    }

    public void init() {
        super.init();
        if (pageSize < 1) {
            pageSize = 10;
        }
        pageNumber = PAGE_START;
        changePageOrSize(pageNumber, pageSize);
    }

    @Override
    public int getRowNumberOffset() {
        return pageSize * pageNumber;
    }

    @Override
    protected Collection extractCollection(IndexedModelBinding collectionBinding, int[] parentIteratorIndices, boolean isMainLevel) {
        if (isMainLevel) {
            Object bindingResultObj = collectionBinding.getValue(parentIteratorIndices);

            if (bindingResultObj != null) {
                return extractMainCollection(collectionBinding, bindingResultObj);
            } else {
                return null;
            }
        } else { // lower level iterators are not pageable - delegate to normal collection extraction
            return super.extractCollection(collectionBinding, parentIteratorIndices, isMainLevel);
        }
    }

    private Collection extractMainCollection(IndexedModelBinding collectionBinding, Object bindingResultObj) {
        if (bindingResultObj instanceof PageModel) {
            PageModel pageModel = (PageModel) bindingResultObj;
            this.pageModel = pageModel;
            if (this.pageModel.isResetNeeded()) {
                changePageOrSize(PAGE_START, pageSize);
            }
            if (this.pageModel.isRefreshNeeded()) {
                this.pageModel.doRefresh(pageable);
            }
            this.currentPage = pageModel.getPage();
            return extractMainCollection();
            //check also if bindingResultObj is instance of Page to keep compatibility with old implementation
        } else if (bindingResultObj instanceof Page) {
            this.currentPage = (Page) bindingResultObj;
            return extractMainCollection();
        } else {
            throw new FhException("Not instance of PageModel: " + collectionBinding.getBindingExpression());
        }
    }

    private Collection extractMainCollection() {
        this.mainCollection = currentPage.getContent();
        return this.mainCollection;
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (valueChange.hasMainValueChanged() && containsNumeric(valueChange.getMainValue())) {
            // selected row
            if (getSelectedElementBinding() != null) {
                String newTextValue = valueChange.getMainValue();
                if (newTextValue != null) {
                    newTextValue = newTextValue.substring(1, newTextValue.length() - 1);
                    this.selectedRowsNumbers = Arrays.stream(newTextValue.split(","))
                            .map(String::trim).mapToInt(Integer::parseInt).toArray();
                    Collection<Object> bindedObjectsList = getBindedObjectsList();
                    Object newSelectedElement;
                    if (isMultiselect()) {
                        newSelectedElement = getSelectedElementsBasedOnRowsNumbers(bindedObjectsList, this.selectedRowsNumbers);
                        if (getSelectedElementBinding().getBindingResult() != null) {
                            Collection muliselectCol = (Collection) getSelectedElementBinding().getBindingResult().getValue();
                            if (muliselectCol != null) {
                                muliselectCol.removeAll(bindedObjectsList);
                                muliselectCol.addAll((Collection) newSelectedElement);
                            }
                            else {
                                getSelectedElementBinding().setValue(newSelectedElement);
                            }
                        }
                        else {
                            getSelectedElementBinding().setValue(newSelectedElement);
                        }
                    } else {
                        if (selectedRowsNumbers[0] > -1 && bindedObjectsList.size() > selectedRowsNumbers[0]) {
                            newSelectedElement = CollectionsUtils.get(bindedObjectsList, selectedRowsNumbers[0]);
                        } else {
                            newSelectedElement = null;
                        }
                        getSelectedElementBinding().setValue(newSelectedElement);
                    }
                }
            }
        }

        boolean refreshNeeded = false;
        if (valueChange.hasAttributeChanged(CURRENT_PAGE_ATTRIBUTE)) {
            int pageNumber = valueChange.getIntAttribute(CURRENT_PAGE_ATTRIBUTE);
            // change page number
            changePageOrSize(pageNumber, this.pageable.getPageSize());
            refreshNeeded = true;
        }
        if (valueChange.hasAttributeChanged(SORT_BY_ATTRIBUTE)) {
            String sortBy = valueChange.getStringAttribute(SORT_BY_ATTRIBUTE);
            String directionString = valueChange.getStringAttribute(DIRECTION_ATTRIBUTE);
            Direction direction = directionString != null ? Direction.valueOf(directionString) : null;
            ColumnPaged column = (ColumnPaged) getSortingColumn(sortBy, getColumns());
            // change sort direction or property
            this.pageable = new PageRequest(pageNumber, this.pageable.getPageSize(), direction, column.getSortBy());
            refreshNeeded = true;
        }
        if (valueChange.hasAttributeChanged(PAGE_SIZE_ATTRIBUTE)) {
            int pageSize = valueChange.getIntAttribute(PAGE_SIZE_ATTRIBUTE);
            // change page size and start from page number 0
            changePageOrSize(0, pageSize);
            refreshNeeded = true;
        }
        if (refreshNeeded && pageModel != null) {
            pageModel.refreshNeeded();
        }
    }


    public boolean containsNumeric(String text) {
        if (text == null) {
            return false;
        }
        int startIdx = text.indexOf("[");
        int endIdx = text.indexOf("]");
        if (startIdx > endIdx) {
            FhLogger.error("Could not retrieve numeric value from passed String: {}", text);
            return false;
        }
        return text != null && text.matches(".*\\d.*");
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (currentPage != null && totalPages != currentPage.getTotalPages()) {
            totalPages = currentPage.getTotalPages();
            elementChange.addChange(DISPLAYED_TOTAL_PAGES, totalPages);
        }

        if (currentPage != null && totalRows != currentPage.getTotalElements()) {
            totalRows = currentPage.getTotalElements();
            elementChange.addChange(DISPLAYED_TOTAL_ROWS, totalRows);
        }

        if (currentPage != null && pageNumber != currentPage.getNumber()) {
            pageNumber = currentPage.getNumber();
            elementChange.addChange(DISPLAYED_PAGE_NUMBER, pageNumber);
            if (pageNumber != pageable.getPageNumber()) {
                changePageOrSize(pageNumber, pageable.getPageSize());
            }
        }

        if (pageSize != pageable.getPageSize()) {
            if (getForm().getViewMode() == Form.ViewMode.DESIGN) {
                changePageOrSize(0, pageSize);
            } else {
                pageSize = pageable.getPageSize();
            }
            elementChange.addChange(DISPLAYED_PAGE_SIZE, pageSize);
            pageNumber = pageable.getPageNumber();
            elementChange.addChange(DISPLAYED_PAGE_NUMBER, pageNumber);
        }

        this.language = LanguageResolver.languageChanges(getForm().getAbstractUseCase().getUserSession(), this.language, elementChange);

        return elementChange;
    }

    @Override
    protected Collection<Object> getBindedObjectsList() {
        if (this.getCollection() == null) {
            throw new FhBindingException("Table '" + this.getId() + "' has not binding for 'collection'!");
        }
        Object list = this.getCollection().getBindingResult().getValue();

        if (list != null) {
            if (list instanceof Collection) {
                return (Collection<Object>) list;
            } else if (list instanceof Page && ((Page) list).getContent() != null) {
                return ((Page) list).getContent();
            } else if (list instanceof PageModel && ((PageModel) list).getPage() != null && ((PageModel) list).getPage().getContent() != null) {
                return ((PageModel) list).getPage().getContent();
            } else if (!(list instanceof Page) && !(list instanceof PageModel)) {
                throw new FhBindingException("Binded for table '" + getId() + "' class object '" + list.getClass().getSimpleName() + "' is not a Collection nor Page!");
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        FhLogger.trace(this.getClass(), logger -> logger.log("TablePaged will handle {}", selectedRowsNumbers[0]));
        if (ON_PAGE_CHANGE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onPageChange);
        } else if (ON_SORT_CHANGE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onSortChange);
        } else if (ON_PAGE_SIZE_CHANGE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onPageSizeChange);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnPageChange(ActionBinding onPageChange) {
        this.onPageChange = onPageChange;
    }

    public IActionCallbackContext setOnPageChange(IActionCallback onPageChange) {
        return CallbackActionBinding.createAndSet(onPageChange, this::setOnPageChange);
    }

    public void setOnPageSizeChange(ActionBinding onPageSizeChange) {
        this.onPageSizeChange = onPageSizeChange;
    }

    public IActionCallbackContext setOnPageSizeChange(IActionCallback onPageSizeChange) {
        return CallbackActionBinding.createAndSet(onPageSizeChange, this::setOnPageSizeChange);
    }

    public void setOnSortChange(ActionBinding onSortChange) {
        this.onSortChange = onSortChange;
    }

    public IActionCallbackContext setOnSortChange(IActionCallback onSortChange) {
        return CallbackActionBinding.createAndSet(onSortChange, this::setOnSortChange);
    }

    @Override
    protected ColumnPaged createExampleColumn(int nameSuffix) {
        ColumnPaged column = new ColumnPaged(getForm());
        column.setLabelModelBinding(new StaticBinding<>("Column " + nameSuffix));
        column.setTable(this);
        column.setGroupingParentComponent(this);
        column.init();
        return column;
    }

    private void changePageOrSize(int newPageNumber, int newPageSize) {
        Order order = getSortingOrder();
        if (order != null) {
            this.pageable = new PageRequest(newPageNumber, newPageSize, order.getDirection(), order.getProperty());
        } else {
            this.pageable = new PageRequest(newPageNumber, newPageSize);
        }
    }

    private Order getSortingOrder() {
        if (this.pageable != null && this.pageable.getSort() != null && !this.pageable.getSort().isEmpty()) {
            Iterator<Order> orderIterator = this.pageable.getSort().iterator();
            return orderIterator.next();
        } else if (this.defaultSortBy != null) {
            return new Order(this.defaultSortByAsc ? Direction.ASC : Direction.DESC, this.defaultSortBy);
        } else {
            return null;
        }
    }

    private Column getSortingColumn(String sortBy, List<? extends Component> components) {
        for (Component component : components) {
            if (component instanceof Column) {
                Column column = (Column) component;
                if (sortBy.equals(column.getId())) {
                    return column;
                } else if (column.getSubcomponents() != null) {
                    Column nestedColumn = getSortingColumn(sortBy, column.getSubcomponents());
                    if (nestedColumn != null) {
                        return nestedColumn;
                    }
                }
            }
        }
        return null;
    }
}
