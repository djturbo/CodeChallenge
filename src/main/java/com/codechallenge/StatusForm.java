package com.codechallenge;

public class StatusForm {
	String reference;
	String channel;
	public StatusForm(String reference, String channel) {
		super();
		this.reference = reference;
		this.channel = channel;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	
}
