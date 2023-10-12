import {TestBed} from '@angular/core/testing';

import {FhFormsManagerNgService} from './fh-forms-manager-ng.service';

describe('FhFormsManagerNgService', () => {
  let service: FhFormsManagerNgService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FhFormsManagerNgService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
