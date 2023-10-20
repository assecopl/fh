import {TestBed} from '@angular/core/testing';

import {__connectorService} from './__connector.service';

describe('ConnectorService', () => {
    let service: __connectorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
      service = TestBed.inject(__connectorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
