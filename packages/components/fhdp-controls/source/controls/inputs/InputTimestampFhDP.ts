import 'fh-basic-controls/dist/source/external/bootstrap-datetimepicker.min.js';
import {InputDateFhDP} from "./InputDateFhDP";
import {FhContainer, HTMLFormComponent, LanguageChangeObserver} from "fh-forms-handler";

class InputTimestampFhDP extends InputDateFhDP implements LanguageChangeObserver {

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.backendFormat = "YYYY-MM-DDTHH:mm:ss.SSS";
        this.format = this.componentObj.format || 'YYYY-MM-DD HH:mm:ss';
        // @ts-ignore
        this.keySupport = FhContainer.get("FormComponentKeySupport")(this.componentObj, this);

        this.dateTimePickerConfig = {
            showTodayButton: this.isAdditionalButtons,
            showClear: this.isAdditionalButtons,
            locale: this.i18n.selectedLanguage,
            useStrict: true,
            format: this.format,
            defaultDate: InputDateFhDP.isDateValid(this.rawValue, this.format)? this.rawValue : '',
            keepInvalid: true,
            tooltips: this.tooltipsI18n[this.i18n.selectedLanguage],
            widgetParent : 'body'
        }

        this.setAvailableTimeRange();

        this.lastValueParser = (lastValue: string) => InputDateFhDP.toDateOrLeave(this.lastValue, this.backendFormat, this.format);
    }

    create() {
        let input = document.createElement('input');
        ['fc', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        input.id = this.id;
        input.type = 'text';

        input.placeholder = this.makePlaceholder(this.format);
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

        this.inputGroupElement.classList.add('date');
        (<any>$(this.inputGroupElement)).datetimepicker(this.dateTimePickerConfig);

        if(this.isLastValueEnabled!==false) {
            this.createLastValueElement();
        }
        this.display();      

        $(this.inputGroupElement).data("DateTimePicker").date(
            InputDateFhDP.toDateOrLeave(this.rawValue, this.backendFormat, this.format));

        $(this.input).on('blur', this.inputBlurEvent.bind(this));
        $(this.input).on('change', this.inputChangeEvent.bind(this));
    };

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

    inputBlurEvent() {
        this.input.value = InputDateFhDP.toDateOrLeave(this.input.value, this.format, this.format);
        this.updateModel();
        $(this.inputGroupElement).data("DateTimePicker").date(
            InputDateFhDP.toDateOrLeave(this.rawValue, this.backendFormat, this.format));
    }

    onInputEvent() {
        this.input.value = InputDateFhDP.toDateOrClear(this.input.value, this.format, this.format);
        this.updateModel();
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

    updateModel() {
        this.rawValue = InputDateFhDP.toDateOrLeave(this.input.value, this.format, this.backendFormat);
        const compareRaw = InputDateFhDP.toDateOrLeave(this.rawValue, this.backendFormat, this.format);
        const compareLast = InputDateFhDP.toDateOrLeave(this.lastValue, this.backendFormat, this.format);
        let theSameValue = compareRaw == compareLast;
        if(this.lastValue===undefined && !!this.newValueText) {
            theSameValue = this.rawValue === '';
        }
        this.toogleLastValueElement(theSameValue);
        if (this.onChange != null && this.oldValue !== this.rawValue) {
            this.fireEventWithLock('onChange', this.onChange);
        }
    };
}

export {InputTimestampFhDP};
