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
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A specific MessageGenerator implementation that can generate a message for a
 * patchset created event.
 *
 * @author Matthew Montgomery
 */
public class PatchSetCreatedMessageGenerator extends MessageGenerator
{
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PatchSetCreatedMessageGenerator.class);

    private PatchSetCreatedEvent event;
    private ProjectConfig config;

    /**
     * Creates a new PatchSetCreatedMessageGenerator instance using the
     * provided PatchSetCreatedEvent instance.
     *
     * @param event The PatchSetCreatedEvent instance to generate a
     *              message for.
     */
    protected PatchSetCreatedMessageGenerator(PatchSetCreatedEvent event,
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
        if(!config.isEnabled()) return false;

        boolean result;
        result = true;

        try
        {
            Pattern pattern;
            pattern = Pattern.compile(config.getIgnore(), Pattern.DOTALL);

            Matcher matcher;
            matcher = pattern.matcher(event.change.commitMessage);

            // If the ignore pattern matches, publishing should not happen
            result = !matcher.matches();
        }
        catch (Exception e)
        {
            LOGGER.warn("The specified ignore pattern was invalid", e);
        }

        return result;
    }

    @Override
    public String generate()
    {
        String message = "";
        String patchSet = "";

        if (event.patchSet != null) {
            boolean isNewPatchSet;
            try {
                isNewPatchSet = (Integer.parseInt(event.patchSet.number) > 1);
            } catch (NumberFormatException nfe) {
                isNewPatchSet = false;
            }
            if (isNewPatchSet) {
                patchSet = String.format("a new patch set (#%s) at ", event.patchSet.number);
            }
        }

        String whatHappened = String.format("%s (%s) proposed %s%s",
                escape(event.uploader.name),
                escape(event.uploader.username),
                escape(patchSet),
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
                .withColor(AttachmentMessage.Builder.COLOR_WARNING)
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
