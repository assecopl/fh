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
require("pc-bootstrap4-datetimepicker/build/js/bootstrap-datetimepicker.min");
var InputDate_1 = require("./InputDate");
var InputTimestamp_pl_1 = require("./i18n/InputTimestamp.pl");
var InputTimestamp_en_1 = require("./i18n/InputTimestamp.en");
var fh_forms_handler_1 = require("fh-forms-handler");
var InputTimestamp = /** @class */ (function (_super) {
    __extends(InputTimestamp, _super);
    function InputTimestamp(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.tooltipsI18n = {
            "pl": InputTimestamp_pl_1.InputTimestampPL,
            "en": InputTimestamp_en_1.InputTimestampEN
        };
        _this.backendFormat = "YYYY-MM-DDTHH:mm:ss.SSS";
        _this.format = _this.componentObj.format || 'YYYY-MM-DD HH:mm';
        // @ts-ignore
        _this.keySupport = fh_forms_handler_1.FhContainer.get("FormComponentKeySupport")(_this.componentObj, _this);
        $.fn.datetimepicker.defaults.tooltips = _this.tooltipsI18n[_this.i18n.selectedLanguage];
        $.extend(true, $.fn.datetimepicker.defaults, {
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
        return _this;
    }
    InputTimestamp.prototype.create = function () {
        var input = document.createElement('input');
        ['fc', 'inputTimestamp', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        // input.type = 'datetime-local';
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
        $(this.inputGroupElement).datetimepicker({
            locale: this.i18n.selectedLanguage,
            useStrict: true,
            format: this.format,
            defaultDate: InputDate_1.InputDate.isDateValid(this.rawValue, this.format) ? this.rawValue : '',
            keepInvalid: true
        });
        $(this.inputGroupElement).data("DateTimePicker").date(InputDate_1.InputDate.toDateOrLeave(this.rawValue, this.backendFormat, this.format));
        $(this.input).on('blur', this.inputBlurEvent.bind(this));
        $(this.input).on('change', this.inputChangeEvent.bind(this));
    };
    ;
    InputTimestamp.prototype.applyDatepicker = function () {
        return;
    };
    InputTimestamp.prototype.disableDatepicker = function () {
        return;
    };
    InputTimestamp.prototype.onInputEvent = function () {
        this.input.value = InputDate_1.InputDate.toDateOrClear(this.input.value, this.format, this.format);
        this.updateModel();
    };
    InputTimestamp.prototype.addStyles = function () {
        _super.prototype.addStyles.call(this);
        if (this.componentObj.height != undefined) {
            this.input.style.height = this.componentObj.height;
        }
    };
    InputTimestamp.prototype.languageChanged = function (code) {
        var component = $(this.inputGroupElement);
        component.data("DateTimePicker").destroy();
        component.datetimepicker.defaults.tooltips = this.tooltipsI18n[code];
        component.datetimepicker({
            locale: code,
            useStrict: true,
            format: this.format,
            defaultDate: InputDate_1.InputDate.toDateOrClear(this.input.value, this.format, this.format),
            keepInvalid: true
        });
    };
    InputTimestamp.prototype.wrap = function (skipLabel, isInputElement) {
        _super.prototype.wrap.call(this, skipLabel, isInputElement);
    };
    InputTimestamp.prototype.destroy = function (removeFromParent) {
        $(this.input).off('blur', this.inputBlurEvent.bind(this));
        $(this.input).off('change', this.inputChangeEvent.bind(this));
        var component = $(this.inputGroupElement);
        var data = component.data("DateTimePicker");
        if (data != null)
            data.destroy();
        this.i18n.unsubscribe(this);
        _super.prototype.destroy.call(this, removeFromParent);
    };
    return InputTimestamp;
}(InputDate_1.InputDate));
exports.InputTimestamp = InputTimestamp;
//# sourceMappingURL=InputTimestamp.js.map