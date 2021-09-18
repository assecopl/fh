import {HTMLFormComponent} from "fh-forms-handler";
import * as _ from "lodash";
import * as moment from 'moment';
import * as $ from 'jquery';
import 'imports-loader?moment,define=>false,exports=>false!../external/bootstrap-datepicker';

class Calendar extends HTMLFormComponent {
    private readonly blockedDates: any;
    private readonly values: any;
    private readonly label: string;
    private readonly currentDate: any;
    private readonly minDate: any;
    private readonly maxDate: any;
    private readonly changeMonth: any;
    private readonly changeYear: any;
    private calendar: any;
    private onChange: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.values = componentObj.values || {};
        this.currentDate = componentObj.currentDate;
        this.onChange = componentObj.onChange || null;
        this.rawValue = componentObj.rawValue || null;
        this.blockedDates = componentObj.blockedDates || null;
        this.minDate = componentObj.minDate || null;
        this.maxDate = componentObj.maxDate || null;
        this.changeMonth = componentObj.changeMonth || false;
        this.changeYear = componentObj.changeYear || false;
        this.label = _.escape(this.componentObj.label);
    }

    create() {
        let container = document.createElement('div');
        container.classList.add('fc');
        container.classList.add('calendar');

        let label = document.createElement('div');
        label.classList.add('calendar-label');
        label.appendChild(document.createTextNode(this.label));
        container.appendChild(label);

        let calendar = document.createElement('div');
        calendar.id = this.id;
        calendar.classList.add('calendar-content');
        container.appendChild(calendar);

        this.component = container;
        this.handlemarginAndPAddingStyles();
        this.wrap(true);
        this.display();

        let styleTypes = ['color', 'background-color', 'border-color'];

        let dateGroups = this.values;
        let currentDate: any = new Date(this.currentDate);
        let calendarObject = this;
        currentDate.setHours(0, 0, 0, 0);
        currentDate = currentDate.getTime();
        let index = 0;
        Object.keys(dateGroups).forEach(function (key) {
            let group = key.split('=');
            if (group.length == 2 && group[1] != '') {
                let styleClass = document.createElement("style");
                styleClass.innerHTML =
                    '.color-' + group[1].replace('#', '') + '{ ' + styleTypes[index] + ': '
                    + group[1] + '  !important; }';
                calendar.appendChild(styleClass);
            }
            index++;
        });

        // var disabledDays = ["2017-02-14","2017-02-15","2017-02-16"];
        let disabledDays = [];
        if (this.blockedDates) {
            disabledDays = this.blockedDates;
        }

        let minDate = this.minDate;
        let maxDate = this.maxDate;

        // this.blockedDates = componentObj.blockedDates || null;
        // this.minDate = componentObj.minDate || null;
        // this.maxDate = componentObj.maxDate || null;

        this.calendar = $(`#${calendar.id}`).datepicker(<any>{
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
                let pickerDay: any = new Date(date);
                pickerDay.setHours(0, 0, 0, 0);
                pickerDay = pickerDay.getTime();
                let className = '';
                let title = [];
                if (pickerDay == currentDate) {
                    className += 'current ';
                }

                Object.keys(dateGroups).forEach(function (key) {
                    dateGroups[key].forEach(function (dateGroup) {
                        let selectedDay: any = new Date(dateGroup);
                        selectedDay.setHours(0, 0, 0, 0);
                        selectedDay = selectedDay.getTime();
                        if (pickerDay == selectedDay) {
                            let group = key.split('=');
                            //title += group[0]+', ';
                            title.push(group[0]);
                            if (group.length == 2 && group[1] != '') {
                                className += 'color-' + group[1].replace('#', '') + ' ';
                            } else if (group.length == 2 && group[1] == '') {
                                className += 'default ';
                            }
                        }
                    });
                });

                let dateString = moment(date).format('yy-mm-dd');
                let availableDay = disabledDays.indexOf(dateString) == -1;
                if (minDate && maxDate) {
                    let from = Date.parse(minDate);
                    let to = Date.parse(maxDate);
                    let check = Date.parse(dateString);

                    if (check > from && check < to && availableDay) {
                        availableDay = true;
                    } else {
                        className += 'disabled ';
                        availableDay = false;
                    }
                }

                // return [true, className, title.join(', ')];
                return {enabled: availableDay, classes: className, tooltip: title.join(', ')};
                // return [false, className, title.join(', ')];
            }
        });
        if (this.rawValue != null) {
            $('#' + calendar.id).datepicker("setDate", new Date(this.rawValue));
        }

        let calendarContent;
        calendarContent = calendar.querySelector('.datepicker').childNodes;
        calendarContent.forEach(element => {
            element.addEventListener('click', e => {
                let calendarElement = e.currentTarget.closest('.fc.calendar');
                calendarElement.click();
            });
        });
    };

    update(change) {
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

    extractChangedAttributes() {
        return this.changesQueue.extractChangedAttributes();
    };

    destroy(removeFromParent: boolean) {
        this.calendar.datepicker('destroy');

        super.destroy(removeFromParent);
    }
}

export {Calendar};