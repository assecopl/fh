import {HTMLFormComponent} from "fh-forms-handler";
import * as lodash from 'lodash';

class FHMLStandalone extends HTMLFormComponent {
    private content: string = null;
    private tag: string = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("FHMLStandalone log", componentObj);

        this.buildInnerHTML();
        this.content = componentObj.content;
        this.tag = componentObj.tag;
    }

    create() {
        if (!this.isValidTag(this.tag)) {
            this.tag = 'div';
            console.warn(`Tag ${this.tag} is invalid! Will be replaced by 'DIV'!`);
        }

        const mainElement = document.createElement(this.tag);
        this.buildInnerHTML();
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
                    case 'tag':
                        this.tag = newValue;
                        this.component.tag = this.info;
                        break;
                }
            }.bind(this));
        }
    };

    isValidTag(tag) {
        return document.createElement(tag).toString() !== "[object HTMLUnknownElement]";
    }

    buildInnerHTML() {
            let inner = '';
            console.log('this.componentObj.content', this.componentObj.content);
            if (this.componentObj.content) {
                let content = lodash.escape((this.componentObj.content) ? this.componentObj.content.replace('\\(',
                    '{').replace('\\)', '}') : '');

                content = content.replace(/(?:\r\n|\r|\n)/g, '<br>');
                content = content.replace(/&#39;/g, '\'');
                if (this.fhml.needParse(content)) {
                    inner = this.fhml.parse(content, true);
                } else {
                    inner = content;
                }
            }
            inner = inner.replace('&amp;nbsp;', '&nbsp;');
            this.componentObj.content = inner;
        };
}

export {FHMLStandalone}