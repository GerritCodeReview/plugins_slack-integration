package com.cisco.gerrit.plugins.slack;

import com.cisco.gerrit.plugins.slack.client.WebhookClient;

public class WebhookClientStub extends WebhookClient
{
	public boolean published = false; 
	
	@Override
	public boolean publish(String message, String webhookurl)
	{
		this.published = true;
		return true;
	}
}
