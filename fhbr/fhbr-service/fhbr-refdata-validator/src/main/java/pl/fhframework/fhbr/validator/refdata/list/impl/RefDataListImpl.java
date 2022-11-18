/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.fhframework.fhbr.validator.refdata.list.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.fhframework.fhbr.validator.refdata.list.RefDataList;


public class RefDataListImpl implements RefDataList {

    private Map<String,List<String>> codes = new HashMap<String,List<String>>();
    
    public List<String> getCodeList() {
        return new ArrayList<String>(codes.keySet());
    }

    public List<String> getXPathList(String code) {
        return new ArrayList<String>(codes.get(code));
    }

    public void add(String code, String xpath) {
        if (!codes.keySet().contains(code)) {
            codes.put(code, new ArrayList<String>());
        } 
        codes.get(code).add(xpath);        
    }
}
