import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FhBasicControlsNgComponent} from './fh-basic-controls-ng.component';

describe('FhBasicControlsNgComponent', () => {
    let component: FhBasicControlsNgComponent;
    let fixture: ComponentFixture<FhBasicControlsNgComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [FhBasicControlsNgComponent]
        });
        fixture = TestBed.createComponent(FhBasicControlsNgComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
