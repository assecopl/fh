import {
    AfterViewInit,
    Component,
    Directive,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import {FormsManagerService} from "./forms-manager.service";
import first from "../example/first.json"
import {AbstractDynamicLoadComponent} from "./AbstractDynamicLoadComponent";






@Component({
    selector: 'fh-forms-manager-ng',
    template: `
        <p>fh-forms-manager-ng</p>
        <div class="ad-banner-example">
            <h3>Subelements</h3>
            <ng-container *ngFor="let element of subelements">
                <fh-dynamic-component [data]="element"></fh-dynamic-component>
            </ng-container>

        </div>
  `,
    styles: []
})
export class FhFormsManagerNgComponent extends AbstractDynamicLoadComponent implements OnInit, OnDestroy, AfterViewInit {


    // @ViewChild(AdDirective, {static: true}) adHost!: AdDirective;

    // @ViewChild('dynamicComponentContainer', {static: true,read: ViewContainerRef})
    // public adHost3!:ViewContainerRef;
    constructor(private fm: FormsManagerService) {
        super();
        this.subelements.push(first.openForm[0])
    }

    override ngOnInit(): void {
        super.ngOnInit()


    }

    override ngOnDestroy(): void {
        super.ngOnDestroy()
    }

    ngAfterViewInit(): void {

    }


}

