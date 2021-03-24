import {HTMLFormComponent} from "fh-forms-handler";
import 'bootstrap/js/dist/collapse';
import {PanelGroup} from "../controls/PanelGroup";
import {PanelGroupWrapperEN} from "../controls/i18n/PanelGroupWrapper/translations.en";
import {PanelGroupWrapperPL} from "../controls/i18n/PanelGroupWrapper/translations.pl";

class PanelGroupWrapper extends HTMLFormComponent {

    protected components: PanelGroup[];

    private onGroupChange: any;
    private activeGroup: any;
    private iconOpened: string;
    private iconClosed: string;

    private toggleAll:boolean = null;
    protected checkboxElement:HTMLInputElement = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent)

        this.i18n.registerStrings('en',PanelGroupWrapperEN);
        this.i18n.registerStrings('pl', PanelGroupWrapperPL);

        this.activeGroup = componentObj.activeGroup;
        this.onGroupChange = this.componentObj.onGroupChange;


        this.iconOpened = this.componentObj.iconOpened? this.componentObj.iconOpened : "";
        this.iconClosed = this.componentObj.iconClosed? this.componentObj.iconClosed : "";
        this.toggleAll = this.componentObj.toggleAll ? Boolean(this.componentObj.toggleAll) : null;
    }

    create() {
        let accordion = document.createElement('div');

        accordion.id = this.id;
        ['fc', 'panelGroupWrapper', 'row'].forEach(function (cssClass) {
            accordion.classList.add(cssClass);
        });

        /**
         * Toggle All checkbox
         */
        let checkboxDiv = document.createElement('div');
        let formCheck = document.createElement('div');
        let label = document.createElement('label');
        checkboxDiv.classList.add('col-12')
        formCheck.classList.add('form-check')
        formCheck.classList.add('form-group')
        let checkbox = document.createElement('input');
        checkbox.value = "true"
        checkbox.type = 'checkbox'
        checkbox.checked == this.toggleAll ? false : true;
        checkbox.style.marginTop = '0px';
        checkbox.style.marginRight = '10px';
        checkbox.style.marginLeft = '0px';
        checkbox.style.display = 'block';
        checkbox.classList.add('form-check-input');
        label.classList.add('form-check-label');
        label.style.marginLeft = '25px';

        label.innerText = this.i18n.__('panelgroupwrapper.show_all')

        formCheck.appendChild(checkbox)
        formCheck.appendChild(label)
        checkboxDiv.appendChild(formCheck)
        accordion.appendChild(checkboxDiv)

        checkbox.addEventListener('change', function () {
            if(this.toggleAll === true) {
                this.collapseAll();
            } else {
                this.showAll();
            }
        }.bind(this))

        this.checkboxElement = checkbox;
        this.component = accordion;
        this.hintElement = accordion;
        this.handlemarginAndPAddingStyles();
        this.wrap(true);

        this.display();

        if (this.componentObj.subelements) {
                (this.componentObj.subelements || []).forEach(function (componentObj, idx) {
                    componentObj = this.updateSubcomponentData(componentObj, idx);
                    this.addComponent(componentObj, idx);
                }.bind(this));
        }
        this.updateToggleAllStatus();
    };


    public updateSubcomponentData(componentObj, idx) {

        //Set PanelGroup as collapsable.
        componentObj["collapsible"] = true;

        //Ovverride icons if they are set.
        componentObj['iconClosed'] = this.iconClosed ? this.iconClosed : componentObj['iconClosed'];
        componentObj['iconOpened'] = this.iconOpened ? this.iconOpened : componentObj['iconOpened'];

        if(this.activeGroup == idx){
            componentObj['collapsed'] = false;
        }

        if(this.toggleAll !== null){
                componentObj['collapsed'] = this.toggleAll;
        }

        return componentObj

    };

    update(change) {
        super.update(change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'activeGroup':
                    this.collapseAll();
                    this.activeGroup = newValue;
                    this.components[this.activeGroup].uncollapse()
                    break;
                case 'toggleAll':
                    this.toggleAll = newValue ? Boolean(newValue) : this.toggleAll;
                    if(newValue === true) {
                        this.showAll();
                    } else {
                        this.collapseAll();
                    }

                    break;
            }
        }.bind(this));
    };

    collapseAll() {
       this.components.forEach((value, index) => {
           value.collapse()
       })
        this.toggleAll = false;
        this.checkboxElement.checked  = false;
    }

    showAll() {
        this.components.forEach((value, index) => {
            value.uncollapse()
        })
        this.toggleAll = true;
        this.checkboxElement.checked  = true;
    }

    updateToggleAllStatus() {
        let closedCount = 0;
        this.components.forEach((value, index) => {
           if(value.collapsed === true) {
               closedCount++;
           }
        })
        if(0 == closedCount) {
            this.checkboxElement.checked = true
            this.toggleAll = true;
        }else if(this.components.length == closedCount) {
            this.checkboxElement.checked = false
            this.toggleAll = false;
        } else {
            this.checkboxElement.checked = null;
            this.toggleAll = null;
        }
    }
}

export {PanelGroupWrapper};
