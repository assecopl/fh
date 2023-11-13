import {Component, forwardRef, Injector, Input, Optional, SkipSelf} from "@angular/core";
import {FhngHTMLElementC} from "../../models/componentClasses/FhngHTMLElementC";
import {FhngComponent} from "../../models/componentClasses/FhngComponent";
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";
import {SafeStyle} from "@angular/platform-browser";

@Component({
    selector: '[fhng-xml-view]',
    templateUrl: './xml-viewer.component.html',
    styleUrls: ['./xml-viewer.component.sass'],
    providers: [
        {provide: FhngComponent, useExisting: forwardRef(() => XMLViewerFhDPComponent)},
    ]
})
export class XMLViewerFhDPComponent extends FhngHTMLElementC {
    @Input()
    public content: string;

    public override width = BootstrapWidthEnum.MD12;

    public override mb2 = false;

    public override styles: SafeStyle & any = {
            background: 'var(--color-bg-xml-view)',
            padding: '10px',
            lineHeight: '1.3 rem',
            fontFamily: '\'Verdana\', monospace'
        };

    constructor(
        public override injector: Injector,
        @Optional() @SkipSelf() parentFhngComponent: FhngComponent
    ) {
        super(injector, parentFhngComponent);
    }

    override mapAttributes(data: IDataAttributes & {content: string}) {
        super.mapAttributes(data);

        this.content = data.content;
    }
}
