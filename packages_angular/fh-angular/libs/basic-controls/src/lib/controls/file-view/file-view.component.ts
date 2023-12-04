import {
  Component,
  forwardRef,
  Host,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {DomSanitizer, SafeResourceUrl,} from '@angular/platform-browser';
import {HttpClient} from '@angular/common/http';
import {FhngComponent, FormComponent} from '@fh-ng/forms-handler';

@Component({
  selector: 'fhng-file-view',
  templateUrl: './file-view.component.html',
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => FileViewComponent),
    },
  ],
})
export class FileViewComponent
  extends FhngReactiveInputC
  implements OnInit, OnChanges {
  public width = 'md-12';

  @Input()
  public url: string = null;

  public safeUrl: SafeResourceUrl;

  public text: string;

  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @SkipSelf() iForm: FormComponent,
    private sanitizer: DomSanitizer,
    private httpClient: HttpClient
  ) {
    super(injector, parentFhngComponent, iForm);
  }

  ngOnInit() {
    super.ngOnInit();

    this.determineType();

    this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.url);
  }

  private determineType() {
    if (this.url.endsWith('.pdf') || this.url.endsWith('.PDF')) {
      this.type = 'PDF';
    } else if (
      this.url.endsWith('.txt') ||
      this.url.endsWith('.xml') ||
      this.url.endsWith('.TXT') ||
      this.url.endsWith('.XML')
    ) {
      this.type = 'TEXT';

      this.httpClient
        .get(this.url, {responseType: 'text'})
        .subscribe((result: any) => {
          if (typeof result == 'object') {
            this.text = new XMLSerializer().serializeToString(result);
          } else {
            this.text = result;
          }
        });
    } else {
      this.type = 'IMAGE';
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
