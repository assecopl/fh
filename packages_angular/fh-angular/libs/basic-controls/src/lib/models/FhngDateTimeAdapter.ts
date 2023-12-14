import {NgbDate, NgbDateAdapter, NgbDateStruct, NgbTimeStruct,} from '@ng-bootstrap/ng-bootstrap';
import {Injectable} from '@angular/core';
import moment from 'moment';
import {FhngDateUtils} from "@fh-ng/forms-handler";
import * as _ from "lodash";


/**
 * Custom class for handling datetime format for NgbDatepicker and NgbTimepicker.
 */
@Injectable()
export class FhngDateAdapter extends NgbDateAdapter<string> {
  public tmpDate: FhngDateTimeStruct = {
    day: 0,
    hour: 0,
    minute: 0,
    month: 0,
    second: 0,
    year: 0,
  };
  public dateFormat: string = FhngDateUtils.FRONTEND_DATE_FORMAT;
  public backendFormat: string = FhngDateUtils.BACKEND_DATE_FORMAT;
  public returnType: 'string' | 'Date' = 'Date';
  public curentDate: Date;

  constructor() {
    super();
    this.curentDate = new Date();
    this.todayToTmpDate();
  }

  /**
   * Converts a native `string date` to a `FhngDateTimeStruct`.
   */
  public fromModel(date: string | NgbDate | Date): FhngDateTimeStruct | any {
    if (!date) {
      return '';
    }

    if (date) {
      if (date instanceof NgbDate) {
        // @ts-ignore
        this.tmpDate = this._toFhngDateTimeStruct(date);
        return this.tmpDate;
      } else {
        // @ts-ignore //string and Date
        this.tmpDate = this.parseToFhngDateTime(date);
        return this.tmpDate;
      }
    }
  }

  /**
   * Converts a `FhngDateTimeStruct` to a native `Date`.
   */
  public toModel(date: FhngDateTimeStruct): any {
    let d: Date | string = '';
    if (date) {
      if (this.returnType === 'string') {
        d = this.formatDateTime(date, this.backendFormat);
      }
      if (this.returnType === 'Date') {
        //Aby Data poprawnie się zachowywała musimy stworzyć ją jako datę w UTC, dodany do niej zostanie czysty timezone offset
        //Dzieki temu będziemy mogli pracować na niej jak na zwykłej dacie,
        //zostanie popranwie przesłana do serwera (jako data UTC) a lokalnie będziemy mogli ją wświetlać bez godzin/minut/sekund.
        d = moment(
          this.formatDateTime(date, this.backendFormat),
          FhngDateUtils.FRONTEND_DATE_FORMAT
        ).toDate();
      }
    }
    return d;
  }

  private _toFhngDateTimeStruct(date: FhngDateTimeStruct): FhngDateTimeStruct {
    if (!this.tmpDate) {
      this.todayToTmpDate();
    }

    if (date) {
      if (date['year']) {
        if (date.year < this.curentDate.getFullYear() - 100) {
          this.tmpDate['year'] = this.curentDate.getFullYear() - 100;
        } else if (date.year > this.curentDate.getFullYear() + 100) {
          this.tmpDate['year'] = this.curentDate.getFullYear() + 100;
        } else {
          this.tmpDate['year'] = date.year;
        }

        if (date.month > 12) {
          this.tmpDate['month'] = 12;
        } else {
          this.tmpDate['month'] = date.month;
        }

        if (date.day > 31) {
          this.tmpDate['day'] = 31;
        } else {
          this.tmpDate['day'] = date.day;
        }
      }
      if (date['hour']) {
        this.tmpDate['hour'] = date.hour;
        this.tmpDate['minute'] = date.minute;
        this.tmpDate['second'] = date.second;
      }
    }

    return this.tmpDate;
  }

  public todayToTmpDate() {
    const current = moment();
    const date: FhngDateTimeStruct | any = new NgbDate(null, null, null);
    // this.tmpDate = {
    date.year = current.year();
    date.month = current.month() + 1;
    date.day = current.date();
    date.hour = current.hour();
    date.minute = current.minute();
    date.second = current.second();
    // };
    this.tmpDate = date;
  }

  /**
   * Those functions handles how the date is rendered and parsed from keyboard i.e. in the bound input field.
   */
  parse(value: string): FhngDateTimeStruct {
    const result: FhngDateTimeStruct = this.parseToFhngDateTime(value);

    this.tmpDate = result;
    return result;
  }

  format(date: FhngDateTimeStruct): string {
    const result: string = this.formatDateTime(date);
    return result;
  }

  formatDateTime(date: FhngDateTimeStruct, format: string = null): string {
    let result: string = '';
    if (date) {
      this.tmpDate = this._toFhngDateTimeStruct(date);

      if (this.tmpDate && _.isNumber(this.tmpDate.year)) {
        result = moment([
          this.tmpDate.year,
          this.tmpDate.month - 1,
          this.tmpDate.day,
          this.tmpDate.hour,
          this.tmpDate.minute,
          this.tmpDate.second,
          0,
        ]).format(format ? format : this.dateFormat);
      }
    }
    return result;
  }

  private parseToFhngDateTime(value: string | Date): FhngDateTimeStruct {
    let date: FhngDateTimeStruct | any = '';
    if (value) {
      date = new NgbDate(null, null, null);
      let current = moment(value, this.dateFormat);

      console.log('parseToFhngDateTime', value, this.dateFormat, current)

      if (!current.isValid()) {
        if (!new RegExp(/^[A-Za-z]+$/).test(value.toString())) {
          //Jeżeli data jest nieprawidłowa i zawiera literki to nie uzupełniona została całkowicie maska.
          //Jeżeli nie zawiera literek i jest niepoprawna to robimy fallback do aktualnej daty.
          current = moment();
        }
      }
      if (current.isValid()) {
        date.year = current.year();
        date.month = current.month() + 1;
        date.day = current.date();
        date.hour = current.hour();
        date.minute = current.minute();
        date.second = current.second();
      }
    }
    return date;
  }
}

@Injectable()
export class FhngDateTimeAdapter extends FhngDateAdapter {
  constructor() {
    super();
    this.dateFormat = FhngDateUtils.FRONTEND_DATETIME_FORMAT;
    this.backendFormat = FhngDateUtils.BACKEND_DATETIME_FORMAT;
  }

  /**
   * @Override
   * Converts a `FhngDateTimeStruct` to a native `Date`.
   */
  public override toModel(date: FhngDateTimeStruct): string {
    let d = null;
    if (date) {
      if (this.returnType === 'string') {
        d = this.formatDateTime(date, this.backendFormat);
      }
      if (this.returnType === 'Date') {
        d = moment(this.formatDateTime(date, this.backendFormat)).toDate();
        d['isDateTime'] = true;
      }
    }
    return d;
  }
}

type FhngDateTimeStruct = NgbDateStruct & NgbTimeStruct;
