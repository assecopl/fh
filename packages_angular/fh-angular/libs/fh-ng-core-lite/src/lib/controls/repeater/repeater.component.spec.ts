import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {RepeaterComponent} from './group.component';

describe('GroupComponent', () => {
  let component: RepeaterComponent;
  let fixture: ComponentFixture<RepeaterComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepeaterComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepeaterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
