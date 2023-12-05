import {Component, forwardRef, Injector, OnInit, Optional, SkipSelf, Input} from "@angular/core";
import {FhngHTMLElementC} from "../../models/componentClasses/FhngHTMLElementC";
import {FhngComponent, I18nService, IDataAttributes} from "@fh-ng/forms-handler";

interface IEmbedPageDataAttributes extends IDataAttributes {
  src: string;
  label?: string;
}

interface ActiveXObject {
  new (s: string): any;
}

declare var ActiveXObject: ActiveXObject;

@Component({
  selector: '[fhng-embed-page]',
  templateUrl: './embed-page.component.html',
  styleUrls: ['./embed-page.component.sass'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => EmbedPageComponent),
    },
  ],
})
export class EmbedPageComponent extends FhngHTMLElementC implements OnInit {
  @Input()
  public labelSize: string;

  @Input()
  public src: string;

  public base64regex:RegExp = /^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/;

  public get isBase64(): boolean {
    return this.base64regex.test(this.src);
  }

  public get mimeType(): string {
    if (this.src) {
      const matches = this.src.toString().match(/[^:]\w+\/[\w-+\d.]+(?=;|,)/);
      return matches ? matches[0] : null;
    }

    return null;
  }

  public get isAcrobatInstalled(): boolean {
    return !!this._getPDFPlugin();
  };

  public get isSupportPDF(): boolean {
    let hasPDFViewer = false;

    try {
      var pdf =
          navigator.mimeTypes &&
          navigator.mimeTypes['application/pdf']
              ? navigator.mimeTypes['application/pdf'].enabledPlugin
              : 0;
      if (pdf) hasPDFViewer = true;
    } catch (e) {
      if (navigator.mimeTypes['application/pdf'] != undefined) {
        hasPDFViewer = true;
      }
    }

    return hasPDFViewer;
  }

  public get browserName(): string {
    var userAgent = navigator ? navigator.userAgent.toLowerCase() : "other";

    if (userAgent.indexOf("chrome") > -1) { return "chrome"; }
    else if (userAgent.indexOf("safari") > -1) { return "safari"; }
    else if (userAgent.indexOf("msie") > -1 || userAgent.indexOf("trident") > -1) { return "ie"; }
    else if (userAgent.indexOf("firefox") > -1) { return "firefox";}
    return userAgent;
  };


  public get acrobatInfo () {
    return {
      browser: this.browserName,      // Return browser name
      acrobat: this.isAcrobatInstalled,   // return pdf viewer is enabled or not
      acrobatVersion: this._getAcrobatVersion  // reurn acrobat version for browser
    }
  }

  constructor(
    public override injector: Injector,
    public i18n: I18nService,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public override mapAttributes(data: IEmbedPageDataAttributes): void {
    super.mapAttributes(data);

    this.src = data.src;
    this.label = data.label || this.getExtension();

    console.log('EmbeddedViewComponent:map', data)
  }

  public getExtension(): string {
    return 'file.' + this.src.toString().match(/[^:/]\w+(?=;|,)/)[0];
  }

  public createBlob (): string {
    if (this.isBase64 && this.mimeType) {
      try {
        const base64 = this.src.toString().split("base64,")[1];
        var blob = this._base64ToBlob(base64);

        return URL.createObjectURL(blob);
      } catch (error) {
        console.log("Blob creation problem, fallback to base64");
      }
    }

    return this.src || null;
  }

  private _base64ToBlob (base64): Blob {
    const binStr = atob( base64 );
    const len = binStr.length;
    const arr = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      arr[ i ] = binStr.charCodeAt( i );
    }
    return new Blob( [ arr ], { type: this.mimeType || 'application/octet-stream' } );
  }

  private _getPDFPlugin(): any {
    if (this.browserName == 'ie') {
      return this._getActiveXObject('AcroPDF.PDF') || this._getActiveXObject('PDF.PdfCtrl');
    }

    return this._getNavigatorPlugin('adobe acrobat') || this._getNavigatorPlugin('pdf') || this._getNavigatorPlugin('foxit reader');  // works for all plugins which has word like 'adobe acrobat', 'pdf' and 'foxit reader'.
  };

  private _getActiveXObject(name: string): any {
    try {
      return new ActiveXObject(name);
    } catch (e) {
      return null
    }
  };

  private _getNavigatorPlugin(name: string): Plugin | null {
    try {
      for (const key in navigator.plugins) {
        var plugin = navigator.plugins[key];
        if (plugin.name.toLowerCase().indexOf(name) > -1) {
          return plugin;
        }
      }
    } catch (e) {}

    return null;
  };

  private _getAcrobatVersion(): number {
    try {
      var plugin = this._getPDFPlugin();

      if (this.browserName == 'ie') {
        var versions = plugin.GetVersions().split(',');
        var latest = versions[0].split('=');
        return parseFloat(latest[1]);
      }

      if (plugin.version) return parseInt(plugin.version);
      return plugin.name

    }
    catch (e) {}

    return null;
  }

}
