import {TablePagedPL} from './i18n/TablePaged.pl';
import {TablePagedEN} from './i18n/TablePaged.en';
import {Table} from "./Table";
import {HTMLFormComponent} from "fh-forms-handler";

class TablePaged extends Table {
    private pageSizeSelect: any;
    private pageSizeSelect_clone: any;
    private sortedBy: any;
    private paginator: HTMLElement;
    private paginator_second: HTMLElement;
    private pageInfo: HTMLElement;
    private pageInfo_second: HTMLElement;
    private paginationAboveTable: boolean = false;
    private pageSize: number;
    private sortDirection: any;

    private readonly onPageChange: any;
    private totalPages: number;
    private totalRows: number;
    private currentPage: any;
    private readonly paginatorOffset: any;
    private pageChangeListeners: HTMLElement[];

    private pageSizes: Number[] = [5, 10, 15, 25];
    private pageSizeAsButtons: boolean = false;


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.keyEventLoopDownTurnOff();

        this.i18n.registerStrings('en', TablePagedEN);
        this.i18n.registerStrings('pl', TablePagedPL);

        this.paginator = null;
        this.pageInfo = null;
        this.totalPages = this.componentObj.totalPages;
        this.totalRows = this.componentObj.totalRows;
        this.paginationAboveTable = this.componentObj.paginationAboveTable ? this.componentObj.paginationAboveTable : false;
        this.currentPage = this.componentObj.pageable.pageNumber || 0;
        this.paginatorOffset = 2;
        this.onPageChange = this.componentObj.onPageChange;
        this.pageChangeListeners = [];

        this.sortedBy = null;
        this.sortDirection = null;

        this.pageSize = this.componentObj.pageable.pageSize || 10;
        this.pageSizeAsButtons = this.componentObj.pageSizeAsButtons || false;
        this.pageSizes =  this.componentObj.pageSizes || [5, 10, 15, 25];

        this.pageSizeSelect = null;
        this.pageSizeSelect_clone = null;
    }


    create() {
        super.create();

        let toolsRow = document.createElement('div');
        toolsRow.classList.add('row');
        toolsRow.classList.add('toolsRow');

        let box = document.createElement('div');
        box.id = this.id + '_rowsCountSelector';
        box.classList.add('rowsCountSelector');
        box.classList.add('form-inline');
        box.classList.add('col-md-6');

       this.pageSizeSelect = this.buildPageSizeSelect();

        let pageInfo = this.buildPageInfo();

        box.appendChild(this.pageSizeSelect);

        let cecordsText = this.__('rows per page');
        cecordsText.classList.add('pl-3');

        this.buildTablePagedToolsLabels(this.pageSizeSelect, box);

        box.appendChild(cecordsText);
        toolsRow.appendChild(box);

        let pagination = this.createPaginationElement();

        pagination.classList.add('pagination-first');

        let paginator = this.buildPaginator();

        pagination.appendChild(pageInfo);
        pagination.appendChild(paginator);

        this.buildTablePagedToolsLabels(pagination, toolsRow);
        toolsRow.appendChild(pagination);

        this.htmlElement.appendChild(toolsRow);
        this.paginator = paginator;
        this.pageInfo = pageInfo;

        if (this.paginationAboveTable) {
            let toolsRow_clone = document.createElement('div');
            toolsRow_clone.classList.add('row');
            toolsRow_clone.classList.add('toolsRow');
            let box_clone = document.createElement('div');
            box_clone.id = box.id;
            box_clone.classList.add('rowsCountSelector');
            box_clone.classList.add('form-inline');
            box_clone.classList.add('col-md-6');

            this.pageSizeSelect_clone =  this.buildPageSizeSelect();
            this.buildTablePagedToolsLabels(this.pageSizeSelect_clone, box_clone);

            box_clone.appendChild(this.pageSizeSelect_clone);
            box_clone.appendChild(cecordsText.cloneNode(true));


            toolsRow_clone.appendChild(box_clone);

            let pagination_clone = this.createPaginationElement();
            pagination_clone.classList.add('pagination-second');

            let pageInfo_second = this.buildPageInfo();
            pagination_clone.appendChild(pageInfo_second);
            let paginator_second = this.buildPaginator();
            pagination_clone.appendChild(paginator_second);
            this.buildTablePagedToolsLabels(pagination_clone, toolsRow_clone);
            toolsRow_clone.appendChild(pagination_clone);
            $(this.htmlElement).prepend(toolsRow_clone);
            this.paginator_second = paginator_second;
            this.pageInfo_second = pageInfo_second;
        }
    };


    buildPageSizeSelect():HTMLElement {
        let select = null;
        if (!this.pageSizeAsButtons) {
            select = document.createElement('select');
            select.id = this.id + '_rowsPerPageSelector';
            select.classList.add('form-control');
            this.pageSizes.forEach(function (number) {
                let option = document.createElement('option');
                option.value = number;
                option.appendChild(document.createTextNode(number));
                select.appendChild(option);
            }.bind(this));
            select.value = this.pageSize;
            select.addEventListener('change', function (event) {
                this.pageSizeSelectEvent(event.target.value);
                this.pageSizeSelect.value = event.target.value;
                if (this.paginationAboveTable) {
                    this.pageSizeSelect_clone.value = event.target.value;
                }
            }.bind(this));
            select.disabled = (this.accessibility != 'EDIT');
        }

        if (this.pageSizeAsButtons) {
            select = document.createElement('div');
            select.id = this.id + '_rowsPerPageSelector';
            select.classList.add('btn-group');
            select.setAttribute("role", "group");
            this.pageSizes.forEach(function (number) {
                let button = document.createElement('button');
                button.classList.add('btn');
                button.classList.add('btn_'+number.toString());
                button.classList.add('btn-outline-primary');
                button.classList.add('page-link');
                if(this.pageSize == number){
                    button.classList.add('active');
                }

                button.innerText = number.toString();
                button.addEventListener('click', function (event:any) {
                    $(this.pageSizeSelect).find('button').removeClass('active');
                    $(this.pageSizeSelect).find('.btn_'+number.toString()).addClass('active');
                    this.pageSizeSelectEvent(number);
                    if (this.paginationAboveTable) {
                        $(this.pageSizeSelect_clone).find('button').removeClass('active');
                        $(this.pageSizeSelect_clone).find('.btn_'+number.toString()).addClass('active');
                    }
                }.bind(this))
                select.appendChild(button);
            }.bind(this));
        }
        return select
    }

    pageSizeSelectEvent(size:any) {
        this.pageSize = size;
        this.changesQueue.queueAttributeChange('pageSize', this.toInt(this.pageSize));
        this.fireEventWithLock('onPageSizeChange', null);
    }

    buildTablePagedToolsLabels(tablePagedTool, toolContainer) {
        let label = document.createElement('label');
        let labelId = tablePagedTool.id + '_label';
        label.id = labelId;
        label.classList.add('col-form-label');
        label.classList.add('sr-only');
        label.setAttribute('for', tablePagedTool.id);
        if (tablePagedTool.type === 'select-one') {
            label.innerHTML = 'rowsPerPageSelector';
        } else if (tablePagedTool.classList.contains('table-pagination')) {
            label.innerHTML = 'pagination';
        }
        this.component.setAttribute('aria-describedby', labelId);
        toolContainer.appendChild(label);
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'pageNumber':
                        this.currentPage = newValue;

                        this.updatePaginator();
                        this.updatePageInfo();

                        this.scrollTopInside();
                        $(window).trigger('pageChanged.table');
                        break;
                    case 'totalPages':
                        this.totalPages = newValue;
                        this.updatePaginator();
                        this.scrollTopInside();
                        break;
                    case 'totalRows':
                        this.totalRows = newValue;
                        this.updatePageInfo();
                        this.scrollTopInside();
                        break;
                }
            }.bind(this));
        }
    };

    onPaginatorClick(event) {
        event.preventDefault();
        event.stopPropagation();
        if (this.accessibility != 'EDIT') return;
        this.currentPage = event.target.parentNode.dataset.pageNumber;
        this.changesQueue.queueAttributeChange('currentPage', this.toInt(this.currentPage));
        this.fireEventWithLock('onPageChange', this.onPageChange);
    }

    cleanUpSortableComponents() {
        this.components.forEach(function (component) {
            if (component.isSortable && component.id != this.sortedBy) {
                let icon = component.sorter.firstChild;
                icon.classList.remove('fa-sort-amount-down');
                icon.classList.remove('fa-sort-amount-up');
                icon.classList.add('fa-sort');
                if (component.subColumnsExists && component.subColumnsExists == true) {
                }
            } else if (component.isSortable == false && component.subColumnsExists
                && component.subColumnsExists == true) {
                let sortedBy = this.sortedBy;
                $('.parent-' + component.id).each(function () {
                    let element = $(this);
                    if (element.attr('sorter') != sortedBy) {
                        let icon = element.children();
                        icon.removeClass('fa-sort-amount-down');
                        icon.removeClass('sort-amount-down');
                        icon.addClass('fa-sort');
                    }
                });
            }

        }.bind(this));
    };

    buildPageInfo() {
        this.pageSize = this.toInt(this.pageSize);

        let recordsFrom: number = (this.pageSize * this.currentPage);

        if (this.totalRows > 0) {
            recordsFrom += 1;
        }

        let recordsTo = (this.pageSize * this.currentPage) + this.pageSize;

        if (this.totalRows < recordsTo) {
            recordsTo = this.totalRows;
        }

        let pageInfo = document.createElement('span');
        pageInfo.classList.add('table-page-info');

        let recordsLabel = this.__('records');
        let counterLeft = document.createElement('span');
        counterLeft.innerText = recordsFrom + ' - ' + recordsTo;
        let recordsFromLabel = this.__('records_from');

        let counterRight = document.createElement('span');
        counterRight.innerText = this.totalRows.toString();

        pageInfo.appendChild(recordsLabel);
        pageInfo.appendChild(counterLeft);
        pageInfo.appendChild(recordsFromLabel);
        pageInfo.appendChild(counterRight);

        return pageInfo;
    }

    buildPaginator() {
        this.pageChangeListeners.forEach(function (listener) {
            listener.removeEventListener('click', this.onPaginatorClick.bind(this));
        }.bind(this));

        this.pageChangeListeners = [];
        let paginator = document.createElement('nav');

        let list = document.createElement('ul');
        list.classList.add('pagination');

        let first = document.createElement('li');
        first.classList.add('page-item');
        let firstLink = document.createElement('a');
        firstLink.href = '#';
        firstLink.classList.add('page-link');
        firstLink.title = this.i18n.__('first page');
        firstLink.innerHTML = '&lt;&lt;';
        if (this.currentPage == 0) {
            first.classList.add('disabled');
        } else if (this.onPageChange) {
            first.dataset.pageNumber = '0';
            firstLink.addEventListener('click', this.onPaginatorClick.bind(this));
            this.pageChangeListeners.push(firstLink);
        }
        first.appendChild(firstLink);
        list.appendChild(first);

        let previous = document.createElement('li');
        previous.classList.add('page-item');
        let previousLink = document.createElement('a');
        previousLink.href = '#';
        previousLink.classList.add('page-link');
        previousLink.title = this.i18n.__('previous page');
        previousLink.innerHTML = '&lt;';
        if (this.currentPage == 0) {
            previous.classList.add('disabled');
        } else if (this.onPageChange) {
            previous.dataset.pageNumber = (this.currentPage - 1).toString();
            previousLink.addEventListener('click', this.onPaginatorClick.bind(this));
            this.pageChangeListeners.push(previousLink);
        }
        previous.appendChild(previousLink);
        list.appendChild(previous);

        for (let start = Math.max(0, this.currentPage - this.paginatorOffset),
                 end = Math.min(this.totalPages, this.currentPage + this.paginatorOffset + 1);
             start < end; start++
        ) {
            let item = document.createElement('li');
            item.classList.add('page-item');
            item.dataset.pageNumber = start.toString();
            let itemLink = document.createElement('a');
            itemLink.title = this.i18n.__('X page') + (start + 1);
            itemLink.classList.add('page-link');
            itemLink.href = '#';
            itemLink.innerHTML = (start + 1).toString();
            if (start == this.currentPage) {
                item.classList.add('active');
            }

            if (this.onPageChange) {
                itemLink.addEventListener('click', this.onPaginatorClick.bind(this));
                this.pageChangeListeners.push(itemLink);
            }

            item.appendChild(itemLink);
            list.appendChild(item);
        }

        let next = document.createElement('li');
        next.classList.add('page-item');
        let nextLink = document.createElement('a');
        nextLink.classList.add('page-link');
        nextLink.href = '#';
        nextLink.title = this.i18n.__('next page');
        nextLink.innerHTML = '&gt;';
        if (this.currentPage >= this.totalPages - 1) {
            next.classList.add('disabled');
        } else if (this.onPageChange) {
            next.dataset.pageNumber = this.currentPage + 1;
            nextLink.addEventListener('click', this.onPaginatorClick.bind(this));
            this.pageChangeListeners.push(nextLink);
        }
        next.appendChild(nextLink);
        list.appendChild(next);

        let last = document.createElement('li');
        last.classList.add('page-item');
        let lastLink = document.createElement('a');
        lastLink.classList.add('page-link');
        lastLink.href = '#';
        lastLink.title = this.i18n.__('last page');
        lastLink.innerHTML = '&gt;&gt;';
        if (this.currentPage >= this.totalPages - 1) {
            last.classList.add('disabled');
        } else if (this.onPageChange) {
            last.dataset.pageNumber = (this.totalPages - 1).toString();
            lastLink.addEventListener('click', this.onPaginatorClick.bind(this));
            this.pageChangeListeners.push(lastLink);
        }
        last.appendChild(lastLink);
        list.appendChild(last);

        paginator.appendChild(list);

        return paginator;
    };

    changeSort(newSortedBy, newSortDirection) {
        this.sortedBy = newSortedBy;
        this.changesQueue.queueAttributeChange('sortBy', newSortedBy);
        if (newSortDirection !== undefined) { // undefined -> not changed
            this.sortDirection = newSortDirection;
        }
        // always send sortDirection with sortedBy change
        this.changesQueue.queueAttributeChange('direction', this.sortDirection);
        this.cleanUpSortableComponents();

        this.fireEventWithLock('onSortChange', null);
    };

    toInt = function (stringOrInt) {
        if (typeof stringOrInt == 'string') {
            return parseInt(stringOrInt);
        } else {
            return stringOrInt;
        }
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);
        if (this.pageSizeSelect) {
            this.pageSizeSelect.disabled = (this.accessibility != 'EDIT');
        }
        this.accessibility = accessibility;
    };

    destroy(removeFromParent: boolean) {
        this.pageSizeSelect.removeEventListener('change', this.pageSizeSelectEvent.bind(this));

        (this.pageChangeListeners || []).forEach(function (listener) {
            listener.removeEventListener('click', this.onPaginatorClick.bind(this));
        }.bind(this));

        super.destroy(removeFromParent);
    }

    protected updatePageInfo() {
        let pageInfo = this.buildPageInfo();
        // this.htmlElement.querySelector('.pagination-first').replaceChild(pageInfo, this.pageInfo);
        $(this.pageInfo).replaceWith(pageInfo);
        this.pageInfo = pageInfo;
        if (this.paginationAboveTable) {
            let pageInfo_second = this.buildPageInfo();
            this.htmlElement.querySelector('.pagination-second').replaceChild(pageInfo_second, this.pageInfo_second);
            this.pageInfo_second = pageInfo_second;
        }
    }

    protected updatePaginator() {
        let paginator2 = this.buildPaginator();
        // this.htmlElement.querySelector('.pagination-first').replaceChild(paginator2, this.paginator);
        $(this.paginator).replaceWith(paginator2);
        this.paginator = paginator2;
        if (this.paginationAboveTable) {
            let paginator_second2 = this.buildPaginator();
            // this.htmlElement.querySelector('.pagination-second').replaceChild(paginator_second2, this.paginator_second);
            $(this.paginator_second).replaceWith(paginator_second2);
            this.paginator_second = paginator_second2;
        }
    }

    protected createPaginationElement() {
        let pagination = document.createElement('div');
        pagination.id = this.id + '_pagination';
        pagination.classList.add('table-pagination');
        pagination.classList.add('col-md-6');
        pagination.classList.add('text-right');
        return pagination;
    }
}

export {TablePaged};
