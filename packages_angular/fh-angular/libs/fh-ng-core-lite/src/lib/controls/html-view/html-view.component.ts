import {FhngHTMLElementC} from "../../models/componentClasses/FhngHTMLElementC";
import {Component, forwardRef, Injector, Input, Optional, SkipSelf} from "@angular/core";
import {FhngComponent} from "../../models/componentClasses/FhngComponent";
import {I18nService} from "../../service/i18n.service";
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";

@Component({
    selector: '[fhng-html-view]',
    templateUrl: './html-view.component.html',
    styleUrls: ['./html-view.component.sass'],
    providers: [
        {
            provide: FhngComponent,
            useExisting: forwardRef(() => HtmlViewComponent),
        },
    ],
})
export class HtmlViewComponent extends FhngHTMLElementC {
    public override mb2 = false;

    public override width = BootstrapWidthEnum.MD12;

    @Input()
    public text: string;

    constructor(
        public override injector: Injector,
        public i18n: I18nService,
        @Optional() @SkipSelf() parentFhngComponent: FhngComponent
    ) {
        super(injector, parentFhngComponent);
    }

    public override mapAttributes(data: IDataAttributes & {text: string}) {
        super.mapAttributes(data);

        this.text = data.text;
    }
}
