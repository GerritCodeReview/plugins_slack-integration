package com.cisco.gerrit.plugins.slack.config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfigFactory;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Project.NameKey.class})
public class PluginConfigSnapshotProviderTest
{
	@Test
	public void providesConfigurationFromProjectConfigFileInCasePluginConfigFileIsNotAvailable() throws Exception
	{
		final String VALID_PROJECT = "my-first-project";
		final String SOME_BRANCH = "somebranch";
		
		PluginConfigFactory configFactory =
				TestToolbox.generatePluginConfigurationBasedOnProjectConfigFile(VALID_PROJECT);
		
		PluginConfigSnapshot pluginConfig =
				PluginConfigSnapshotProvider.createSnapshot(
						configFactory, VALID_PROJECT, SOME_BRANCH);
		assertNotNull((ProjectConfigFileBasedSnapshot) pluginConfig);
	}
	
	@Test
	public void providesConfigurationFromPluginConfigFileIfPresent() throws Exception
	{
		final String VALID_PROJECT_NAME = "my-first-project";
		final String VALID_BRANCH_NAME = "master";
		
		PluginConfigFactory configFactory =
				TestToolbox.generatePluginConfigurationBasedOnPluginConfigFile(VALID_PROJECT_NAME, VALID_BRANCH_NAME);
		
		PluginConfigSnapshot pluginConfig =
				PluginConfigSnapshotProvider.createSnapshot(
						configFactory, VALID_PROJECT_NAME, VALID_BRANCH_NAME);
		
		assertNotNull((PluginConfigFileBasedSnapshot)pluginConfig);
	}
}
