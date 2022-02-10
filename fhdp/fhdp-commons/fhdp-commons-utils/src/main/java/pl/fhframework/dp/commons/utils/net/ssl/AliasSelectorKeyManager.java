package pl.fhframework.dp.commons.utils.net.ssl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author PT
 * @version :  $, :  $
 * @created 07/10/2019
 */
public class AliasSelectorKeyManager extends X509ExtendedKeyManager implements KeyManager {

    private Logger log = LoggerFactory.getLogger(AliasSelectorKeyManager.class);

    private X509KeyManager sourceKeyManager = null;
    private String alias;

    public AliasSelectorKeyManager(X509KeyManager keyManager, String alias) {
        this.sourceKeyManager = keyManager;
        this.alias = alias;

    }

    public String chooseClientAlias(String[] keyType, Principal[] issuers,
                                    Socket socket) {
        log.debug("#####chooseClientAlias");
        boolean aliasFound = false;

        for (String kt : keyType) {
            log.debug("#####chooseClientAlias keyType: {}",  kt);
        }

        for (Principal iss : issuers) {
            log.debug("#####chooseClientAlias issuer: {}",  iss.getName());
        }


        //Get all aliases from the key manager. If any matches with the managed alias,
        //then return it.
        //If the alias has not been found, return null (and let the API to handle it,
        //causing the handshake to fail).

        for (int i = 0; i < keyType.length && !aliasFound; i++) {
            String[] validAliases = sourceKeyManager.getClientAliases(keyType[i], issuers);
            if (validAliases != null) {
                for (int j = 0; j < validAliases.length && !aliasFound; j++) {

                    log.debug("#####chooseClientAlias: {}", validAliases[j]);

                    if (validAliases[j].equals(alias)) aliasFound = true;
                }
            }
        }

        if (aliasFound) {
            return alias;
        } else return null;
    }

    public String chooseServerAlias(String keyType, Principal[] issuers,
                                    Socket socket) {
        return sourceKeyManager.chooseServerAlias(keyType, issuers, socket);
    }

    public X509Certificate[] getCertificateChain(String alias) {
        log.debug("#####getCertificateChain");
        return sourceKeyManager.getCertificateChain(alias);
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        log.debug("#####getClientAliases");
        return sourceKeyManager.getClientAliases(keyType, issuers);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        log.debug("#####getPrivateKey");
        return sourceKeyManager.getPrivateKey(alias);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        log.debug("#####getServerAliases");
        return sourceKeyManager.getServerAliases(keyType, issuers);
    }

    @Override
    public String chooseEngineClientAlias(String[] strings, Principal[] prncpls, SSLEngine ssle) {
        log.debug("#####chooseEngineClientAlias");
        return super.chooseEngineClientAlias(strings, prncpls, ssle);
    }

    @Override
    public String chooseEngineServerAlias(String string, Principal[] prncpls, SSLEngine ssle) {
        log.debug("#####chooseEngineServerAlias");
        return super.chooseEngineServerAlias(string, prncpls, ssle);
    }


}

