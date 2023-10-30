import {Directive, inject} from "@angular/core";
import {FormComponentChangesQueue} from "../../service/FormComponentChangesQueue";
import {Util} from "../../service/Util";

/**
 * Component odpowiedzialny za pobieranie zmian przed wysyłką zapytania socketowego.
 */
@Directive()
export abstract class FhngChangesComponent {

  abstract id: string;
  abstract formId: string;

  protected changesQueue: FormComponentChangesQueue = new FormComponentChangesQueue();
  protected util: Util = inject(Util);

  public childFhngComponents: FhngChangesComponent[] = [];

  public extractChangedAttributes() {
    if (this.changesQueue) {
      return this.changesQueue.extractChangedAttributes();
    }
    return {};
  }

  public collectAllChanges() {
    var allChanges = [];
    this.childFhngComponents.forEach(function (component) {
      var changes = component.collectAllChanges();
      allChanges = allChanges.concat(changes);
    }.bind(this));
    return this.collectChanges(allChanges);
  };

  protected collectChanges(allChanges) {
    var pendingChangedAttributes;
    // if (this.designMode) {
    // in design mode only changesQueue should be used
    // pendingChangedAttributes = this.changesQueue.extractChangedAttributes();
    // } else {
    pendingChangedAttributes = this.extractChangedAttributes();
    // }
    if (this.util.countProperties(pendingChangedAttributes) > 0) {
      allChanges.push({
        fieldId: this.id,
        changedAttributes: pendingChangedAttributes,
        formId: this.formId
      });
    }
    return allChanges;
  };


}
