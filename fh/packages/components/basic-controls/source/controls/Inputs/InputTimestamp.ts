import * as moment from 'moment';
import '../../external/bootstrap-datetimepicker.min.js';
import {InputDate} from "./InputDate";
import {InputTimestampPL} from './i18n/InputTimestamp.pl';
import {InputTimestampEN} from './i18n/InputTimestamp.en';
import {HTMLFormComponent} from "fh-forms-handler";
import {FhContainer} from "fh-forms-handler";
import {LanguageChangeObserver} from 'fh-forms-handler';

class InputTimestamp extends InputDate implements LanguageChangeObserver {

    private tooltipsI18n = {
        "pl": InputTimestampPL,
        "en": InputTimestampEN
    };

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.backendFormat = "YYYY-MM-DDTHH:mm:ss.SSS";
        this.format = this.componentObj.format || 'YYYY-MM-DD HH:mm:ss';
        // @ts-ignore
        this.keySupport = FhContainer.get("FormComponentKeySupport")(this.componentObj, this);

        (<any>$.fn).datetimepicker.defaults.tooltips = this.tooltipsI18n[this.i18n.selectedLanguage];
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
    }

    create() {
        let input = document.createElement('input');
        ['fc', 'inputTimestamp', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        input.id = this.id;
        input.type = 'text';

        input.placeholder = this.format;

        this.i18n.subscribe(this);

        // must be before onInput/onChange events
        this.keySupportCallback = this.keySupport.addKeyEventListeners(input);

        this.input = input;
        this.component = input;
        this.focusableComponent = input;
        this.hintElement = this.component;

        this.wrap(false, true);
        this.addStyles();
        this.createAddon();
        this.display();

        (<any>$(this.inputGroupElement)).datetimepicker({
            locale: this.i18n.selectedLanguage,
            useStrict: true,
            format: this.format,
            defaultDate: InputDate.isDateValid(this.rawValue, this.format)? this.rawValue : '',
            keepInvalid: true
        });

        $(this.inputGroupElement).data("DateTimePicker").date(
            InputDate.toDateOrLeave(this.rawValue, this.backendFormat, this.format));

        $(this.input).on('blur', this.inputBlurEvent.bind(this));
        $(this.input).on('change', this.inputChangeEvent.bind(this));
    };

    applyDatepicker() {
        return;
    }

    disableDatepicker() {
        return;
    }

    onInputEvent() {
        this.input.value = InputDate.toDateOrClear(this.input.value, this.format, this.format);
        this.updateModel();
    }

    addStyles() {
        super.addStyles();

        if (this.componentObj.height != undefined) {
            this.input.style.height = this.componentObj.height;
        }
    }

    languageChanged(code: string) {
        let component = (<any>$(this.inputGroupElement));
        component.data("DateTimePicker").destroy();
        component.datetimepicker.defaults.tooltips = this.tooltipsI18n[code];
        component.datetimepicker({
            locale: code,
            useStrict: true,
            format: this.format,
            defaultDate: InputDate.toDateOrClear(this.input.value, this.format, this.format),
            keepInvalid: true
        });
    }

    wrap(skipLabel, isInputElement) {
        super.wrap(skipLabel, isInputElement);
    }

    getDefaultWidth(): string {
        return "md-3";
    }

    destroy(removeFromParent: boolean) {
        $(this.input).off('blur', this.inputBlurEvent.bind(this));
        $(this.input).off('change', this.inputChangeEvent.bind(this));

        let component = (<any>$(this.inputGroupElement));
        let data = component.data("DateTimePicker");
        if (data != null) data.destroy();

        this.i18n.unsubscribe(this);

        super.destroy(removeFromParent);
    }
}

export {InputTimestamp};
