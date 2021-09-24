import {HTMLFormComponent} from "fh-forms-handler";

class ValidateMessages extends HTMLFormComponent {
    private isNavigationEnabled: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.isNavigationEnabled = this.componentObj.navigation;
    }

    create() {

        var message = document.createElement('div');
        message.id = this.id;
        message.classList.add('alert');
        switch (this.componentObj.level) {
            case 'OK':
                message.classList.add('alert-success');
                break;
            case 'INFO':
                message.classList.add('alert-info');
                break;
            case 'WARNING':
                message.classList.add('alert-warning');
                break;
            case 'ERROR':
                message.classList.add('alert-danger');
                break;
            case 'BLOCKER':
                message.classList.add('alert-danger');
                break;
            default:
                message.classList.add('alert-danger');
                break;
        }

        this.component = message;
        this.wrap();
        this.addStyles();
        this.display();

        this.setMessages(this.componentObj.validateMessages);
        console.log("ValidateMessages", this.componentObj.htmlAccessibilityRole , this.componentObj.htmlRole)
    };

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'validateMessages':
                        this.setMessages(newValue);
                        break;
                }
            }.bind(this));
        }
    };

    setMessages(messages) {
        while (this.component.firstChild) {
            this.component.removeChild(this.component.firstChild);
        }

        if (typeof messages === 'undefined' || !messages.length) {
            this.setAccessibility('HIDDEN');
            return;
        } else {
            this.setAccessibility('EDIT');
        }
        messages.forEach(function (message) {
            var paragraph = document.createElement('div');
            if (message.elementLabel != null) {
                var strong = document.createElement('span');
                if (this.fhml.needParse(message.elementLabel)) {
                    strong.innerHTML = this.fhml.parse(message.elementLabel + ': ');
                } else {
                    strong.appendChild(document.createTextNode(message.elementLabel + ': '));
                }

                strong.dataset.elementId = message.elementId;
                paragraph.appendChild(strong);
            }
            paragraph.appendChild(document.createTextNode(message.message));
            paragraph.dataset.elementId = message.elementId;
            if (this.isNavigationEnabled && message.elementId != null) {
                paragraph.style.cursor = 'pointer';
                paragraph.addEventListener('click', function (event) {
                    this.formsManager.focusComponent(event.target.dataset.elementId);
                }.bind(this));
            }

            this.component.appendChild(paragraph);
        }.bind(this));
    };
}

export {ValidateMessages};
