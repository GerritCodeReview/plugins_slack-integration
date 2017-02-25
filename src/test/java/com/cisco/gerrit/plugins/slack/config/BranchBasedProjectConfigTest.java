package com.cisco.gerrit.plugins.slack.config;

import org.eclipse.jgit.lib.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.Is.is;
import java.util.Set;

import static org.mockito.Mockito.mock;

import com.google.common.collect.Sets;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.*;
import com.google.gerrit.server.project.NoSuchProjectException;

import static org.junit.Assert.*;



@RunWith(PowerMockRunner.class)
@PrepareForTest({Project.NameKey.class})
public class BranchBasedProjectConfigTest
{
	final private String SOME_GERRIT_PROJECT_NAME = "my-first-project";
	final private String BRANCH_SECTION = "branch";
	final String VALID_BRANCH_NAME = "refs/heads/master";

	// Project configuration as it would be in slack-integration.config
	//
	//     .------------------------------------- section
	//     v           v------------------------- subsection
	// [branch "refs/heads/master"]
	//	enabled = true                       <--- name
	//	webhookurl = https://<web-hook-url>
	//	channel = development
	//	user = gerrit
	//	ignore = "^WIP.*"
	//
	// [branch "refs/heads/maintenance"]
	//	enabled = false
	//	webhookurl = https://<another-web-hook-url>
	//	channel = maintenance
	//	user = mightygerrit
	//	ignore = ".*"
	
	Set<String> generateTwoBranchNames()
	{
		final String FIRST_SUBSECTION = "refs/heads/master";
		//final String SECOND_SUBSECTION = "refs/heads/maintenance";
		
		return Sets.newHashSet(FIRST_SUBSECTION);//, SECOND_SUBSECTION);
	}
	
	public PluginConfigFactory generateConfigurationContainingOneBranchSection(String gerritProject, String branchName) throws NoSuchProjectException
	{
		PluginConfigFactory pluginConfigFactory = mock(PluginConfigFactory.class);

		Config pluginConfig = mock(Config.class);
		when(pluginConfig.getSubsections(BRANCH_SECTION))
			.thenReturn(generateTwoBranchNames());
		when(pluginConfig.getBoolean(BRANCH_SECTION, branchName, "enabled", false))
        	.thenReturn(true);
		when(pluginConfig.getString(BRANCH_SECTION, branchName, "webhookurl"))
		    .thenReturn("https://<web-hook-url>");
		when(pluginConfig.getString(BRANCH_SECTION, branchName, "channel"))
		    .thenReturn("development");
		when(pluginConfig.getString(BRANCH_SECTION, branchName, "username"))
        	.thenReturn("gerrit");
		when(pluginConfig.getString(BRANCH_SECTION, branchName, "ignore"))
			.thenReturn("^WIP.*");
		
		Project.NameKey gerritProjectKey = mock(Project.NameKey.class);
		PowerMockito.mockStatic(Project.NameKey.class);
		when(Project.NameKey.parse(gerritProject)).thenReturn(gerritProjectKey);

		when(pluginConfigFactory.getProjectPluginConfig(
				gerritProjectKey, BranchBasedProjectConfig.PLUGIN_NAME))
			.thenReturn(pluginConfig);
		
		return pluginConfigFactory;
	}
	
	@Test
	public void enabledStateIsReturnedFromPluginConfig() throws NoSuchProjectException
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		BranchBasedProjectConfig projectConfiguration =
				new BranchBasedProjectConfig(
						configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertTrue(projectConfiguration.isEnabled());
	}
	
	@Test
	public void webhookUrlIsReturnedFromPluginConfig() throws NoSuchProjectException
	{
		
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		BranchBasedProjectConfig projectConfiguration =
				new BranchBasedProjectConfig(
						configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertThat(projectConfiguration.getWebhookUrl(), is("https://<web-hook-url>"));
	}	
	
	@Test
	public void channelIsReturnedFromPluginConfig() throws NoSuchProjectException
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		BranchBasedProjectConfig projectConfiguration =
				new BranchBasedProjectConfig(
						configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertEquals("gerrit", projectConfiguration.getUsername());
	}
	
	@Test
	public void gerritIsReturnedFromPluginConfig() throws NoSuchProjectException
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		BranchBasedProjectConfig projectConfiguration =
				new BranchBasedProjectConfig(
						configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertEquals("gerrit", projectConfiguration.getUsername());
	}
	
	@Test
	public void ignoreIsReturnedFromPluginConfig() throws NoSuchProjectException
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		BranchBasedProjectConfig projectConfiguration =
				new BranchBasedProjectConfig(
						configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertEquals("^WIP.*", projectConfiguration.getIgnore());
	}	
}
