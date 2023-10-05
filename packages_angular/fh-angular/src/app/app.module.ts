import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {FhFormsManagerNgModule} from "fh-forms-manager-ng";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        NgbModule,
        BrowserModule,
        FhFormsManagerNgModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
