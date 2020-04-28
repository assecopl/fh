package pl.fhframework.core.preferences;

import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.List;
import java.util.Map;

/**
 * Interface of service to manage (read and save) user preferences
 */
public interface UserPreferencesService {
    /**
     * Saves list of preferences
     * @param preferences Map of preferences to save
     * @throws ConfigurationException Any exception that occurs while initializing a Configuration
     */
    void setPreferences(Map<String, Object> preferences) throws ConfigurationException;

    /**
     * Saves single preference
     * @param key Key name of preference to save
     * @param value Value of preference to save
     * @throws ConfigurationException Any exception that occurs while initializing a Configuration
     */
    void setPreference(String key, Object value) throws ConfigurationException;

    /**
     * Gets single preference with string value
     * @param key Preference key
     * @return Preference value
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    String getStringPreference(String key) throws ConfigurationException;

    /**
     * Gets single preference with string value
     * @param key Preference key
     * @return Preference value or default value
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    String getStringPreference(String key, String default_) throws ConfigurationException;

    /**
     * Gets single preference with integer value
     * @param key Preference key
     * @return Preference value
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    Integer getIntPreference(String key) throws ConfigurationException;

    /**
     * Gets single preference with integer value
     * @param key Preference key
     * @return Preference value or default value
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    int getIntPreference(String key, int default_) throws ConfigurationException;

    /**
     * Gets single preference with boolean value
     * @param key Preference key
     * @return Preference value
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    Boolean getBooleanPreference(String key) throws ConfigurationException;

    /**
     * Gets single preference with boolean value
     * @param key Preference key
     * @return Preference value or default value
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    boolean getBooleanPreference(String key, boolean default_) throws ConfigurationException;

    /**
     * Removes single preference
     * @param key Preference to remove key
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    void clearPreference(String key) throws ConfigurationException;

    /**
     * Removes multiple preferences
     * @param keys Keys of preferences to remove
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    void clearPreferences(List<String> keys) throws ConfigurationException;

    /**
     * Removes all preferences
     * @throws ConfigurationException Thrown if preference with provided key is not found
     */
    void clearPreferences() throws ConfigurationException;
}
