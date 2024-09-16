package SMTP;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;


/**
 The Mail class represents an email message with the ability to draft and send emails using Gmail SMTP server.
 */
public class Mail {



    /**
     The session used for email communication.
     */
    Session newSession = null;

    /** The MimeMessage object representing the email message. */
    MimeMessage mimeMessage = null;
    public static void main(String[] args) throws MessagingException, IOException {
//        Mail mail = new Mail();
//        mail.setupServerProperties();
//        mail.draftEmail("alexobz09@gmail.com","hi alex");
//        mail.sendEmail();
    }

    /**
     Constructs a new Mail object.
     */
    public Mail(){

    }

    /**
     Sends the email message.
     @throws MessagingException if there is an error in sending the email.
     */
    public void sendEmail() throws MessagingException {
        String fromUser = "alexobz01@gmail.com";
        String fromUserPassword = "gdqjalpudtihjibk";
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost,fromUser,fromUserPassword);
        transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
        transport.close();


    }


    /**

     Creates a MimeMessage object for the email message with an attachment.

     @param recipient the email address of the recipient.

     @param emailBody the body of the email message.

     @param attachmentFilePath the file path of the attachment.

     @return the MimeMessage object representing the email message.

     @throws MessagingException if there is an error in creating the MimeMessage object.

     @throws AddressException if the recipient email address is invalid.

     @throws IOException if there is an error in reading the attachment file.
     */
    public MimeMessage draftEmail(String recipient, String emailBody, String attachmentFilePath) throws MessagingException, AddressException, IOException {
        String[] emailRecipients = {recipient};
        String emailSubject = "Test Email";
        mimeMessage = new MimeMessage(newSession);

        for(int i = 0; i < emailRecipients.length; i++){
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(emailRecipients[i])));
        }
        mimeMessage.setSubject(emailSubject);


        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody,"text/html;charset=UTF-8");

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File(String.valueOf(attachmentFilePath)));

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        multipart.addBodyPart(attachmentBodyPart);

        mimeMessage.setContent(multipart);
        return mimeMessage;

    }


    /**
     *
     Creates a draft email message with the given recipient and email body.

     @param recipient the email address of the recipient

     @param emailBody the body of the email message

     @return a MimeMessage object that represents the draft email message

     @throws MessagingException if there is an error in creating the email message

     @throws AddressException if there is an error in creating the email address for the recipient

     @throws IOException if there is an error in attaching a file to the email message
     */
    public MimeMessage draftEmail(String recipient, String emailBody) throws MessagingException, AddressException, IOException {
        String[] emailRecipients = {recipient};
        String emailSubject = "Test Email";
        mimeMessage = new MimeMessage(newSession);

        for(int i = 0; i < emailRecipients.length; i++){
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(emailRecipients[i])));
        }
        mimeMessage.setSubject(emailSubject);


        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody,"text/html;charset=UTF-8");


        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);


        mimeMessage.setContent(multipart);
        return mimeMessage;

    }


    /**
     This method sets up the server properties for sending an email.
     It sets the properties for the email server and starts a new session using the configured properties.
     The properties set include the port number, authentication, and starttls settings for the email server.
     The session object is then assigned to the newSession variable.
     */
    public void setupServerProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable","true");
        newSession = Session.getDefaultInstance(properties,null);
    }

}