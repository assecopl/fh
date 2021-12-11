package pl.fhframework.compiler.core.uc.ts.generator;

import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.Dependency;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.MetaModelService;

public class AdHocTsClassCodeGenerator extends AbstractNgClassCodeGenerator {
    public AdHocTsClassCodeGenerator(ModuleMetaModel moduleMetaModel, Dependency classDependency, MetaModelService metaModelService) {
        super(moduleMetaModel, classDependency, null, metaModelService);
    }

    @Override
    protected void generateClassBody() {

    }
}
