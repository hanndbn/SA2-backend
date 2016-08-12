package com.tvi.apply.util.mail;

import java.util.Calendar;
import java.util.List;

public interface IEmailHelper
{
	void send(Email email);

	void closeSession();

	Email download(String messageid);

	long count(Calendar formdate);

	List<String> scan(Calendar fromdate, int n, int p);

}
