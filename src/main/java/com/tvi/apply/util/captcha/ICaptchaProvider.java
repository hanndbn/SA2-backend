package com.tvi.apply.util.captcha;

public interface ICaptchaProvider
{

	CaptchaHead create(String ip);

	boolean validate(long capid, String cap, String ip);

}
