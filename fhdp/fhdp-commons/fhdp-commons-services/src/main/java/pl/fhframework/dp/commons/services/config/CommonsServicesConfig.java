package pl.fhframework.dp.commons.services.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pl.fhframework.dp.commons.services.facade.FacadeServiceCtl;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

@Configuration
//@ComponentScan("pl.fhframework.dp.commons.services")
public class CommonsServicesConfig extends AbstractMongoClientConfiguration {

    @Value("${mongo.hostAndPort:172.25.107.187:27018}")
    private String hostAndPort;
    @Value("${mongo.database:fhdp-repository}")
    private String dbName;
    @Value("${mongo.client.pool.minSize:10}")
    private int minSize;
    @Value("${mongo.client.pool.maxSize:30}")
    private int maxSize;

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
    public MongoClient mongoClient() {
        return super.mongoClient();
    }

//    @Bean
//    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        String[] hosts = hostAndPort.split(Pattern.quote(","));
        builder
                .applyToClusterSettings(settings  -> {
                    settings.hosts(serversList(hosts));
                })
                .applyToConnectionPoolSettings(poolSettings -> {
                    poolSettings.minSize(minSize)
                            .maxSize(maxSize);
                });
    }

    private List<ServerAddress> serversList(String[] hosts) {
        List<ServerAddress> ret = new ArrayList<>();
        for(String host: hosts) {
            String[] elements = host.split(Pattern.quote(":"));
            ServerAddress address = new ServerAddress(elements[0], Integer.parseInt(elements[1]));
            ret.add(address);
        }
        return ret;
    }

    @Bean
    FacadeServiceCtl facadeServiceCtl() {
        return new FacadeServiceCtl();
    }

    @Override
    protected String getDatabaseName() {
        return dbName;
    }
}
