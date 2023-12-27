import {Injectable} from "@angular/core";
import {TranslationWidth} from "@angular/common";
import {NgbDatepickerI18n, NgbDateStruct} from "@ng-bootstrap/ng-bootstrap";
import {I18nService} from "@fh-ng/forms-handler";

@Injectable()
export class DatepickerI18nService extends NgbDatepickerI18n {
  constructor(private _translate: I18nService) {
    super();
  }
  public getDayAriaLabel(date: NgbDateStruct): string {
    return "";
  }

  public getMonthFullName(month: number, year?: number): string {
    return this._translate.__('fh.timestamp.months')[month-1];
  }

  public getMonthShortName(month: number, year?: number): string {
    return this.getMonthFullName(month, year);
  }

  public getWeekdayLabel(weekday: number, width?: TranslationWidth): string {
    return this._translate.__('fh.timestamp.weekdays')[weekday-1];
  }
}
