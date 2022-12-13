package pl.fhframework.fhbr.validator.refdata;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageSeverity;
import pl.fhframework.fhbr.api.service.impl.ValidationMessageImpl;

@Getter @Setter
public class RefDataValidationMessage extends ValidationMessageImpl {
	
	protected String originalValue;
	
	
	public RefDataValidationMessage() {
		super();
	}

	public RefDataValidationMessage(ValidationMessageSeverity severity, String message, String pointer, String ruleCode, String originalValue) {
		super(severity, message, pointer);
		setRuleCode(ruleCode);
		setOriginalValue(originalValue);
	}

}
