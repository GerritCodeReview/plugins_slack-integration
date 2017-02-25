package com.cisco.gerrit.plugins.slack.config;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.powermock.api.mockito.PowerMockito;
import org.eclipse.jgit.lib.Config;
import org.mockito.internal.util.collections.Sets;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.data.AccountAttribute;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.gerrit.server.events.PatchSetEvent;

public class TestToolbox
{
	/*
	 * Generates a plugin configuration factory mocked with the following information:
	 * 
	 * [plugin "slack-integration"]
	 * 	enabled = true
	 * 	webhookurl = https://webhook/
	 * 	channel = general
	 * 	username = gerrit
	 * 	ignore = ^WIP.*
	 */
	public static PluginConfigFactory generatePluginConfigurationBasedOnProjectConfigFile(
			String projectName) throws Exception
	{
		PowerMockito.mockStatic(Project.NameKey.class);

		Project.NameKey projectKey = mock(Project.NameKey.class);
        when(Project.NameKey.parse(projectName)).thenReturn(projectKey);

        PluginConfigFactory pluginConfigFactory = mock(PluginConfigFactory.class);
        PluginConfig pluginConfig = mock(PluginConfig.class);
        
        // Setup mocks
        when(pluginConfigFactory.getFromProjectConfigWithInheritance(
        		projectKey, ProjectConfigFileSnapshot.CONFIG_NAME))
                .thenReturn(pluginConfig);
        when(pluginConfigFactory.getProjectPluginConfigWithInheritance(
        		projectKey, ProjectConfigFileSnapshot.CONFIG_NAME))
        		.thenReturn(new Config());
        
        when(pluginConfig.getBoolean("enabled", false))
                .thenReturn(true);
        when(pluginConfig.getString("webhookurl", ""))
                .thenReturn("https://webook/");
        when(pluginConfig.getString("channel", "general"))
                .thenReturn("test-channel");
        when(pluginConfig.getString("username", "gerrit"))
                .thenReturn("test-user");
        when(pluginConfig.getString("ignore", ""))
                .thenReturn("^WIP.*");

        return pluginConfigFactory;
	}

	/*
	 * Generates a plugin configuration factory mocked with the following information:
	 * 
	 *  <slack-integration.config>
	 *  
	 *  [branch "branchName"]
	 */
	public static PluginConfigFactory generatePluginConfigurationBasedOnPluginConfigFile(
			String projectName,
			String branchName) throws Exception
	{
		PowerMockito.mockStatic(Project.NameKey.class);
		Project.NameKey projectKey = mock(Project.NameKey.class);
		when(Project.NameKey.parse(projectName)).thenReturn(projectKey);
		
		// Add a subsection that would be stored in the plugin config file
		Set<String> subsections = Sets.newSet(branchName);
		Config pluginConfig = mock(Config.class);
		when(pluginConfig.getSubsections(
				PluginConfigFileSnapshot.BRANCH_SECTION_CONFIG_NAME))
			.thenReturn(subsections);

		when(pluginConfig.getBoolean(
				PluginConfigFileSnapshot.BRANCH_SECTION_CONFIG_NAME, branchName, "enabled", false))
	    	.thenReturn(true);
		when(pluginConfig.getString(
				PluginConfigFileSnapshot.BRANCH_SECTION_CONFIG_NAME, branchName, "webhookurl"))
		    .thenReturn("https://<web-hook-url>");
		when(pluginConfig.getString(
				PluginConfigFileSnapshot.BRANCH_SECTION_CONFIG_NAME, branchName, "channel"))
		    .thenReturn("development");
		when(pluginConfig.getString(
				PluginConfigFileSnapshot.BRANCH_SECTION_CONFIG_NAME, branchName, "username"))
	    	.thenReturn("gerrit");
		when(pluginConfig.getString(
				PluginConfigFileSnapshot.BRANCH_SECTION_CONFIG_NAME, branchName, "ignore"))
			.thenReturn("^WIP.*");		
		
		PluginConfigFactory configFactory = mock(PluginConfigFactory.class);
		when(configFactory.getProjectPluginConfigWithInheritance(
				projectKey, ProjectConfigFileSnapshot.CONFIG_NAME))
			.thenReturn(pluginConfig);

		return configFactory;
	}
	
	public static PatchSetEvent generateMinimalPatchSetEvent(String projectName, String branchName)
	{
		PatchSetCreatedEvent event = new PatchSetCreatedEvent();
		event.change = new ChangeAttribute();
		event.uploader = new AccountAttribute();
		event.change.branch = branchName;
		event.change.commitMessage = "Some message";
		event.change.project = projectName;
		event.change.url = "http://some.gerrit.url";
		
		
/*		ChangeAttribute change = mock(ChangeAttribute.class);
		when(change.branch).thenReturn(branchName);

		when(change.commitMessage).thenReturn("Some message");
		when(change.project).thenReturn(projectName);
		when(change.url).thenReturn("http://some.gerrit.url");
		
		AccountAttribute account = mock(AccountAttribute.class);
		when(account.name).thenReturn("John Doe");
		
		PatchSetCreatedEvent event = mock(PatchSetCreatedEvent.class);
		when(event.change).thenReturn(change);
		when(event.uploader).thenReturn(account);
		*/
		return event;
	}
	
}
