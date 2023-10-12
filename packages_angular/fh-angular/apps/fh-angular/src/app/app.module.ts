import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FhNgCoreLiteModule} from "fh-ng-core-lite";

@NgModule({
  declarations: [AppComponent],
  imports: [NgbModule, BrowserModule, FhNgCoreLiteModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {
}
