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
var fh_forms_handler_1 = require("fh-forms-handler");
var _ = require("lodash");
var moment = require("moment");
var $ = require("jquery");
require("imports-loader?moment,define=>false,exports=>false!../external/bootstrap-datepicker");
var Calendar = /** @class */ (function (_super) {
    __extends(Calendar, _super);
    function Calendar(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.values = componentObj.values || {};
        _this.currentDate = componentObj.currentDate;
        _this.onChange = componentObj.onChange || null;
        _this.rawValue = componentObj.rawValue || null;
        _this.blockedDates = componentObj.blockedDates || null;
        _this.minDate = componentObj.minDate || null;
        _this.maxDate = componentObj.maxDate || null;
        _this.changeMonth = componentObj.changeMonth || false;
        _this.changeYear = componentObj.changeYear || false;
        _this.label = _.escape(_this.componentObj.label);
        return _this;
    }
    Calendar.prototype.create = function () {
        var container = document.createElement('div');
        container.classList.add('fc');
        container.classList.add('calendar');
        var calendar = document.createElement('div');
        calendar.id = this.id;
        calendar.classList.add('calendar-content');
        container.appendChild(calendar);
        this.component = container;
        this.handlemarginAndPAddingStyles();
        this.wrap(false);
        this.display();
        var styleTypes = ['color', 'background-color', 'border-color'];
        var dateGroups = this.values;
        var currentDate = new Date(this.currentDate);
        var calendarObject = this;
        currentDate.setHours(0, 0, 0, 0);
        currentDate = currentDate.getTime();
        var index = 0;
        Object.keys(dateGroups).forEach(function (key) {
            var group = key.split('=');
            if (group.length == 2 && group[1] != '') {
                var styleClass = document.createElement("style");
                styleClass.innerHTML =
                    '.color-' + group[1].replace('#', '') + '{ ' + styleTypes[index] + ': '
                        + group[1] + '  !important; }';
                calendar.appendChild(styleClass);
            }
            index++;
        });
        // var disabledDays = ["2017-02-14","2017-02-15","2017-02-16"];
        var disabledDays = [];
        if (this.blockedDates) {
            disabledDays = this.blockedDates;
        }
        var minDate = this.minDate;
        var maxDate = this.maxDate;
        // this.blockedDates = componentObj.blockedDates || null;
        // this.minDate = componentObj.minDate || null;
        // this.maxDate = componentObj.maxDate || null;
        this.calendar = $("#" + calendar.id).datepicker({
            dateFormat: 'yy-mm-dd',
            onSelect: function (date) {
                calendarObject.rawValue = date;
                calendarObject.changesQueue.queueValueChange(calendarObject.rawValue);
                if (calendarObject.onChange != null) {
                    calendarObject.fireEventWithLock('onChange', calendarObject.onChange);
                }
            },
            changeMonth: this.changeMonth,
            changeYear: this.changeYear,
            beforeShowDay: function (date) {
                var pickerDay = new Date(date);
                pickerDay.setHours(0, 0, 0, 0);
                pickerDay = pickerDay.getTime();
                var className = '';
                var title = [];
                if (pickerDay == currentDate) {
                    className += 'current ';
                }
                Object.keys(dateGroups).forEach(function (key) {
                    dateGroups[key].forEach(function (dateGroup) {
                        var selectedDay = new Date(dateGroup);
                        selectedDay.setHours(0, 0, 0, 0);
                        selectedDay = selectedDay.getTime();
                        if (pickerDay == selectedDay) {
                            var group = key.split('=');
                            //title += group[0]+', ';
                            title.push(group[0]);
                            if (group.length == 2 && group[1] != '') {
                                className += 'color-' + group[1].replace('#', '') + ' ';
                            }
                            else if (group.length == 2 && group[1] == '') {
                                className += 'default ';
                            }
                        }
                    });
                });
                var dateString = moment(date).format('yy-mm-dd');
                var availableDay = disabledDays.indexOf(dateString) == -1;
                if (minDate && maxDate) {
                    var from = Date.parse(minDate);
                    var to = Date.parse(maxDate);
                    var check = Date.parse(dateString);
                    if (check > from && check < to && availableDay) {
                        availableDay = true;
                    }
                    else {
                        className += 'disabled ';
                        availableDay = false;
                    }
                }
                // return [true, className, title.join(', ')];
                return { enabled: availableDay, classes: className, tooltip: title.join(', ') };
                // return [false, className, title.join(', ')];
            }
        });
        if (this.rawValue != null) {
            $('#' + calendar.id).datepicker("setDate", new Date(this.rawValue));
        }
        var calendarContent;
        calendarContent = calendar.querySelector('.datepicker').childNodes;
        calendarContent.forEach(function (element) {
            element.addEventListener('click', function (e) {
                var calendarElement = e.currentTarget.closest('.fc.calendar');
                calendarElement.click();
            });
        });
    };
    ;
    Calendar.prototype.update = function (change) {
        $.each(change.changedAttributes, function (name, newValue) {
            switch (name) {
                case 'rawValue':
                    $('#' + this.id).datepicker("setDate", new Date(newValue));
                    break;
                case 'label':
                    this.label = _.escape(newValue);
                    $(this.htmlElement).find('.calendar-label').text(this.label);
            }
        }.bind(this));
    };
    ;
    Calendar.prototype.extractChangedAttributes = function () {
        return this.changesQueue.extractChangedAttributes();
    };
    ;
    Calendar.prototype.destroy = function (removeFromParent) {
        this.calendar.datepicker('destroy');
        _super.prototype.destroy.call(this, removeFromParent);
    };
    return Calendar;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Calendar = Calendar;
//# sourceMappingURL=Calendar.js.map