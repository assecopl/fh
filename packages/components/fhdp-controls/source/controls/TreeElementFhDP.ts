import {HTMLFormComponent} from "fh-forms-handler";
import {TreeElement} from 'fh-basic-controls';
import {TreeElementHelper} from './helpers/TreeElementFhDPHelper'

class TreeElementFhDP extends TreeElement {
  private expandedException?: string;
  private onLabelClickOverride?: any;
  private selectedOverride: boolean;
  private labelOverride: string;
  private currentElement: boolean;

  constructor(componentObj: any, parent: HTMLFormComponent) {
    super(componentObj, parent);
    console.log("TreeElementFhDP", componentObj)
    this.expandedException = componentObj.expandedException;
    console.log('expandedException', this.expandedException);
    this.onLabelClickOverride = componentObj.onLabelClick;
    this.selectedOverride = componentObj.selected || false;
    this.labelOverride = componentObj.label;
    TreeElementHelper.getInstance().registerElement(this);
  }

  create() {
    super.create();

    if (this.expandedException) {
      const exceptions = this.expandedException.split('|');
      for (const ex of exceptions) {
        if (this.labelOverride && this.labelOverride.includes(ex)) {
          setTimeout(() => {
            this.collapse();
            this.toggleCollaped();
          }, 100);
        }
      }

      if(this.accessibility !== 'HIDDEN') {
        let regex = /[a-zA-Z_0-9]+\[0\]/;
        if (this.id.endsWith('[0]')) {
          let match = this.id.match(regex);
          if (match && match.length && match[0] === this.id) {
            this.setCurrent(true);
            this.selectBranch(document.getElementById(this.id));
          }
        }
      }
    }
  }

  expand() {
    super.expand();
    
    (this.components || []).forEach(function (node) {
      if (node.componentObj.type === 'TreeElementFhDP') {
          node.expand();
      }
  }.bind(this));
};

  setCurrent(isCurrent) {
    this.currentElement = isCurrent;
    this.toggleHighlight();
  }

  selectBranch(branchToSelect) {
    branchToSelect.children[0].children[0].click();
    if ((branchToSelect.children[0].children[0].children[0] as HTMLElement).classList.contains('fa-caret-right')) {
      (branchToSelect.children[0].children[0].children[0] as HTMLElement).click();
    }
  }
  
  toggleHighlight() {
    if (this.currentElement) {
      this.component.querySelector('.row').style.color = 'var(--color-main)';
    } else {
      this.component.querySelector('.row').style.color = 'var(--color-tree-element-selected)';
    }
  }

  destroy(removeFromParent) {
    TreeElementHelper.getInstance().remove(this);
    super.destroy(removeFromParent);
  };

  update(change) {
    super.update(change);
    
    if (change.addedComponents) {
      const firstNewEl = change.addedComponents[Object.keys(change.addedComponents)[0]];
      if (firstNewEl && firstNewEl[0]) {
        const newElId = firstNewEl[0].id;
        const el = document.getElementById(newElId);
        try {
          if ((el.parentElement.parentElement.children[0].children[0].children[0] as HTMLElement).classList.contains('fa-caret-right')) {
            (el.parentElement.parentElement.children[0].children[0].children[0] as HTMLElement).click();
          }
          if (el) {
            const row = el.children[0];
            if (row && row.children[0] && (row.children[0] as HTMLElement).click) {
              (row.children[0] as HTMLElement).click();
              if ((row.children[0].children[0] as HTMLElement).classList.contains('fa-caret-right')) {
                (row.children[0].children[0] as HTMLElement).click();
              }
            }
          }
        } catch (e) {}
      }
    }
  }

  labelClicked(event) {
    event.stopPropagation();
    TreeElementHelper.getInstance().setCurrent(this);

    this.changesQueue.queueAttributeChange('selected', this.selectedOverride);

    if (this.onLabelClickOverride) {
        this.fireEventWithLock('onLabelClick', "onLabelClick");
    }
    return false;
  };
}

export {TreeElementFhDP}
