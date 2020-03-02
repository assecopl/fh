import { HTMLFormComponent } from "fh-forms-handler";
import 'imports-loader?moment,define=>false,exports=>false!../external/bootstrap-datepicker';
declare class Calendar extends HTMLFormComponent {
    private readonly blockedDates;
    private readonly values;
    private readonly label;
    private readonly currentDate;
    private readonly minDate;
    private readonly maxDate;
    private readonly changeMonth;
    private readonly changeYear;
    private calendar;
    private onChange;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    update(change: any): void;
    extractChangedAttributes(): {};
    destroy(removeFromParent: boolean): void;
}
export { Calendar };
