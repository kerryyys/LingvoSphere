package com.lingvosphere.backend.Interfaces;

/*import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public static void sendPasswordResetEmail(String fromEmail, String fromPassword, String toEmail,String resetToken){
        try{
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host",SMTP_HOST);
        properties.put("mail.smtp.port",SMTP_PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(fromEmail,fromPassword);
            }
        });
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmail));
            msg.setSubject("Password Reset");
            msg.setText("Click the following link to reset your password: http:website.com/reset-password?token=" + resetToken);

            Transport.send(msg);

            System.out.println("Password reset email sent successfully");
        }catch(MessagingException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
*/