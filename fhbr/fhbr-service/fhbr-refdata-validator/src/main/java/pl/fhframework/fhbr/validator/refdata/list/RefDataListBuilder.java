/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.fhframework.fhbr.validator.refdata.list;

import pl.fhframework.fhbr.api.exception.ValidationException;

public interface RefDataListBuilder {
    public RefDataList createRefDataList(String namespace) throws ValidationException;
}
