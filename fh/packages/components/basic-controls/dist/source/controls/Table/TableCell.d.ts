import { HTMLFormComponent } from "fh-forms-handler";
import { FH } from "fh-forms-handler";
declare class TableCell extends HTMLFormComponent {
    protected fh: FH;
    private horizontalAlign;
    private verticalAlign;
    private rowspan;
    private readonly ieFocusFixEnabled;
    designMode: boolean;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    applyChange(change: any): void;
    setAccessibility(accessibility: any): void;
}
export { TableCell };
