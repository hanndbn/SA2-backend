package com.tvi.common;

import com.tvi.common.entity.CaptchaHead;

public interface ICaptchaProvider
{

	CaptchaHead create(String ip);

	boolean validate(long capid, String cap, String ip);

}
