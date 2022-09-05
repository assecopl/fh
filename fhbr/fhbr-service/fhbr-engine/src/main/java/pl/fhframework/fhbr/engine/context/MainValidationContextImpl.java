/*
 * Copyright 2019 Asseco Poland S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file,
 * or at: http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package pl.fhframework.fhbr.engine.context;

import lombok.Getter;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.engine.ValidatorServiceImpl;
import pl.fhframework.fhbr.engine.audit.AuditPoint;
import pl.fhframework.fhbr.engine.audit.AuditRulSetStart;

import java.time.Clock;
import java.time.LocalDate;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 31/08/2022
 */
public class MainValidationContextImpl {

    @Getter
    private final String initialRuleSetCode;
    @Getter
    private final LocalDate initialOnDate;
    @Getter
    private final ValidationMessageFactory messageFactory;
    @Getter
    private final ValidatorServiceImpl validatorService;
    @Getter
    private final Clock clock;
    @Getter
    private final AuditPoint auditPoint;


    public MainValidationContextImpl(ValidationMessageFactory messageFactory, ValidatorServiceImpl validatorService, String initialRuleSetCode, LocalDate onDate, Clock clock) {
        this.messageFactory = messageFactory;
        this.validatorService = validatorService;
        this.initialRuleSetCode = initialRuleSetCode;
        this.initialOnDate = onDate != null ? onDate : LocalDate.now();
        this.clock = clock;
        this.auditPoint = new AuditRulSetStart(initialRuleSetCode);
    }

}
