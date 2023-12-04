abstract class BaseEvent {
    protected i18n: any;

    protected constructor() {
    }

    public abstract fire(data: any);
}

export {BaseEvent};
