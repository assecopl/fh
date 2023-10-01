import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {SpacerComponent} from './spacer.component';

describe('SpacerComponent', () => {
    let component: SpacerComponent;
    let fixture: ComponentFixture<SpacerComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [SpacerComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(SpacerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
