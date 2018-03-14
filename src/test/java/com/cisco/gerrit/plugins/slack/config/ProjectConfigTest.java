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

package com.cisco.gerrit.plugins.slack.config;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/** Tests for the PluginConfig class. */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Project.NameKey.class})
public class ProjectConfigTest {
  private static final String PROJECT_NAME = "test-project";

  private Project.NameKey mockNameKey = mock(Project.NameKey.class);

  private PluginConfigFactory mockConfigFactory = mock(PluginConfigFactory.class);

  private PluginConfig mockPluginConfig = mock(PluginConfig.class);

  private ProjectConfig config;

  @Before
  public void setup() throws Exception {
    PowerMockito.mockStatic(Project.NameKey.class);
    when(Project.NameKey.parse(PROJECT_NAME)).thenReturn(mockNameKey);

    Project.NameKey projectNameKey;
    projectNameKey = Project.NameKey.parse(PROJECT_NAME);

    // Setup mocks
    when(mockConfigFactory.getFromProjectConfigWithInheritance(
            projectNameKey, ProjectConfig.CONFIG_NAME))
        .thenReturn(mockPluginConfig);

    when(mockPluginConfig.getBoolean("enabled", false)).thenReturn(true);
    when(mockPluginConfig.getString("webhookurl", "")).thenReturn("https://webook/");
    when(mockPluginConfig.getString("channel", "general")).thenReturn("test-channel");
    when(mockPluginConfig.getString("username", "gerrit")).thenReturn("test-user");
    when(mockPluginConfig.getString("ignore", "")).thenReturn("^WIP.*");
    when(mockPluginConfig.getBoolean("publish-on-patch-set-created", true)).thenReturn(true);
    when(mockPluginConfig.getBoolean("publish-on-change-merged", true)).thenReturn(true);
    when(mockPluginConfig.getBoolean("publish-on-comment-added", true)).thenReturn(true);
    when(mockPluginConfig.getBoolean("publish-on-reviewer-added", true)).thenReturn(true);

    config = new ProjectConfig(mockConfigFactory, PROJECT_NAME);
  }

  @Test
  public void testIsEnabled() throws Exception {
    assertTrue(config.isEnabled());
  }

  @Test
  public void testGetWebhookUrl() throws Exception {
    assertThat(config.getWebhookUrl(), is(equalTo("https://webook/")));
  }

  @Test
  public void testGetChannel() throws Exception {
    assertThat(config.getChannel(), is(equalTo("test-channel")));
  }

  @Test
  public void testGetUsername() throws Exception {
    assertThat(config.getUsername(), is(equalTo("test-user")));
  }

  @Test
  public void testShouldPublishOnPatchSetCreated() throws Exception {
    assertThat(config.shouldPublishOnPatchSetCreated(), is(equalTo(true)));
  }

  @Test
  public void testShouldPublishOnChangeMerged() throws Exception {
    assertThat(config.shouldPublishOnChangeMerged(), is(equalTo(true)));
  }

  @Test
  public void testShouldPublishOnCommentAdded() throws Exception {
    assertThat(config.shouldPublishOnCommentAdded(), is(equalTo(true)));
  }

  @Test
  public void testShouldPublishOnReviewerAdded() throws Exception {
    assertThat(config.shouldPublishOnReviewerAdded(), is(equalTo(true)));
  }
}
