import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {TreeElementComponent} from './tree-element.component';

describe('TreeElementComponent', () => {
  let component: TreeElementComponent;
  let fixture: ComponentFixture<TreeElementComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TreeElementComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TreeElementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
