import { OutputLabel } from "../OutputLabel";
import { HTMLFormComponent } from 'fh-forms-handler';
declare class DropdownItem extends OutputLabel {
    private labelComponent;
    private readonly url;
    constructor(componentObj: any, parent: HTMLFormComponent);
    create(): void;
    onClickEvent(): void;
    buildInnerHTML(): void;
    destroy(removeFromParent: any): void;
}
export { DropdownItem };
