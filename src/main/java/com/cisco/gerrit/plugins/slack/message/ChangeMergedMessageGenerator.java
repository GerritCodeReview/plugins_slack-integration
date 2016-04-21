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

import com.cisco.gerrit.plugins.slack.config.ProjectConfig;
import com.cisco.gerrit.plugins.slack.util.ResourceHelper;
import com.google.gerrit.server.events.ChangeMergedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specific MessageGenerator implementation that can generate a message for
 * a change merged event.
 *
 * @author Matthew Montgomery
 */
public class ChangeMergedMessageGenerator extends MessageGenerator
{
    /**
     * The class logger instance.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ChangeMergedMessageGenerator.class);

    private ProjectConfig config;
    private ChangeMergedEvent event;

    /**
     * Creates a new ChangeMergedMessageGenerator instance using the provided
     * ChangeMergedEvent instance.
     *
     * @param event The ChangeMergedEvent instance to generate a message for.
     */
    protected ChangeMergedMessageGenerator(ChangeMergedEvent event,
            ProjectConfig config)
    {
        if (event == null)
        {
            throw new NullPointerException("event cannot be null");
        }

        this.event = event;
        this.config = config;
    }

    @Override
    public boolean shouldPublish()
    {
        return config.isEnabled();
    }

    @Override
    public String generate()
    {
        String message = "";

        String whatHappened = String.format("%s (%s) merged patch set #%s of %s",
                escape(event.submitter.name),
                escape(event.submitter.username),
                escape(event.change.currentPatchSet.number),
                escape(event.change.url));
        String topic = "";
        if (event.change.topic != null && !event.change.topic.isEmpty()) {
            topic = String.format(" - (%s)", event.change.topic);
        }
        String attachmentTitle = String.format("%s - (%s)%s",
                escape(event.change.project),
                escape(event.change.branch),
                escape(topic));
        String attachmentValue = escape(event.change.commitMessage.split("\n")[0]);

        AttachmentMessage.Builder builder = new AttachmentMessage.Builder(config)
                .withPretext(whatHappened)
                .withColor(AttachmentMessage.Builder.COLOR_ERROR)
                .withAttachmentTitle(attachmentTitle)
                .withAttachmentValue(attachmentValue);

        try
        {
            message = builder.build().generate();
        }
        catch (Exception e)
        {
            LOGGER.error("Error generating message: " + e.getMessage(), e);
        }

        return message;
    }
}
