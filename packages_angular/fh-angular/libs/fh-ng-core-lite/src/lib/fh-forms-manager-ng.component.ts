import {Component,} from '@angular/core';


@Component({
    selector: 'fh-forms-manager-ng',
    template: `
        <div class="">
            <!--            <ng-container *ngFor="let element of subelements">-->
            <!--                <fh-dynamic-component [data]="element" [formId]="formId" [formId]="formId"></fh-dynamic-component>-->
            <!--            </ng-container>-->
        </div>
        <div class="fh-layout-div">
            <!--            <nav class="navbar navbar-expand-lg navbar-light bg-light">-->
            <!--                <div class="container-fluid">-->
            <!--                    <a class="navbar-brand" href="#">Navbar</a>-->
            <!--                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">-->
            <!--                        <span class="navbar-toggler-icon"></span>-->
            <!--                    </button>-->
            <!--                    <div class="collapse navbar-collapse" id="navbarSupportedContent">-->
            <!--                        <ul class="navbar-nav me-auto mb-2 mb-lg-0">-->
            <!--                            <li class="nav-item">-->
            <!--                                <a class="nav-link active" aria-current="page" href="#">Home</a>-->
            <!--                            </li>-->
            <!--                            <li class="nav-item">-->
            <!--                                <a class="nav-link" href="#">Link</a>-->
            <!--                            </li>-->
            <!--                            <li class="nav-item dropdown">-->
            <!--                                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">-->
            <!--                                    Dropdown-->
            <!--                                </a>-->
            <!--                                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">-->
            <!--                                    <li><a class="dropdown-item" href="#">Action</a></li>-->
            <!--                                    <li><a class="dropdown-item" href="#">Another action</a></li>-->
            <!--                                    <li><hr class="dropdown-divider"></li>-->
            <!--                                    <li><a class="dropdown-item" href="#">Something else here</a></li>-->
            <!--                                </ul>-->
            <!--                            </li>-->
            <!--                            <li class="nav-item">-->
            <!--                                <a class="nav-link disabled" href="#" tabindex="-1" aria-disabled="true">Disabled</a>-->
            <!--                            </li>-->
            <!--                        </ul>-->
            <!--                        <form class="d-flex">-->
            <!--                            <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search">-->
            <!--                            <button class="btn btn-outline-success" type="submit">Search</button>-->
            <!--                        </form>-->
            <!--                    </div>-->
            <!--                </div>-->
            <!--            </nav>-->
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
    `
})
export class FhFormsManagerNgComponent {

    constructor() {

    }

}
