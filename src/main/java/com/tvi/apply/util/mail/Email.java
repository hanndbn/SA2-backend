package com.tvi.apply.util.mail;

public class Email
{
	public String from;
	public String to;
	public String[] cc;
	public String[] bcc;
	public String[] attachment;
	public String subject;
	public String body;
	public String sendtime;
	public String messageid;

	public Email()
	{}

	public Email(String from, String to, String[] cc, String[] bcc, String[] attachment, String subject, String body, String sendtime, String messageid) {
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.attachment = attachment;
		this.subject = subject;
		this.body = body;
		this.sendtime = sendtime;
		this.messageid = messageid;
	}
}
