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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cisco.gerrit.plugins.slack.config.ProjectConfig;
import com.google.common.base.Suppliers;
import com.google.gerrit.entities.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.data.AccountAttribute;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.gerrit.server.events.WorkInProgressStateChangedEvent;
import org.junit.Test;

/**
 * Tests for the WorkInProgressStateChangedGenerator class. The expected behavior is that the
 * WorkInProgressStateChangedGenerator should publish when the state changes.
 */
public class WorkInProgressStateChangedGeneratorTest {
  private static final String PROJECT_NAME = "test-project";

  private Project.NameKey mockNameKey = mock(Project.NameKey.class);

  private PluginConfigFactory mockConfigFactory = mock(PluginConfigFactory.class);

  private PluginConfig mockPluginConfig = mock(PluginConfig.class);

  private WorkInProgressStateChangedEvent mockEvent = mock(WorkInProgressStateChangedEvent.class);
  private AccountAttribute mockAccount = mock(AccountAttribute.class);
  private ChangeAttribute mockChange = mock(ChangeAttribute.class);

  private ProjectConfig getConfig(boolean publishOnWipReady) throws Exception {
    Project.NameKey projectNameKey;
    projectNameKey = Project.NameKey.parse(PROJECT_NAME);

    // Setup mocks
    when(mockConfigFactory.getFromProjectConfigWithInheritance(
            projectNameKey, ProjectConfig.CONFIG_NAME))
        .thenReturn(mockPluginConfig);

    when(mockConfigFactory.getFromGerritConfig(ProjectConfig.CONFIG_NAME))
        .thenReturn(mockPluginConfig);

    when(mockPluginConfig.getBoolean("enabled", false)).thenReturn(true);
    when(mockPluginConfig.getString("webhookurl", "")).thenReturn("https://webook/");
    when(mockPluginConfig.getString("channel", "general")).thenReturn("testchannel");
    when(mockPluginConfig.getString("username", "gerrit")).thenReturn("testuser");
    when(mockPluginConfig.getString("ignore", "")).thenReturn("^WIP.*");
    when(mockPluginConfig.getBoolean("publish-on-patch-set-created", true)).thenReturn(true);
    when(mockPluginConfig.getBoolean("publish-on-wip-ready", true)).thenReturn(publishOnWipReady);

    return new ProjectConfig(mockConfigFactory, PROJECT_NAME);
  }

  private ProjectConfig getConfig() throws Exception {
    return getConfig(true /* publishOnWipReady */);
  }

  @Test
  public void factoryCreatesExpectedType() throws Exception {
    ProjectConfig config = getConfig();
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator instanceof WorkInProgressStateChangedGenerator, is(true));
  }

  @Test
  public void publishesWhenExpected() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();
    mockEvent.change = Suppliers.ofInstance(mockChange);

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(true));
  }

  @Test
  public void doesNotPublishWhenTurnedOff() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig(false /* publishOnWipReady */);

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(false));
  }

  @Test
  public void doesNotPublishWhenWorkInProgress() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();
    mockEvent.change = Suppliers.ofInstance(mockChange);
    mockChange.wip = true;

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(false));
  }

  @Test
  public void generatesExpectedMessage() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();
    mockEvent.changer = Suppliers.ofInstance(mockAccount);
    mockEvent.change = Suppliers.ofInstance(mockChange);

    mockChange.number = 1234;
    mockChange.project = "testproject";
    mockChange.branch = "master";
    mockChange.url = "https://change/";
    mockChange.commitMessage = "This is the title\nThis is the message body.";

    mockAccount.name = "Unit Tester";

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    String expectedResult;
    expectedResult =
        "{\n"
            + "  \"channel\": \"#testchannel\",\n"
            + "  \"attachments\": [\n"
            + "    {\n"
            + "      \"fallback\": \"Unit Tester proposed testproject (master) https://change/: This is the title\",\n"
            + "      \"pretext\": \"Unit Tester proposed <https://change/|testproject (master) change 1234>\",\n"
            + "      \"title\": \"This is the title\",\n"
            + "      \"title_link\": \"https://change/\",\n"
            + "      \"text\": \"\",\n"
            + "      \"color\": \"good\"\n"
            + "    }\n"
            + "  ]\n"
            + "}\n";

    String actualResult;
    actualResult = messageGenerator.generate();

    assertThat(actualResult, is(equalTo(expectedResult)));
  }
}
