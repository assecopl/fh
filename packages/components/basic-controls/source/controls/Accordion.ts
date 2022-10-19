import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";
import 'bootstrap/js/dist/collapse';

class Accordion extends HTMLFormComponent {
    private onGroupChange: any;
    private activeGroup: any;
    private iconOpened: string;
    private iconClosed: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.activeGroup = componentObj.activeGroup;
        this.onGroupChange = this.componentObj.onGroupChange;


        this.iconOpened = this.componentObj.iconOpened? this.componentObj.iconOpened : "";
        this.iconClosed = this.componentObj.iconClosed? this.componentObj.iconClosed : "";
    }

    create() {
        let accordion = document.createElement('div');
        accordion.id = this.id;
        ['fc', 'accordion'].forEach(function (cssClass) {
            accordion.classList.add(cssClass);
        });
        this.component = accordion;
        this.hintElement = accordion;
        this.handlemarginAndPAddingStyles();
        this.wrap(true);

        this.display();

        if (this.componentObj.subelements) {
                (this.componentObj.subelements || []).forEach(function (componentObj) {
                    componentObj['iconClosed'] = this.iconClosed ? this.iconClosed : null;
                    componentObj['iconOpened'] = this.iconOpened ? this.iconOpened : null;
                    this.addComponent(componentObj);
                }.bind(this));
        }
    };

    addComponent(componentObj) {
        let component = this.fh.createComponent(componentObj, this);

        this.components.push(component);
        component.create();
        // if (this.designMode) {
        //     component.component.appendChild(component.toolbox);
        //     component.component.addEventListener('mouseenter', function () {
        //         component.showToolbox();
        //     }.bind(this));
        //     component.component.addEventListener('mouseleave', function () {
        //         component.hideToolbox();
        //     }.bind(this));
        // }
        this.contentWrapper.replaceChild(component.component, component.htmlElement);
        component.component.classList.remove('mb-3');
        component.htmlElement = component.component;

        let button = document.createElement('button');
        button.dataset.index = (this.components.length - 1).toString();
        button.dataset.toggle = 'collapse';
        button.classList.add('btn');
        button.attributes['type'] = 'button';
        button.classList.add('btn-block');
        button.classList.add('collapseBtn');

        let active = this.activeGroup === parseInt(button.dataset.index);
        if (!active) {
            button.classList.add('collapsed');
        }

        // link.dataset.parent = '#' + this.id;

        button.setAttribute('aria-expanded', active ? 'true' : 'false');

        let title = component.htmlElement.querySelector('.card-header');
        if (title != null) {
            let titleContent = title.firstChild;
            title.removeChild(titleContent);
            button.appendChild(titleContent);
            title.appendChild(button);
        }

        if (this.onGroupChange) {
            button.addEventListener('click', function (event) {
                if (this.activeGroup == button.dataset.index) {
                    event.preventDefault();
                }
                this.activeGroup = button.dataset.index;
                this.changesQueue.queueValueChange(this.activeGroup);
                this.fireEvent('onGroupChange', this.onGroupChange);
            }.bind(this));
        }

        let body = component.htmlElement.querySelector('.card-body');
        let collapseWrapper = document.createElement('div');
        collapseWrapper.dataset.parent = '#' + this.id;
        collapseWrapper.classList.add('collapse');
        if (active || this.designMode) {
             collapseWrapper.classList.add('show');
        }

        let collapsibleBtn = 'collapsible-btn_' + Math.floor((Math.random() * 10000) + 1);
        collapseWrapper.classList.add(collapsibleBtn);
        let checkIfCollapsibleBtnExists = document.querySelectorAll('.' + collapsibleBtn);

        if (checkIfCollapsibleBtnExists.length > 0) {
            collapseWrapper.classList.remove(collapsibleBtn);
            collapsibleBtn = 'collapsible-btn_' + Math.floor((Math.random() * 10000) + 1);
            collapseWrapper.classList.add(collapsibleBtn);
        }

        button.dataset.target = '.' + collapsibleBtn;
        button.setAttribute('aria-controls', collapsibleBtn);
        this.setButtonAccessibility(button, component.accessibility);

        if(this.iconClosed && this.iconOpened){
            const icon = document.createElement('i');
            icon.classList.add('fa');
            icon.classList.add('mx-2');
            icon.classList.add('collapsibleIcon');

            const iconClosed:any = icon.cloneNode(true);

            iconClosed.classList.add(this.iconClosed);
            iconClosed.classList.add("collapsibleIconClosed");

            icon.classList.add(this.iconOpened);
            icon.classList.add("collapsibleIconOpened");

            button.classList.add("d-flex")
            button.classList.add("align-items-center")
            button.appendChild(icon);
            button.appendChild(iconClosed);
        }



        collapseWrapper.appendChild(body);
        component.component.appendChild(collapseWrapper);
    };

    update(change) {
        super.update(change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {
                case 'activeGroup':
                    this.collapse(this.activeGroup);
                    this.activeGroup = newValue;
                    this.show(newValue);
                    break;
            }
        }.bind(this));
    };

    getAdditionalButtons(): AdditionalButton[] {
        return [
            new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add panel')
        ];
    }

    setButtonAccessibility(button, accessibility) {
        if (accessibility !== 'HIDDEN') {
            button.classList.remove('d-none');
            button.classList.remove('invisible');
        }
        switch (accessibility) {
            case 'EDIT':
                button.disabled = false;
                break;
            case 'VIEW':
                button.disabled = true;
                break;
            case 'HIDDEN':
                if(this.invisible){
                    button.classList.add('invisible');
                } else {
                    button.classList.add('d-none');
                }
                break;
            case 'DEFECTED':
                button.disabled = true;
                button.title = 'Broken control';
                break;
        }
    };

    collapse(groupIdx) {
        // @ts-ignore
        let button = this.acomponents[groupIdx].component.querySelector('button');
        button.setAttribute('aria-expanded', 'false');
        button.classList.add("collapsed");
        // @ts-ignore
        let div = this.components[groupIdx].component.querySelector('div.collapse');
        div.classList.remove("show");
    }

    show(groupIdx) {
        // @ts-ignore
        let button = this.components[groupIdx].component.querySelector('button');
        button.setAttribute('aria-expanded', 'true');
        button.classList.remove("collapsed");
        // @ts-ignore
        let div = this.components[groupIdx].component.querySelector('div.collapse');
        div.classList.add("show");
    }
}

export {Accordion};
