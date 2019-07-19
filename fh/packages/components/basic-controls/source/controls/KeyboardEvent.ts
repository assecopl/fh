import {FormComponent} from "fh-forms-handler";
import hotkeys from 'hotkeys-js';

class KeyboardEvent extends FormComponent {
    private readonly shortcut: string;
    private readonly event: string;

    constructor(componentObj: any, parent: FormComponent) {
        super(componentObj, parent);

        this.shortcut = componentObj.shortcut.toLowerCase().replace('_', '+');
        this.event = componentObj.eventBinding;
        hotkeys.filter = function () {
            return true;
        };
        hotkeys(this.shortcut, function (event) {
            event.preventDefault();
            this.fireEventWithLock('onClick', this.event);
        }.bind(this));
    }

    destroy(removeFromParent) {
        hotkeys.unbind(this.shortcut);

        super.destroy(removeFromParent);
    };
}

export {KeyboardEvent};