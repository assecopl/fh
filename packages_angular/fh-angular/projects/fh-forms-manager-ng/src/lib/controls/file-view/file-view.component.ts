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
    SkipSelf
} from '@angular/core';
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';
import {DocumentedComponent} from '@ewop/ng-core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {FormComponent} from "../form/form.component";
import {DomSanitizer, SafeResourceUrl, SafeUrl} from "@angular/platform-browser";
import {HttpClient} from "@angular/common/http";

@DocumentedComponent({
    category: DocumentedComponent.Category.IMAGE_HTML_MD,
    value: "Component responsible for displaying of file resource provided by URL.", icon: "fa fa-edit"
})
@Component({
    selector: 'ewop-file-view',
    templateUrl: './file-view.component.html',
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => FileViewComponent)}
    ],
})
export class FileViewComponent extends EwopReactiveInputC implements OnInit, OnChanges {

    public width = 'md-12';

    @Input()
    public url: string = null;

    public safeUrl: SafeResourceUrl;

    public text: string;

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: FormComponent,
                private sanitizer: DomSanitizer,
                private httpClient: HttpClient
    ) {
        super(injector, parentEwopComponent, iForm);
    }

    ngOnInit() {
        super.ngOnInit();

        this.determineType();

        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.url);
    }

    private determineType() {
        if (this.url.endsWith(".pdf") || this.url.endsWith(".PDF")) {
            this.type = 'PDF';
        } else if (this.url.endsWith(".txt") || this.url.endsWith(".xml") ||
            this.url.endsWith(".TXT") || this.url.endsWith(".XML")) {
            this.type = 'TEXT';

            this.httpClient.get(this.url, {responseType: 'text'}).subscribe((result: any) => {
                if ((typeof result) == 'object') {
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
