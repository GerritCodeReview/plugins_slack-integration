/*
 * Copyright 2017 Cisco Systems, Inc.
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

import static org.apache.commons.lang3.StringUtils.substringBefore;

import com.cisco.gerrit.plugins.slack.config.ProjectConfig;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.gerrit.server.events.CommentAddedEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specific MessageGenerator implementation that can generate a message for a comment added event.
 *
 * @author Kenneth Pedersen
 * @author Matthew Montgomery
 */
public class CommentAddedMessageGenerator implements MessageGenerator {
  /** The class logger instance. */
  private static final Logger LOGGER = LoggerFactory.getLogger(CommentAddedMessageGenerator.class);

  private ProjectConfig config;
  private CommentAddedEvent event;

  /**
   * Creates a new CommentAddedMessageGenerator instance using the provided CommentAddedEvent
   * instance.
   *
   * @param event The CommentAddedEvent instance to generate a message for.
   */
  CommentAddedMessageGenerator(CommentAddedEvent event, ProjectConfig config) {
    if (event == null) {
      throw new NullPointerException("event cannot be null");
    }

    this.event = event;
    this.config = config;
  }

  @Override
  public boolean shouldPublish() {
    if (!config.isEnabled() || !config.shouldPublishOnCommentAdded()) {
      return false;
    }

    try {
      ChangeAttribute change;
      change = event.change.get();
      if (config.getIgnorePrivatePatchSet() && Boolean.TRUE.equals(change.isPrivate)) {
        return false;
      }
      if (config.getIgnoreWorkInProgressPatchSet() && Boolean.TRUE.equals(change.wip)) {
        return false;
      }
    } catch (Exception e) {
      LOGGER.warn("Error checking private and work-in-progress status", e);
    }

    boolean result;
    result = true;

    try {
      Pattern pattern;
      pattern = Pattern.compile(config.getIgnoreCommentAuthor(), Pattern.DOTALL);

      Matcher matcher;
      matcher = pattern.matcher(event.author.get().username);

      // If the ignore pattern matches, publishing should not happen
      result = !matcher.matches();
    } catch (Exception e) {
      LOGGER.warn("The specified ignore-comment-author pattern was invalid", e);
    }

    return result;
  }

  @Override
  public String generate() {
    String message;
    message = "";

    LOGGER.info(substringBefore(event.change.get().commitMessage, "\n"));
    LOGGER.info(event.comment);

    try {
      MessageTemplate template;
      template = new MessageTemplate();

      template.setChannel(config.getChannel());
      template.setName(event.author.get().name);
      template.setAction("commented on");
      template.setProject(event.change.get().project);
      template.setBranch(event.change.get().branch);
      template.setUrl(event.change.get().url);
      template.setNumber(event.change.get().number);
      template.setTitle(substringBefore(event.change.get().commitMessage, "\n"));
      template.setMessage(event.comment);

      message = template.render();
    } catch (Exception e) {
      LOGGER.error("Error generating message: " + e.getMessage(), e);
    }

    return message;
  }
}
