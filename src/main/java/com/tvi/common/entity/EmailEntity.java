
package com.tvi.common.entity;

public class EmailEntity
{
	public final String title;
	public final String body;
	public final String signature;

	public EmailEntity(String title, String body, String signature)
	{
		this.title = title;
		this.body = body;
		this.signature = signature;
	}
}
