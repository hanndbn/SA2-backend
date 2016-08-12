package com.tvi.apply.util.mail;

import com.tvi.apply.util.file.IFileHelper;
import com.tvi.apply.util.rar.IRarPacker;
import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.*;

public class EmailHelper implements IEmailHelper
{

	final EmailAccount account;
	final IFileHelper filemgr;
	final IRarPacker packer;
	private Session session;
	private Session sendsession;
	private Store store;

	public EmailHelper(EmailAccount account, IFileHelper filemgr, IRarPacker packer)
	{
		this.account = account;
		this.packer = packer;
		this.filemgr = filemgr;
	}

	public void closeSession()
	{
		this.session = null;
	}
	private Session sendSession()
	{
		if (this.sendsession != null) return sendsession;


		final String username = account.username;//change accordingly
		final String password = account.password;//change accordingly
		// Assuming you are sending email through relay.jangosmtp.net

		String host = account.outgoing;
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "25");
		// Get the Session object.
		sendsession = Session.getInstance(props, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, password);
			}
		});

		return sendsession;
	}

	@Override
	public void send(Email email)
	{
		// Recipient's email ID needs to be mentioned.
		String to = email.to;
		// Sender's email ID needs to be mentioned
		String from = email.from;


		try
		{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(sendSession());
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));
			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

		/* integrate cc adresses*/
			int numOfCC = 0;
			if (email.cc != null) numOfCC = email.cc.length;
			if (numOfCC > 0)
			{
				InternetAddress[] cc = new InternetAddress[numOfCC];
				for (int i = 0; i < numOfCC; i++)
				{
					cc[i] = InternetAddress.parse(email.cc[i])[0];
				}
				message.addRecipients(Message.RecipientType.CC, cc);
			}

		/* integrate bcc adresses */
			int numOfBCC = 0;
			if (email.bcc != null) numOfBCC = email.bcc.length;
			if (numOfBCC > 0)
			{
				InternetAddress[] bcc = new InternetAddress[numOfBCC];
				for (int i = 0; i < numOfBCC; i++)
				{
					bcc[i] = InternetAddress.parse(email.bcc[i])[0];
				}
				message.addRecipients(Message.RecipientType.BCC, bcc);
			}

			// Set Subject: header field

			message.setHeader("Content-Type", "text/html; charset=UTF-8");
			message.setSubject(email.subject, "utf-8");

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Create the message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText(email.body, "UTF-8"); // Now set the actual message
			messageBodyPart.setHeader("Content-Type", "text/html; charset=UTF-8");
			multipart.addBodyPart(messageBodyPart);

			//attach file
			int numOfAttackFile = 0;
			if (email.attachment != null) numOfAttackFile = email.attachment.length;
			if (numOfAttackFile > 0)
			{
				for (int i = 0; i < numOfAttackFile; i++)
				{
					messageBodyPart = new MimeBodyPart();
					String filename = email.attachment[i];
					DataSource source = new FileDataSource(filename);
					messageBodyPart.setDataHandler(new DataHandler(source));

					messageBodyPart.setFileName(source.getName());
					multipart.addBodyPart(messageBodyPart);
				}
			}
			// Send the complete message parts
			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException e)
		{
			throw new RuntimeException(e);
		}
	}


	Session getSession()
	{

		if (session != null) return session;

		Properties properties = new Properties();
		properties.put("mail.imap.host", account.incoming);
		properties.put("mail.imap.port", "995");
		properties.put("mail.imap.starttls.enable", "true");
		properties.put("mail.imap.ssl.trust", "*");
		session = Session.getDefaultInstance(properties);
		return session;
	}

	Store getImapStore()
	{

		if (store == null || !store.isConnected())
		{
			try
			{
				store = getSession().getStore("imap");
				store.connect(account.incoming, account.username, account.password);
			} catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return store;
	}

	@Override
	public Email download(String messageid)
	{

		MessageIDTerm messageIDTerm = new MessageIDTerm(messageid);

		try
		{

			Folder emailFolder = getImapStore().getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.search(messageIDTerm);

			String from = ((InternetAddress) messages[0].getFrom()[0]).getAddress();
			Address[] recipients = messages[0].getRecipients(RecipientType.TO);
			String to = "";
			if (recipients != null) for (Address a : recipients)
			{
				to = ((InternetAddress) a).getAddress();
			}
			//String to = ((InternetAddress) messages[0].getRecipients(RecipientType.TO)[).getAddress();

			Address[] ccAddress = messages[0].getRecipients(RecipientType.CC);
			String[] cc = null;
			if (ccAddress != null)
			{
				int addressSize = ccAddress.length;
				cc = new String[addressSize];
				for (int i = 0; i < addressSize; i++)
				{
					cc[i] = ((InternetAddress) ccAddress[i]).getAddress();
				}
			}

			String messageId = messages[0].getHeader("Message-ID")[0];
			String sendTime = messages[0].getReceivedDate().toString();
			String subject = messages[0].getSubject();
			String body = "";
			List<String> attack = new ArrayList<String>();
			String[] attackment = null;
			try
			{
				body = downMailRec(attack, filemgr, messages[0].getContent());

				attackment = new String[attack.size()];
				for (int i = 0; i < attack.size(); i++)
				{
					attackment[i] = attack.get(i);
				}

				emailFolder.close(false);

			} catch (Exception e)
			{
				e.printStackTrace();
			}

			return new Email(from, to, cc, null, attackment, subject, body, sendTime, messageId);
		} catch (MessagingException e)
		{
			throw new RuntimeException(e);
		}
	}

	private String downMailRec(List<String> attack, IFileHelper filemgr, Object contentObject) throws MessagingException, IOException
	{
		String body = "";
		if (contentObject instanceof Multipart)
		{
			BodyPart clearTextPart;
			BodyPart htmlTextPart;
			Multipart content = (Multipart) contentObject;
			int count = content.getCount();

			for (int i = 0; i < count; i++)
			{
				BodyPart part = content.getBodyPart(i);
				if (part.getContent() instanceof Multipart)
				{
					body += downMailRec(attack, filemgr, part.getContent());
				} else if (Part.ATTACHMENT.equalsIgnoreCase((part).getDisposition()))
				{
					InputStream inputStream = part.getInputStream();
					byte[] bytes = IOUtils.toByteArray(inputStream);
					String filerar = filemgr.saveFile(bytes, "", MimeUtility.decodeText(part.getFileName()));
					String absfilerar = filemgr.convertToAbsolute(filerar);
					if (packer.isCompressedFile(absfilerar))
					{
						List<String> files = packer.unpackLevel1(absfilerar);
						for (String f : files)
						{
							attack.add(filemgr.convertToRelative(f));
						}
					}
					attack.add(filerar);
				} else if (part.isMimeType("text/plain"))
				{
					clearTextPart = part;
					Charset charset = Charset.forName("UTF-8");
					body += charset.decode(charset.encode((String) clearTextPart.getContent()));
				} else if (part.isMimeType("text/html"))
				{
					htmlTextPart = part;
					Charset charset = Charset.forName("UTF-8");
					String html = (String) htmlTextPart.getContent();
					body += charset.decode(charset.encode(html));
				}
			}

		} else if (contentObject instanceof String) // a simple text message
		{
			body = (String) contentObject;
		}
		return body;
	}

	@Override
	public long count(final Calendar fromdate)
	{
		SearchTerm term = new SearchTerm()
		{
			@Override
			public boolean match(Message arg0)
			{
				Date receivedDate;
				long yesterday = System.currentTimeMillis() - 60 * 60 * 1000;
//				Date fDate = fromdate.getTime();
				Date fDate = new Date(yesterday);
				try
				{
					receivedDate = arg0.getReceivedDate();
					if (receivedDate.after(fDate))
					{
						return true;
					}
				} catch (MessagingException e)
				{
					throw new RuntimeException(e);
				}
				return false;
			}
		};

		try
		{
			Store store = getImapStore();
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.search(term);
			emailFolder.close(false);
            store.close();
			return messages.length;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> scan(final Calendar fromdate, int n, int p)
	{
		List<String> messageIds = new ArrayList<String>();
		List<String> returnMessageIds = new ArrayList<String>();


		SearchTerm term = new SearchTerm()
		{

			@Override
			public boolean match(Message arg0)
			{
				Date receivedDate;
				long yesterday = System.currentTimeMillis() - 60 * 60 * 1000;
//				Date fDate = fromdate.getTime();
				Date fDate = new Date(yesterday);
				try
				{
					receivedDate = arg0.getReceivedDate();
					if (receivedDate.after(fDate))
					{
						return true;
					}
				} catch (MessagingException e)
				{
					throw new RuntimeException(e);
				}

				return false;
			}
		};

		try
		{
			Store store = getImapStore();
//			store.connect(account.incoming, account.username, account.password);
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.search(term);
			//			Message[] messages = emailFolder.getMessages();
			for (Message message : messages)
			{
				if (message.getHeader("Message-ID") == null || message.getHeader("Message-ID").length == 0) continue;
				messageIds.add(message.getHeader("Message-ID")[0]);
//				System.out.println(message.getReceivedDate().toString());
			}

			int startIndex = p * n;
			final int maxIndex = (p + 1) * n;
			if (messageIds.size() >= startIndex + 1)
			{
				while (startIndex < maxIndex && startIndex < messageIds.size())
				{
					returnMessageIds.add(messageIds.get(startIndex));
					startIndex++;
				}
			}
			emailFolder.close(false);
            store.close();
			return returnMessageIds;
		} catch (MessagingException e)
		{
			throw new RuntimeException(e);
		}
	}

}
