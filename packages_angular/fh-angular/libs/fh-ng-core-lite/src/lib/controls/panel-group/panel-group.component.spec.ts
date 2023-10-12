import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {PanelGroupComponent} from './panel-group.component';

describe('PanelGroupComponent', () => {
  let component: PanelGroupComponent;
  let fixture: ComponentFixture<PanelGroupComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PanelGroupComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
