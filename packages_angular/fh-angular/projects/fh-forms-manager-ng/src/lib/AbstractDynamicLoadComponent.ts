import {Component, Directive, Input, OnDestroy, OnInit, ViewChild, ViewContainerRef} from "@angular/core";
import {AdDirective} from "projects/fh-forms-manager-ng/src/lib/directive/ad.directive";
import {FormComponent} from "./controls/form/form.component";

@Directive()
export class AbstractDynamicLoadComponent implements OnInit, OnDestroy {

    @Input() subelements: any[] = [];

    currentAdIndex = -1;

    // @ViewChild("adHost",{static: true, read: ViewContainerRef})
    // public adHost!: ViewContainerRef;

    constructor() {
    }

    public loadComponent() {
        // this.currentAdIndex = (this.currentAdIndex + 1) % this.subelements.length;
        // const element = this.subelements[this.currentAdIndex];
        //
        // const viewContainerRef = this.adHost;
        // viewContainerRef.clear();
        //
        // const componentRef = viewContainerRef.createComponent<FormComponent>(FormComponent);
        // componentRef.instance.innerId = element.id;
        // componentRef.instance.label = element.label;
        // componentRef.instance.subelements = element.subelements;

    }

    ngOnDestroy(): void {
    }

    ngOnInit(): void {
        this.loadComponent();
    }
}
