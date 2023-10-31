import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {PanelHeaderFhDPComponent} from './panel-header.component';

describe('PanelHeaderFhDPComponent', () => {
  let component: PanelHeaderFhDPComponent;
  let fixture: ComponentFixture<PanelHeaderFhDPComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PanelHeaderFhDPComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelHeaderFhDPComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
