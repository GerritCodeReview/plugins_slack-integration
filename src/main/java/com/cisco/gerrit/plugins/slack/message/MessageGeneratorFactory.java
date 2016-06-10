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

package com.cisco.gerrit.plugins.slack.message;

import com.cisco.gerrit.plugins.slack.config.PluginConfigSnapshot;
import com.google.gerrit.server.events.ChangeMergedEvent;
import com.google.gerrit.server.events.CommentAddedEvent;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.gerrit.server.events.ReviewerAddedEvent;
import com.google.gerrit.server.events.PatchSetEvent;


/**
 * Factory used to create event specific MessageGenerator instances.
 *
 * @author Matthew Montgomery
 */
public class MessageGeneratorFactory
{
    // Made private to prevent instantiation
    private MessageGeneratorFactory() {}

    
    /**
     * Creates a new MessageGenerator for patch set created events.
     *
     * @param event A PatchSetEvent instance
     * @param config A ProjectConfigFileBasedSnapshot instance for the given event
     *
     * @return A MessageGenerator instance capable of generating a message for
     * a events. Supported events are: PatchSetCreated, CommentAdded, MergeCreated.
     */
    public static MessageGenerator newInstance(PatchSetEvent event,
            PluginConfigSnapshot config)
    {
    	MessageGenerator messageGenerator = null;
    	
    	if (event instanceof PatchSetCreatedEvent)
    	{	
    		messageGenerator =
    				new PatchSetCreatedMessageGenerator((PatchSetCreatedEvent)event, config);
    	}
    	else
    	if (event instanceof CommentAddedEvent)
    	{
    		messageGenerator =
    			new CommentAddedMessageGenerator((CommentAddedEvent)event, config);
    	}
    	else
    	if (event instanceof ChangeMergedEvent)
    	{
    		messageGenerator =
    			new ChangeMergedMessageGenerator((ChangeMergedEvent)event, config);
    	}
        else
        if (event instanceof ReviewerAddedEvent)
        {
            messageGenerator =
                    new ReviewerAddedMessageGenerator((ReviewerAddedEvent)event, config);
        }
    	else
    	{
    		messageGenerator = new UnsupportedMessageGenerator(event, config);
    	}
    	
    	return messageGenerator;
    }
}
