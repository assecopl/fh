import {HTMLFormComponent} from "fh-forms-handler";

class Footer extends HTMLFormComponent {
    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }

    create() {
        var footer = document.createElement('div');
        ['fc', 'footer', 'card-footer'].forEach(function (cssClass) {
            footer.classList.add(cssClass);
        });

        var content = document.createElement('div');
        content.classList.add('row');
        content.classList.add('eq-row');

        footer.appendChild(content);

        this.component = footer;
        this.htmlElement = this.component;
        this.contentWrapper = content;
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };
}

export {Footer};