import {HTMLFormComponent} from "fh-forms-handler";

class Link extends HTMLFormComponent {
    private stickedLabel: boolean;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        if (componentObj.labelPosition == null) {
            componentObj.labelPosition = 'left';
            componentObj.inputSize = undefined;
            this.stickedLabel = true;
        }

    }

    create() {
        var link = document.createElement('a');

        var isIcon = !!this.componentObj.icon;
        var inner;

        link.id = this.id;
        link.classList.add('fc');
        link.classList.add('link');
        link.href = this.processURL(this.util.getPath(this.componentObj.url));
        if (this.componentObj.newWindow) {
            link.target = '_blank';
        }

        inner = (this.componentObj.value) ? this.componentObj.value.replace('\\(',
            '{').replace('\\)', '}') : '';

        if (isIcon) {
            var icon = document.createElement('i');
            var classes = this.componentObj.icon.split(' ');
            switch (classes[0]) {
                case 'fa':
                    icon.classList.add('fa-fw');
                    break;
            }
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }

            if (this.componentObj.iconAlignment && this.componentObj.iconAlignment.toLowerCase() === 'after') {
                inner = inner + icon.outerHTML;
            } else {
                inner = icon.outerHTML + inner;
            }
        }

        if (inner !== "") {
            link.innerHTML += inner;
        } else if (!isIcon) {
            link.innerHTML += link.href;
        }

        var additionalWrapper = document.createElement('div');
        additionalWrapper.classList.add('link-wrapper');
        additionalWrapper.appendChild(link);
        this.component = additionalWrapper;
        this.hintElement = this.component;
        this.wrap(true);
        if (this.stickedLabel) {
            this.htmlElement.classList.add('stickedLabel');
        }
        this.addStyles();
        this.display();
    };

    wrap(skipLabel) {
        super.wrap(skipLabel);
        this.htmlElement.classList.add('form-group');
    };

    update(change) {
        super.update(change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            // no special attributes supported
        }.bind(this));
    };

    /**
     * @Override
     */
    public getDefaultWidth():string {
        return null;
    }
}

export {Link};