import {CustomActions, FhContainer} from "fh-forms-handler";
import {DictionaryComboFhDP} from "../inputs/DictionaryComboFhDP";

interface DictionaryComboValidationResult {
  id: string, 
  result: boolean
}

export class DictionaryComboFhDPHelper {
  private static __instance: DictionaryComboFhDPHelper;
  private elements: DictionaryComboFhDP[];
  public static getInstance() {
    if (!DictionaryComboFhDPHelper.__instance) {
      DictionaryComboFhDPHelper.__instance = new DictionaryComboFhDPHelper();
      DictionaryComboFhDPHelper.__instance.registerCallback('dictionaryComboValidated', DictionaryComboFhDPHelper.__instance.dictionaryComboValidated.bind(DictionaryComboFhDPHelper.__instance));
    }
    return DictionaryComboFhDPHelper.__instance;
  }

  private constructor() {
    this.elements = new Array();
  }

  registerElement(element: DictionaryComboFhDP) {
    this.elements.push(element);
  }
  
  registerCallback(name: string, callback: (componentId ?: string) => void) {
    let customActions = FhContainer.get<CustomActions>('CustomActions');
    if (customActions == null) {
        return new Error('CustomActions is not registered.');
    }
    customActions.callbacks[name] = callback;
  }

  dictionaryComboValidated(input: string) {
    let meta: DictionaryComboValidationResult;
    try {
      meta = JSON.parse(input);
    } catch (e) {console.log(e); return}
    for (const element of this.elements) {
      if (element.guuid === meta.id) {
        element.validated(meta.result)
      }
    }
  }
}
