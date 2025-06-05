<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" container="mainForm" id="documentationHolder" label="{$.fh.docs.cache_application_cache}">
    <AvailabilityConfiguration>
        <ReadOnly>iCreateCache, iInfiBeans, iCreateCacheImpl</ReadOnly>
    </AvailabilityConfiguration>
    <PanelGroup width="md-12" label="JCache" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" value="{$.fh.docs.cache_fh_components_use_jcache_api}" id="_Form_PanelGroup1_OutputLabel"/>

        <Spacer width="md-12" height="20" id="_Form_PanelGroup1_Spacer"/>
        <PanelGroup width="md-12" label="{$.fh.docs.cache_application_properties}" id="_Form_PanelGroup1_PanelGroup">
            <OutputLabel width="md-12" value="{$.fh.docs.cache_parameters_that_can_be_defined_in} application.properties: " id="_Form_PanelGroup1_PanelGroup_OutputLabel1"/>
            <Spacer width="md-12" height="10" id="_Form_PanelGroup1_PanelGroup_Spacer1"/>
            <Table collection="{appProps}" iterator="item" id="_Form_PanelGroup1_PanelGroup_Table1">
                <Column label="{$.fh.docs.cache_property_name}" value="{item.value1}" id="_Form_PanelGroup1_PanelGroup_Table1_Column1"/>
                <Column label="{$.fh.docs.cache_description}" value="{item.value2}" id="_Form_PanelGroup1_PanelGroup_Table1_Column2"/>
                <Column label="{$.fh.docs.cache_default_value}" value="{item.value3}" id="_Form_PanelGroup1_PanelGroup_Table1_Column3"/>
            </Table>
            <OutputLabel width="md-12" value="{$.fh.docs.cache_when_parameter}" id="_Form_PanelGroup1_PanelGroup_OutputLabel2"/>
            <Spacer width="md-12" height="20" id="_Form_PanelGroup1_PanelGroup_Spacer2"/>
            <OutputLabel width="md-12" value="{$.fh.docs.cache_application_has_to_provide}" id="_Form_PanelGroup1_PanelGroup_OutputLabel3"/>
            <InputText id="iCreateCache" rowsCount="3" width="md-12">
                <![CDATA[![ESCAPE[public interface CreateCacheProvider {
    <C extends Configuration> void createCache(String cacheName, C configuration, CacheManager cacheManager);
}
]]]]>
            </InputText>
            <Spacer width="md-12" height="20" id="_Form_PanelGroup1_PanelGroup_Spacer3"/>
            <OutputLabel width="md-12" value="{$.fh.docs.cache_fh_defines_following_cache_names}:" id="_Form_PanelGroup1_PanelGroup_OutputLabel4"/>
            <Table collection="{cacheProps}" iterator="item" id="_Form_PanelGroup1_PanelGroup_Table2">
                <Column label="{$.fh.docs.cache_cache_name}" value="{item.value1}" id="_Form_PanelGroup1_PanelGroup_Table2_Column1"/>
                <Column label="{$.fh.docs.cache_description}" value="{item.value2}" id="_Form_PanelGroup1_PanelGroup_Table2_Column2"/>
                <Column label="{$.fh.docs.cache_cache_properties}" value="{item.value3}" id="_Form_PanelGroup1_PanelGroup_Table2_Column3"/>
            </Table>
        </PanelGroup>
    </PanelGroup>
    <PanelGroup width="md-12" label="Infinispan" id="_Form_PanelGroup2">
        <Spacer width="md-12" height="10" id="_Form_PanelGroup2_Spacer1"/>
        <OutputLabel width="md-12" value="{$.fh.docs.cache_infinispan_as_a_cache_provider}:" id="_Form_PanelGroup2_OutputLabel1"/>
        <InputText id="iInfiBeans" rowsCount="26" width="md-12">
            <![CDATA[![ESCAPE[     @Bean
    public CacheManager jCacheCacheManager(EmbeddedCacheManager embeddedCacheManager) {
        return new JCacheManager(Caching.getCachingProvider().getDefaultURI(),
                embeddedCacheManager, Caching.getCachingProvider());
    }

    @Bean
    public EmbeddedCacheManager infiniSpanCacheManager(
            @Qualifier("asyncReplicationConfig") org.infinispan.configuration.cache.Configuration asyncReplicationConfig) {
        TransportConfigurationBuilder transportConfigurationBuilder = new GlobalConfigurationBuilder().transport().clusterName(fhConfiguration.getClusterName()).defaultTransport();
        if (getClass().getClassLoader().getResource("jgroup.xml") != null) {
            transportConfigurationBuilder.addProperty("configurationFile", "jgroups.xml");
        }
        return new DefaultCacheManager(
                transportConfigurationBuilder.build(),
                asyncReplicationConfig);
    }

    @Bean(name = "asyncReplicationConfig")
    public org.infinispan.configuration.cache.Configuration asyncReplicationConfig() {
        return new ConfigurationBuilder()
                .clustering()
                .cacheMode(CacheMode.REPL_ASYNC)
                .eviction().type(EvictionType.COUNT).size(10000)
                .build();
    }]]]]>
        </InputText>
        <Spacer width="md-12" height="10" id="_Form_PanelGroup2_Spacer2"/>
        <OutputLabel width="md-12" value="{$.fh.docs.cache_implementation_of_createcacheprovider_interface}:" id="_Form_PanelGroup2_OutputLabel2"/>
        <InputText id="iCreateCacheImpl" rowsCount="15" width="md-12">
            <![CDATA[![ESCAPE[@Component
public class InfinispanCreateCacheProvider implements CreateCacheProvider {
    @Autowired
    EmbeddedCacheManager cm;

    @Autowired
    @Qualifier("asyncReplicationConfig")
    org.infinispan.configuration.cache.Configuration infiniSpanConfiguration;

    @Override
    public <C extends Configuration> void createCache(String cacheName, C configuration, CacheManager cacheManager) {
        cm.defineConfiguration(cacheName, infiniSpanConfiguration);
        cacheManager.createCache(cacheName, configuration);
    }
}
]]]]>
        </InputText>
        <Spacer width="md-12" height="10" id="_Form_PanelGroup2_Spacer3"/>
        <OutputLabel width="md-12" value="{$.fh.docs.cache_infinispan_uses_jgroups_for_creating}" id="_Form_PanelGroup2_OutputLabel3"/>
        <Table collection="{jgroupsProps}" iterator="item" id="_Form_PanelGroup2_Table">
            <Column label="{$.fh.docs.cache_system_property}" value="{item.value1}" id="_Form_PanelGroup2_Table_Column1"/>
            <Column label="{$.fh.docs.cache_description}" value="{item.value2}" id="_Form_PanelGroup2_Table_Column2"/>
            <Column label="{$.fh.docs.cache_default_value}" value="{item.value3}" id="_Form_PanelGroup2_Table_Column3"/>
        </Table>
    </PanelGroup>
</Form>