package com.tvi.apply.util.mail;

import com.tvi.apply.business.core.IEmailMgt;
import com.tvi.apply.util.file.PrivateFileHelper;
import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Calendar;

public class EmailHelperTest extends TestCase
{

	private IEmailHelper h ;
	public void setUp() throws Exception
	{
		super.setUp();


	}

	public void tearDown() throws Exception
	{

	}

	public void testSend() throws Exception
	{

		System.out.println(con());
	}

	public void testDownload() throws Exception
	{


		//EmailAccount emailaccount = new EmailAccount("hr@tinhvan.com", "Rh05)&!#", "mail.tinhvan.com", "mail.tinhvan.com", "POP3", "s");

		//long count = h.count(Calendar.getInstance());
		//Email e = h.download("<20141023013419538512@ao.com>");
		//System.out.println(e.body);
	}

	public void testCount() throws Exception
	{

	}

	public void testScan() throws Exception
	{

	}

	public String con()
	{
		// Returns a charset object for the named charset.

		Charset charset = Charset.forName("ISO-8859-1");
		Charset charset2 = Charset.forName("UTF-8");


		// Constructs a new decoder for this charset.

		CharsetDecoder decoder = charset.newDecoder();


		// Constructs a new encoder for this charset.

		CharsetEncoder encoder = charset2.newEncoder();


		// Wrap the character sequence into a buffer.

		CharBuffer uCharBuffer = CharBuffer.wrap("thÃ¡nh áº§");


		try
		{

			// Encode the remaining content of a single input character buffer to a new byte buffer.

			// Converts to ISO-8859-1 bytes and stores them to the byte buffer

			ByteBuffer bbuf = encoder.encode(uCharBuffer);


			// Decode the remaining content of a single input byte buffer to a new character buffer.

			// Converts from ISO-8859-1 bytes and stores them to the character buffer

			CharBuffer cbuf = decoder.decode(bbuf);

			String s = cbuf.toString();
			return s;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}