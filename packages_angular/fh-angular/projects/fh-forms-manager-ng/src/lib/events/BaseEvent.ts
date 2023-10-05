abstract class BaseEvent {
  protected i18n: any;

  public abstract fire(data: any);
}

export {BaseEvent};
