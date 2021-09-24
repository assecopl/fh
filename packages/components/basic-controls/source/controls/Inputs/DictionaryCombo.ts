import {FhContainer, HTMLFormComponent} from 'fh-forms-handler';
import getDecorators from "inversify-inject-decorators";
import { Combo } from "./Combo";

let { lazyInject } = getDecorators(FhContainer);

class DictionaryCombo extends Combo {

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
    }

    public getInputValue(val){
            return  val.displayAsTarget ? val.targetValue : val.displayedValue;
    }

}

export {DictionaryCombo}
