import {Component, Input, OnInit} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {FhngComponent} from "@fh-ng/forms-handler";


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
    this.modalService.open(content, {size:"xxl", ariaLabelledBy: 'modal-basic-title'})
  }

  ngOnInit() {
    this.mapcomponentData();
  }

  mapcomponentData() {
    let dataKeys = Object.keys(this.component);
    dataKeys.forEach(key => {
      if (this.component[key] && (typeof this.component[key] !== "object")) {
        this.componentData2[key] = this.component[key];
      } else {
        // if(Array.isArray(this.component[key])) this.componentData2[key] = this.component[key];
      }
    })
  }

}

