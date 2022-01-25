import {HTMLFormComponent} from "fh-forms-handler";
import * as sanitizeHtml from 'sanitize-html/dist/sanitize-html';

class HtmlViewExtended extends HTMLFormComponent {
    protected code: string;
    protected element: HTMLDivElement;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.code = this.sanitizeCode(this.componentObj.text || '');
    }

    sanitizeCode(code: string) {
        // @ts-ignore
        return sanitizeHtml(code, {
            allowedTags: ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'blockquote', 'p', 'a', 'ul', 'ol',
                'nl', 'li', 'b', 'i', 'strong', 'em', 'strike', 'code', 'hr', 'br', 'div',
                'table', 'thead', 'caption', 'tbody', 'tr', 'th', 'td', 'pre', 'iframe', 'center', 'span'],
            allowedAttributes: {
                '*': ['class', 'style'],
                a: ['href', 'name', 'target'],
                img: ['src']
            },
            selfClosing: ['img', 'br', 'hr', 'area', 'base', 'basefont', 'input', 'link', 'meta'],
            allowedSchemes: ['http', 'https', 'ftp', 'mailto'],
            allowedSchemesByTag: {},
            allowedSchemesAppliedToAttributes: ['href', 'src', 'cite'],
            allowProtocolRelative: true
        });
    }

    create() {
        console.log("******* HTMLViewExt 0.98 **********")
        this.element = document.createElement('div');
        this.element.id = this.id + "_element";
        this.element.innerHTML = this.code;

        this.component = this.element;
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;

        this.wrap(false, false);
        this.display();
    };

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                if (name === 'text') {
                    this.element.innerHTML = this.sanitizeCode(newValue);
                }
            }.bind(this));
        }
    }
}

export {HtmlViewExtended};
