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

  protected modalRef: NgbModalRef = null;

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
          this.modalRef = this.modalService.open(cType, {
            backdrop: 'static',
            keyboard: false,
            modalDialogClass: this.resolveModalSize()
          });

          this.modalRef.componentInstance.formType = form.formType;
          this.modalRef.componentInstance.subelements = form?.subelements || [];
          this.modalRef.componentInstance.nonVisualComponents = form?.nonVisualSubcomponents || [];
          this.modalRef.componentInstance.formId = form?.id;
          this.modalRef.componentInstance.id = form?.id;
          this.modalRef.componentInstance.label = form.label;
          this.modalRef.componentInstance.modal = form.modal;
          this.modalRef.componentInstance.hideHeader = form.hideHeader;
          this.modalRef.componentInstance.container = this;
        }
        this.changeDetectorRef.detectChanges();
      }
      ;
    });

    this.formManager.closeFormsSubject.subscribe((formId: string) => {
      if (this.form && this.form.id == formId) {
        if (this.modalRef) {
          this.modalRef.dismiss();
        }
        this.form = null;
        this.changeDetectorRef.detectChanges();
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
