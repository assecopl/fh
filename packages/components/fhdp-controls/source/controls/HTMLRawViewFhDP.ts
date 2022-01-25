import {HTMLFormComponent} from "fh-forms-handler";

class HTMLRawViewFhDP extends HTMLFormComponent {
    private content: string = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("HTMLRawViewFhDP log", componentObj);
        this.content = componentObj.content;
    }

    create() {

        const mainElement = document.createElement('div');
        mainElement.innerHTML = this.content;

        this.component = mainElement;
        this.wrap(false, false);
        this.addStyles();
        this.display();
    }

    update(change) {
        super.update(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'content':
                        this.buildInnerHTML();
                        this.content = newValue;
                        this.component.content = this.content;
                        break;
                }
            }.bind(this));
        }
    };
}

export {HTMLRawViewFhDP}
