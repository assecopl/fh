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
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Order;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@Control(parents = {GroupingComponent.class}, invalidParents = {Table.class, TablePaged.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "collection",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = {Page.class, PageModel.class}, commonUse = true, bindingOnly = true, functionalArea = CONTENT, priority = 15))
@DocumentedComponent(category = DocumentedComponent.Category.TABLE_AND_TREE, value = "Table that allows to arrange data like text, images, links, other tables, etc. into rows and columns of cells inside of page. Lazyloads its data.", icon = "fa fa-table")
public class TableLazy extends Table {

    private static final String SORT_BY_ATTRIBUTE = "sortBy";
    private static final String DIRECTION_ATTRIBUTE = "direction";
    private static final String LOAD_MORE_ATTRIBIUTE = "loadMorePage";
    private static final String LOAD_All_ATTRIBIUTE = "loadAllPages";

    @Getter
    @Setter
    private PageRequest loadable = null;

    @Getter
    private int totalPages;

    @Getter
    private long totalRows;

    @Getter
    private int pageNumber;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "9999")
    @DocumentedComponentAttribute(value = "Amount of records displayed on first show")
    private Integer startSize;

    @JsonIgnore
    private Page loadMorePage;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "false")
    @DocumentedComponentAttribute(value = "Load all data automaticly")
    private Boolean loadAllPages;

    @JsonIgnore
    private PageModel pageModel;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(defaultValue = "-")
    @DocumentedComponentAttribute(defaultValue = "-", value = "Method executed when loading more content")
    private ActionBinding onLoadMore;

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
    private String language;

    private static final int PAGE_START = 0;

    private static final String ON_LOAD_MORE = "onLoadMore";

    private static final String ON_SORT_CHANGE = "onSortChange";

    private static final String DISPLAYED_TOTAL_PAGES = "totalPages";

    private static final String DISPLAYED_TOTAL_ROWS = "totalRows";

    private static final String CURRENT_PAGE_NUMBER = "pageNumber";

    public TableLazy(Form form) {
        super(form);
    }

    public void init() {
        super.init();

        //If startSize is not set we show all data.
        if (startSize == null) {
            startSize = 9999;
        }
        if(loadAllPages == null) loadAllPages = false;

        pageNumber = PAGE_START;
        changePageOrSize(pageNumber, startSize);
    }

    @Override
    public int getRowNumberOffset() {
        return startSize * pageNumber;
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
                changePageOrSize(PAGE_START, startSize);
            }
            if (this.pageModel.isRefreshNeeded()) {
                this.pageModel.doRefresh(loadable);
            }
            this.loadMorePage = pageModel.getPage();
            return extractMainCollection();
            //check also if bindingResultObj is instance of Page to keep compatibility with old implementation
        } else if (bindingResultObj instanceof Page) {
            this.loadMorePage = (Page) bindingResultObj;
            return extractMainCollection();

        } else {
            throw new FhException("Not instance of PageModel: " + collectionBinding.getBindingExpression());
        }
    }

    private Collection extractMainCollection() {
        this.mainCollection = loadMorePage.getContent();
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
        if (valueChange.hasAttributeChanged(LOAD_MORE_ATTRIBIUTE)) {
            int pageNumber = valueChange.getIntAttribute(LOAD_MORE_ATTRIBIUTE);
            // change page number
            changePageOrSize(0, this.startSize * (pageNumber+1));
            refreshNeeded = true;
        }
        if (valueChange.hasAttributeChanged(LOAD_All_ATTRIBIUTE)) {
//                TimeUnit.SECONDS.sleep(10);
                // change page number
                this.setLoadAllPages(valueChange.getBooleanAttribute(LOAD_MORE_ATTRIBIUTE));
                changePageOrSize(0, this.loadable.getPageSize() * loadMorePage.getTotalPages());
                refreshNeeded = true;
        }
        if (valueChange.hasAttributeChanged(SORT_BY_ATTRIBUTE)) {
            String sortBy = valueChange.getStringAttribute(SORT_BY_ATTRIBUTE);
            String directionString = valueChange.getStringAttribute(DIRECTION_ATTRIBUTE);
            Direction direction = directionString != null ? Direction.valueOf(directionString) : null;
            ColumnLazy column = (ColumnLazy) getSortingColumn(sortBy, getColumns());
            // change sort direction or property
            this.loadable = new PageRequest(pageNumber, this.loadable.getPageSize(), direction, column.getSortBy());
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

        if (loadMorePage != null && totalPages != loadMorePage.getTotalPages()) {
            totalPages = loadMorePage.getTotalPages();
            elementChange.addChange(DISPLAYED_TOTAL_PAGES, totalPages);
        }

        if (loadMorePage != null && totalRows != loadMorePage.getTotalElements()) {
            totalRows = loadMorePage.getTotalElements();
            elementChange.addChange(DISPLAYED_TOTAL_ROWS, totalRows);
        }

        if (loadMorePage != null && pageNumber != loadMorePage.getNumber()) {
            pageNumber = loadMorePage.getNumber();
            elementChange.addChange(CURRENT_PAGE_NUMBER, pageNumber);
        }

//        elementChange.addChange(LOAD_All_ATTRIBIUTE, this.loadAllPages);

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
        if (ON_LOAD_MORE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onLoadMore);
        } else if (ON_SORT_CHANGE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onSortChange);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnLoadMore(ActionBinding onLoadMore) {
        this.onLoadMore = onLoadMore;
    }

    public IActionCallbackContext setOnLoadMore(IActionCallback onLoadMore) {
        return CallbackActionBinding.createAndSet(onLoadMore, this::setOnLoadMore);
    }

    public void setOnSortChange(ActionBinding onSortChange) {
        this.onSortChange = onSortChange;
    }

    public IActionCallbackContext setOnSortChange(IActionCallback onSortChange) {
        return CallbackActionBinding.createAndSet(onSortChange, this::setOnSortChange);
    }

    @Override
    protected ColumnLazy createExampleColumn(int nameSuffix) {
        ColumnLazy column = new ColumnLazy(getForm());
        column.setLabelModelBinding(new StaticBinding<>("Column " + nameSuffix));
        column.setTable(this);
        column.setGroupingParentComponent(this);
        column.init();
        return column;
    }

    private void changePageOrSize(int newPageNumber, int newPageSize) {
        Order order = getSortingOrder();
        if (order != null) {
            this.loadable = PageRequest.of(newPageNumber, newPageSize, order.getDirection(), order.getProperty());
        } else {
            this.loadable = PageRequest.of(newPageNumber, newPageSize);
        }
    }

    private Order getSortingOrder() {
        if (this.loadable != null && this.loadable.getSort() != null && !this.loadable.getSort().isEmpty()) {
            Iterator<Order> orderIterator = this.loadable.getSort().iterator();
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
