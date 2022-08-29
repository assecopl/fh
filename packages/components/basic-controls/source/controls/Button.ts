import {HTMLFormComponent} from "fh-forms-handler";
import {AdditionalButton} from "fh-forms-handler";
//TODO fix import - import only when needed (move to index.html)
// import '../external/google_recaptchav3_api';


class Button extends HTMLFormComponent {
    private readonly style: any;
    private readonly onClick: any;

    private readonly reCAPTCHA: boolean;
    private readonly captchaSiteKey: string;

    private ButtonPL = {
        "button_icon": "Ikona"
    };
    private ButtonEN = {
        "button_icon": "Icon"
    };

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.style = this.componentObj.style;
        this.onClick = this.componentObj.onClick;

        this.reCAPTCHA = this.componentObj.reCAPTCHA;
        this.captchaSiteKey = this.componentObj.captchaSiteKey;


        this.i18n.registerStrings('pl', this.ButtonPL);
        this.i18n.registerStrings('en', this.ButtonEN);
    }

    create() {
        let label = this.componentObj.label;



        let button = document.createElement('button');
        button.id = this.id;
        ['fc', 'button', 'btn', 'btn-' + this.style, 'btn-block'].forEach(function (cssClass) {
            button.classList.add(cssClass);
        });

        //Check if there is icon inside
        let needParse = this.fhml.needParse(label);
        label = this.resolveLabelAndIcon(label);
        if (needParse && label) {
            //Get raw text from label, remove all html tags. Basicly remove icon tag.
            let text = this.fhml.removeHtmlTags(label);
            if(text.length == 0) {
                //Fill aria-label when there is no text inside label - only icon.
                //Fill with icon default string.
                button.setAttribute("aria-label", this.i18n.__("button_icon"));
            }
        }

        if(this.reCAPTCHA && !this.captchaSiteKey) {
            label = "Site key not provided"
        }

        button.innerHTML = label;
        // + "<div style='width:0px !imporant; height: 0px !imporant;color:transparent;'>h</div>";
        // button.value = "adsa";

        if (this.onClick) {
            button.addEventListener('click', this.onClickEvent.bind(this));
        }

        this.component = button;
        this.hintElement = this.component;
        this.focusableComponent = button;
        this.wrap(true);
        this.addStyles();

        this.display();

        if (this.component.classList.contains('listButton')) {
            this.htmlElement.classList.add('listButtonWrapper');
        } else if (this.component.classList.contains('listOpertationButton')) {
            this.htmlElement.classList.add('listOperationButtonWrapper')
        } else if (this.component.id === 'designerDeleteFormElement') {
            this.htmlElement.classList.add('designerDeleteFormElementWrapper')
        }

        if (this.designMode && this.accessibility === 'VIEW') {
            this.component.classList.add('disabledElement');
            this.component.disabled = false;
        }

    };

    onClickEvent(event) {
        event.stopPropagation();
        if (this._formId === 'FormPreview') {
            this.fireEvent('onClick', this.onClick);
        } else {
            if(this.reCAPTCHA) {

                // @ts-ignore
                grecaptcha.ready(function () {
                    // @ts-ignore
                    grecaptcha.execute(this.captchaSiteKey, {action: 'submit'}).then(function (token) {
                        // Add your logic to submit to your backend server here.
                        this.fireEventWithLock('onClick', this.onClick, [token]);
                    }.bind(this));
                }.bind(this));
            } else {
                this.fireEventWithLock('onClick', this.onClick);
            }
        }
        event.target.blur();
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'label':
                        this.component.innerHTML = this.resolveLabelAndIcon(newValue);
                        break;
                    case 'style':
                        this.component.classList.remove('btn-' + this.style);
                        this.component.classList.add('btn-' + newValue);
                        this.style = newValue;
                }
            }.bind(this));
        }

    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);

        switch (accessibility) {
            case 'DEFECTED':
                this.component.classList.remove('btn-' + this.style);
                this.component.classList.add('btn-danger');
                break;
            default:
                if (this.style !== 'danger') {
                    this.component.classList.remove('btn-danger');
                    this.component.classList.add('btn-' + this.style);
                }
                break;
        }
    };

    resolveLabelPosition() {
        if (this.componentObj.leftPadding != undefined) {
            let spacerElement = document.createElement('div');
            this.htmlElement.appendChild(spacerElement);
            this.htmlElement.insertBefore(spacerElement, this.htmlElement.firstChild);

            this.htmlElement.classList.add('positioned');
            this.htmlElement.classList.add('left');

            this.setInputAndLabelPosition(
                this.componentObj.leftPadding,
                spacerElement,
                this.component
            );
        }
    }

    wrap(skipLabel) {
        super.wrap(skipLabel);
        this.htmlElement.classList.add('form-group');
    };

    resolveLabelAndIcon(label) {
        let l = this.fhml.resolveValueTextOrEmpty(label);
        return l;
    };

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    getAdditionalButtons(): AdditionalButton[] {
        if (this.parent.componentObj.type === 'ButtonGroup') {
            return [
                new AdditionalButton('moveUp', 'arrow-left', 'Move left'),
                new AdditionalButton('moveDown', 'arrow-right', 'Move right')
            ];
        } else {
            return [];
        }
    }

    destroy(removeFromParent) {
        this.focusableComponent = undefined;

        if (this.onClick) {
            this.component.removeEventListener('click', this.onClickEvent.bind(this));
        }

        super.destroy(removeFromParent);
    }

    /**
     * @Override
     */
    public getDefaultWidth():string {
        return null;
    }

}

export {Button};
