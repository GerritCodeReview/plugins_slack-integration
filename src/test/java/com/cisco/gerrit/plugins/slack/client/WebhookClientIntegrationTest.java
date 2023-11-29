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

package com.cisco.gerrit.plugins.slack.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cisco.gerrit.plugins.slack.config.ProjectConfig;
import com.cisco.gerrit.plugins.slack.message.MessageTemplate;
import com.cisco.gerrit.plugins.slack.util.ResourceHelper;
import com.google.gerrit.entities.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Test;

public class WebhookClientIntegrationTest {
  private static final String PROJECT_NAME = "test-project";

  private PluginConfigFactory mockConfigFactory = mock(PluginConfigFactory.class);
  private PluginConfig mockPluginConfig = mock(PluginConfig.class);

  private ProjectConfig getConfig() throws Exception {
    Project.NameKey projectNameKey;
    projectNameKey = Project.NameKey.parse(PROJECT_NAME);

    // Setup mocks
    when(mockConfigFactory.getFromProjectConfigWithInheritance(
            projectNameKey, ProjectConfig.CONFIG_NAME))
        .thenReturn(mockPluginConfig);

    when(mockConfigFactory.getFromGerritConfig(ProjectConfig.CONFIG_NAME))
        .thenReturn(mockPluginConfig);

    // Internal proxy integration test config
    // when(mockPluginConfig.getString("proxy-host", null)).thenReturn("127.0.0.1");
    // when(mockPluginConfig.getInt("proxy-port", 8080)).thenReturn(8080);
    // when(mockPluginConfig.getString("proxy-username", null)).thenReturn("user");
    // when(mockPluginConfig.getString("proxy-password", null)).thenReturn("password");

    return new ProjectConfig(mockConfigFactory, PROJECT_NAME);
  }

  @Test
  public void canPublishMessage() throws Exception {
    Properties properties = new Properties();
    try (InputStream testProperties = ResourceHelper.loadNamedResourceAsStream("test.properties")) {
      properties.load(testProperties);
    }

    String webhookUrl = properties.getProperty("webhook-url");
    assumeNotNull(webhookUrl);

    WebhookClient client;
    client = new WebhookClient(getConfig());

    MessageTemplate template;
    template = new MessageTemplate();

    template.setChannel("general");
    template.setName("Integration Tester");
    template.setAction("proposed");
    template.setProject("project");
    template.setBranch("master");
    template.setUrl("http://gerrit/1234");
    template.setNumber(1234);
    template.setTitle("Adds a test commit message");

    assertTrue(client.publish(template.render(), webhookUrl));
  }

  @Test
  public void canPublishMessageWithLongMessage() throws Exception {
    WebhookClient client;
    client = new WebhookClient(getConfig());

    Properties properties = new Properties();
    try (InputStream testProperties = ResourceHelper.loadNamedResourceAsStream("test.properties")) {
      properties.load(testProperties);
    }

    String webhookUrl = properties.getProperty("webhook-url");
    assumeNotNull(webhookUrl);

    MessageTemplate template;
    template = new MessageTemplate();

    template.setChannel("general");
    template.setName("Integration Tester");
    template.setAction("commented on");
    template.setProject("project");
    template.setBranch("master");
    template.setUrl("http://gerrit/1234");
    template.setNumber(1234);
    template.setTitle("Adds a test commit message");
    template.setMessage(
        "It provides a bunch of really great things. "
            + "I am mostly trying to fill out a really long comment to "
            + "test message rendering. Slack should do the right thing "
            + "but this will be on multiple lines in IRC.\n\n\n\n\n"
            + "This is hidden.");

    assertTrue(client.publish(template.render(), webhookUrl));
  }
}
