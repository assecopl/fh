import { HTMLFormComponent } from 'fh-forms-handler';
import { SelectComboMenu } from '../SelectComboMenu';
declare class SelectComboMenuOptimized extends SelectComboMenu {
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    setValues(values: any): void;
    setAccessibility(accessibility: any): void;
    /**
     * @Override
     * @param accessibility
     */
    display(): void;
    render(): void;
}
export { SelectComboMenuOptimized };
