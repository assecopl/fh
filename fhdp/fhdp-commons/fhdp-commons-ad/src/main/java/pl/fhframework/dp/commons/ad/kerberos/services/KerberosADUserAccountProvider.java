package pl.fhframework.dp.commons.ad.kerberos.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.util.StringUtils;
import pl.fhframework.dp.commons.ad.model.BusinessRole;
import pl.fhframework.dp.commons.ad.model.RoleInstance;
import pl.fhframework.dp.commons.ad.model.UserAccount;
import pl.fhframework.core.security.model.IUserAccount;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Service
@RequiredArgsConstructor
@Slf4j
public class KerberosADUserAccountProvider {
    private static final String PROVIDER_TYPE = "KERBEROS-AD";

    private static final Pattern SUB_ERROR_CODE = Pattern
            .compile(".*data\\s([0-9a-f]{3,4}).*");

    // Error codes
    private static final int USERNAME_NOT_FOUND = 0x525;
    private static final int INVALID_PASSWORD = 0x52e;
    private static final int NOT_PERMITTED = 0x530;
    private static final int PASSWORD_EXPIRED = 0x532;
    private static final int ACCOUNT_DISABLED = 0x533;
    private static final int ACCOUNT_EXPIRED = 0x701;
    private static final int PASSWORD_NEEDS_RESET = 0x773;
    private static final int ACCOUNT_LOCKED = 0x775;
    private static final String FIRST_NAME = "givenname";
    private static final String LAST_NAME = "sn";
    private static final String LOGIN = "userprincipalname";

    @Value("${fhdp.security.provider.ad.admin:jacekb}")
    private String adminName;
    @Value("${fhdp.security.provider.ad.admin.password:0104@asdF}")
    private String adminPass;
    @Value("${fhdp.security.provider.ad.server:}")
    private String server;
    @Value("${fhdp.security.provider.ad.port:}")
    private String port;
    @Value("${fhdp.security.provider.ad.domain:}")
    private String domain;
    @Value("${fhdp.security.provider.ad.rootDn:}")
    private String rootDn;
    @Value("${fhdp.security.provider.ad.searchFilter:(&(objectClass=user)(userPrincipalName={0}))}")
    private String searchFilter;


    ContextFactory contextFactory = new ContextFactory();
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    protected UserDetailsContextMapper userDetailsContextMapper = new LdapUserDetailsMapper();



    static class ContextFactory {
        DirContext createContext(Hashtable<?, ?> env) throws NamingException {
            return new InitialLdapContext(env, null);
        }
    }

    public IUserAccount findUserAccountByLogin(String login) {
        DirContext ctx = bindAsUser(adminName, adminPass);
        try {
            DirContextOperations userData = searchForUser(ctx, login);
            UserDetails user = this.userDetailsContextMapper.mapUserFromContext(userData,
                    login,
                    loadUserAuthorities(userData));
            UserAccount ret = createUserAccount(user, userData);
            return ret;
        }
        catch (NamingException e) {
            log.error("Failed to locate directory entry for authenticated user: "
                    + login, e);
            throw badCredentials(e);
        }
        finally {
            LdapUtils.closeContext(ctx);
        }
    }

    private UserAccount createUserAccount(UserDetails user, DirContextOperations userData) {
        UserAccount ret = new UserAccount();
        ret.setLogin(userData.getStringAttribute(LOGIN));
        ret.setFirstName(userData.getStringAttribute(FIRST_NAME));
        ret.setLastName(userData.getStringAttribute(LAST_NAME));
        ret.setPassword("akuku");
        ret.setRoles(new ArrayList<>());
        for(GrantedAuthority a:user.getAuthorities()) {
            RoleInstance role = new RoleInstance();
            role.setBusinessRole(new BusinessRole(a.getAuthority()));
            ret.getRoles().add(role);
        }
        return ret;
    }

    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData) {
        String[] groups = userData.getStringAttributes("memberOf");
        if (groups == null) {
            log.debug("No values for 'memberOf' attribute.");
            return AuthorityUtils.NO_AUTHORITIES;
        }
        if (log.isDebugEnabled()) {
            log.debug("'memberOf' attribute values: " + Arrays.asList(groups));
        }
        ArrayList<GrantedAuthority> authorities = new ArrayList<>(
                groups.length);
        for (String group : groups) {
            authorities.add(new SimpleGrantedAuthority(new DistinguishedName(group)
                    .removeLast().getValue()));
        }
        return authorities;
    }

    private DirContextOperations searchForUser(DirContext context, String username)
            throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String bindPrincipal = createBindPrincipal(username);
        String searchRoot = rootDn != null ? rootDn
                : searchRootFromPrincipal(bindPrincipal);

        try {
            return SpringSecurityLdapTemplate.searchForSingleEntryInternal(context,
                    searchControls, searchRoot, searchFilter,
                    new Object[] { bindPrincipal, username });
        }
        catch (IncorrectResultSizeDataAccessException incorrectResults) {
            // Search should never return multiple results if properly configured - just
            // rethrow
            if (incorrectResults.getActualSize() != 0) {
                throw incorrectResults;
            }
            // If we found no results, then the username/password did not match
            UsernameNotFoundException userNameNotFoundException = new UsernameNotFoundException(
                    "User " + username + " not found in directory.", incorrectResults);
            throw badCredentials(userNameNotFoundException);
        }
    }

    private String searchRootFromPrincipal(String bindPrincipal) {
        int atChar = bindPrincipal.lastIndexOf('@');
        if (atChar < 0) {
            log.debug("User principal '" + bindPrincipal
                    + "' does not contain the domain, and no domain has been configured");
            throw badCredentials();
        }
        return rootDnFromDomain(bindPrincipal.substring(atChar + 1,
                bindPrincipal.length()));
    }

    private String rootDnFromDomain(String domain) {
        String[] tokens = StringUtils.tokenizeToStringArray(domain, ".");
        StringBuilder root = new StringBuilder();
        for (String token : tokens) {
            if (root.length() > 0) {
                root.append(',');
            }
            root.append("dc=").append(token);
        }
        return root.toString();
    }


    private DirContext bindAsUser(String username, String password) {
        final String bindUrl = "ldap://" + server + ":" + port;

        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        String bindPrincipal = createBindPrincipal(username);
        env.put(Context.SECURITY_PRINCIPAL, bindPrincipal);
        env.put(Context.PROVIDER_URL, bindUrl);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.OBJECT_FACTORIES, DefaultDirObjectFactory.class.getName());
//        env.putAll(this.contextEnvironmentProperties);

        try {
            return contextFactory.createContext(env);
        }
        catch (NamingException e) {
            if ((e instanceof AuthenticationException)
                    || (e instanceof OperationNotSupportedException)) {
                handleBindException(bindPrincipal, e);
                throw badCredentials(e);
            }
            else {
                throw LdapUtils.convertLdapException(e);
            }
        }
    }

    private void handleBindException(String bindPrincipal, NamingException exception) {
        if (log.isDebugEnabled()) {
            log.debug("Authentication for " + bindPrincipal + " failed:" + exception);
        }

        handleResolveObj(exception);

        int subErrorCode = parseSubErrorCode(exception.getMessage());

        if (subErrorCode <= 0) {
            log.debug("Failed to locate AD-specific sub-error code in message");
            return;
        }

        log.info("Active Directory authentication failed: "
                + subCodeToLogMessage(subErrorCode));

//        if (convertSubErrorCodesToExceptions) {
            raiseExceptionForErrorCode(subErrorCode, exception);
//        }
    }

    private void handleResolveObj(NamingException exception) {
        Object resolvedObj = exception.getResolvedObj();
        boolean serializable = resolvedObj instanceof Serializable;
        if (resolvedObj != null && !serializable) {
            exception.setResolvedObj(null);
        }
    }

    private int parseSubErrorCode(String message) {
        Matcher m = SUB_ERROR_CODE.matcher(message);

        if (m.matches()) {
            return Integer.parseInt(m.group(1), 16);
        }

        return -1;
    }

    private void raiseExceptionForErrorCode(int code, NamingException exception) {
        String hexString = Integer.toHexString(code);
        Throwable cause = new ActiveDirectoryAuthenticationException(hexString,
                exception.getMessage(), exception);
        switch (code) {
            case PASSWORD_EXPIRED:
                throw new CredentialsExpiredException(messages.getMessage(
                        "LdapAuthenticationProvider.credentialsExpired",
                        "User credentials have expired"), cause);
            case ACCOUNT_DISABLED:
                throw new DisabledException(messages.getMessage(
                        "LdapAuthenticationProvider.disabled", "User is disabled"), cause);
            case ACCOUNT_EXPIRED:
                throw new AccountExpiredException(messages.getMessage(
                        "LdapAuthenticationProvider.expired", "User account has expired"),
                        cause);
            case ACCOUNT_LOCKED:
                throw new LockedException(messages.getMessage(
                        "LdapAuthenticationProvider.locked", "User account is locked"), cause);
            default:
                throw badCredentials(cause);
        }
    }

    private String subCodeToLogMessage(int code) {
        switch (code) {
            case USERNAME_NOT_FOUND:
                return "User was not found in directory";
            case INVALID_PASSWORD:
                return "Supplied password was invalid";
            case NOT_PERMITTED:
                return "User not permitted to logon at this time";
            case PASSWORD_EXPIRED:
                return "Password has expired";
            case ACCOUNT_DISABLED:
                return "Account is disabled";
            case ACCOUNT_EXPIRED:
                return "Account expired";
            case PASSWORD_NEEDS_RESET:
                return "User must reset password";
            case ACCOUNT_LOCKED:
                return "Account locked";
        }

        return "Unknown (error code " + Integer.toHexString(code) + ")";
    }

    private BadCredentialsException badCredentials() {
        return new BadCredentialsException(messages.getMessage(
                "LdapAuthenticationProvider.badCredentials", "Bad credentials"));
    }

    private BadCredentialsException badCredentials(Throwable cause) {
        return (BadCredentialsException) badCredentials().initCause(cause);
    }

    private String createBindPrincipal(String username) {
        if (domain == null || username.toLowerCase().endsWith(domain)) {
            return username;
        }
        return username + "@" + domain;
    }
 }
