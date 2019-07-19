import {HTMLFormComponent} from "fh-forms-handler";
import {FormComponent} from "fh-forms-handler";

class OptionsList extends HTMLFormComponent {
    private onIconClick: any;
    private elements: any = [];

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.onIconClick = this.componentObj.onIconClick || null;
    }

    create() {
        var list = document.createElement("div");

        list.classList.add("list-group");
        list.classList.add('mb-3');
        list.id = this.id;
        this.component = list;

        this.wrap(true);

        this.contentWrapper = list;
        this.addStyles();
        this.display();

        if (this.componentObj.height) {
            list.style.height = "" + this.componentObj.height + 'px';
            list.style.overflowY = "auto";
        }

        // invoke subelements creation
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };

    extractChangedAttributes() {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        var attrs = {};
        if (this.elements) {
            /*var allChanges = null;*/
            var elementsChanges = [];
            this.elements.forEach(function (element) {
                if (element.checked != undefined) {
                    if (element.isChecked !== element.checked) {
                        element.isChecked = element.checked;
                        elementsChanges.push({
                            id: element.id, value: element.checked
                        });
                    }
                }
            }.bind(this));

            if (elementsChanges.length > 0) {
                // may be changed to attr per
                attrs[FormComponent.VALUE_ATTRIBUTE_NAME] = elementsChanges;
            }
        }
        return attrs;
    };

    addComponent(componentObj) {
        componentObj.onIconClick = this.onIconClick;
        var component = this.fh.createComponent(componentObj, this);

        this.components.push(component);
        component.create();
    };
}

export {OptionsList};