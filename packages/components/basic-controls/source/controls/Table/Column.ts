import {HTMLFormComponent} from "fh-forms-handler";
import {Table} from "./Table";
import {AdditionalButton} from "fh-forms-handler";

class Column extends HTMLFormComponent {
    private buttonElement: any;
    private isSortable: any;
    private sorter: any;
    private readonly rowspan: any;
    private readonly subColumnsExists: boolean;
    private subelements: any;
    private colspan: any;
    private subcomponents: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.isSortable = Boolean(this.componentObj.sortable);
        this.sorter = null;
        this.rowspan = this.componentObj.rowspan || null;
        this.subColumnsExists = this.componentObj.subColumnsExists || false;
        this.subelements = this.componentObj.subelements || [];
        this.colspan = this.componentObj.colspan || 0;
        this.subcomponents = [];
    }

    create() {
        let column = document.createElement('th');
        column.id = this.id;
        column.classList.add(this.id);
        if (this.width && this.width.length > 0) {
            column.style.width = this.width[0].includes("px")? this.width[0]: this.width[0]  + '%';
        }
        // HTMLComponent recognized and updated label
        this.buttonElement = document.createElement('span');
        this.labelElement = document.createElement('span');

        if (this.isSortable) {
            this.buttonElement = document.createElement('button');
            this.buttonElement.classList.add("btn");
            this.buttonElement.classList.add("btn-th");
        }

        this.buttonElement.append(this.labelElement);
        column.appendChild(this.buttonElement);
        this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);

        if (this.isSortable) {
            let sorter = document.createElement('span');
            sorter.classList.add('pull-right');
            sorter.classList.add('clearfix');
            sorter.classList.add('sorter');

            let icon = document.createElement('i');
            icon.classList.add('fa');
            icon.classList.add('fa-sort');

            sorter.appendChild(icon);
            this.buttonElement.appendChild(sorter);

            this.sorter = sorter;

            column.classList.add('sortable');
            // column.classList.add(column.id);
            column.addEventListener('click', this.columnClickEvent.bind(this));
        }

        this.component = column;
        this.htmlElement = this.component;
        this.contentWrapper = this.component;
        this.display();

        if (this.parent instanceof Table) {
            this.parent.totalColumns++;
        }


        if (this.subColumnsExists && this.subColumnsExists === true) {
            let currentRow = $(this.component).parent('tr');
            if (currentRow.next().length === 0) {
                currentRow.after('<tr></tr>');
            }

            let nextRow = currentRow.next();
            let oldContentWrapper = this.contentWrapper;
            this.contentWrapper = nextRow[0];

            this.subelements.forEach(function (subelement) {
                let subcomponent = this.fh.createComponent(subelement, this);
                subcomponent.create();

                this.components.push(subcomponent);

                if (subelement.subColumnsExists && subelement.subColumnsExists === true) {
                    this.colspan += subcomponent.colspan;
                } else {
                    this.colspan++;
                }
            }.bind(this));

            this.contentWrapper = oldContentWrapper;
        }
        if (this.rowspan) {
            this.component.setAttribute('rowspan', this.rowspan);
        }
        this.setColspan(this.colspan);
    };

    calculateColspan() {
        if (this.subColumnsExists && this.subColumnsExists === true) {
            this.colspan = 0;

            let hiddenCount = 0;
            this.components.forEach(function (subcomponent) {
                if (subcomponent.accessibility === 'HIDDEN') {
                    hiddenCount += 1;
                    return;
                }
                if (subcomponent.componentObj.subColumnsExists && subcomponent.componentObj.subColumnsExists === true) {
                    this.colspan += subcomponent.colspan;
                } else {
                    this.colspan++;
                }
            }.bind(this));

            this.setColspan(this.colspan);
        }
    };

    setAccessibility(accessibility) {
        // Alvays show in design mode.
        if (this.designMode && accessibility === 'HIDDEN') {
            accessibility = 'EDIT';
        }

        super.setAccessibility(accessibility);

        if (this.parent instanceof Column && this.parent.subColumnsExists === true) {
            let hiddenSubcolumnsCount = 0;

            this.parent.components.forEach(function (subcomponent) {
                if (subcomponent.accessibility === 'HIDDEN') {
                    hiddenSubcolumnsCount += 1;
                }
            });

            if (hiddenSubcolumnsCount > 0 && this.parent.components.length === hiddenSubcolumnsCount) {
                this.parent.setAccessibility('HIDDEN');
            }
        }

        if (this.designMode) {
            this.htmlElement.classList.remove('disabled', 'fc-disabled');
            this.htmlElement.classList.add('fc-editable');
        }
    };

    setColspan(colspan) {
        if (colspan === 0 && this.component.hasAttribute('colspan')) {
            this.component.removeAttribute('colspan');
            return;
        }

        this.component.setAttribute('colspan', colspan);
    };

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name) {
                switch (name) {
                    case 'accessibility':
                        // setting accessibility done in HTMLFormComponent.update()
                        // just redraw columns
                        let parentTable = this.parent;
                        while (parentTable.componentObj.type !== 'Table') {
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

    columnClickEvent() {
        let sortDirection;

        let icon = this.sorter.firstChild;
        if (icon.classList.contains('fa-sort')) {
            icon.classList.remove('fa-sort');
            icon.classList.add('fa-sort-amount-down');
            this.component.setAttribute("aria-sort", "ascending")
            sortDirection = 'ASC';
        } else if (icon.classList.contains('fa-sort-amount-up')) {
            icon.classList.remove('fa-sort-amount-up');
            icon.classList.add('fa-sort-amount-down');
            this.component.setAttribute("aria-sort", "ascending")
            sortDirection = 'ASC';
        } else if (icon.classList.contains('fa-sort-amount-down')) {
            icon.classList.remove('fa-sort-amount-down');
            icon.classList.add('fa-sort-amount-up');
            sortDirection = 'DESC';
            this.component.setAttribute("aria-sort", "descending")
        }

        // @ts-ignore
        this.parent.changeSort(this.id, sortDirection);
    }

    destroy(removeFromParent: boolean) {
        if (this.isSortable) {
            this.component.removeEventListener('click', this.columnClickEvent.bind(this));
        }

        super.destroy(removeFromParent);
    }
}

export {Column};
