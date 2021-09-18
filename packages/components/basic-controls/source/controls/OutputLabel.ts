import {HTMLFormComponent} from "fh-forms-handler";
import * as lodash from "lodash";
import getDecorators from "inversify-inject-decorators";
import {FhContainer} from "fh-forms-handler";
import {FH} from "fh-forms-handler";

let {lazyInject} = getDecorators(FhContainer);

class OutputLabel extends HTMLFormComponent {
    @lazyInject("FH")
    protected fh: FH;

    private icon: string;
    private iconAlignment: string;
    protected onClick: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        this.onClick = this.componentObj.onClick;
        this.icon = this.componentObj.icon;
        this.iconAlignment = this.componentObj.iconAlignment;
    }

    create() {
        let label = null;
        // @ts-ignore
        if (this.fh.isIE() && this.parent.ieFocusFixEnabled == true) {
            label = document.createElement('span-a');
        } else {
            label = document.createElement('span');
        }
        label.id = this.id;

        // output labels for table columns in Designer
        if (this.designMode) {
            let labelId = label.id.split("_");
            let columnLabelMarkers = ['value', 'based', 'label'];
            let columnValueLabel = "";

            columnLabelMarkers.forEach(attribute => {
                if (labelId.indexOf(attribute) !== -1) {
                    columnValueLabel += attribute;
                }
            });

            if (columnValueLabel === 'valuebasedlabel') {
                label.classList.add('valueBasedLabel');
            }

            let columnId = labelId.slice(0,3).join("_");
            label.dataset.columnId = columnId;
        }


        ['fc', 'outputLabel'].forEach(function (cssClass) {
            label.classList.add(cssClass);
        });

        if (this.onClick) {
            label.addEventListener('click', function (event) {
                event.stopPropagation();
                this.fireEventWithLock('onClick', this.onClick, event);
                event.target.blur();
            }.bind(this));
        }

        this.component = label;
        this.buildInnerHTML();
        this.hintElement = this.component;

        this.wrap();
        this.addStyles();
        this.display();
    };

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'value':
                        this.componentObj.value = newValue;
                        this.buildInnerHTML();
                        break;
                    case 'url':
                        this.componentObj.url = newValue;
                        this.buildInnerHTML();
                        break;
                }
            }.bind(this));
        }
    };

    buildInnerHTML() {
        let inner = '';
        if (this.componentObj.value) {
            let value = lodash.escape((this.componentObj.value) ? this.componentObj.value.replace('\\(',
                '{').replace('\\)', '}') : '');

            value = value.replace(/(?:\r\n|\r|\n)/g, '<br>');
            value = value.replace(/&#39;/g, '\'');
            if (this.fhml.needParse(value)) {
                inner = this.fhml.parse(value, true);
            } else {
                inner = value;
            }
        }
        inner = inner.replace('&amp;nbsp;', '&nbsp;');

        if (this.icon) {
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

            if (this.iconAlignment && this.iconAlignment == 'AFTER') {
                inner = inner + icon.outerHTML;
            } else {
                inner = icon.outerHTML + inner;
            }
        }

        this.component.innerHTML = inner;
    };

    protected wrap(skipLabel: boolean = false, isInputElement: boolean = false) {
        let wrappedComponent = this.innerWrap();
        // @ts-ignore
        if (this.fh.isIE() && this.parent.ieFocusFixEnabled == true) {
            let wrapper = document.createElement('div-a');
            ['fc', 'wrapper'].forEach(function (cssClass) {
                wrapper.classList.add(cssClass);
            });

            this.wrapInner(wrapper, wrappedComponent, skipLabel, isInputElement);
        } else {
            super.wrap();
        }
    }
}

export {OutputLabel};
