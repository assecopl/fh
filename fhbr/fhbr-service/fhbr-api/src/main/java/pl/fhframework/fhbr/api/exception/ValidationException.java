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

package pl.fhframework.fhbr.api.exception;

import lombok.Getter;
import org.slf4j.LoggerFactory;
import pl.fhframework.fhbr.api.service.ValidatorService;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
@Getter
public class ValidationException extends RuntimeException {

    private final String messageKey;

    private final Object[] argArray;

    private boolean logged = false;

    @Override
    public String getLocalizedMessage() {
        return messageKey;
    }

    public ValidationException(String message, String... argArray) {
        super(message);
        this.messageKey = "fhbr.exception.generalException";
        this.argArray = argArray;
    }

    public ValidationException(String message, Throwable t, String... argArray) {
        super(message, t);
        this.messageKey = "fhbr.exception.generalException";
        this.argArray = argArray;
        if (t != null && t instanceof ValidationException) {
            logged = ((ValidationException) t).isLogged();
        }
    }

    public ValidationException(String messageKey, String message, Throwable t, String... argArray) {
        super(message, t);
        this.messageKey = messageKey;
        this.argArray = argArray;
        if (t != null && t instanceof ValidationException) {
            logged = ((ValidationException) t).isLogged();
        }
    }


    private boolean isLogged() {
        return logged || (this.getCause() != null && this.getCause() != this && this.getCause() instanceof ValidationException && ((ValidationException) this.getCause()).isLogged());
    }

    public void logError() {
        if (!isLogged()) {
            logged = true;
            LoggerFactory.getLogger(ValidatorService.class).error("{} [args: {}]",
                    this.getClass().getSimpleName(), argArray, this);
        }
    }

}
