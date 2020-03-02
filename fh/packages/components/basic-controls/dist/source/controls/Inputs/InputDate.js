"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var moment = require("moment");
require("imports-loader?moment,define=>false,exports=>false!../../external/inputmask");
require("imports-loader?moment,define=>false,exports=>false!../../external/bootstrap-datepicker");
var InputText_1 = require("./InputText");
var InputDate_pl_1 = require("./i18n/InputDate.pl");
var InputDate_en_1 = require("./i18n/InputDate.en");
var fh_forms_handler_1 = require("fh-forms-handler");
var fh_forms_handler_2 = require("fh-forms-handler");
var InputDate = /** @class */ (function (_super) {
    __extends(InputDate, _super);
    function InputDate(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.backendFormat = 'YYYY-MM-DD';
        _this.format = _this.componentObj.format || 'YYYY-MM-DD';
        // @ts-ignore
        _this.keySupport = fh_forms_handler_2.FhContainer.get("FormComponentKeySupport")(_this.componentObj, _this);
        _this.mask = _this.format.replace(/[a-zA-Z]/g, '9');
        _this.maskEnabled = _this.componentObj.maskEnabled || false;
        _this.maskDynamic = _this.componentObj.maskDynamic || false;
        _this.highlightToday = _this.componentObj.highlightToday || false;
        _this.inputDateHeight = _this.componentObj.height;
        _this.datepickerEnabled = false;
        _this.i18n.registerStrings('pl', InputDate_pl_1.InputDatePL);
        _this.i18n.registerStrings('en', InputDate_en_1.InputDateEN);
        $.fn.datepicker.dates['pl'] = {
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
        $.fn.datepicker.dates['en'] = {
            days: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
            daysShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            daysMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
            months: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            monthsShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            today: "Today",
            clear: "Clear",
            format: "mm/dd/yyyy",
            titleFormat: "MM yyyy",
            weekStart: 0
        };
        return _this;
    }
    InputDate.prototype.create = function () {
        var input = document.createElement('input');
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
    ;
    InputDate.prototype.applyDatepicker = function () {
        if (!this.datepickerEnabled) {
            var options = {
                forceParse: false,
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
            }
            catch (e) {
                console.error(e);
            }
        }
    };
    InputDate.prototype.disableDatepicker = function () {
        if (this.datepickerEnabled) {
            var component = $(this.inputGroupElement);
            component.datepicker('destroy');
            this.htmlElement.classList.remove('fc-editable');
            this.datepickerEnabled = false;
        }
    };
    InputDate.prototype.inputBlurEvent = function () {
        this.input.value = InputDate.toDateOrLeave(this.input.value, this.format, this.format);
        this.updateModel();
    };
    InputDate.prototype.inputChangeEvent = function () {
        this.input.value = InputDate.toDateOrLeave(this.input.value, this.format, this.format);
        this.updateModel();
    };
    InputDate.prototype.createAddon = function () {
        var addon = document.createElement('span');
        addon.classList.add('input-group-text');
        addon.innerText = ' ';
        var icon = document.createElement('i');
        if (this.componentObj.icon) {
            var classes = this.componentObj.icon.split(' ');
            icon.classList.add(classes[0]);
            if (classes[1]) {
                icon.classList.add(classes[1]);
            }
        }
        else {
            icon.classList.add('fa');
            icon.classList.add('fa-calendar');
        }
        addon.appendChild(icon);
        var addonDiv = document.createElement('div');
        addonDiv.classList.add('input-group-prepend');
        addonDiv.classList.add('datepickerbutton');
        addonDiv.appendChild(addon);
        this.inputGroupElement.classList.add('date');
        if (this.componentObj.iconAlignment === 'BEFORE') {
            this.inputGroupElement.insertBefore(addonDiv, this.inputGroupElement.firstChild);
        }
        else if (this.componentObj.iconAlignment === 'AFTER') {
            addonDiv.classList.remove('input-group-prepend');
            addonDiv.classList.add('input-group-append');
            this.inputGroupElement.appendChild(addonDiv);
        }
        else {
            this.inputGroupElement.insertBefore(addonDiv, this.inputGroupElement.firstChild);
        }
        this.component.classList.add('datepickerInput');
    };
    InputDate.prototype.setAccessibility = function (accessibility) {
        _super.prototype.setAccessibility.call(this, accessibility);
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
    };
    InputDate.prototype.makePlaceholder = function (format) {
        if (this.accessibility == 'EDIT') {
            format = format.replace(new RegExp('Y', 'g'), this.__('year_character').innerText);
            format = format.replace(new RegExp('M', 'g'), this.__('month_character').innerText);
            format = format.replace(new RegExp('D', 'g'), this.__('day_character').innerText);
            return format;
        }
        else {
            return '';
        }
    };
    InputDate.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
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
    ;
    InputDate.prototype.onValueInput = function () {
        if (this.maskEnabled) {
            if (this.maskPlugin.isComplete(this.input)) {
                this.updateModel();
            }
        }
        else {
            this.updateModel();
        }
    };
    InputDate.prototype.updateModel = function () {
        this.rawValue = InputDate.toDateOrLeave(this.input.value, this.format, this.backendFormat);
        if (this.onChange != null && this.oldValue !== this.rawValue) {
            this.fireEventWithLock('onChange', this.onChange);
        }
    };
    ;
    InputDate.isDateValid = function (date, sourceFormat) {
        return moment(date, [sourceFormat], InputDate.MOMENT_JS_STRICT_MODE).isValid();
    };
    ;
    InputDate.toDateOrLeave = function (text, sourceFormat, targetFormat) {
        return InputDate.isDateValid(text, sourceFormat) ? moment.utc(text, [sourceFormat], InputDate.MOMENT_JS_STRICT_MODE).format(targetFormat) : text;
    };
    ;
    InputDate.toDateOrClear = function (text, sourceFormat, targetFormat) {
        return InputDate.isDateValid(text, sourceFormat) ? moment.utc(text, [sourceFormat], InputDate.MOMENT_JS_STRICT_MODE).format(targetFormat) : '';
    };
    ;
    InputDate.prototype.extractChangedAttributes = function () {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        var attrs = {};
        if (this.rawValue !== this.oldValue) {
            attrs[fh_forms_handler_1.FormComponent.VALUE_ATTRIBUTE_NAME] = this.rawValue;
            this.oldValue = this.rawValue;
        }
        return attrs;
    };
    ;
    InputDate.prototype.disableMask = function () {
        if (this.maskEnabled && this.inputmaskEnabled) {
            // @ts-ignore
            Inputmask.remove(this.input);
            this.inputmaskEnabled = false;
        }
    };
    InputDate.prototype.applyMask = function () {
        if (this.maskEnabled && !this.inputmaskEnabled) {
            // @ts-ignore
            this.maskPlugin = Inputmask({
                clearMaskOnLostFocus: false,
                greedy: false,
                jitMasking: this.maskDynamic,
                placeholder: this.input.placeholder,
                definitions: {},
                insertMode: 0,
                alias: "date",
                mask: this.mask
            }).mask(this.input);
            this.inputmaskEnabled = true;
        }
    };
    ;
    // noinspection JSUnusedGlobalSymbols
    InputDate.prototype.languageChanged = function (code) {
        this.disableDatepicker();
        this.applyDatepicker();
    };
    InputDate.prototype.wrap = function (skipLabel, isInputElement) {
        _super.prototype.wrap.call(this, skipLabel, isInputElement);
    };
    InputDate.prototype.destroy = function (removeFromParent) {
        // noinspection JSIgnoredPromiseFromCall
        this.i18n.unsubscribe(this);
        this.maskPlugin = null;
        $(this.input).off('input', this.onValueInput.bind(this));
        $(this.input).off('blur', this.inputBlurEvent.bind(this));
        $(this.input).off('change', this.inputChangeEvent.bind(this));
        this.disableDatepicker();
        this.disableMask();
        _super.prototype.destroy.call(this, removeFromParent);
    };
    /**
     * Czy uzywac trybu scislego biblioteki momentjs
     * Na przykład  czy data 2016-12-24T00:00:00.000+00:00 będzie nieprawidłowa dla maski YYYY-MM-DD
     * @type {boolean}
     */
    InputDate.MOMENT_JS_STRICT_MODE = true;
    return InputDate;
}(InputText_1.InputText));
exports.InputDate = InputDate;
//# sourceMappingURL=InputDate.js.map