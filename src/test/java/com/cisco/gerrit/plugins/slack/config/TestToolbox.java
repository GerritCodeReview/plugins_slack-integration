package com.cisco.gerrit.plugins.slack.config;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Set;

import org.powermock.api.mockito.PowerMockito;
import org.eclipse.jgit.lib.Config;
import org.mockito.internal.util.collections.Sets;

import com.google.common.base.Suppliers;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.Branch;
import com.google.gerrit.reviewdb.client.Change;
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
        Project.NameKey projectKey = Project.NameKey.parse(projectName);

        PowerMockito.mockStatic(Change.Key.class);
        Change.Key changeKey = mock(Change.Key.class);
        PowerMockito.mockStatic(Change.Id.class);
        Change.Id changeId = mock(Change.Id.class);
        PowerMockito.mockStatic(Account.Id.class);
        Account.Id accountId = mock(Account.Id.class);

	    Branch.NameKey branchKey = new Branch.NameKey(projectKey, branchName);
	    Timestamp timestamp = new Timestamp(12);
	    Change change = new Change(changeKey, changeId, accountId, branchKey, timestamp);
		PatchSetCreatedEvent event = new PatchSetCreatedEvent(change);
		event.change = Suppliers.ofInstance(new ChangeAttribute());
		event.uploader = Suppliers.ofInstance(new AccountAttribute());
		event.change.get().branch = branchName;
		event.change.get().commitMessage = "Some message";
		event.change.get().project = projectName;
		event.change.get().url = "http://some.gerrit.url";

		return event;
	}
	
}
