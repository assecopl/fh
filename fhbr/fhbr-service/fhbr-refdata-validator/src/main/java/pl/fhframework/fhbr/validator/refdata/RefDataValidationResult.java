/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.fhframework.fhbr.validator.refdata;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.fhbr.api.service.ValidationResult;

@Getter @Setter
public class RefDataValidationResult extends ValidationResult {
	
	Integer resultCode = 1;
	String resultDescription = "";

    public void setResult(Integer code, String description) {
    	resultCode = code;
    	resultDescription = description;
    }

	public void addMessage(String string, String code, String path, String description, String value) {
	}

}
