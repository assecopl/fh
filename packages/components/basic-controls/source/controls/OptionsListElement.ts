import {HTMLFormComponent} from "fh-forms-handler";

class OptionsListElement extends HTMLFormComponent {
    private readonly onIconClick: any;
    private readonly onChange: any;

    private isChecked: boolean;
    private isTitle: boolean;
    private canCheck: boolean = true;
    private checked: boolean;
    private checkBox: HTMLElement;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.onIconClick = componentObj.onIconClick || null;
        this.onChange = componentObj.onChange || null;
    }

    create() {
        let value = this.componentObj.value ?
            (this.componentObj.value.length > 1 ? this.componentObj.value : '&#160;') : '&#160;';

        let listElement = document.createElement("div");
        listElement['role'] = 'button';
        listElement.classList.add("list-group-item");
        listElement.classList.add("list-group-item-action");
        listElement.classList.add("form-check");
        listElement.classList.add("form-check-inline");
        listElement.classList.add("d-flex");

        let label = document.createElement('label');
        label.classList.add('mr-auto');
        label.classList.add('form-check-label');
        label.innerText = value;
        listElement.appendChild(label);

        listElement.id = this.componentObj.id;
        this.isChecked = this.componentObj.checked;
        this.isTitle = !!this.componentObj.title;
        this.canCheck = true;

        if (this.componentObj.icon) {
            let icon = document.createElement('i');
            let classes = this.componentObj.icon.split(' ');
            icon.classList.add('fa-fw');
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
            if (this.onIconClick) {
                icon.addEventListener('click', function (event) {
                    event.stopPropagation();
                    this.fireEventWithLock('onIconClick', this.onIconClick);
                }.bind(this));
            }

            listElement.appendChild(icon);
        }

        if (this.parent.componentObj.displayCheckbox) {
            let checkBox = document.createElement("input");
            checkBox.checked = this.componentObj.checked;  // set input checked property
            checkBox.type = "checkbox";
            checkBox.classList.add("form-check-input");
            listElement.appendChild(checkBox);
            this.checkBox = checkBox;

            if (this.isTitle) {
                listElement.addEventListener("click", function () {
                    this.toggleCheckAll(this.parent, listElement, checkBox);
                }.bind(this));

                listElement.classList.add("active");
            } else {
                listElement.addEventListener("click", function (e) {
                    e.stopImmediatePropagation();
                    this.toogleCheckOneCheckBox(this.parent, listElement, checkBox);
                }.bind(this));
                checkBox.addEventListener("click", function (e) {
                    e.stopImmediatePropagation();
                    this.toogleCheckOneCheckBox(this.parent, listElement, checkBox);
                }.bind(this));

                if (this.componentObj.checked != this.checked) {
                    this.checked = this.componentObj.checked;
                    this.toogleElement(listElement);
                }

                // @ts-ignore
                this.parent.elements.forEach(function (element) {
                    if (element.isTitle) {
                        element.canCheck = true; // mozliwosc zaznaczenia belki tytulowej
                        element.readOnly = false;
                        element.checkBox.readOnly = false;
                    }
                }.bind(this));
            }
        } else {
            if (!this.isTitle) {
                listElement.addEventListener("click", function () {
                    this.checked = !this.checked;
                    this.toogleElement(listElement);
                }.bind(this));
                if (this.componentObj.checked !== this.checked) {
                    this.checked = this.componentObj.checked;
                    this.toogleElement(listElement);
                }
            } else {
                listElement.classList.add("active");
            }
        }

        this.htmlElement = listElement;
        this.component = listElement;
        this.contentWrapper = (<any>this.parent).contentWrapper;
        (<any>this.parent).elements.push(this);

        this.display();
    };

    destroy(removeFromParent) {
        if (removeFromParent) {
            for (let i = 0; i < (<any>this.parent).elements.length; ++i) {
                let element = (<any>this.parent).elements[i];
                if (element.id === this.id) {
                    (<any>this.parent).elements.splice(i, 1);
                    break;
                }
            }

            // @ts-ignore
            if (this.parent.elements.length == 1) {
                let element = (<any>this.parent).elements[0];
                if (element.isTitle) {
                    if (element.checkBox) {
                        element.checkBox.checked = false;
                    }
                    element.checked = false;
                    element.canCheck = false;
                }
            }
        }

        super.destroy(removeFromParent);
    };

    toogleElement(element) {
        if (this.isTitle) return;
        if (this.checked) {
            element.classList.add("list-group-item-primary");
        } else {
            element.classList.remove("list-group-item-primary");
        }
    };

    toogleCheckOneCheckBox(listComponent, elementHtml, elementCheckboxHtml, forcedValue = null) {
        if (this.canCheck) {
            let checkedValue = forcedValue != null ? forcedValue : !this.checked;
            elementCheckboxHtml.checked = checkedValue;
            this.checked = checkedValue;

            this.toogleElement(elementHtml);
            if (this.onChange) {
                listComponent.fireEvent('onChange');
            }
        }

        if (forcedValue == null) {
            let allChecked = true;
            let allUnchecked = true;

            // @ts-ignore
            this.parent.elements.forEach(function (element) {
                if (!element.isTitle) {
                    allUnchecked = allUnchecked && !element.htmlElement.querySelector('input').checked;
                    allChecked = allChecked && element.htmlElement.querySelector('input').checked;
                }
            });

            // @ts-ignore
            this.parent.elements.forEach(function (element) {
                if (element.isTitle) {
                    element.htmlElement.querySelector('input').checked = allChecked;
                }
            });

            if (allUnchecked) {
                // @ts-ignore
                this.parent.elements.forEach(function (element) {
                    if (element.isTitle) {
                        element.htmlElement.querySelector('input').checked = false;
                    }
                });
            }
        }
    };

    toggleCheckAll(listContainer, listElement, titleCheckboxHtml) {
        if (this.canCheck) {
            let checked = titleCheckboxHtml.checked;

            // @ts-ignore
            this.parent.elements.forEach(function (element) {
                let elementCheckboxHtml = element.htmlElement.querySelector('input');

                if (!element.htmlElement.isTitle) {
                    this.toogleCheckOneCheckBox.call(element, this.parent, element.htmlElement, elementCheckboxHtml, checked);
                }
            }.bind(this));

            if (this.onChange) {
                listContainer.fireEvent('onChange');
            }
        } else {
            listElement.querySelector('input').checked = listElement.checked;
        }
    };
}

export {OptionsListElement};
