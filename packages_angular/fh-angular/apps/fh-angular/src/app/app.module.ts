import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FhNgCoreLiteModule} from "fh-ng-core-lite";
import {Connector} from "../../../../libs/fh-ng-core-lite/src/lib/Socket/Connector";
import {Util} from "../../../../libs/fh-ng-core-lite/src/lib/service/Util";
import {ApplicationLockService} from "../../../../libs/fh-ng-core-lite/src/lib/service/application-lock.service";
import {I18nService} from "../../../../libs/fh-ng-core-lite/src/lib/service/i18n.service";
import {SocketHandler} from "../../../../libs/fh-ng-core-lite/src/lib/Socket/SocketHandler";

@NgModule({
  declarations: [AppComponent],
  imports: [NgbModule, BrowserModule,
    FhNgCoreLiteModule.forRoot({production: false, development: true, debug: true})
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {

  constructor(private connector: Connector,
              private util: Util,
              private applicationLock: ApplicationLockService,
              private i18n: I18nService,
              private socketHandler: SocketHandler) {
    connector.setup(util.getPath('socketForms'))
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
