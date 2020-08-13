package pl.fhframework.core.preferences;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.eclipse.core.runtime.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.fhframework.SessionManager;
import pl.fhframework.core.logging.FhLogger;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {
    @Value("${fhframework.userprefs.output.directory:_userprefs}")
    private String workDirectory;

    @PostConstruct
    private void createDirectory() {
        File directory = new File(workDirectory);

        if (!directory.exists()) {
            if (directory.mkdir()) {
                FhLogger.info("User preferences directory created.");
            } else {
                FhLogger.error("User preferences directory cannot be created.");
            }
        } else {
            FhLogger.info("User preferences directory already exists.");
        }
    }

    /**
     * Checks if user preferences file exists
     */
    private boolean checkUserPreferencesFile() {
        File userfile = getUserPrefsFile();
        return userfile.exists();
    }

    /**
     * Creates user preferences file
     */
    private void createUserPreferencesFile() {
        File userfile = getUserPrefsFile();

        if (!userfile.exists()) {
            try {
                if (userfile.createNewFile()) {
                    FhLogger.info("Preferences file for user '%s' created.", userfile.getAbsolutePath());
                } else {
                    FhLogger.error("Preferences file for user '%s' cannot be created.", userfile.getAbsolutePath());
                }
            } catch (IOException e) {
                FhLogger.error("Preferences file for user '%s' cannot be created.", userfile.getAbsolutePath(), e);
            }
        } else {
            FhLogger.info("Preferences file for user '%s' already exists.", userfile.getAbsolutePath());
        }
    }

    /**
     * Deletes user preferences file
     */
    private void deleteUserPreferencesFile() {
        File userfile = getUserPrefsFile();

        if (userfile.exists()) {
            if (userfile.delete()) {
                FhLogger.info("Preferences file for user '%s' deleted.", userfile.getAbsolutePath());
            } else {
                FhLogger.error("Preferences file for user '%s' cannot be deleted.", userfile.getAbsolutePath());
            }
        }
    }

    /**
     * Gets user preferences file path for user
     */
    private File getUserPrefsFile() {
        String login = SessionManager.getSystemUser().getLogin();
        return new File(workDirectory + Path.SEPARATOR + login);
    }

    /**
     * Returns configurated Apache Commons Configuration builder
     */
    private FileBasedConfigurationBuilder<FileBasedConfiguration> getConfigurationBuilder() {
        File userfile = getUserPrefsFile();

        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new Parameters().fileBased()
                        .setFile(userfile));
    }

    @Override
    public String getStringPreference(String key) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            return null;
        }

        return getConfigurationBuilder().getConfiguration().getString(key);
    }

    @Override
    public String getStringPreference(String key, String default_) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            return default_;
        }

        return getConfigurationBuilder().getConfiguration().getString(key, default_);
    }

    @Override
    public Integer getIntPreference(String key) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            return null;
        }

        return getConfigurationBuilder().getConfiguration().getInt(key);
    }

    @Override
    public int getIntPreference(String key, int default_) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            return default_;
        }

        return getConfigurationBuilder().getConfiguration().getInt(key, default_);
    }

    @Override
    public Boolean getBooleanPreference(String key) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            return null;
        }

        return getConfigurationBuilder().getConfiguration().getBoolean(key);
    }

    @Override
    public boolean getBooleanPreference(String key, boolean default_) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            return default_;
        }

        return getConfigurationBuilder().getConfiguration().getBoolean(key, default_);
    }

    @Override
    public void clearPreference(String key) throws ConfigurationException {
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = getConfigurationBuilder();
        builder.getConfiguration().clearProperty(key);
        builder.save();

        if (builder.getConfiguration().isEmpty()) {
            this.deleteUserPreferencesFile();
        }
    }

    @Override
    public void clearPreferences(List<String> keys) throws ConfigurationException {
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = getConfigurationBuilder();
        for (String key : keys) {
            builder.getConfiguration().clearProperty(key);
        }

        builder.save();

        if (builder.getConfiguration().isEmpty()) {
            this.deleteUserPreferencesFile();
        }
    }

    @Override
    public void clearPreferences() throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            return;
        }

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = getConfigurationBuilder();
        builder.getConfiguration().clear();
        builder.save();

        if (builder.getConfiguration().isEmpty()) {
            this.deleteUserPreferencesFile();
        }
    }

    @Override
    public void setPreferences(Map<String, Object> preferences) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            createUserPreferencesFile();
        }

        FileBasedConfigurationBuilder<FileBasedConfiguration> build = getConfigurationBuilder();
        Configuration config = build.getConfiguration();

        createUserPreferencesFile();

        preferences.forEach(config::setProperty);
        build.save();
    }

    @Override
    public void setPreference(String key, Object value) throws ConfigurationException {
        if (!checkUserPreferencesFile()) {
            createUserPreferencesFile();
        }

        FileBasedConfigurationBuilder<FileBasedConfiguration> build = getConfigurationBuilder();
        Configuration config = build.getConfiguration();

        createUserPreferencesFile();

        config.setProperty(key, value);
        build.save();
    }
}
