package com.cisco.gerrit.plugins.slack.config;
import java.util.Set;

import org.eclipse.jgit.lib.Config;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfigFactory;

public class PluginConfigSnapshotProvider
{
	public static PluginConfigSnapshot createSnapshot(
			PluginConfigFactory configFactory,
			String projectName,
			String branchName) throws Exception
	{
		PluginConfigSnapshot pluginConfigSnapshot = null;
		
		Project.NameKey projectKey = Project.NameKey.parse(projectName);
		Config pluginConfig =
				configFactory.getProjectPluginConfigWithInheritance(
				        projectKey, ProjectConfigFileSnapshot.CONFIG_NAME);
		
		Set<String> subsections = pluginConfig.getSubsections(
				PluginConfigFileSnapshot.BRANCH_SECTION_CONFIG_NAME);
		if (!subsections.isEmpty())
		{
			pluginConfigSnapshot = new PluginConfigFileSnapshot(configFactory, projectName, branchName);
		}
		else
		{
			pluginConfigSnapshot = new ProjectConfigFileSnapshot(configFactory, projectName);
		}
		return pluginConfigSnapshot; 
	}
}
