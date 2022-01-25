import {CheckBox} from 'fh-basic-controls';
import {FhContainer, HTMLFormComponent} from 'fh-forms-handler';
import getDecorators from "inversify-inject-decorators";

let {lazyInject} = getDecorators(FhContainer);

class CheckBoxFhDP extends CheckBox {
  protected isDefaultStyle: boolean;
  protected isTriState: boolean;
  protected isIntermediate: boolean = false;

  constructor(componentObj: any, parent: HTMLFormComponent) {
    super(componentObj, parent);
    if (this.componentObj.rawValue === 'undefined' || this.componentObj.rawValue === undefined || this.componentObj.rawValue === 'null' || this.componentObj.rawValue === null) {
      this.rawValue = null;
    } else {
      this.rawValue = this.componentObj.rawValue == true || this.componentObj.rawValue == "true";
    }
    this.isDefaultStyle = componentObj.isDefaultStyle;
    this.isTriState = componentObj.isTriState;
  }

  create() {
    super.create();
    if(!this.isDefaultStyle===true) {
      this.htmlElement.classList.add('form-switch');
    }

    if(this.isTriState===true) {
      $(this.component).on('change', this.onChangeTriState.bind(this));
    }

    if(this.rawValue === null) {
      this.input.indeterminate = true;
      this.input.checked = true;
    }
  }

  onChangeTriState() {
    if(this.input.checked===false) {
      if(this.isIntermediate===false) {
        this.input.indeterminate = true;
        this.isIntermediate = true;
        this.input.checked = true;
      } else {
        this.isIntermediate = false;
      }
    }
  }

  update(change) {
    super.update(change);
  }

  extractChangedAttributes() {
    return this.changesQueue.extractChangedAttributes();
  };

  destroy(removeFromParent: boolean) {
    super.destroy(removeFromParent);
  }
}

export {CheckBoxFhDP}
