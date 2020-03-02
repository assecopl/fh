import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";

class Group extends HTMLFormComponent {
    private readonly onClick: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.onClick = this.componentObj.onClick;
        this.componentObj.verticalAlign = this.componentObj.verticalAlign || 'top';
    }

    create() {
        let group = document.createElement('div');
        group.id = this.id;
        ['fc', 'group', 'row', 'eq-row'].forEach(function (cssClass) {
            group.classList.add(cssClass);
        });

        if (this.onClick) {
            group.addEventListener('click', function (event) {
                event.stopPropagation();
                this.fireEventWithLock('onClick', this.onClick, event);
            }.bind(this));
        }

        this.component = group;
        this.hintElement = this.component;
        this.wrap(true);
        this.addStyles();
        /*
         this.htmlElement = this.component;
         this.contentWrapper = this.htmlElement;
         */
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };

    // noinspection JSUnusedGlobalSymbols
    setPresentationStyle(presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    }

    getAdditionalButtons(): any[] {
        return [
            // new AdditionalButton('addDefaultSubcomponent', 'plus', 'Add empty row')
        ];
    }

    getDefaultWidth(): string {
        return "md-12";
    }
}

export {Group};
