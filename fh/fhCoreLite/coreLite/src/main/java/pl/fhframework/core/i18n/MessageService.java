package pl.fhframework.core.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import pl.fhframework.core.FhFrameworkException;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for resolving messages i18n based on key, used in property files. This service is
 * based on Spring ApplicationContext.
 */
@Service
public class MessageService {

    private static final String FH_CORE_MSG_BEAN_NAME = "fhCoreMessageSource";
    private static final String ENUM_KEY_PREFIX = "enum.";

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${fhframework.language.default:en-GB}")
    private String defaultLanguage;

    /**
     * Creates specific message bundle with given bundleName.
     *
     * @param bundleName - bundleName for message as prefix
     * @return new instance of MessageBundle
     */
    public MessageBundle getBundle(String bundleName) {
        return new MessageBundle(bundleName);
    }

    /**
     * Creates Message bundle with no predefined prefix for bundle.
     *
     * @return new instance of MessageBundle
     */
    public MessageBundle getAllBundles() {
        return new MessageBundle(null);
    }

    @Bean(name = FH_CORE_MSG_BEAN_NAME)
    public MessageSource fhCoreMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("classpath:core");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    public class MessageBundle {

        private String bundleName;

        protected MessageBundle(String bundleName) {
            this.bundleName = bundleName;
        }

        /**
         * Getting message by key from message source for given languageTag. Also this method first
         * will check if bundleName was not null or empty, then it will look for message within all
         * beans. Otherwise if bundleName is provided, it will look for message within this messages
         * source. If language tag is null or empty, it will get message for default locale. If
         * messages was not found with given key, it will throw NoSuchMessageException. WARNING!!
         * This method does not return message for Fh Core messages.
         *
         * @param key            - key used in bundle
         * @param args           - Array of arguments that will be filled in for params within the
         *                       message (params look like "{0}", "{1,date}", "{2,time}" within a
         *                       message), or {@code null} if none.
         * @param locale         - locale tag, example: "pl-PL"
         * @param defaultMessage - default message if key not found
         * @return message, if found
         * @throws NoSuchMessageException if the message wasn't found
         * @throws FhFrameworkException if bean with given bundle name is not found
         */
        public String getMessage(String key, Object[] args, Locale locale, String defaultMessage) {
            Locale foundLocale = getLanguageOrDefault(locale);
            if (StringUtils.isNullOrEmpty(bundleName)) {
                return tryFindFirstMessage(key, args, foundLocale, defaultMessage);
            } else {
                MessageSource msgSource = (MessageSource) applicationContext.getBean(bundleName);
                if (msgSource == null) {
                    throw new FhFrameworkException("Bean not found in context: " + bundleName);
                }
                return msgSource.getMessage(key, args, defaultMessage, foundLocale);
            }
        }

        /**
         * Same as getMessage(String key, Locale locale) but getting locale from user session.
         *
         * @param key - key used in bundle
         * @return message, if found
         * @throws NoSuchMessageException if the message wasn't found
         * @throws FhFrameworkException if bean with given bundle name is not found
         */
        public String getMessage(String key) {
            return getMessage(key, null, getUserLanguage(), "Key: " + key + " not found");
        }

        /**
         * Same as getMessage(String key, Locale locale) but getting locale from user session.
         *
         * @param key            - key used in bundle
         * @param defaultMessage - default message if key not found
         * @return message, if found
         * @throws FhFrameworkException if bean with given bundle name is not found
         */
        public String getMessage(String key, String defaultMessage) {
            return getMessage(key, null, getUserLanguage(), defaultMessage);
        }

        /**
         * Same as getMessage(String key) but also supports arguments for message.
         *
         * @param key  - key used in bundle
         * @param args - Array of arguments that will be filled in for params within the message
         *             (params look like "{0}", "{1,date}", "{2,time}" within a message), or {@code
         *             null} if none.
         * @return message, if found
         * @throws NoSuchMessageException if the message wasn't found
         * @throws FhFrameworkException if bean with given bundle name is not found
         */
        public String getMessage(String key, Object[] args) {
            return getMessage(key, args, getUserLanguage(), "Key: " + key + " not found");
        }

        /**
         * Same as getMessage(String key, Locale locale) but getting locale from user session.
         *
         * @param key            - key used in bundle
         * @param args           - Array of arguments that will be filled in for params within the
         *                       message (params look like "{0}", "{1,date}", "{2,time}" within a
         *                       message), or {@code null} if none.
         * @param defaultMessage - default message if key not found
         * @return message, if found
         * @throws FhFrameworkException if bean with given bundle name is not found
         */
        public String getMessage(String key, Object[] args, String defaultMessage) {
            return getMessage(key, args, getUserLanguage(), defaultMessage);
        }

        /**
         * Gets message corresponding to given enum value.
         * Message is searched using key constructed from enum's class name (canonical or simple) and enum value name,
         * e.g. enum.pl.fhframework.enums.SimpleEnum.VALUE or enum.SimpleEnum.VALUE.
         * Returns enum value toString() result, if message wasn't found.
         * @param enumValue enum value
         * @return message
         */
        public String getEnumMessage(Enum<?> enumValue) {
            if (enumValue == null) {
                return null;
            }
            MessageService.MessageBundle allBundles = getAllBundles();
            String fullClassKey = ENUM_KEY_PREFIX + enumValue.getClass().getCanonicalName() + "." + enumValue.name();
            String value = allBundles.getMessage(fullClassKey, null, getUserLanguage(), null);
            if (value != null) {
                return value;
            }
            String shortClassKey = ENUM_KEY_PREFIX + enumValue.getClass().getSimpleName() + "." + enumValue.name();
            return allBundles.getMessage(shortClassKey, enumValue.toString());
        }

        private Locale getUserLanguage() {
            UserSession session = SessionManager.getUserSession();
            if (session != null) {
                return session.getLanguage();
            } else {
                return getLanguageOrDefault(null);
            }
        }

        private String tryFindFirstMessage(String key, Object[] args, Locale locale, String defaultMessage) {
            Map<String, MessageSource> beansOfType = applicationContext.getBeansOfType(MessageSource.class);

            // todo: maybe think of some other way to reorder messages. But fh core message must be last to get message from.
            // Tried to use hierarchy of MessagesSource, but not all implementations supports that.
            List<MessageSource> customMessages = beansOfType.entrySet().stream()
                    .filter(bean -> !bean.getKey().equals(FH_CORE_MSG_BEAN_NAME))
                    .map(Map.Entry::getValue).collect(Collectors.toList());
            customMessages.add(beansOfType.get(FH_CORE_MSG_BEAN_NAME));

            for (MessageSource source : customMessages) {
                String message = source.getMessage(key, args, null, locale);
                if (message != null) {
                    return message;
                }
            }
            return defaultMessage;
        }

        private Locale getLanguageOrDefault(Locale locale) {
            if (locale == null) {
                final String defaultLanguage = MessageService.this.defaultLanguage;
                return Locale.forLanguageTag(defaultLanguage);
            }
            return locale;
        }

    }
}
