import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {OutputLabelComponent} from './output-label.component';

describe('OutputLabelComponent', () => {
    let component: OutputLabelComponent;
    let fixture: ComponentFixture<OutputLabelComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [OutputLabelComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(OutputLabelComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
