/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.fhbr.validator.refdata.list;

import java.util.List;

public interface RefDataList {

    List<String> getCodeList();
    List<String> getXPathList(String code);
    
}
