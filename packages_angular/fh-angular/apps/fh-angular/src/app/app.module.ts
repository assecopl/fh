import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {
  ApplicationLockService,
  Connector,
  FhNgFormsHandlerModule,
  I18nService,
  SocketHandler,
  Utils
} from "@fh-ng/forms-handler";
import {FhNgBasicControlsModule} from "@fh-ng/basic-controls";
import {CommonModule} from '@angular/common';

@NgModule({
  declarations: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [CommonModule, NgbModule, BrowserModule, FhNgBasicControlsModule,
    FhNgFormsHandlerModule.forRoot({production: false, development: true, debug: true})
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {

  constructor(private connector: Connector,
              private utils: Utils,
              private applicationLock: ApplicationLockService,
              private i18n: I18nService,
              private socketHandler: SocketHandler) {
    connector.setup(utils.getPath('socketForms'))
    // connector.target = util.getPath('socketForms');
    connector.reconnectCallback = () => {
      console.log(i18n.__('error.connection_lost'))
      // applicationLock.createInfoDialog(i18n.__('error.connection_lost'), null, null, null, null, false);
    }
    connector.openCallback = () => {
      console.log("close info dialog");
      // applicationLock.closeInfoDialog();
    }

    socketHandler.addConnector(connector);
  }


}
