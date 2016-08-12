package com.tvi.apply.util.mail;

public class EmailAccount
{
	public String username;
	public String password;
	public String outgoing;
	public String incoming;
	public String incomingprotocol;
	public String outgoingprotocol;

	public EmailAccount(String username, String password, String outgoing, String incoming, String incomingprotocol, String outgoingprotocol) {
		this.username = username;
		this.password = password;
		this.outgoing = outgoing;
		this.incoming = incoming;
		this.incomingprotocol = incomingprotocol;
		this.outgoingprotocol = outgoingprotocol;
	}
}
