/*
 * Copyright 2016 Cisco Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

package com.cisco.gerrit.plugins.slack;

import com.cisco.gerrit.plugins.slack.client.WebhookClient;
import com.cisco.gerrit.plugins.slack.config.PluginConfigSnapshot;
import com.cisco.gerrit.plugins.slack.config.PluginConfigSnapshotProvider;
import com.cisco.gerrit.plugins.slack.message.MessageGenerator;
import com.cisco.gerrit.plugins.slack.message.MessageGeneratorFactory;
import com.google.gerrit.common.EventListener;
import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.events.Event;
import com.google.gerrit.server.events.PatchSetEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for Gerrit change events and publishes messages to Slack.
 */
@Listen
@Singleton
public class PublishEventListener implements EventListener
{
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PublishEventListener.class);

    private PluginConfigFactory configFactory;
    private WebhookClient webhookClient;
    
    @Inject
    public PublishEventListener(PluginConfigFactory configFactory)
    {
    	this.configFactory = configFactory;
    	this.webhookClient = new WebhookClient();
    }
    
    public PublishEventListener(PluginConfigFactory configFactory,
    		WebhookClient webhookClient)
    {
    	this.configFactory = configFactory;
    	this.webhookClient = webhookClient;
    }    

    @Override
    public void onEvent(Event event)
    {
        try
        {
        	if (event instanceof PatchSetEvent)
        	{
            	PatchSetEvent patchSetEvent = (PatchSetEvent) event;
            	
	            PluginConfigSnapshot pluginConfig = 
	            		PluginConfigSnapshotProvider.createSnapshot(
	            				configFactory,
	            				patchSetEvent.change.get().project,
	            				patchSetEvent.change.get().branch);
	
	            MessageGenerator messageGenerator =
	            		MessageGeneratorFactory.newInstance(patchSetEvent, pluginConfig);

	            if (messageGenerator.shouldPublish())
	            {
	                webhookClient.publish(messageGenerator.generate(),
	                		pluginConfig.getWebhookUrl());
	            }
        	}
        }
        catch (Throwable e)
        {
            LOGGER.error("Event " + event + " processing failed", e);
        }
    }
}
