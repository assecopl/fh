package pl.fhframework.dp.commons.services.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service("javaMailService")
@Lazy
public class JavaMailService {

    @Value("${fhdp.mail.from:fhdp@asseco.pl}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Transactional(propagation = Propagation.SUPPORTS)
    public String sendMail(String to, String subject, String text) {
        return sendMail(to, subject, text, null);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public String sendMail(String to, String subject, String text, Attachment... attachments) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");

            helper.setFrom(from);

            if (to.contains(",")) {
                helper.setTo(to.split(","));
            } else {
                helper.setTo(to);
            }
            helper.setSubject(subject);
            if (text != null) {
                helper.setText(text, text.contains("<html") || text.contains("<body"));
            }
            if (attachments != null) {
                for (Attachment attachment : attachments) {
                    helper.addAttachment(attachment.getFileName(), attachment.getDataSource());
                }
            }

            javaMailSender.send(message);
            return message.getMessageID();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

//    public void sendMail(String to, String subject, String text) {
//        try {
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message);
//            helper.setFrom(from);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text);
//            javaMailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void setFrom(String from) {
        this.from = from;
    }

    public static class Attachment {
        DataSource dataSource;
        String fileName;

        public Attachment(DataSource dataSource, String fileName) {
            this.dataSource = dataSource;
            this.fileName = fileName;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

}