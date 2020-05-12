import * as moment from 'moment';
import 'imports-loader?moment,define=>false,exports=>false!../../external/inputmask';
import 'imports-loader?moment,define=>false,exports=>false!../../external/bootstrap-datepicker';
import {InputText} from "./InputText";
import {InputDatePL} from './i18n/InputDate.pl';
import {InputDateEN} from './i18n/InputDate.en';
import {HTMLFormComponent, FormComponent} from 'fh-forms-handler';
import {FhContainer} from "fh-forms-handler";
import {LanguageChangeObserver} from "fh-forms-handler";

class InputDate extends InputText implements LanguageChangeObserver {
    protected backendFormat: string;
    protected format: string;
    protected maskEnabled: boolean;
    protected datepickerEnabled: boolean;
    protected highlightToday: boolean;
    protected inputDateHeight: any;

    /**
     * Czy uzywac trybu scislego biblioteki momentjs
     * Na przykład  czy data 2016-12-24T00:00:00.000+00:00 będzie nieprawidłowa dla maski YYYY-MM-DD
     * @type {boolean}
     */
    private static MOMENT_JS_STRICT_MODE: boolean = true;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.backendFormat = 'YYYY-MM-DD';
        this.format = this.componentObj.format || 'YYYY-MM-DD';
        // @ts-ignore
        this.keySupport = FhContainer.get("FormComponentKeySupport")(this.componentObj, this);
        this.mask = this.format.replace(/[a-zA-Z]/g, '9');
        this.maskEnabled = this.componentObj.maskEnabled || false;
        this.maskDynamic = this.componentObj.maskDynamic || false;
        this.highlightToday = this.componentObj.highlightToday || false;
        this.inputDateHeight = this.componentObj.height;
        this.datepickerEnabled = false;

        this.i18n.registerStrings('pl', InputDatePL);
        this.i18n.registerStrings('en', InputDateEN);

        (<any>$.fn.datepicker).dates['pl'] = {
            days: ["Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"],
            daysShort: ["Niedz.", "Pon.", "Wt.", "Śr.", "Czw.", "Piąt.", "Sob."],
            daysMin: ["Ndz.", "Pn.", "Wt.", "Śr.", "Czw.", "Pt.", "Sob."],
            months: ["Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"],
            monthsShort: ["Sty.", "Lut.", "Mar.", "Kwi.", "Maj", "Cze.", "Lip.", "Sie.", "Wrz.", "Paź.", "Lis.", "Gru."],
            today: "Dzisiaj",
            weekStart: 1,
            clear: "Wyczyść",
            format: "dd.mm.yyyy"
        };
        (<any>$.fn.datepicker).dates['en'] = {
            days: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            daysShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            daysMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
            months: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            monthsShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            today: "Today",
            clear: "Clear",
            format: "mm/dd/yyyy",
            titleFormat: "MM yyyy", /* Leverages same syntax as 'format' */
            weekStart: 0
        };
    }

    create() {
        let input = document.createElement('input');
        ['fc', 'inputDate', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        input.id = this.id;
        input.type = 'text';

        if (this.inputDateHeight && parseInt(this.inputDateHeight) > 34) {
            input.style.height = this.inputDateHeight;
            input.appendChild(document.createTextNode(this.rawValue));
        }

        input.placeholder = this.makePlaceholder(this.format);
        input.value = InputDate.toDateOrLeave(this.rawValue, this.backendFormat, this.format);

        // must be before onInput/onChange events
        this.keySupportCallback = this.keySupport.addKeyEventListeners(input);

        // noinspection JSIgnoredPromiseFromCall
        this.i18n.subscribe(this);

        this.input = input;
        this.focusableComponent = input;
        this.component = input;
        this.hintElement = this.component;

        this.wrap(false, true);
        this.addStyles();

        this.createAddon();

        $(this.input).on('input', this.onValueInput.bind(this));
        $(this.input).on('blur', this.inputBlurEvent.bind(this));
        $(this.input).on('change', this.inputChangeEvent.bind(this));

        if (this.accessibility == 'EDIT') {
            this.applyMask();
            this.applyDatepicker();
        }

        this.display();
    };

    applyDatepicker() {
        if (!this.datepickerEnabled) {
            let options = {
                forceParse: false, // prevent datepicker from making dates from entered invalid / empty text
                format: this.format.toLowerCase(),
                weekStart: 1,
                todayHighlight: false,
                zIndexOffset: 1030,
                enableOnReadonly: false,
                language: this.i18n.selectedLanguage,
                autoclose: 1,
                showOnFocus: 0,
                inline: false
            };
            if (this.highlightToday) {
                options.todayHighlight = true;
            }
            try {
                // @ts-ignore
                $(this.inputGroupElement).datepicker(options);
                this.datepickerEnabled = true;
                this.htmlElement.classList.add('fc-editable');
            } catch (e) {
                console.error(e);
            }
        }
    }

    disableDatepicker() {
        if (this.datepickerEnabled) {
            let component = (<any>$(this.inputGroupElement));
            component.datepicker('destroy');
            this.htmlElement.classList.remove('fc-editable');
            this.datepickerEnabled = false;
        }
    }

    inputBlurEvent() {
        this.input.value = InputDate.toDateOrLeave(this.input.value, this.format, this.format);
        this.updateModel();
    }

    inputChangeEvent() {
        this.input.value = InputDate.toDateOrLeave(this.input.value, this.format, this.format);
        this.updateModel();
    }

    createAddon() {
        let addon = document.createElement('span');
        addon.classList.add('input-group-text');
        addon.innerText = ' ';

        let icon = document.createElement('i');

        if (this.componentObj.icon) {
            let classes = this.componentObj.icon.split(' ');
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
        } else {
            icon.classList.add('fa');
            icon.classList.add('fa-calendar');
        }

        addon.appendChild(icon);

        let addonDiv = document.createElement('div');
        addonDiv.classList.add('input-group-prepend');
        addonDiv.classList.add('datepickerbutton');
        addonDiv.appendChild(addon);

        this.inputGroupElement.classList.add('date');
        if (this.componentObj.iconAlignment === 'BEFORE') {
            this.inputGroupElement.insertBefore(addonDiv, this.inputGroupElement.firstChild);
        } else if (this.componentObj.iconAlignment === 'AFTER') {
            addonDiv.classList.remove('input-group-prepend');
            addonDiv.classList.add('input-group-append');
            this.inputGroupElement.appendChild(addonDiv);
        } else {
            this.inputGroupElement.insertBefore(addonDiv, this.inputGroupElement.firstChild);
        }

        this.component.classList.add('datepickerInput');
    }

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);

        switch (accessibility) {
            case 'EDIT':
                this.applyDatepicker();
                break;
            case 'VIEW':
                this.disableDatepicker();
                break;
            case 'DEFECTED':
                this.disableDatepicker();
                break;
        }

        $(this.input).prop("disabled", this.component.disabled);
    }

    protected makePlaceholder(format: string) {
        if (this.accessibility == 'EDIT') {
            format = format.replace(new RegExp('Y', 'g'), this.__('year_character').innerText);
            format = format.replace(new RegExp('M', 'g'), this.__('month_character').innerText);
            format = format.replace(new RegExp('D', 'g'), this.__('day_character').innerText);

            return format;
        } else {
            return '';
        }
    }

    update(change) {
        super.update(change);

        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'rawValue':
                    this.input.value = InputDate.toDateOrLeave(newValue, this.backendFormat, this.format);
                    this.rawValue = InputDate.toDateOrLeave(newValue, this.backendFormat, this.backendFormat);
                    this.oldValue = InputDate.toDateOrLeave(newValue, this.backendFormat, this.backendFormat);
                    break;
            }
        }.bind(this));
    };

    onValueInput() {
        if (this.maskEnabled) {
            if (this.maskPlugin.isComplete(this.input)) {
                this.updateModel();
            }
        } else {
            this.updateModel();
        }
    }

    updateModel() {
        this.rawValue = InputDate.toDateOrLeave(this.input.value, this.format, this.backendFormat);
        if (this.onChange != null && this.oldValue !== this.rawValue) {
            this.fireEventWithLock('onChange', this.onChange);
        }
    };

    static isDateValid(date, sourceFormat) {
        return moment(date, [sourceFormat], InputDate.MOMENT_JS_STRICT_MODE).isValid();
    };

    static toDateOrLeave(text, sourceFormat, targetFormat) {
        return InputDate.isDateValid(text, sourceFormat) ? moment.utc(text, [sourceFormat], InputDate.MOMENT_JS_STRICT_MODE).format(targetFormat) : text;
    };

    static toDateOrClear(text, sourceFormat, targetFormat) {
        return InputDate.isDateValid(text, sourceFormat) ? moment.utc(text, [sourceFormat], InputDate.MOMENT_JS_STRICT_MODE).format(targetFormat) : '';
    };

    extractChangedAttributes() {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        let attrs = {};
        if (this.rawValue !== this.oldValue) {
            attrs[FormComponent.VALUE_ATTRIBUTE_NAME] = this.rawValue;
            this.oldValue = this.rawValue;
        }

        return attrs;
    };

    protected disableMask() {
        if (this.maskEnabled && this.inputmaskEnabled) {
            // @ts-ignore
            Inputmask.remove(this.input);
            this.inputmaskEnabled = false;
        }
    }

    protected applyMask() {
        if (this.maskEnabled && !this.inputmaskEnabled) {
            // @ts-ignore
            this.maskPlugin = Inputmask({
                clearMaskOnLostFocus: false,
                greedy: false,
                jitMasking: this.maskDynamic,
                placeholder: this.input.placeholder,
                definitions: {},
                insertMode: true,
                alias: "date",
                mask: this.mask
            }).mask(this.input);
            this.inputmaskEnabled = true;
        }
    };

    // noinspection JSUnusedGlobalSymbols
    languageChanged(code: string) {
        this.disableDatepicker();
        this.applyDatepicker();
    }

    wrap(skipLabel, isInputElement) {
        super.wrap(skipLabel, isInputElement);
    }

    destroy(removeFromParent: boolean) {
        // noinspection JSIgnoredPromiseFromCall
        this.i18n.unsubscribe(this);

        this.maskPlugin = null;

        $(this.input).off('input', this.onValueInput.bind(this));
        $(this.input).off('blur', this.inputBlurEvent.bind(this));
        $(this.input).off('change', this.inputChangeEvent.bind(this));


        this.disableDatepicker();
        this.disableMask();

        super.destroy(removeFromParent);
    }
}

export {InputDate};
