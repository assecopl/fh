import {
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import {FormsManager} from "../../Socket/FormsManager";

@Component({
  selector: '[fhng-container]',
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

  public form: any = null;

  constructor(
    private formManager: FormsManager
  ) {
  }

  ngOnDestroy(): void {

  }

  ngOnInit() {

    this.formManager.openedFormsSubject.subscribe((form) => {
      if (form.container == this.id) this.form = form;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {

  }

}
