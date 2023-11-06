import {Injectable} from "@angular/core";
import {
  NgbDateAdapter,
  NgbDateParserFormatter,
  NgbDateStruct,
  NgbTimeAdapter,
  NgbTimeStruct
} from "@ng-bootstrap/ng-bootstrap";
import {FhngDateUtils} from "../models/FhngDateUtils";
import moment from "moment";

@Injectable()
export class CustomNgbDatetimeService extends NgbDateParserFormatter {
  private _frontendFormat = FhngDateUtils.FRONTEND_DATETIME_FORMAT;

  private _backendFormat = FhngDateUtils.BACKEND_DATETIME_FORMAT;

  private _tmpDate: NgbDateStruct & NgbTimeStruct | null = null;

  get backendFormat(): string {
    return this._backendFormat;
  }

  set backendFormat(value: string) {
    if (value) {
      this._backendFormat = value;
    }
  }
  get frontendFormat(): string {
    return this._frontendFormat;
  }

  set frontendFormat(value: string) {
    if (value) {
      this._frontendFormat = value;
    }
  }

  public parse(value: string): NgbDateStruct & NgbTimeStruct  | null {
    return this.fromModel(value, this.backendFormat);
  }

  public format(date: NgbDateStruct & NgbTimeStruct | null): string | null {
    return this.toModel(date, this.frontendFormat);
  }

  public fromModel (value: string, format?: string): NgbDateStruct & NgbTimeStruct | null {

    this._tmpDate = null;

    if (value) {
      let current = moment(value, format || this.backendFormat);

      this._tmpDate = {
        year: current.year(),
        month: current.month() +1,
        day:current.date(),
        hour: current.hour(),
        minute: current.minute(),
        second: current.second()
      }
    }
    return this._tmpDate;
  }

  public toModel (date: NgbDateStruct & NgbTimeStruct | null, format?: string): string | null {
      return date ? moment()
        .year(date?.year ? date.year : this._tmpDate.year)
        .month(date?.month ? date.month-1 : this._tmpDate.month-1)
        .date(date?.day ? date.day : this._tmpDate.day)
        .hour(date?.hour ? date.hour : this._tmpDate.hour)
        .minute(date?.minute ? date.minute : this._tmpDate.minute)
        .second(date?.second ? date.second : this._tmpDate.second)
        .format(format || this.backendFormat) : null;
  }
}
