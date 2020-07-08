import { HTMLFormComponent } from "fh-forms-handler";
import { FH } from "fh-forms-handler";
declare class TableCellOptimized extends HTMLFormComponent {
    protected fh: FH;
    private horizontalAlign;
    private verticalAlign;
    private rowspan;
    private readonly ieFocusFixEnabled;
    designMode: boolean;
    componentsToProcess: string[];
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    protected addComponents(componentsList: any): void;
    applyChange(change: any): void;
    setAccessibility(accessibility: any): void;
    /**
     * @Override
     * @param accessibility
     */
    display(): void;
    render(): void;
}
export { TableCellOptimized };
