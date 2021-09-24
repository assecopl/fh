package pl.fhframework.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;

import javax.annotation.PostConstruct;

/**
 * Basic class for all Formatters which does auto-self register into Spring container.
 * @param <T> Type of business object to convert.
 */
public abstract class AutoRegisteredFormatter<T> implements Formatter<T> {

	private FhConversionService formatterRegistry;

	public FhConversionService getFormatterRegistry() {
		return formatterRegistry;
	}

	@Autowired
	public void setConversionService(FhConversionService formatterRegistry) {
		this.formatterRegistry = formatterRegistry;
	}

	@PostConstruct
	private void register() {
		formatterRegistry.addFormatter(this);
	}

}