import {TestBed} from '@angular/core/testing';

import {FhBasicControlsNgService} from './fh-basic-controls-ng.service';

describe('FhBasicControlsNgService', () => {
    let service: FhBasicControlsNgService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(FhBasicControlsNgService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
