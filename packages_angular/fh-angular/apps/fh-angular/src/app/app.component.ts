import {Component, OnInit} from '@angular/core';
import {FH} from "@fh-ng/forms-handler";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {

  constructor(private fh: FH) {
  }

  ngOnInit(): void {
    this.fh.init();

  }
  title = 'fh-angular';
}
