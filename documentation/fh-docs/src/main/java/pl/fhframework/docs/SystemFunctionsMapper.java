package pl.fhframework.docs;

import pl.fhframework.core.security.ISystemFunctionId;
import pl.fhframework.core.security.ISystemFunctionsMapper;

public class SystemFunctionsMapper implements ISystemFunctionsMapper {

    @Override
    public ISystemFunctionId getSystemFunction(String systemFunction) {
        for (ISystemFunctionId wer : DocsSystemFunction.values()){
            if (wer.getName().equalsIgnoreCase(systemFunction)){
                return wer;
            }
        }
        return null;
    }

    @Override
    public ISystemFunctionId[] getAllSubsystemFunctions() {
        return DocsSystemFunction.values();
    }
}
