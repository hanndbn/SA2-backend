package com.tvi.GmailWrapper;

import com.tvi.common.IMailSender;
import com.tvi.common.entity.EmailEntity;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;

import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailWrapper implements IMailSender {

    private final Properties mailServerProperties;
    private static Session getMailSession;
    private static MimeMessage generateMailMessage;
    private Transport transport;

    private void keepAlive() {
        if (transport.isConnected() == false) {
            try {
                transport.connect("smtp.gmail.com", "speedyapply@gmail.com", "sieunhansiptim");
                //transport.connect("smtp.gmail.com", "vuonuomtinhvan@gmail.com", "vuonuom1102");
                //transport.connect("smtp.gmail.com", "spath.donotreply@gmail.com", "adklfj092j4oijckldsjf209j4rcj092cj3409");
            } catch (MessagingException ex) {
                throw new RuntimeException("cannot login to mail server: " + ex.toString());
            }
        }
    }

    public GmailWrapper() {
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587"); // TLS Port
        mailServerProperties.put("mail.smtp.auth", "true"); // Enable Authentication
        mailServerProperties.put("mail.smtp.starttls.enable", "true"); // Enable StartTLS  
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        try {
            transport = getMailSession.getTransport("smtp");
        } catch (NoSuchProviderException ex) {
            throw new RuntimeException("cannot connect to mail server: " + ex.toString());
        }

    }

    @Override
    public void send(String address, EmailEntity email) {
        keepAlive();
        generateMailMessage = new MimeMessage(getMailSession);
        try {
            generateMailMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
            generateMailMessage.setSubject(email.title, "utf-8");
            generateMailMessage.setContent((email.body + "<br/>" + email.signature), "text/html; charset=UTF-8");

            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        } catch (MessagingException ex) {

            throw new RuntimeException("cannot send email: " + ex.toString());
        }
    }
}
