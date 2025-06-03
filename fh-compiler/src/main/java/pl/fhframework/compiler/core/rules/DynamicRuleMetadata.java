package pl.fhframework.compiler.core.rules;

import lombok.Data;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;

@Data
public class DynamicRuleMetadata extends DynamicClassMetadata {
    Rule rule;
}
