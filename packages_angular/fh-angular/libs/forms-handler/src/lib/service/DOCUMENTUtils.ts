import {DOCUMENT} from "@angular/common";
import {Inject, Injectable} from "@angular/core";

@Injectable({
    providedIn: 'root',
})
class DOCUMENTUtils {


    constructor(@Inject(DOCUMENT) private document: Document) {
    }


    toggleClassById(id: string, remove?: string, add?: string) {
        const element = this.document.getElementById(id);
        if (!!element) {
            if (!!remove) {
                element.classList.remove(remove);
            }
            if (!!add) {
                element.classList.add(add);
            }
        }
    }

    /**
     * Edits classlist of element wrapper provided by id
     * @param id Id of DOM element
     * @param remove class name that should be removed from element wrapper
     * @param add class name that should be added in element wrapper
     */
    toggleClassByWrapper(id: string, remove?: string, add?: string) {
        const element = this.document.getElementById(id);
        if (!!element) {
            const wrapper = (element.parentNode as HTMLElement);
            if (!!wrapper) {
                if (!!remove) {
                    wrapper.classList.remove(remove);
                }
                if (!!add) {
                    wrapper.classList.add(add);
                }
            }
        }
    }


}

export {DOCUMENTUtils};
