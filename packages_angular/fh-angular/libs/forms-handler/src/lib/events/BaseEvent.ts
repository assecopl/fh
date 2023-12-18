abstract class BaseEvent {
    protected i18n: any;

    public constructor() {
    }

    public abstract fire(data: any);
}

export {BaseEvent};
