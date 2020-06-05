import {TablePagedPL} from './i18n/TablePaged.pl';
import {TablePagedEN} from './i18n/TablePaged.en';
import {Table} from "./Table";
import {HTMLFormComponent} from "fh-forms-handler";

class TablePaged extends Table {
    private pageSizeSelect: any;
    private sortedBy: any;
    private paginator: HTMLElement;
    private pageInfo: HTMLElement;
    private pageSize: number;
    private sortDirection: any;

    private readonly onPageChange: any;
    private readonly totalPages: number;
    private readonly totalRows: number;
    private currentPage: any;
    private readonly paginatorOffset: any;
    private pageChangeListeners: HTMLElement[];


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.keyEventLoopDownTurnOff();

        this.i18n.registerStrings('en', TablePagedEN);
        this.i18n.registerStrings('pl', TablePagedPL);

        this.paginator = null;
        this.pageInfo = null;
        this.totalPages = this.componentObj.totalPages;
        this.totalRows = this.componentObj.totalRows;
        this.currentPage = this.componentObj.pageable.pageNumber || 0;
        this.paginatorOffset = 2;
        this.onPageChange = this.componentObj.onPageChange;
        this.pageChangeListeners = [];

        this.sortedBy = null;
        this.sortDirection = null;

        this.pageSize = this.componentObj.pageable.pageSize || 10;

        this.pageSizeSelect = null;
    }


    create() {
        super.create();

        let toolsRow = document.createElement('div');
        toolsRow.classList.add('row');
        toolsRow.classList.add('toolsRow');

        let box = document.createElement('div');
        box.classList.add('rowsCountSelector');
        box.classList.add('form-inline');
        box.classList.add('col-md-6');

        this.pageSizeSelect = document.createElement('select');
        this.pageSizeSelect.classList.add('form-control');
        [5, 10, 15, 25].forEach(function (number) {
            let option = document.createElement('option');
            option.value = number;
            option.appendChild(document.createTextNode(number));
            this.pageSizeSelect.appendChild(option);
        }.bind(this));
        this.pageSizeSelect.value = this.pageSize;
        this.pageSizeSelect.addEventListener('change', this.pageSizeSelectEvent.bind(this));
        this.pageSizeSelect.disabled = (this.accessibility != 'EDIT');

        let pageInfo = this.buildPageInfo();

        box.appendChild(this.pageSizeSelect);
        let cecordsText = this.__('rows per page');
        cecordsText.classList.add('pl-3');
        box.appendChild(cecordsText);
        toolsRow.appendChild(box);

        let pagination = document.createElement('div');
        pagination.classList.add('table-pagination');
        pagination.classList.add('col-md-6');
        pagination.classList.add('text-right');

        let paginator = this.buildPaginator();
        pagination.appendChild(pageInfo);
        pagination.appendChild(paginator);
        toolsRow.appendChild(pagination);

        this.htmlElement.appendChild(toolsRow);
        this.paginator = paginator;
        this.pageInfo = pageInfo;
    };

    pageSizeSelectEvent(event) {
        this.pageSize = event.target.value;
        this.changesQueue.queueAttributeChange('pageSize', this.toInt(this.pageSize));
        this.fireEventWithLock('onPageSizeChange', null);
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'pageNumber':
                        this.currentPage = newValue;

                        let paginator = this.buildPaginator();
                        this.htmlElement.querySelector('.table-pagination').replaceChild(paginator, this.paginator);
                        this.paginator = paginator;

                        let pageInfo = this.buildPageInfo();
                        this.htmlElement.querySelector('.table-pagination').replaceChild(pageInfo, this.pageInfo);
                        this.pageInfo = pageInfo;

                        this.scrollTopInside();
                        $(window).trigger('pageChanged.table');
                        break;
                    case 'totalPages':
                        this.totalPages = newValue;
                        let paginator2 = this.buildPaginator();
                        this.htmlElement.querySelector('.table-pagination').replaceChild(paginator2, this.paginator);
                        this.paginator = paginator2;
                        this.scrollTopInside();
                        break;
                    case 'totalRows':
                        this.totalRows = newValue;
                        let pageInfo2 = this.buildPageInfo();
                        this.htmlElement.querySelector('.table-pagination').replaceChild(pageInfo2, this.pageInfo);
                        this.pageInfo = pageInfo2;
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
        let recordsFromLabel=  this.__('records_from');

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
}

export {TablePaged};
