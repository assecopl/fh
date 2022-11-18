/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.fhframework.fhbr.validator.refdata.checker;


public class RefDataInaccessibilityRuntimeException extends RuntimeException {

    public RefDataInaccessibilityRuntimeException(String refDataCode) {
        super("Inaccessibility of reference data with code " + refDataCode + ".");
    }
    public RefDataInaccessibilityRuntimeException(String refDataCode,Throwable cause) {
        super(refDataCode,cause);
    }    

}
