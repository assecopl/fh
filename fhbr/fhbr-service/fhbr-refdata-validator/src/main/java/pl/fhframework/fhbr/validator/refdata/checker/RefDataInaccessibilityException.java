/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.fhframework.fhbr.validator.refdata.checker;

import pl.fhframework.fhbr.api.exception.ValidationException;

public class RefDataInaccessibilityException extends ValidationException {

    public RefDataInaccessibilityException(String refDataCode) {
        super("Inaccessibility of reference data with code " + refDataCode + ".");
    }
    public RefDataInaccessibilityException(String refDataCode,Throwable cause) {
        super(refDataCode,cause);
    }    

}
