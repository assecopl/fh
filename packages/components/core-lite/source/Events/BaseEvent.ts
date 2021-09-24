import {Container, injectable} from "inversify";
import {I18n} from "../I18n/I18n";
import getDecorators from "inversify-inject-decorators";
import {FhContainer} from "../FhContainer";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
abstract class BaseEvent {
    @lazyInject("I18n")
    protected i18n: I18n;

    public abstract fire(data: any);
}

export {BaseEvent};