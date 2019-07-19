import {FormComponent} from "fh-forms-handler";
import getDecorators from "inversify-inject-decorators";
import {ApplicationLock, Connector} from "fh-forms-handler";
import {FhContainer} from "fh-forms-handler";

let { lazyInject } = getDecorators(FhContainer);

class Timer extends FormComponent {

    @lazyInject('ApplicationLock')
    private applicationLock: ApplicationLock;

    @lazyInject('Connector')
    private connector: Connector;

    private interval: number;
    private active: boolean;
    private onTimer: string;
    private timer: any;

    constructor(componentObj: any, parent: FormComponent) {
        super(componentObj, parent);

        this.interval = this.componentObj.interval;;
        this.active = this.componentObj.active;
        this.onTimer = this.componentObj.onTimer;
    }

    create() {
        this.setupTimer();
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'active':
                        this.active = newValue;
                        this.setupTimer();
                        break;
                    case 'interval':
                        this.interval = newValue;
                        this.setupTimer();
                        break;
                }
            }.bind(this));
        }
    }

    private setupTimer() {
        if (this.onTimer != null && this.getViewMode() == 'NORMAL') {
            if (this.timer != null) {
                clearTimeout(this.timer);
                this.timer = null;
            }
            if (this.active && this.interval > 0) {
                this.timer = setTimeout(this.timeout.bind(this), 1000 * this.interval);
            }
        }
    }

    destroy(removeFromParent) {
        super.destroy(removeFromParent);

        if (this.timer != null) {
            clearTimeout(this.timer);
            this.timer = null;
        }
    };

    private timeout() {
        if (!this.destroyed) {
            if (this.applicationLock.isActive() || !this.connector.isOpen() || !this.isFormActive()) {
                // delay until application lock is taken down or form if activated
                this.timer = setTimeout(this.timeout.bind(this), 200);
            } else {
                // set next timeout and fire event
                this.setupTimer();
                this.fireEventWithLock('onTimer', this.onTimer);
            }
        }
    }
}

export {Timer};