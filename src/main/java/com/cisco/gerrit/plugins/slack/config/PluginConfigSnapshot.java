package com.cisco.gerrit.plugins.slack.config;

public interface PluginConfigSnapshot {

	public boolean isEnabled();
	public String getWebhookUrl();
	public String getChannel();
	public String getUsername();
	public String getIgnorePattern();
	public boolean shouldPublishOnChangeMerged();
	public boolean shouldPublishOnCommentAdded();
	public boolean shouldPublishOnPatchSetCreated();
	public boolean shouldPublishOnReviewerAdded();
}
