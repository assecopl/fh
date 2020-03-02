import {OutputLabel} from "../OutputLabel";
import { HTMLFormComponent } from 'fh-forms-handler';

class DropdownItem extends OutputLabel {
    private labelComponent: any;
    private readonly url: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.url = this.componentObj.url || null;
        if (this.url) {
            this.url = this.util.getPath(this.url);
        }
        this.onClick = this.componentObj.onClick;
        this.labelComponent = null;
    }

    create() {
        let label = document.createElement('span');
        label.id = this.id;
        ['fc', 'outputLabel'].forEach(function (cssClass) {
            label.classList.add(cssClass);
        });

        this.component = label;
        this.buildInnerHTML();
        this.hintElement = this.component;

        this.labelComponent = this.component;
        this.labelComponent.id = this.id + "___label"; // przeniesienie identyfikatora z utworzonej etykiety

        let element = document.createElement('a');
        element.classList.add('dropdown-item');
        element.id = this.id;

        if (this.url) {
            element.href = this.url;
        }
        element.appendChild(this.labelComponent);

        if (this.onClick) {
            element.addEventListener('click', this.onClickEvent.bind(this));
        }

        this.component = element;
        this.htmlElement = this.component;
        // this.wrap(true);
        this.display();
    };

    onClickEvent() {
        this.fireEventWithLock('onClick', this.onClick);
    }

    buildInnerHTML() {
        if (this.labelComponent != null) { // called during label update
            this.component = this.labelComponent;
            super.buildInnerHTML();
            this.component = this.htmlElement;
        } else { // initial call inside OutputLabel.create()
            super.buildInnerHTML();
        }
    }

    destroy(removeFromParent) {
        if (this.onClick) {
            this.component.removeEventListener('click', this.onClickEvent.bind(this));
        }

        super.destroy(removeFromParent);
    }
}

export {DropdownItem};
