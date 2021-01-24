package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    private String username;
    private String password;
    private String addressOfRecipient;
    private Properties props;

    public EmailSender(String username, String password, String addressOfRecipient) {
        this.username = username;
        this.password = password;
        this.addressOfRecipient = addressOfRecipient;

        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
    }

    public void send(String subject, String text){
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            //от кого
            message.setFrom(new InternetAddress(username));
            //кому
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addressOfRecipient));
            //тема сообщения
            message.setSubject(subject);
            //текст
            message.setContent(text, "text/html");
//            message.setText(text);

            //отправляем сообщение
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}