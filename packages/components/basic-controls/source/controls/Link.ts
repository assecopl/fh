import {HTMLFormComponent} from "fh-forms-handler";

class Link extends HTMLFormComponent {
    private stickedLabel: boolean;
    private link:any;
    private inner:any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        if (componentObj.labelPosition == null) {
            componentObj.labelPosition = 'left';
            componentObj.inputSize = undefined;
            this.stickedLabel = true;
        }

    }

    create() {
        let link = document.createElement('a');

        let isIcon = !!this.componentObj.icon;
        let inner;

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
            let icon = document.createElement('i');
            let classes = this.componentObj.icon.split(' ');
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

        let additionalWrapper = document.createElement('div');
        additionalWrapper.classList.add('link-wrapper');
        additionalWrapper.appendChild(link);
        this.link = link;
        this.inner = inner;
        this.component = additionalWrapper;
        this.hintElement = this.component;
        this.wrap();
        if (this.stickedLabel) {
            this.htmlElement.classList.add('stickedLabel');
        }
        this.addStyles();
        this.display();
    };

    wrap() {
        let wrapper = document.createElement('div');
        ['fc', 'wrapper', 'form-group'].forEach(function (cssClass) {
            wrapper.classList.add(cssClass);
        });

        if (this.width) {
            // @ts-ignore
            this.setWrapperWidth(wrapper, undefined, this.width);
        } else {
            wrapper.classList.add('inline');
        }

        if (this.componentObj.value) {
            let label = document.createElement('label');
            let labelValue = this.fhml.resolveValueTextOrEmpty(this.componentObj.value);
            let labelId = this.id + '_label';
            label.id = labelId;
            label.classList.add('col-form-label');
            label.classList.add('sr-only');
            label.innerHTML = labelValue;
            label.setAttribute('for', this.id);
            label.setAttribute('aria-label', labelValue);
            this.component.insertBefore(label, this.component.firstChild);
            this.labelElement = label;
            this.component.setAttribute('aria-describedby', label.id);
        }

        wrapper.appendChild(this.component);
        this.htmlElement = wrapper;
        this.contentWrapper = this.component;

    };

    update(change) {
        super.update(change);
        $.each(change.changedAttributes || [], function (name, newValue) {
            switch (name) {

                case 'url':
                    this.componentObj.url = newValue;
                    this.link.href = this.processURL(this.util.getPath(this.componentObj.url));
                    let isIcon = !!this.componentObj.icon;
                    if (this.inner !== "") {
                        this.link.innerHTML += this.inner;
                    } else if (!isIcon) {
                        this.link.innerHTML += this.link.href;
                    }
                    break;
            }
        }.bind(this));
    };

    /**
     * @Override
     */
    public getDefaultWidth():string {
        return "md-2";
    }
}

export {Link};
