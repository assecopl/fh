/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.fhframework.fhbr.validator.refdata.checker;

import java.time.LocalDateTime;


public interface RefDataChecker {
    public boolean isValid(String refDataCode, String value, LocalDateTime date);
}
