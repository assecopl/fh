import {Component,} from '@angular/core';


@Component({
    selector: 'fh-forms-manager-ng',
    template: `
        <div class="">
        </div>
        <div class="fh-layout-div">

            <div>
                <nav class="navbar navbar-expand-md navbar-dark bg-dark">
                    <div class="container-fluid">
                        <a class="navbar-brand" href="#">POC Angular</a>
                        <button
                                class="navbar-toggler"
                                type="button"
                                data-bs-toggle="collapse"
                                data-bs-target="#navbarSupportedContent"
                                aria-controls="navbarSupportedContent"
                                aria-expanded="false"
                                aria-label="Toggle navigation"
                        >
                            <span class="navbar-toggler-icon"></span>
                        </button>
                        <div class="collapse navbar-collapse " id="navbarTogglerDemo03" fhng-container="navbarForm">
                        </div>
                    </div>
                </nav>
                <div class="container-fluid">
                    <div class="row">
                        <div
                                id="menuForm" fhng-container
                                class="col-sm-12 col-md-3 col-lg-3 col-xl-2 mt-3"
                        >
                        </div>
                        <div id="mainForm" class="col mt-3" fhng-container>

                        </div>
                    </div>
                </div>
            </div>
        </div>
        <fhng-application-lock></fhng-application-lock>
        <fhng-notifications></fhng-notifications>
        <fhng-container id="MODAL_VIRTUAL_CONTAINER"></fhng-container>
    `
})
export class FhFormsManagerNgComponent {

    constructor() {

    }

}
