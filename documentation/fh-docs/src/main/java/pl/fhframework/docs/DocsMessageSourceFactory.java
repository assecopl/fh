package pl.fhframework.docs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class DocsMessageSourceFactory {

  @Bean(name = "fhDocsMessageSource")
  public MessageSource fhDocsMessageSource() {
      ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
      messageSource.setDefaultEncoding("UTF-8");
      messageSource.setBasenames("classpath:fh-docs");
      messageSource.setUseCodeAsDefaultMessage(false);
      messageSource.setCacheMillis(1000);
      messageSource.setFallbackToSystemLocale(false);
      return messageSource;
  }

}
