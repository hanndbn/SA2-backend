package com.tvi.apply.util.mail.gmailwapper;

import com.tvi.apply.util.mail.EmailAccount;
import com.tvi.apply.util.mail.EmailEntity;
import com.tvi.apply.util.mail.IMailSender;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailWrapper implements IMailSender
{

	final String address;
	final String password;
	private final Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
	private Transport transport;

	private void keepAlive()
	{
		if (transport.isConnected() == false)
		{
			try
			{
				transport.connect(address, password);
			} catch (MessagingException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}

	public GmailWrapper(EmailAccount account)
	{

		this.address = account.username;
		this.password = account.password;
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		mailServerProperties.put("mail.smtp.ssl.trust", "*");
		mailServerProperties.put("mail.smtp.host", account.outgoing);
		mailServerProperties.put("mail.smtp.port", "25"); // TLS Port
		mailServerProperties.put("mail.smtp.auth", "true"); // Enable Authentication
		mailServerProperties.put("mail.smtp.starttls.enable", "true"); // Enable StartTLS
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		try
		{
			transport = getMailSession.getTransport("smtp");
		} catch (NoSuchProviderException ex)
		{
			throw new RuntimeException(ex);
		}
	}


	@Override
	public void send(String address, EmailEntity email)
	{
		keepAlive();
		generateMailMessage = new MimeMessage(getMailSession);
		try
		{
			generateMailMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
			generateMailMessage.setSubject(email.title, "utf-8");
			generateMailMessage.setContent((email.body + "<br/>" + email.signature), "text/html; charset=UTF-8");
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		} catch (MessagingException ex)
		{
			throw new RuntimeException(ex);
		}
	}

}
