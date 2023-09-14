import {HTMLFormComponent} from "fh-forms-handler";
import getDecorators from "inversify-inject-decorators";
import {FH} from "fh-forms-handler";
import {FhContainer} from "fh-forms-handler";

let {lazyInject} = getDecorators(FhContainer);

class TableCell extends HTMLFormComponent {
    @lazyInject("FH")
    protected fh: FH;

    private horizontalAlign: any;
    private verticalAlign: any;
    private rowspan: any;
    private readonly ieFocusFixEnabled: boolean;
    public designMode: boolean;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.rowspan = this.componentObj.rowspan || null;
        this.horizontalAlign = this.componentObj.horizontalAlign || null;
        this.verticalAlign = this.componentObj.verticalAlign || null;
        this.designMode = this.componentObj.designMode || (this.parent != null && this.parent.designMode);
        // @ts-ignore
        this.ieFocusFixEnabled = this.parent.parent.ieFocusFixEnabled;
    }

    create() {
        let cell = null;
        // @ts-ignore
        if (this.fh.isIE() && this.ieFocusFixEnabled == true) {
            cell = document.createElement('td-a');
        } else {
            cell = document.createElement('td');
        }

        cell.id = this.id;
        cell.classList.add('tableCell');
        if (this.rowspan) {
            // @ts-ignore
            cell.rowSpan = this.rowspan;
        }
        if (this.horizontalAlign) {
            cell.classList.add('col-halign-' + this.horizontalAlign.toLowerCase())
        }
        if (this.verticalAlign) {
            cell.classList.add('col-valign-' + this.verticalAlign.toLowerCase())
        }

        var row = null;
        if (this.fh.isIE() && this.ieFocusFixEnabled == true) {
            row = document.createElement('div-a');
        } else {
            row = document.createElement('div');
        }
        row.classList.add('row');
        row.classList.add('mx-0');
        cell.appendChild(row);

        this.component = cell;
        this.htmlElement = this.component;
        this.contentWrapper = row;

        this.display();

        if (this.componentObj.visibility === "HIDDEN") {
            cell.classList.add('d-none');
        }

        if (this.componentObj.tableCells) {
            this.addComponents(this.componentObj.tableCells);
        }
    };

    applyChange(change) {
        // console.log('-------------------', this.id, this.combinedId);
        if (this.id === change.formElementId) {
            // console.log('--', this.id, change.formElementId, change);
            this.update(change);
        } else {
            this.components.forEach(function (component) {
                // console.log(component.id, change.formElementId, change);
                component.applyChange(change);
            });
        }
    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);

        if (!this.designMode) {
            this.htmlElement.classList.remove('disabled', 'fc-disabled');
            this.htmlElement.classList.add('fc-editable');
        }
        // remember accessibility changes in source compomenet objects to keep this change after refreshing the table
        this.componentObj.accessibility = accessibility;
        this.componentObj.visibility = accessibility;
    };
}

export {TableCell};
