package pl.fhframework.dp.commons.fh.adm.security;

import pl.fhframework.core.security.ISystemFunctionId;
import pl.fhframework.core.security.ISystemFunctionsMapper;

import java.util.Arrays;

public class FhAdmSystemFunctionsMapper implements ISystemFunctionsMapper {

    @Override
    public ISystemFunctionId getSystemFunction(String systemFunction) {
        return Arrays.stream(FhAdmSystemFunction.values())
                    .filter(function -> function.getName().equalsIgnoreCase(systemFunction))
                    .findAny().orElse(null);
    }

    @Override
    public ISystemFunctionId[] getAllSubsystemFunctions() {
        return FhAdmSystemFunction.values();
    }

}
