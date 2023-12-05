import {Directive, Input} from '@angular/core';
import {FhngComponent, Page} from "@fh-ng/forms-handler";

/**
 * FIXME Przebudować Tabelę!!!!! Między innymi tak aby nie korzystać z ViewCHild i ContentCHild - działają z opuźnieniem.
 */

@Directive()
export abstract class FormComponentRef extends FhngComponent {

}
