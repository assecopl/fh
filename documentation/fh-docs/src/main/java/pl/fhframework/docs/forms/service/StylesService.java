package pl.fhframework.docs.forms.service;

import org.springframework.stereotype.Service;
import pl.fhframework.model.forms.Styleable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by k.czajkowski on 16.01.2017.
 */
@Service
public class StylesService {

   public List<Styleable.Style>  getStyles() {
        List<Styleable.Style> styles = Stream.of(Styleable.Style.values()).collect(Collectors.toList());
        return styles;
    }
}
