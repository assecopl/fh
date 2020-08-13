package pl.fhframework.core.reports;

import pl.fhframework.core.security.ISystemFunctionId;
import pl.fhframework.core.security.ISystemFunctionsMapper;

/**
 * @author tomasz.kozlowski (created on 2017-12-20)
 */
public class SystemFunctionsMapper implements ISystemFunctionsMapper {

    @Override
    public ISystemFunctionId getSystemFunction(String systemFunction) {
        for (ISystemFunctionId wer : ReportsSystemFunction.values()){
            if (wer.getName().equalsIgnoreCase(systemFunction)){
                return wer;
            }
        }
        return null;
    }

    @Override
    public ISystemFunctionId[] getAllSubsystemFunctions() {
        return ReportsSystemFunction.values();
    }

}
