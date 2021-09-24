import {injectable} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {FormsManager} from "../Socket/FormsManager";
import {FhContainer} from "../FhContainer";

let { lazyInject } = getDecorators(FhContainer);

@injectable()
class CustomActions {
    @lazyInject('FormsManager')
    private formsManager: FormsManager;

    private _callbacks: { [key:string]: (data:string) => void; } = {};

    public CustomActions() {
    }

    public get callbacks(): {[key: string]: (data:string) => void} {
        return this._callbacks;
    }
}

export { CustomActions};