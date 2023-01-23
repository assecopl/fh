import {createElement} from 'react';
import * as ReactDOM from 'react-dom';
import {HTMLFormComponent} from "fh-forms-handler";
import {XMLViewerFhDPR} from './XMLViewerFhDPR'

class XMLViewerFhDP extends HTMLFormComponent {
    private content: string = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("XMLViewerFhDP log", componentObj);

        this.content = componentObj.content;
    }

    create() {

        let mainElement = document.createElement('div');
        mainElement = this.styleMainElement(mainElement);
        mainElement.innerHTML = this.content;


        this.component = mainElement;
        this.wrap(false, false);
        this.addStyles();
        this.display();

        this.renderXML();
    }

    update(change) {
        super.update(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'content':
                        this.content = newValue;
                        this.renderXML();
                        break;
                }
            }.bind(this));
        }
    };

    renderXML() {
        ReactDOM.render(createElement(XMLViewerFhDPR, {content: this.content}), this.component);
    }

    styleMainElement(element) {
        element.style.background = "var(--color-bg-xml-view)";
        element.style.padding = "10px";
        element.style.lineHeight = "1.3rem";
        element.style.fontFamily = "'Verdana', monospace";
        return element;
    }
}

export {XMLViewerFhDP}
