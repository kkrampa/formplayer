package services;

import beans.InstallRequestBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commcare.api.persistence.SqliteIndexedStorageUtility;
import org.javarosa.core.services.storage.IStorageIndexedFactory;
import org.javarosa.core.services.storage.IStorageUtilityIndexed;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.ApplicationUtils;

/**
 * FormPlayer's storage factory that negotiates between parsers/installers and the storage layer
 */
@Component
@Scope(value = "request")
public class FormplayerStorageFactory implements IStorageIndexedFactory{

    private String username;
    private String domain;
    private String appId;
    private String databasePath;
    private String trimmedUsername;

    private final Log log = LogFactory.getLog(FormplayerStorageFactory.class);


    public void configure(InstallRequestBean authenticatedRequestBean) {
        configure(authenticatedRequestBean.getUsername(),
                authenticatedRequestBean.getDomain(),
                authenticatedRequestBean.getAppId());
    }

    public void configure(String username, String domain, String appId) {
        log.info(String.format("Configuring StorageFactory with username %s, domain %s, appId %s",
                username, domain, appId));
        this.username = username;
        this.domain = domain;
        this.appId = appId;
        this.trimmedUsername = StringUtils.substringBefore(username, "@");
        this.databasePath = ApplicationUtils.getApplicationDBPath(domain, username, appId);
    }

    @Override
    public IStorageUtilityIndexed newStorage(String name, Class type) {
        return new SqliteIndexedStorageUtility(type, trimmedUsername, name, databasePath);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public String getTrimmedUsername() {
        return trimmedUsername;
    }
}
