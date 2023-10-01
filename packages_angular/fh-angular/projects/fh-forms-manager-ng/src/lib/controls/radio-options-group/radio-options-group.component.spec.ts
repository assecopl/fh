import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {RadioOptionsGroupComponent} from './radio-options-group.component';

describe('RadioOptionsGroupComponent', () => {
    let component: RadioOptionsGroupComponent;
    let fixture: ComponentFixture<RadioOptionsGroupComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [RadioOptionsGroupComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(RadioOptionsGroupComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
