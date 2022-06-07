package pl.fhframework.dp.commons.prints.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 24/09/2021
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class PrintApplication extends SpringBootServletInitializer {
}
