import {Component, forwardRef, Injector, OnInit, Optional, SkipSelf,} from '@angular/core';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from "@fh-ng/forms-handler";
import {FhngHTMLElementC} from "../../models/componentClasses/FhngHTMLElementC";

@Component({
  selector: 'fhng-cookie-info',
  templateUrl: './cookie-info.component.html',
  styleUrls: ['./cookie-info.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => CookieInfoComponent)},
  ],
})
export class CookieInfoComponent extends FhngComponent implements OnInit {


  protected showCookieInfo:boolean = false;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

  }

  override ngOnInit() {
    super.ngOnInit();
    const cookieHandled=  window.localStorage.getItem('cookieHandled');
    if(cookieHandled != 'true'){
      this.showCookieInfo = true;
    } else {
      this.showCookieInfo = false;
    }
  }

  public closeClick = () => {
    window.localStorage.setItem('cookieHandled', 'true');
    this.showCookieInfo = false;
    // removeCookieAlert();
  }


}





