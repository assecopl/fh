package pl.fhframework.dp.commons.ad.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 05/01/2021
 */
@Service
@Slf4j
public class LdapService {

    @Value("${fhdp.security.provider.ad.groupsBase:}")
    private String groupsBase;

    @Value("${fhdp.security.provider.ad.orgUnitsBase:}")
    private String orgUnitsBase;

    @Autowired
    LdapTemplate ldapTemplate;

    public boolean isGroup(String groupName) {
        LdapQuery query = getQuery(groupsBase, groupName);
        List<String> groups = performSearch(query);
        if(groups.isEmpty()) {
            query = getQuery("CN=Builtin", groupName);
            groups = performSearch(query);
        }
        return !groups.isEmpty();
    }

    public boolean isOrgUnit(String name) {
        LdapQuery query = getQuery(orgUnitsBase, name);
        List<String> groups = performSearch(query);
        return !groups.isEmpty();
    }

    private List<String> performSearch(LdapQuery query) {
        return ldapTemplate.search(query, (AttributesMapper<String>) attrs -> (String) attrs.get("CN").get());
    }

    private LdapQuery getQuery(String groupsBase, String groupName) {
        return query()
                .base(groupsBase)
                .attributes("CN")
                .where("objectclass").is("group")
                .and("CN").is(groupName);
    }
}
