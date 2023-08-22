package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.component.include.template.AddressModel;
import pl.fhframework.core.designer.ComponentElement;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by krzysztof.kobylarek on 2016-12-28.
 */

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IncludeElement extends ComponentElement {

    private AddressModel shippingModel = new AddressModel();
    private AddressModel invoiceModel = new AddressModel(){
        @Override
        public List<String> getCountries() {
            List<String> countries = super.getCountries();
            return countries.stream().map((s)->s.toUpperCase()).collect(Collectors.toList());
        }
    };

}
