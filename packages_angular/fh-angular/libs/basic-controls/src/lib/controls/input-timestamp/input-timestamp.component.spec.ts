import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {InputTimestampComponent} from './input-timestamp.component';

describe('InputTimestampComponent', () => {
  let component: InputTimestampComponent;
  let fixture: ComponentFixture<InputTimestampComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InputTimestampComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InputTimestampComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
