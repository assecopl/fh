import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FhFormsManagerNgComponent} from './fh-forms-manager-ng.component';

describe('FhFormsManagerNgComponent', () => {
    let component: FhFormsManagerNgComponent;
    let fixture: ComponentFixture<FhFormsManagerNgComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [FhFormsManagerNgComponent]
        });
        fixture = TestBed.createComponent(FhFormsManagerNgComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
