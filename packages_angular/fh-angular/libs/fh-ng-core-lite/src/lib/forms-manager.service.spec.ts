import {TestBed} from '@angular/core/testing';

import {FormsManagerService} from './forms-manager.service';

describe('FormsManagerService', () => {
  let service: FormsManagerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormsManagerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
