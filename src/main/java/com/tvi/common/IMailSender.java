package com.tvi.common;

import com.tvi.common.entity.EmailEntity;

public interface IMailSender
{
	void send(String address, EmailEntity email);
}
