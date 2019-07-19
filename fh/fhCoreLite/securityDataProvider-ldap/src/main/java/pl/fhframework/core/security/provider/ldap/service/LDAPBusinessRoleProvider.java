package pl.fhframework.core.security.provider.ldap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.provider.ldap.model.BusinessRole;
import pl.fhframework.core.security.provider.service.BusinessRoleProvider;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-06-13)
 */
@Service
public class LDAPBusinessRoleProvider implements BusinessRoleProvider {

    private static final String PROVIDER_TYPE = "LDAP";

    @Value("${fhframework.security.provider.ldap.group-base}")
    private String groupBase;
    @Value("${fhframework.security.provider.ldap.group-object-class}")
    private String groupObjectClass;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public IBusinessRole createSimpleBusinessRoleInstance(String roleName) {
        BusinessRole businessRole = new BusinessRole();
        businessRole.setRoleName(roleName);
        return businessRole;
    }

    @Override
    public IBusinessRole findBusinessRoleByName(String roleName) {
        return createSimpleBusinessRoleInstance(roleName);
    }

    @Override
    public List<IBusinessRole> findAllBusinessRoles() {
        return ldapTemplate.search(
                groupBase,
                String.format("(objectclass=%s)", groupObjectClass),
                new BusinessRoleAttributeMapper());
    }

    @Override
    public boolean supportsRoleManagement() {
        return false;
    }

    // Unsupported operations ===========================================================================
    private static final String MESSAGE = "LDAP Security Data Provider does not support this operation";

    @Override
    public IBusinessRole findBusinessRoleWithoutConversation(Long id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public IBusinessRole saveBusinessRole(IBusinessRole businessRole) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void saveBusinessRoles(List<IBusinessRole> roles) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void deleteBusinessRole(IBusinessRole businessRole) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    // Business role attribute mapper class =============================================================
    private class BusinessRoleAttributeMapper implements AttributesMapper<IBusinessRole> {
        @Override
        public IBusinessRole mapFromAttributes(Attributes attributes) throws NamingException {
            BusinessRole businessRole = new BusinessRole();
            businessRole.setRoleName((String)attributes.get("cn").get());
            businessRole.setDescription((String)attributes.get("description").get());
            return businessRole;
        }
    }

    @Override
    public String getBusinessRoleProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public String getBusinessRoleProviderSource() {
        LdapContextSource contextSource = (LdapContextSource) ldapTemplate.getContextSource();
        String[] urls = contextSource.getUrls();
        String ldapUrl;
        if (urls != null && urls.length > 0) {
            ldapUrl = urls[0];
        } else {
            ldapUrl = "...";
        }
        return String.format("%s/%s", ldapUrl, contextSource.getBaseLdapName());
    }

}
