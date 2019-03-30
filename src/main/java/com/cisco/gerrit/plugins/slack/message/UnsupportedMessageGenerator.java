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

import com.google.gerrit.server.events.Event;

/**
 * A specific MessageGenerator implementation that can generate a message for an unsupported Event.
 * The default behavior for this MessageGenerator is to flag that it should not be published.
 *
 * @author Matthew Montgomery
 */
public class UnsupportedMessageGenerator implements MessageGenerator {
  private Event event;

  UnsupportedMessageGenerator(Event event) {
    if (event == null) {
      throw new NullPointerException("event cannot be null");
    }

    this.event = event;
  }

  @Override
  public boolean shouldPublish() {
    return false;
  }

  @Override
  public String generate() {
    StringBuilder message;
    message = new StringBuilder();

    message.append("Unsupported event: ");
    message.append(this.event.toString());

    return message.toString();
  }
}
