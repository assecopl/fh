import {HTMLFormComponent} from "fh-forms-handler";

class Spacer extends HTMLFormComponent {
    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }

    create() {
        this.component = document.createElement('div');
        this.component.id = this.id;
        this.wrap();

        // spacer.classList.add('spacer');
        this.component.parentNode.classList.add('spacer');

        this.addStyles();
        this.display();
    };

    getDefaultWidth(): string {
        return 'md-2';
    }

    wrap() {
        let wrapper = document.createElement('div');
        ['fc', 'wrapper'].forEach(function (cssClass) {
            wrapper.classList.add(cssClass);
        });

        if (this.width) {
            // @ts-ignore
            this.setWrapperWidth(wrapper, undefined, this.width);
        } else {
            wrapper.classList.add('inline');
        }

        wrapper.appendChild(this.component);
        this.htmlElement = wrapper;
        this.contentWrapper = this.component;

    }
}

export {Spacer};
