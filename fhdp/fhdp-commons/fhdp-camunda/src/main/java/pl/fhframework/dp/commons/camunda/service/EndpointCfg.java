package pl.fhframework.dp.commons.camunda.service;

import pl.fhframework.dp.commons.base.model.AuthorizationTypeEnum;
import pl.fhframework.dp.commons.base.model.IEndpointCfg;
import pl.fhframework.dp.transport.endpoints.EndpointCfgDto;

import java.time.LocalDateTime;

/**
 * Once a new web service is being created, its definition should be added to this enum
 */
public enum EndpointCfg implements IEndpointCfg {

  ZISAR_IAPIService("ZISAR", "IAPIService", "http://172.25.107.170:9080/AXSZisarAsyncMock/RiskAnalysisAsyncWebServiceXXX", "AIS", null, AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  ISZTAR_IsztarService("ISZTAR", "IsztarService", "http://172.26.20.14:8180/AISIsztarMock/IsztarServiceWS", "AIS", null,AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  ISZTAR_IsztarExportImportService("ISZTAR", "IsztarExportImportService", "http://172.26.20.14:8180/AISIsztarMock/IsztarExportImportServiceWS", "AIS", null,AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  TQS_IAISAES("TQS", "IAISAES", "http://172.25.107.151:8080/TQS/AIS_AESInterface.svc", "AIS", null, AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  SEAP_SiscSOAP("SEAP", "SiscSOAP", "http://172.25.107.170:9080/ecip-seap-mock/ws", "AIS", null, AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  Mis_RepositoryWS("MIS", "RepositoryWS", "http://10.133.12.202:5555/ws/MisRepository:RepositoryWS_v1r0/RepositoryWS", "MDAS", "mdas_123", AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  PKI_PKISign("PKI", "PKISign", "http://172.25.25.132:8880/pki-mock/SignServiceMock", "user", "user", AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  PKI_PKIWer("PKI", "PKIWer", "http://172.25.25.132:8880/pki-mock/WerServiceMock", "user", "user", AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true),
  PDR_OnDemand("PDR", "OnDemand", "https://pdr.skg.pl/pdr-web/services/OnDemandPort", "skg", "sasasa", AuthorizationTypeEnum.Digest.name(), "skg", null, null, true, null, 5000, 25000, true),
  PDR_LoadData("PDR", "LoadData", "https://pdr.skg.pl/pdr-web/services/LoadDataPort", "szprot", "Zaq1@wsx", AuthorizationTypeEnum.Digest.name(), "szprot", null, null, true, null, 5000, 25000, true),
  PDR_LoadDataSynch("PDR", "LoadDataSynch", "https://pdr.skg.pl/pdr-web/services/LoadDataSynchPort", "szprot", "Zaq1@wsx", AuthorizationTypeEnum.Digest.name(), "szprot", null, null, true, null, 5000, 25000, true),
  PDR_DeactivationIDSISCv2Service("PDR", "DeactivationIDSISCv2Service", "https://pdr.skg.pl/pdr-web/services/DeactivationIDSISCv2Service", "skg", "sasasa", AuthorizationTypeEnum.Digest.name(), "skg", null, null, true, null, 5000, 25000, true),
  AES_Validator("AES", "Validator", "http://172.25.107.170:9380/validatorWS/validator", null, null, AuthorizationTypeEnum.Basic.name(), null, null, null, true, null, 5000, 25000, true);


  EndpointCfg(String systemName, String serviceName, String address, String user, String password, String authType, String wsaFrom, String wsaTo, String wsaReply, Boolean withNonce, String serverThumbprint, Integer connectTimeout, Integer requestTimeout, Boolean logOn) {
    this.serviceCfgDefinition = new EndpointCfgDto(systemName, serviceName, address, user, password, authType, wsaFrom, wsaTo, wsaReply, withNonce, serverThumbprint, connectTimeout, requestTimeout, logOn);
  }

  private final IEndpointCfg serviceCfgDefinition;

  @Override
  public String getSystemName() {
    return serviceCfgDefinition.getSystemName();
  }

  @Override
  public String getServiceName() {
    return serviceCfgDefinition.getServiceName();
  }

  @Override
  public String getAddress() {
    return serviceCfgDefinition.getAddress();
  }

  @Override
  public String getPassword() {
    return serviceCfgDefinition.getPassword();
  }

  @Override
  public String getUser() {
    return serviceCfgDefinition.getUser();
  }

  @Override
  public String getAuthType() {
    return serviceCfgDefinition.getAuthType();
  }

  @Override
  public String getWsaFrom() {
    return serviceCfgDefinition.getWsaFrom();
  }

  @Override
  public String getWsaTo() {
    return serviceCfgDefinition.getWsaTo();
  }

  @Override
  public String getWsaReply() {
    return serviceCfgDefinition.getWsaReply();
  }

  @Override
  public Boolean getWithNonce() {
    return serviceCfgDefinition.getWithNonce();
  }

  @Override
  public String getServerThumbprint() {
    return serviceCfgDefinition.getServerThumbprint();
  }

  @Override
  public Integer getConnectTimeout() {
    return serviceCfgDefinition.getConnectTimeout();
  }

  @Override
  public Integer getRequestTimeout() {
    return serviceCfgDefinition.getRequestTimeout();
  }

  @Override
  public Boolean getLogOn() {
    return serviceCfgDefinition.getLogOn();
  }

  @Override
  public LocalDateTime getLastCheck() {
    return serviceCfgDefinition.getLastCheck();
  }

  @Override
  public Boolean getCheckOk() {
    return serviceCfgDefinition.getCheckOk();
  }

  @Override
  public String getCheckDescription() {
    return serviceCfgDefinition.getCheckDescription();
  }

  @Override
  public Boolean getAutoCheck() {
    return serviceCfgDefinition.getAutoCheck();
  }

  @Override
  public Integer getAutoCheckFrequency() {
    return serviceCfgDefinition.getAutoCheckFrequency();
  }

  @Override
  public String getCheckExpression() {
    return serviceCfgDefinition.getCheckExpression();
  }

  @Override
  public String getCheckProcessId() {
    return serviceCfgDefinition.getCheckProcessId();
  }

  @Override
  public String getProxyHost() {
    return serviceCfgDefinition.getProxyHost();
  }

  @Override
  public Integer getProxyPort() {
    return serviceCfgDefinition.getProxyPort();
  }

  @Override
  public String getProxyUser() {
    return serviceCfgDefinition.getProxyUser();
  }

  @Override
  public String getProxyPassword() {
    return serviceCfgDefinition.getProxyPassword();
  }
}
