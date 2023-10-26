import {Component, Input, OnInit} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'fh-component-debugger',
  templateUrl: './debugger.component.html',
  styleUrls: ['./debugger.component.scss']
})
export class DebuggerComponent implements OnInit {
  @Input()
  public componentData: any = {};
  public componentData2: any = {};

  public isErrorsCollapsed: boolean = true;
  public isErrorsCollapsed2: boolean = true;

  constructor(
    public component: FhngComponent,
    private modalService: NgbModal
  ) {
  }

  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'})
  }

  ngOnInit() {
    this.mapcomponentData();
  }

  mapcomponentData() {
    let dataKeys = Object.keys(this.component);
    dataKeys.forEach(key => {
      if (this.component[key] && typeof this.component[key] !== "object") {
        this.componentData2[key] = this.component[key];
      }
    })
  }

}

