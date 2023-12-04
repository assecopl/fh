import moment from "moment";

export class FhngDateUtils {

    /**
     * Czy uzywac trybu scislego biblioteki momentjs
     * Na przykład  czy data 2016-12-24T00:00:00.000+00:00 będzie nieprawidłowa dla maski YYYY-MM-DD
     * @type {boolean}
     */
    public static MOMENT_JS_STRICT_MODE: boolean = true;

    public static FRONTEND_DATETIME_FORMAT: string = 'YYYY-MM-DD HH:mm:ss';
    public static BACKEND_DATETIME_FORMAT: string = "YYYY-MM-DDTHH:mm:ss";

    public static FRONTEND_DATE_FORMAT: string = 'YYYY-MM-DD';
    public static BACKEND_DATE_FORMAT: string = "YYYY-MM-DD";

    /**
     * Transofrm date from backend to frontend format.
     * @param value
     */
    public static reformatToFrontendString(value: any): string {

        if (value.indexOf("T") > 0) {
            return moment.utc(value, FhngDateUtils.BACKEND_DATETIME_FORMAT, FhngDateUtils.MOMENT_JS_STRICT_MODE).format(FhngDateUtils.FRONTEND_DATETIME_FORMAT);
        } else {
            //Simple Date without time - no need to transform
            return value;
        }
    }

    /**
     * Transofrm date from backend to frontend format.
     * @param value
     */
    public static reformatToFrontendDate(value: string): Date {

        if (value.indexOf("T") > 0) {
            return moment.utc(value, FhngDateUtils.BACKEND_DATETIME_FORMAT, FhngDateUtils.MOMENT_JS_STRICT_MODE).toDate();
        } else {
            //Simple Date without time - no need to transform
            return moment.utc(value, FhngDateUtils.BACKEND_DATE_FORMAT).toDate();
        }
    }


    static isDateValid(date, sourceFormat) {
        return moment(date, [sourceFormat], FhngDateUtils.MOMENT_JS_STRICT_MODE).isValid();
    };
}

/**
 * Override and Extend Date default prototype.
 * Change toString behaviour
 * We want to parse every date to specific format when it appears inside html.
 * TODO Better solution ??
 */
Date.prototype["isDateTime"] = false; // Dodatkowy parametr odpowiedzialny za okreslenie czy mamy do czynienia ze zwykłą datą.
Date.prototype.toString = function () {
    //Sprawdzamy czy data posiada godziny/minuty/sekundy/milisekundy, jeżeli któraś wartość jest wypełniona to traktujemy ją jako Datetime
    //Tylko zwykła data będzie miała zerowe wartości po zmianie jej na UTC.
    const d = moment.utc(this);
    if (d.hour() == 0 && d.minute() == 0 && d.second() == 0 && d.millisecond() == 0 || this["isDateTime"] == true) {
        return moment(this).format(FhngDateUtils.FRONTEND_DATE_FORMAT);
    } else {
        return moment(this).format(FhngDateUtils.FRONTEND_DATETIME_FORMAT);
    }
}


