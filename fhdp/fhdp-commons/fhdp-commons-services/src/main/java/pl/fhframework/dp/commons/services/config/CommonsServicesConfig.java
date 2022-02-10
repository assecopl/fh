package pl.fhframework.dp.commons.services.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.fhframework.dp.commons.services.facade.FacadeServiceCtl;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
public class CommonsServicesConfig {
	@Value("${mail.smtp.auth:true}")
	private String mailSmtpAuth;
	
	@Value("${mail.smtp.starttls.enable:true}")
	private String mailSmtpStarttlsEnable;
	
	@Value("${mail.smtp.host:127.0.0.1}")
	private String mailSmtpHost;
	
	@Value("${mail.smtp.port:995}")
	private String mailSmtpPort;

	@Value("${sender.email:username}")
	private String senderEmail;

	@Value("${sender.password:password}")
	private String senderPassword;
	
	@Autowired
	private Authenticator authenticator;
		
	@Autowired
	private Session session;



	public @Bean 
	Authenticator authenticator() {
		Authenticator result = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(senderEmail, senderPassword);
			}
		};
		return result;
	}
	
	public @Bean 
	Session session() {
		Properties props = new Properties();
		
		props.setProperty("mail.smtp.auth", mailSmtpAuth);
		props.setProperty("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
		props.setProperty("mail.smtp.host", mailSmtpHost);
		props.setProperty("mail.smtp.port", mailSmtpPort);
		return Session.getInstance(props, authenticator);
	}
	
	public @Bean
	JavaMailSender javaMailSender() {
		JavaMailSenderImpl result = new JavaMailSenderImpl();
		
		result.setSession(session);
		
		return result;
	}    
    

    @Bean
    FacadeServiceCtl facadeServiceCtl() {
        return new FacadeServiceCtl();
    }

}
