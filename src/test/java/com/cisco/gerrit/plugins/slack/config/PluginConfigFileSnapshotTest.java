package com.cisco.gerrit.plugins.slack.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.hamcrest.core.Is.is;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.*;
import static org.junit.Assert.*;



@RunWith(PowerMockRunner.class)
@PrepareForTest({Project.NameKey.class})
public class PluginConfigFileSnapshotTest
{
	final private String SOME_GERRIT_PROJECT_NAME = "my-first-project";

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
	//	ignore = "^WIP.*"
	
	PluginConfigFactory generateConfigurationContainingOneBranchSection(String projectName, String branchName) throws Exception
	{
		return TestToolbox.generatePluginConfigurationBasedOnPluginConfigFile(projectName, branchName);
	}
	
	@Test
	public void enabledStateIsReturnedFromPluginConfig() throws Exception
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		PluginConfigFileSnapshot projectConfiguration =
				new PluginConfigFileSnapshot(configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertTrue(projectConfiguration.isEnabled());
	}
	
	@Test
	public void webhookUrlIsReturnedFromPluginConfig() throws Exception
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		PluginConfigFileSnapshot projectConfiguration =
				new PluginConfigFileSnapshot(configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertThat(projectConfiguration.getWebhookUrl(), is("https://<web-hook-url>"));
	}	
	
	@Test
	public void channelIsReturnedFromPluginConfig() throws Exception
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		PluginConfigFileSnapshot projectConfiguration =
				new PluginConfigFileSnapshot(configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertThat(projectConfiguration.getUsername(), is("gerrit"));
	}
	
	@Test
	public void gerritIsReturnedFromPluginConfig() throws Exception
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		PluginConfigFileSnapshot projectConfiguration =
				new PluginConfigFileSnapshot(configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertThat(projectConfiguration.getUsername(), is("gerrit"));
	}
	
	@Test
	public void ignoreIsReturnedFromPluginConfig() throws Exception
	{
		final String VALID_BRANCH_NAME = "refs/heads/master";
		PluginConfigFactory configurationFactory =
				generateConfigurationContainingOneBranchSection(SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);
		
		PluginConfigFileSnapshot projectConfiguration =
				new PluginConfigFileSnapshot(configurationFactory, SOME_GERRIT_PROJECT_NAME, VALID_BRANCH_NAME);

		assertThat(projectConfiguration.getIgnorePattern(), is("^WIP.*"));
	}
}
