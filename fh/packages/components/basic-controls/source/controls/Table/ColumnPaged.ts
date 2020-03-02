import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";
import {fn} from "moment";

declare const ENV_IS_DEVELOPMENT: boolean;

class ColumnPaged extends HTMLFormComponent {
    private isSortable: boolean;
    private rowspan: any;
    private sorter: any;
    private subColumnsExists: any;
    private subelements: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.isSortable = Boolean(this.componentObj.sortable);
        this.sorter = null;
        this.rowspan = this.componentObj.rowspan || null;
        this.subColumnsExists = this.componentObj.subColumnsExists || false;
        this.subelements = this.componentObj.subelements || [];
    }

    create() {

        let column = document.createElement('th');
        if (this.width && this.width.length > 0) {
            column.style.width = this.width[0].includes("px")? this.width[0]: this.width[0]  + '%';
        }
        // HTMLComponent recognized and updated label
        this.labelElement = document.createElement('span');
        column.appendChild(this.labelElement);
        this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);

        this.component = column;
        this.htmlElement = this.component;
        this.contentWrapper = this.component;
        this.display();
        if (this.subColumnsExists) {
            let jQueryComponent = $(this.component);
            let currentRow = jQueryComponent.parent('tr');
            this.setSubcolumns(this, this, jQueryComponent, currentRow, this.setSubcolumns);
        } else {
            if (this.isSortable) {
                this.setSorter(this, this, $(this.component));
            }
        }
        if (this.rowspan) {
            this.component.setAttribute('rowspan', this.rowspan);
        }

    };

    setSorter(parentObject, columnObject, columnElement) {
        let sorter = document.createElement('span');
        sorter.classList.add('clearfix');
        sorter.classList.add('sorter');
        sorter.classList.add('parent-' + parentObject.id);
        sorter.setAttribute('sorter', columnObject.id);
        let icon = document.createElement('i');
        icon.classList.add('fa');
        icon.classList.add('fa-sort');

        sorter.appendChild(icon);
        columnElement.append($(sorter));

        parentObject.sorter = sorter;

        columnElement.addClass('sortable');
        columnElement.on('click', function () {
            let icon = (<any>sorter.firstChild);
            let sortDirection;

            if (icon.classList.contains('fa-sort')) {
                icon.classList.remove('fa-sort');
                icon.classList.add('fa-sort-amount-down');
                sortDirection = 'ASC';
            } else if (icon.classList.contains('fa-sort-amount-up')) {
                icon.classList.remove('fa-sort-amount-up');
                icon.classList.add('fa-sort-amount-down');
                sortDirection = 'ASC';
            } else if (icon.classList.contains('fa-sort-amount-down')) {
                icon.classList.remove('fa-sort-amount-down');
                icon.classList.add('fa-sort-amount-up');
                sortDirection = 'DESC';
            }
            parentObject.sorter = sorter;
            parentObject.parent.changeSort(this.id, sortDirection);
        }.bind(columnObject));
    };

    setSubcolumns(parentObject, columnObject, columnElement, currentRow, callback) {
        if (currentRow.next().length == 0) {
            currentRow.after('<tr></tr>');
        }
        let colspan = 0;
        let nextRow = currentRow.next();
        columnObject.subelements.forEach(function (item) {
            var newColumnElement = $('<th>' + item.label + '</th>');
            // newColumnElement.addClass('parent-'+parentObject.id);
            nextRow.append(newColumnElement);
            // item.parent = columnObject;
            if (item.rowspan) {
                newColumnElement.attr('rowspan', item.rowspan);
            }
            if (item.subColumnsExists && item.subColumnsExists === true) {
                colspan += callback(parentObject, item, newColumnElement, nextRow, callback);
            } else {
                colspan++;
                if (item.sortable && item.sortable == true) {
                    parentObject.setSorter(parentObject, item, newColumnElement);
                }
            }
        });
        columnElement.attr('colspan', colspan);
        if (
            (columnObject.isSortable && columnObject.isSortable == true) ||
            (columnObject.sortable && columnObject.sortable == true)
        ) {
            parentObject.setSorter(parentObject, parentObject, columnElement);
        }

        return colspan;
    };

    update(change) {
        super.update(change);
        if (ENV_IS_DEVELOPMENT) {
            console.log('%c update ', 'background: #F00; color: #00F', change);
        }
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name) {
                switch (name) {
                    case 'accessibility':
                        // setting accessibility done in HTMLFormComponent.update()
                        // just redraw columns
                        var parentTable = this.parent;
                        while (parentTable.componentObj.type !== 'TablePaged') {
                            parentTable = parentTable.parent;
                        }
                        parentTable.redrawColumns();
                        break;
                }
            }.bind(this));
        }
    };

    getDefaultWidth() {
        return null;
    };

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('moveUp', 'arrow-left', 'Move left'),
            new AdditionalButton('moveDown', 'arrow-right', 'Move right')
        ];
    }
}

export {ColumnPaged};
