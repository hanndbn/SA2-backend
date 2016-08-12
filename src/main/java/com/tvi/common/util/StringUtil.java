package com.tvi.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.commons.lang.RandomStringUtils;

public class StringUtil
{

	public static byte[] createPassword(String pass) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] hash = md.digest((pass + "qwertyasdf").getBytes("UTF-8"));
		return hash;
	}

	public static String randomString()
	{
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public static String randomNumberString()
	{
		return RandomStringUtils.random(39, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
	}

}
