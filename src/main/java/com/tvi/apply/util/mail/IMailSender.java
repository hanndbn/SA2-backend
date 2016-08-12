package com.tvi.apply.util.mail;

public interface IMailSender
{
	void send(String address, EmailEntity email);
}
