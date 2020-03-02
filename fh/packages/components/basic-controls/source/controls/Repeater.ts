import {HTMLFormComponent} from "fh-forms-handler";

class Repeater extends HTMLFormComponent {
    private referenceNode: any;
    private rWrapper: any;


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        if (this.parent instanceof HTMLFormComponent) {
            this.container = this.parent.contentWrapper;
        } else {
            this.container = (<any>this.parent).container;
        }

        this.contentWrapper = this.container;
        this.referenceNode = null;
    }

    create() {
        if (this.designMode == true || this.componentObj.width != null) {
            var repeater = document.createElement('div');
            repeater.id = this.id;
            ['fc', 'row', 'eq-row', 'repeater'].forEach(function (cssClass) {
                repeater.classList.add(cssClass);
            });

            this.component = repeater;
            this.wrap(true);
            this.addStyles();
            this.display();
        }

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };

    setAccessibility(accessibility) {
        if (this.htmlElement) {
            if (accessibility === 'HIDDEN') {
                if(this.invisible){
                    this.htmlElement.classList.add('invisible');
                } else {
                    this.htmlElement.classList.add('d-none');
                }
            } else {
                this.htmlElement.classList.remove('d-none');
                this.htmlElement.classList.remove('invisible');
            }
        }
        this.accessibility = accessibility;
    }

    // noinspection JSUnusedGlobalSymbols
    setPresentationStyle(presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    }
}

export {Repeater};