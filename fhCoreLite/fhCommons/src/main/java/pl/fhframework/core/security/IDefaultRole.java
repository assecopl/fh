package pl.fhframework.core.security;

import java.util.Set;

/**
 * @author tomasz.kozlowski (created on 29.06.2018)
 */
public interface IDefaultRole {

    String getName();
    Set<IFunction> getFunctions();
    boolean isAllFunctions();

}
