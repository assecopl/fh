import {Directive, Input} from '@angular/core';
import {FhngComponent} from "../../models/componentClasses/FhngComponent";

/**
 * FIXME Przebudować Tabelę!!!!! Między innymi tak aby nie korzystać z ViewCHild i ContentCHild - działają z opuźnieniem.
 */

@Directive()
export abstract class FormComponentRef extends FhngComponent {

}
