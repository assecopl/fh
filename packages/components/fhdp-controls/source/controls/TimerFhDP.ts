import {HTMLFormComponent} from "fh-forms-handler";

class TimerFhDP extends  HTMLFormComponent{
    private timeout: any;
    private onInterval: any;

    private _interval: any;


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        console.log("TimerFhDP log", componentObj);
        console.log(this.componentObj.timeout, !isNaN(this.componentObj.timeout), new Number(this.componentObj.timeout) > 500);
        if (this.componentObj.timeout && !isNaN(this.componentObj.timeout) && new Number(this.componentObj.timeout) > 500) {
            this.timeout = new Number(this.componentObj.timeout);
            this._interval = setInterval(this.onIntervalEvent.bind(this), this.timeout);
        }
        this.onInterval = this.componentObj.onInterval;
    }

    create() {
        super.create();
    };

    onIntervalEvent() {
        if (this.accessibility === 'EDIT' && this.onInterval) {
            this.fireEvent('onInterval', this.onInterval);
        }
    }


    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'timeout':
                        this.timeout = new Number(newValue);
                        clearInterval(this._interval);
                        if (newValue && !isNaN(newValue) && this.timeout > 0) {
                            this._interval = setInterval(this.onIntervalEvent.bind(this), this.timeout);
                        }
                        break;
                    case 'onInterval':
                        this.onInterval = newValue;
                }
            }.bind(this));
        }

    };

    setAccessibility(accessibility) {
        super.setAccessibility(accessibility);
    };

    destroy(removeFromParent) {
        this.focusableComponent = undefined;
        if (this._interval) {
            clearInterval(this._interval);
        }
        super.destroy(removeFromParent);
    }
}

export {TimerFhDP};
