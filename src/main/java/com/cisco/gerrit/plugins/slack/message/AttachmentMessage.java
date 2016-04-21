/*
 * Copyright 2016 Alexander Martinz <alexander.martinz.ofs@gmail.com>
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

import java.io.IOException;

/**
 * Represents an attachment message and provides a {@link Builder}
 * to generate new messages easily and with a nice API.
 */
public class AttachmentMessage {
    public final ProjectConfig config;

    public String fallback;
    public String pretext;
    public String color;

    public String attachmentTitle;
    public String attachmentValue;

    private AttachmentMessage(ProjectConfig config) {
        this.config = config;
    }

    public String generate() throws IOException {
        String template = ResourceHelper.loadNamedResourceAsString(
                "attachment-message-template.json");

        String channel = config.getChannel();
        // we can specify channels like #general or users like @johndoe
        if (!channel.startsWith("#") || !channel.startsWith("@")) {
            // if the config specified the channel without # or any prefix,
            // help a bit and default to a channel
            channel = String.format("#%s", channel);
        }
        return String.format(template,
                config.getUsername(), channel,
                fallback, pretext, color,
                attachmentTitle, attachmentValue);
    }

    public static class Builder {
        public static final String COLOR_GOOD = "good";
        public static final String COLOR_WARNING = "warning";
        public static final String COLOR_ERROR = "error";

        private final AttachmentMessage attachmentMessage;

        public Builder(ProjectConfig config) {
            attachmentMessage = new AttachmentMessage(config);
        }

        public Builder withFallback(String fallback) {
            attachmentMessage.fallback = fallback;
            return this;
        }

        public Builder withPretext(String pretext) {
            attachmentMessage.pretext = pretext;
            return this;
        }

        public Builder withColor(String hexColorString) {
            attachmentMessage.color = hexColorString;
            return this;
        }

        public Builder withAttachmentTitle(String attachmentTitle) {
            attachmentMessage.attachmentTitle = attachmentTitle;
            return this;
        }

        public Builder withAttachmentValue(String attachmentValue) {
            attachmentMessage.attachmentValue = attachmentValue;
            return this;
        }

        public AttachmentMessage build() {
            // fallback needs to be specified
            if (attachmentMessage.fallback == null || attachmentMessage.fallback.isEmpty()) {
                attachmentMessage.fallback = attachmentMessage.pretext;
            }
            return attachmentMessage;
        }
    }
}
