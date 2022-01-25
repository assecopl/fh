import * as moment from 'moment';
import * as Inputmask from 'fh-basic-controls/dist/source/external/inputmask';
import 'fh-basic-controls/dist/source/external/bootstrap-datepicker';
import { InputTextFhDP } from './InputTextFhDP';
import {InputDateFhDPPl} from './i18n/InputDateFhDP.pl';
import {InputDateFhDPEn} from './i18n/InputDateFhDP.en';
import {InputDateFhDPLt} from './i18n/InputDateFhDP.lt'
import {FhContainer, FormComponent, HTMLFormComponent, LanguageChangeObserver} from 'fh-forms-handler';
import {InputTimestampFhDPPl} from './i18n/InputTimestampFhDP.pl';
import {InputTimestampFhDPEn} from './i18n/InputTimestampFhDP.en';
import {InputTimestampFhDPLt} from './i18n/InputTimestampFhDP.lt';

class InputDateFhDP extends InputTextFhDP implements LanguageChangeObserver {
    protected backendFormat: string;
    protected format: string;
    protected maskEnabled: boolean;
    protected datepickerEnabled: boolean;
    protected highlightToday: boolean;
    protected inputDateHeight: any;
    protected maskPlugin: any;
    protected isAdditionalButtons: boolean;
    protected dateTimePickerConfig: any;
    protected availableTimeRange: string;
    protected tooltipsI18n = {
        "pl": InputTimestampFhDPPl,
        "en": InputTimestampFhDPEn,
        "lt": InputTimestampFhDPLt
    };

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
        this.isAdditionalButtons = this.componentObj.isAdditionalButtons || false;
        this.availableTimeRange = this.componentObj.availableTimeRange;

        this.i18n.registerStrings('lt', InputDateFhDPLt);
        this.i18n.registerStrings('pl', InputDateFhDPPl);
        this.i18n.registerStrings('en', InputDateFhDPEn);

        $.extend(true, (<any>$.fn).datetimepicker.defaults, {
            icons: {
                time: 'far fa-clock',
                date: 'far fa-calendar',
                up: 'fas fa-arrow-up',
                down: 'fas fa-arrow-down',
                previous: 'fas fa-chevron-left',
                next: 'fas fa-chevron-right',
                today: 'fas fa-calendar-check',
                clear: 'far fa-trash-alt',
                close: 'far fa-times-circle'
            }
        });

        this.dateTimePickerConfig = {
            showTodayButton: this.isAdditionalButtons,
            showClear: this.isAdditionalButtons,
            locale: this.i18n.selectedLanguage,
            useStrict: true,
            format: this.format,
            defaultDate: InputDateFhDP.isDateValid(this.rawValue, this.format)? this.rawValue : '',
            keepInvalid: true,
            tooltips: this.tooltipsI18n[this.i18n.selectedLanguage],
        }
        this.setAvailableTimeRange();
    }

    setAvailableTimeRange() {
        if(!!this.availableTimeRange) {
            const date = new Date();
            if(this.availableTimeRange==="FUTURETIME") {
                this.dateTimePickerConfig.minDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
            } else if(this.availableTimeRange==="PASTTIME") {
                this.dateTimePickerConfig.maxDate = new Date(date.getFullYear(), date.getMonth(), date.getDate()+1);
                this.dateTimePickerConfig.disabledDates = [new Date(date.getFullYear(), date.getMonth(), date.getDate()+1)];
            }
        }
    }

    create() {
        console.log('***** InputDateFhDP v.1.00.000 *****')
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
        input.value = InputDateFhDP.toDateOrLeave(this.rawValue, this.backendFormat, this.format);

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

        this.inputGroupElement.classList.add('date');
        (<any>$(this.inputGroupElement)).datetimepicker(this.dateTimePickerConfig);

        this.lastValue = InputDateFhDP.toDateOrLeave(this.lastValue, this.backendFormat, this.format);
        if(this.isLastValueEnabled!==false) {
            this.createLastValueElement();
        }

        $(this.input).on('input', this.onValueInput.bind(this));
        $(this.input).on('blur', this.inputBlurEvent.bind(this));
        $(this.input).on('change', this.inputChangeEvent.bind(this));

        this.inputGroupElement.id = this.id + "_inputGroup";
        this.inputGroupElement.classList.add('designer-ignore');
        if (this.accessibility == 'EDIT') {
            this.applyMask();
        }

        this.display();
    };

    inputBlurEvent() {
        this.input.value = InputDateFhDP.toDateOrLeave(this.input.value, this.format, this.format);
        this.updateModel();
    }

    inputChangeEvent() {
        this.input.value = InputDateFhDP.toDateOrLeave(this.input.value, this.format, this.format);
        this.updateModel();
    }

    createAddon() {
        let addon = document.createElement('span');
        addon.classList.add('input-group-addon');
        let glyphicon = document.createElement('span');
        glyphicon.classList.add('glyphicon');
        glyphicon.classList.add('glyphicon-calendar');

        addon.appendChild(glyphicon);
        this.inputGroupElement.appendChild(addon);

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
        glyphicon.appendChild(icon)
    }

    protected makePlaceholder(format: string) {
        if (this.accessibility == 'EDIT') {
            format = format.replace(new RegExp('Y', 'g'), this.__('year_character').innerText).toLowerCase();
            format = format.replace(new RegExp('M', 'g'), this.__('month_character').innerText).toLowerCase();
            format = format.replace(new RegExp('D', 'g'), this.__('day_character').innerText).toLowerCase();
            format = format.replace(new RegExp('H', 'g'), this.__('hour_character').innerText).toLowerCase();

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
                    this.input.value = InputDateFhDP.toDateOrLeave(newValue, this.backendFormat, this.format);
                    this.rawValue = InputDateFhDP.toDateOrLeave(newValue, this.backendFormat, this.backendFormat);
                    this.oldValue = InputDateFhDP.toDateOrLeave(newValue, this.backendFormat, this.backendFormat);
                    this.lastValue = InputDateFhDP.toDateOrLeave(newValue, this.backendFormat, this.format);
                    break;
                case 'lastValue':
                    this.lastValue = InputDateFhDP.toDateOrLeave(newValue, this.backendFormat, this.format);
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
        this.rawValue = InputDateFhDP.toDateOrLeave(this.input.value, this.format, this.backendFormat);
        let theSameValue = this.rawValue == this.lastValue;
        if(this.lastValue===undefined && !!this.newValueText) {
            theSameValue = this.rawValue === '';
        }
        this.toogleLastValueElement(theSameValue);
        if (this.onChange != null && this.oldValue !== this.rawValue) {
            this.fireEventWithLock('onChange', this.onChange);
        }
    };

    static isDateValid(date, sourceFormat) {
        return moment(date, [sourceFormat], InputDateFhDP.MOMENT_JS_STRICT_MODE).isValid();
    };

    static toDateOrLeave(text, sourceFormat, targetFormat) {
        return InputDateFhDP.isDateValid(text, sourceFormat) ? moment.utc(text, [sourceFormat], InputDateFhDP.MOMENT_JS_STRICT_MODE).format(targetFormat) : text;
    };

    static toDateOrClear(text, sourceFormat, targetFormat) {
        return InputDateFhDP.isDateValid(text, sourceFormat) ? moment.utc(text, [sourceFormat], InputDateFhDP.MOMENT_JS_STRICT_MODE).format(targetFormat) : '';
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
                shiftPositions:false,
                alias: "date",
                mask: this.mask
            }).mask(this.input);
            this.inputmaskEnabled = true;
        }
    };

    // noinspection JSUnusedGlobalSymbols
    languageChanged(code: string) {
        this.input.placeholder = this.makePlaceholder(this.format);
        (<any>$(this.inputGroupElement)).data("DateTimePicker").locale(this.i18n.selectedLanguage);
        (<any>$(this.inputGroupElement)).data("DateTimePicker").tooltips(this.tooltipsI18n[this.i18n.selectedLanguage]);
    }

    wrap(skipLabel, isInputElement) {
        super.wrap(skipLabel, isInputElement);
    }

    getDefaultWidth(): string {
        return "md-3";
    }

    destroy(removeFromParent: boolean) {
        // noinspection JSIgnoredPromiseFromCall
        this.i18n.unsubscribe(this);
        this.maskPlugin = null;

        $(this.input).off('input', this.onValueInput.bind(this));
        $(this.input).off('blur', this.inputBlurEvent.bind(this));
        $(this.input).off('change', this.inputChangeEvent.bind(this));

        this.disableMask();
        super.destroy(removeFromParent);
    }
    protected toogleLastValueElement(theSameValue){
        let oldValueElement = this.inputGroupElement.getElementsByClassName('input-old-value');
        if(oldValueElement && oldValueElement.length > 0) {
            if(theSameValue){
                oldValueElement[0].classList.add('hide-old-value');
                if(this.hideCrossed == "true"){
                    oldValueElement[0].classList.add('input-old-value-remove-line');
                }
            } else {
                oldValueElement[0].classList.remove('hide-old-value');
            }
        }
    }
}

export {InputDateFhDP};
