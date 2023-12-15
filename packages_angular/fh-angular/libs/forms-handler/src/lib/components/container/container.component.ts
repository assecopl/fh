import {
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import {FormsManager} from "../../Socket/FormsManager";
import {IForm} from "../../Base";
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {ComponentManager} from "../../service/component-manager.service";

@Component({
  selector: '[fhng-container],fhng-container',
  templateUrl: './container.component.html',
  styleUrls: ['./container.component.scss'],
})
export class ContainerComponent implements OnInit, OnChanges, OnDestroy {
  @Input('id') public _id: string = null;

  @Input("fhng-container") public _id2: string = null;

  public get id() {
    if (this._id2) {
      return this._id2;
    }
    return this._id;
  }

  public set id(val: string) {
    this._id = val;
  }

  public form: IForm = null;

  protected modalRef: NgbModalRef[] = [];

  constructor(
      private formManager: FormsManager,
      private modalService: NgbModal,
      private componentManager: ComponentManager,
      private changeDetectorRef:ChangeDetectorRef
  ) {
  }

  ngOnDestroy(): void {

  }

  ngOnInit() {
    this.formManager.openedFormsSubject.subscribe((form: IForm) => {
      if (form.container == this.id) {
        this.form = this.resolveModal(form)
        if (form.modal) {
          let cType = this.componentManager.getComponentFactory("Form")
          let modalRef = this.modalService.open(cType, {
            backdrop: 'static',
            keyboard: false,
            modalDialogClass: this.resolveModalSize()
          });

          modalRef.componentInstance.formType = form.formType;
          modalRef.componentInstance.subelements = form?.subelements || [];
          modalRef.componentInstance.nonVisualComponents = form?.nonVisualSubcomponents || [];
          modalRef.componentInstance.formId = form?.id;
          modalRef.componentInstance.id = form?.id;
          modalRef.componentInstance.label = form.label;
          modalRef.componentInstance.modal = form.modal;
          modalRef.componentInstance.hideHeader = form.hideHeader;
          modalRef.componentInstance.container = this;
          this.modalRef.push(modalRef);
        }
        this.changeDetectorRef.detectChanges();
      }
    });

    this.formManager.closeFormsSubject.subscribe((formId: string) => {
      //close main form
      if (this.form && this.form.id == formId) {
        this.form = null;
        this.changeDetectorRef.detectChanges();
      }
      let modalToDelete:number = -1;
      this.modalRef.forEach((modal,index) => {
        if(modal.componentInstance.id == formId){
          modal.dismiss();
          modalToDelete = index;
        }
      });
      if(modalToDelete > -1){
        this.modalRef.splice(modalToDelete, 1);
      }
    });
  }

  resolveModalSize() {
    let cssClass = ""
    switch (this.form.modalSize) {
      case 'SMALL':
        cssClass = 'modal-sm';
        break;
      case 'REGULAR':
        cssClass = 'modal-md';
        break;
      case 'LARGE':
        cssClass = 'modal-lg';
        break;
      case 'XLARGE':
        cssClass = 'modal-xlg';
        break;
      case 'XXLARGE':
        cssClass = 'modal-xxlg';
        break;
      case 'FULL':
        cssClass = 'modal-full';
        break;
    }
    return cssClass;
  }

  ngOnChanges(changes: SimpleChanges): void {

  }

  resolveModal(form: IForm) {
    form.modal = false;
    if (["MODAL_OVERFLOW", "MODAL"].includes(form.formType)) {
      form.modal = true;
    }
    return form;
  }

}
