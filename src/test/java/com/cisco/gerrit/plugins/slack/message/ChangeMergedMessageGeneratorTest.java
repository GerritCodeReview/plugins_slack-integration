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
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.data.AccountAttribute;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.gerrit.server.events.ChangeMergedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests for the ChangeMergedMessageGeneratorTest class. The expected behavior is that the
 * ChangeMergedMessageGenerator should publish regardless of a configured ignore pattern.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Project.NameKey.class})
public class ChangeMergedMessageGeneratorTest {
  private static final String PROJECT_NAME = "test-project";

  private Project.NameKey mockNameKey = mock(Project.NameKey.class);

  private PluginConfigFactory mockConfigFactory = mock(PluginConfigFactory.class);

  private PluginConfig mockPluginConfig = mock(PluginConfig.class);

  private ChangeMergedEvent mockEvent = mock(ChangeMergedEvent.class);
  private AccountAttribute mockAccount = mock(AccountAttribute.class);
  private ChangeAttribute mockChange = mock(ChangeAttribute.class);

  @Before
  public void setup() throws Exception {
    PowerMockito.mockStatic(Project.NameKey.class);
    when(Project.NameKey.parse(PROJECT_NAME)).thenReturn(mockNameKey);
  }

  private ProjectConfig getConfig(boolean publishOnChangeMerged) throws Exception {
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
    when(mockPluginConfig.getBoolean("publish-on-change-merged", true))
        .thenReturn(publishOnChangeMerged);

    return new ProjectConfig(mockConfigFactory, PROJECT_NAME);
  }

  private ProjectConfig getConfig() throws Exception {
    return getConfig(true /* publishOnChangeMerged */);
  }

  @Test
  public void factoryCreatesExpectedType() throws Exception {
    ProjectConfig config = getConfig();
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator instanceof ChangeMergedMessageGenerator, is(true));
  }

  @Test
  public void publishesWhenExpected() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();
    mockEvent.change = Suppliers.ofInstance(mockChange);
    mockChange.commitMessage = "This is a title\nand a the body.";

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(true));
  }

  @Test
  public void publishesWhenMessageMatchesIgnore() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();
    mockEvent.change = Suppliers.ofInstance(mockChange);
    mockChange.commitMessage = "WIP:This is a title\nand a the body.";

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(true));
  }

  @Test
  public void doesNotPublishWhenTurnedOff() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig(false /* publishOnChangeMerged */);
    mockEvent.change = Suppliers.ofInstance(mockChange);
    mockChange.commitMessage = "This is a title\nand a the body.";

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(false));
  }

  @Test
  public void handlesInvalidIgnorePatterns() throws Exception {
    ProjectConfig config = getConfig();
    when(mockPluginConfig.getString("ignore", "")).thenReturn(null);

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(true));
  }

  @Test
  public void publishesWhenWorkInProgress() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();
    mockEvent.change = Suppliers.ofInstance(mockChange);
    mockChange.wip = true;

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(true));
  }

  @Test
  public void publishesWhenPrivate() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();
    mockEvent.change = Suppliers.ofInstance(mockChange);
    mockChange.isPrivate = true;

    // Test
    MessageGenerator messageGenerator;
    messageGenerator = MessageGeneratorFactory.newInstance(mockEvent, config);

    assertThat(messageGenerator.shouldPublish(), is(true));
  }

  @Test
  public void generatesExpectedMessage() throws Exception {
    // Setup mocks
    ProjectConfig config = getConfig();

    mockEvent.change = Suppliers.ofInstance(mockChange);
    mockEvent.submitter = Suppliers.ofInstance(mockAccount);

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
            + "      \"fallback\": \"Unit Tester merged testproject (master) https://change/: This is the title\",\n"
            + "      \"pretext\": \"Unit Tester merged <https://change/|testproject (master) change 1234>\",\n"
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
