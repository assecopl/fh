/*
 * JAPIS 2018.
 */
package pl.fhframework.dp.commons.prints.i18n;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SimplePropertiesI18NProvider
 *
 * @author <a href="mailto:pawel_kasprzak@skg.pl">Paweł Kasprzak</a>
 * @version $Revision: 3970 $, $Date: 2019-01-18 11:11:45 +0100 (pt., 18 sty
 * 2019) $
 */
public class SimplePropertiesI18NProvider implements I18NProvider {

    private static final long serialVersionUID = -6974982185534664224L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String DEFAULT_BUNDLE_NAME = "app-messages";

    public static final Locale LOCALE_PL = new Locale("pl", "PL");
    public static final Locale LOCALE_EN = Locale.US;

    private final I18NProvider[] parentProviders;

    private final String[] bundleNames;

    private final LoadingCache<Locale, List<ResourceBundle>> bundleCache = CacheBuilder
            .newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<Locale, List<ResourceBundle>>() {

                @Override
                public List<ResourceBundle> load(final Locale key) throws Exception {
                    return loadBundle(key);
                }
            });

    public SimplePropertiesI18NProvider() {
        this(DEFAULT_BUNDLE_NAME);
    }

    public SimplePropertiesI18NProvider(String... bundleNames) {
        this((I18NProvider) null, bundleNames != null && bundleNames.length > 0 ? bundleNames : new String[]{DEFAULT_BUNDLE_NAME});
    }

    public SimplePropertiesI18NProvider(I18NProvider parentProvider, String... bundleNames) {
        this(parentProvider != null ? new I18NProvider[]{parentProvider} : null, bundleNames);
    }

    public SimplePropertiesI18NProvider(I18NProvider[] parentProviders, String[] bundleNames) {
        this.parentProviders = parentProviders;
        this.bundleNames = bundleNames;
    }

    public Locale getDefaultLocale() {
        return getProvidedLocales() != null && !getProvidedLocales().isEmpty()
                ? getProvidedLocales().get(0) : null;
    }

    public List<Locale> getProvidedLocales() {
        return Arrays.asList(LOCALE_PL, LOCALE_EN);
    }

    @Override
    public String getString(Locale locale, String key, Object... params) {
        if (key == null) {
            logger.warn("Got request with null key value!");
            return "";
        }
        if (locale == null) {
            locale = LocaleAccessUtil.getLocale();
        }
        String value = null;

        // current resource bundle(s) 
        for (final ResourceBundle current : bundleCache.getUnchecked(locale)) {
            try {
                value = current.getString(key);
                if (params != null
                        && params.length > 0) {
                    value = MessageFormat.format(value, formatNotNull(params));
                }
            } catch (final MissingResourceException ex) {
                // warn before return
                value = I18NProvider.getNoExistingKeyString(locale, key);
            } catch (Exception ex) {
                logger.error("Error read resource: " + key + " [" + bundleNames + ", " + locale + "]!", ex);
            } finally {
                if (value == null) {
                    value = I18NProvider.getNoExistingKeyString(locale, key);
                }
            }
            if (!I18NProvider.isNoExistingKeyString(value)) {
                return value;
            }
        }
        if (I18NProvider.isNoExistingKeyString(value)
                && parentProviders != null) {
            for (final I18NProvider current : parentProviders) {
                value = current.getString(locale, key, params);
                if (!I18NProvider.isNoExistingKeyString(value)) {
                    return value;
                }
            }
        }
        if (!I18NProvider.isNoExistingKeyString(value)) {
            logger.warn("Can't find resource: " + key + " [" + bundleNames + ", " + locale + "].");
        }
        return value;
    }

    private Object[] formatNotNull(Object... params) {
        return params != null
                ? Arrays.asList(params).stream()
                        .map(param -> param != null ? param : "")
                        .collect(Collectors.toList())
                        .toArray()
                : params;
    }

    protected List<ResourceBundle> loadBundle(final Locale locale) {
        final ClassLoader cl = SimplePropertiesI18NProvider.class.getClassLoader();

        List<ResourceBundle> resourceBundles = Lists.newArrayList();
        for (String current : bundleNames) {
            if (StringUtils.isNotBlank(current)) {
                try {
                    resourceBundles.add(ResourceBundle.getBundle(current, locale, cl, new ResourceBundleCustomControl()));
                    // TODO add parent resource handling 
                } catch (final MissingResourceException ex) {
                    logger.warn("Can't find bundle: " + current + " [" + locale + "].");
                } catch (Exception ex) {
                    logger.error("Error load resource: " + current + " [" + locale + "]!", ex);
                }
            }
        }
        return resourceBundles;
    }

    public static class ResourceBundleCustomControl extends ResourceBundle.Control {

        @Override
        public List<Locale> getCandidateLocales(String baseName, Locale locale) {
            return Arrays.asList(new Locale(locale.getLanguage()), Locale.ROOT);
        }

        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return null;
        }
    }
}
