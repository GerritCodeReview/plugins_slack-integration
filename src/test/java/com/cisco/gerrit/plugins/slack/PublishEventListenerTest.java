package com.cisco.gerrit.plugins.slack;

import org.junit.Assert.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cisco.gerrit.plugins.slack.config.*;

import com.cisco.gerrit.plugins.slack.PublishEventListener;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.events.PatchSetEvent;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Project.NameKey.class})
public class PublishEventListenerTest
{
	@Test
	public void generatesMessageWithConfigurationInProjectConfigFile() throws Exception
	{
		final String VALID_PROJECT_NAME = "my-first-project";
		final String VALID_BRANCH_NAME = "master";
		
		PluginConfigFactory configFactory =
				TestToolbox.generatePluginConfigurationBasedOnProjectConfigFile(VALID_PROJECT_NAME);
		PatchSetEvent event =
				TestToolbox.generateMinimalPatchSetEvent(VALID_PROJECT_NAME, VALID_BRANCH_NAME);

		WebhookClientStub webhookClient = new WebhookClientStub();
		PublishEventListener listener = new PublishEventListener(configFactory, webhookClient);
		listener.onEvent(event);
		
		assertTrue(webhookClient.published);
	}
	
	@Test
	public void generatesMessageWithConfigurationInPluginConfigFile() throws Exception
	{
		final String VALID_PROJECT_NAME = "my-first-project";
		final String VALID_BRANCH_NAME = "master";
		
		PluginConfigFactory configFactory =
				TestToolbox.generatePluginConfigurationBasedOnPluginConfigFile(VALID_PROJECT_NAME, VALID_BRANCH_NAME);
		PatchSetEvent event =
				TestToolbox.generateMinimalPatchSetEvent(VALID_PROJECT_NAME, VALID_BRANCH_NAME);

		WebhookClientStub webhookClient = new WebhookClientStub();
		PublishEventListener listener = new PublishEventListener(configFactory, webhookClient);
		listener.onEvent(event);
		
		assertTrue(webhookClient.published);
	}	
}
