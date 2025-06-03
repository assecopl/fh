package pl.fhframework.compiler.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.ExpressionJavaCodeGenerator;

/**
 * Iterator descriptor used in compilation
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompiledRepeatableIterator {

    private String name;

    private String collectionBinding;

    private String iteratorIndexVar;

    private String rowNoOffsetSupplierVar;

    private ExpressionJavaCodeGenerator.AccessorExpression collectionAccessor;

}
