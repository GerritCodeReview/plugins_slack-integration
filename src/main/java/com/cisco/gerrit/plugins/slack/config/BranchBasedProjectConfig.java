package com.cisco.gerrit.plugins.slack.config;

import java.util.Set;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.project.NoSuchProjectException;

public class BranchBasedProjectConfig
{
	   /**
     * The class logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            ProjectConfig.class);

    /**
     * The name of the plugin config section to lookup within the gerrit.config
     * file.
     */
	public static final String PLUGIN_NAME = "slack-integration";

    private boolean enabled = false;
    private String webhookUrl = null;
    private String channel = null;
    private String username = null;
    private String ignore = null;

    /**
     * Creates a new instance of the ProjectConfig class for the given project.
     *
     * @param configFactory The Gerrit PluginConfigFactory instance to use.
     * @param gerritProjectName The project to use when looking up a configuration.
     * @param gitBranch The name of the git branch for which the configuration shall be provided.
     */
    public BranchBasedProjectConfig(PluginConfigFactory configFactory,
    		String gerritProjectName,
    		String gitBranch)
    {
        Project.NameKey gerritProjectKey;
        gerritProjectKey = Project.NameKey.parse(gerritProjectName);

        try
        {
        	final String BRANCH_SECTION_NAME = "branch"; 
        	Config pluginConfig = configFactory.getProjectPluginConfig(
            		gerritProjectKey, PLUGIN_NAME);
        	
            Set<String> subsections = configFactory.getProjectPluginConfig(
            		gerritProjectKey, PLUGIN_NAME).getSubsections(BRANCH_SECTION_NAME);
            
            if (subsections.contains(gitBranch))
            {
            	final boolean IRRELEVANT_VALUE = false;
            	enabled = pluginConfig.getBoolean(
            			BRANCH_SECTION_NAME, gitBranch, "enabled", IRRELEVANT_VALUE);
            	webhookUrl = pluginConfig.getString(
            			BRANCH_SECTION_NAME, gitBranch, "webhookurl");
            	channel = pluginConfig.getString(
            			BRANCH_SECTION_NAME, gitBranch, "channel");
            	username = pluginConfig.getString(
            			BRANCH_SECTION_NAME, gitBranch, "username");
            	ignore = pluginConfig.getString(
            			BRANCH_SECTION_NAME, gitBranch, "ignore");
            }
        }
        catch (NoSuchProjectException e)
        {
            LOGGER.warn("The specified project could not be found: " +
                    gerritProjectName);
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public String getWebhookUrl()
    {
        return webhookUrl;
    }

    public String getChannel()
    {
        return channel;
    }

    public String getUsername()
    {
        return username;
    }

    public String getIgnore()
    {
        return ignore;
    }
}
