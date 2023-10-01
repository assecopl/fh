import {TestBed} from '@angular/core/testing';

import {FhMLService} from './fh-ml.service';

describe('FhMLService', () => {
    let service: FhMLService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(FhMLService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
