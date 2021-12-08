import {CustomActions, FhContainer} from "fh-forms-handler";
import {TreeElementFhDP} from "../TreeElementFhDP";

interface IOpenTreeBranch {
  parentId: string, 
  nestionId: string
}

export class TreeElementHelper {
  private static __instance: TreeElementHelper;
  private elements: TreeElementFhDP[];
  public static getInstance() {
    if (!TreeElementHelper.__instance) {
      TreeElementHelper.__instance = new TreeElementHelper();
      TreeElementHelper.__instance.registerCallback('openTreeBranch', TreeElementHelper.__instance.openTreeBranch.bind(TreeElementHelper.__instance));
    }
    return TreeElementHelper.__instance;
  }

  private constructor() {
    this.elements = new Array();
  }

  registerElement(element: TreeElementFhDP) {
    this.elements.push(element);
  }

  clearElements() {
    this.elements = new Array();
  }

  setCurrent(element: TreeElementFhDP) {
    this.elements.forEach((el) => {
      el.setCurrent(element === el);
    });
  }

  remove(element: TreeElementFhDP) {
    this.elements.splice(this.elements.indexOf(element), 1);
  }

  registerCallback(name: string, callback: (componentId ?: string) => void) {
    let customActions = FhContainer.get<CustomActions>('CustomActions');
    if (customActions == null) {
        return new Error('CustomActions is not registered.');
    }
    customActions.callbacks[name] = callback;
  }

  openTreeBranch(input: string) {
    let meta: IOpenTreeBranch;
    try {
      setTimeout(() => {
        meta = JSON.parse(input);
        for (const element of this.elements) {
          if (this.isInTreeWithId(meta.parentId, element) && element.id.endsWith(meta.nestionId)) {
            const el = document.getElementById(element.id);

            if ((el.parentElement.parentElement.children[0].children[0].children[0] as HTMLElement).classList.contains('fa-caret-right')) {
              (el.parentElement.parentElement.children[0].children[0].children[0] as HTMLElement).click();

              const elementParentToClick = el.parentElement.parentElement.parentElement.parentElement as HTMLElement;
              const elementToClick = elementParentToClick.children[0].children[0].children[0] as HTMLElement;
              if(elementToClick.classList.contains('fa-caret-right')) {
                elementToClick.click();

                const elementToClick2 = elementParentToClick.parentElement.parentElement.children[0].children[0].children[0] as HTMLElement;
                if(elementToClick2.classList.contains('fa-caret-right')) {
                  elementToClick2.click();
                }
              }
            }

            if (el) {
              this.setCurrent(element);
              element.selectBranch(el);
            }
          }
        }
      }, 100);
    } catch (e) {console.log(e); return}
  }

  isInTreeWithId(id, element) {
    let cElement = document.getElementById(element.component.id);
    if (cElement) {
      while (!!cElement) {
        cElement = cElement.parentElement;
        if (!cElement) {
          return false;
        }
        if (cElement.id && cElement.id === id) {
          return true;
        }
      }
    }
    return false;
  }
}
