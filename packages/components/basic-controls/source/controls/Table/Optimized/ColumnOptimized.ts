import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";

class ColumnOptimized extends HTMLFormComponent {
    private readonly isSortable: any;
    private sorter: any;
    private readonly rowspan: any;
    private readonly subColumnsExists: boolean;
    private subelements: any;
    private colspan: any;
    private subcomponents: any;
    private fixedHeader: boolean;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.isSortable = Boolean(this.componentObj.sortable);
        this.sorter = null;
        this.rowspan = this.componentObj.rowspan || null;
        this.subColumnsExists = this.componentObj.subColumnsExists || false;
        this.fixedHeader = this.componentObj.fixedHeader || false;
        this.subelements = this.componentObj.subelements || [];
        this.colspan = this.componentObj.colspan || 0;
        this.subcomponents = [];
    }

    create() {
        let column = document.createElement('th');
        column.id = this.id;
        column.classList.add(this.id);
        if (this.width) {
            column.style.width = this.width + '%';
        }
        if(this.fixedHeader){
            column.classList.add("fixedHeader_"+this.parentId);
        }
        // HTMLComponent recognized and updated label
        this.labelElement = document.createElement('span');
        column.appendChild(this.labelElement);
        this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);

        this.component = column;
        this.htmlElement = this.component;
        this.contentWrapper = this.component;



        this.container.appendChild(this.htmlElement);

        //console.log("ColumnOptimized before ", this.htmlElement.offsetWidth);
    };

    /**
     * @Override
     * @param accessibility
     */
    public display(): void {
        this.container.appendChild(this.htmlElement);
    }

    setAccessibility(accessibility) {
        // Alvays show in design mode.
        if (this.designMode && accessibility === 'HIDDEN') {
            accessibility = 'EDIT';
        }

        super.setAccessibility(accessibility);

        if (this.parent instanceof ColumnOptimized && this.parent.subColumnsExists === true) {
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
    destroy(removeFromParent: boolean) {
        super.destroy(removeFromParent);
    }

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('moveUp', 'arrow-left', 'Move left'),
            new AdditionalButton('moveDown', 'arrow-right', 'Move right')
        ];
    }
}

export {ColumnOptimized};
