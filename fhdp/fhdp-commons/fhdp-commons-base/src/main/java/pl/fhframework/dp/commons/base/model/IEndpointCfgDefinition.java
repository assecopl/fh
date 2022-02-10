package pl.fhframework.dp.commons.base.model;


/**
 * Endpoint configuration key
 *
 * @author <a href="mailto:dariusz_skrudlik@javiko.pl">Dariusz Skrudlik</a>
 * @version $Revision: 1778 $, $Date: 2019-03-07 20:12:30 +0100 (Cz, 07 mar 2019) $
 * @created 15/01/2019
 */
public interface IEndpointCfgDefinition {

    /**
     * Target system name (code)
     */
    String getSystemName();

    /**
     * Service name
     */
    String getServiceName();

}
