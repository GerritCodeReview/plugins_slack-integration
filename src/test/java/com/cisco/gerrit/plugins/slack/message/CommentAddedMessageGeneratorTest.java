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

import com.google.common.base.Suppliers;
import com.cisco.gerrit.plugins.slack.config.PluginConfigSnapshot;
import com.cisco.gerrit.plugins.slack.config.ProjectConfigFileSnapshot;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.data.AccountAttribute;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.gerrit.server.events.CommentAddedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the CommentAddedMessageGeneratorTest class. The expected behavior
 * is that the CommentAddedMessageGeneratorTest  should publish regardless of a
 * configured ignore pattern.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Project.NameKey.class})
public class CommentAddedMessageGeneratorTest
{
    private static final String PROJECT_NAME = "test-project";

    private Project.NameKey mockNameKey =
            mock(Project.NameKey.class);

    private PluginConfigFactory mockConfigFactory =
            mock(PluginConfigFactory.class);

    private PluginConfig mockPluginConfig =
            mock(PluginConfig.class);

    private CommentAddedEvent mockEvent = mock(CommentAddedEvent.class);
    private AccountAttribute mockAccount = mock(AccountAttribute.class);
    private ChangeAttribute mockChange = mock(ChangeAttribute.class);
    private AccountAttribute mockOwner = mock(AccountAttribute.class);

    @Before
    public void setup() throws Exception
    {
        PowerMockito.mockStatic(Project.NameKey.class);
        when(Project.NameKey.parse(PROJECT_NAME)).thenReturn(mockNameKey);
    }

    private PluginConfigSnapshot getConfig(boolean publishOnCommentAdded) throws Exception
    {
        Project.NameKey projectNameKey;
        projectNameKey = Project.NameKey.parse(PROJECT_NAME);

        // Setup mocks
        when(mockConfigFactory.getFromProjectConfigWithInheritance(
                projectNameKey, ProjectConfigFileSnapshot.CONFIG_NAME))
                .thenReturn(mockPluginConfig);

        when(mockPluginConfig.getBoolean("enabled", false))
                .thenReturn(true);
        when(mockPluginConfig.getString("webhookurl", ""))
                .thenReturn("https://webook/");
        when(mockPluginConfig.getString("channel", "general"))
                .thenReturn("testchannel");
        when(mockPluginConfig.getString("username", "gerrit"))
                .thenReturn("testuser");
        when(mockPluginConfig.getString("ignore", ""))
                .thenReturn("^WIP.*");
        when(mockPluginConfig.getBoolean("publish-on-comment-added", true))
                .thenReturn(publishOnCommentAdded);

        return new ProjectConfigFileSnapshot(mockConfigFactory, PROJECT_NAME);
    }

    private PluginConfigSnapshot getConfig() throws Exception
    {
        return getConfig(true /* publishOnCommentAdded */);
    }

    @Test
    public void factoryCreatesExpectedType() throws Exception
    {
        PluginConfigSnapshot config = getConfig();
        MessageGenerator messageGenerator;
        messageGenerator = MessageGeneratorFactory.newInstance(
                mockEvent, config);

        assertThat(messageGenerator instanceof CommentAddedMessageGenerator,
                is(true));
    }

    @Test
    public void publishesWhenExpected() throws Exception
    {
        // Setup mocks
        PluginConfigSnapshot config = getConfig();
        mockEvent.comment = "This is a title\nAnd a the body.";

        // Test
        MessageGenerator messageGenerator;
        messageGenerator = MessageGeneratorFactory.newInstance(
                mockEvent, config);

        assertThat(messageGenerator.shouldPublish(), is(true));
    }

    @Test
    public void publishesWhenMessageMatchesIgnore() throws Exception
    {
        // Setup mocks
        PluginConfigSnapshot config = getConfig();
        mockEvent.comment = "WIP:This is a title\nAnd a the body.";

        // Test
        MessageGenerator messageGenerator;
        messageGenerator = MessageGeneratorFactory.newInstance(
                mockEvent, config);

        assertThat(messageGenerator.shouldPublish(), is(true));
    }

    @Test
    public void doesNotPublishWhenTurnedOff() throws Exception
    {
        // Setup mocks
        PluginConfigSnapshot config = getConfig(false /* publishOnCommentAdded */);
        mockEvent.comment = "This is a title\nAnd a the body.";

        // Test
        MessageGenerator messageGenerator;
        messageGenerator = MessageGeneratorFactory.newInstance(
                mockEvent, config);

        assertThat(messageGenerator.shouldPublish(), is(false));
    }

    @Test
    public void handlesInvalidIgnorePatterns() throws Exception
    {
        PluginConfigSnapshot config = getConfig();
        when(mockPluginConfig.getString("ignore", ""))
                .thenReturn(null);

        // Test
        MessageGenerator messageGenerator;
        messageGenerator = MessageGeneratorFactory.newInstance(
                mockEvent, config);

        assertThat(messageGenerator.shouldPublish(), is(true));
    }

    @Test
    public void generatesExpectedMessage() throws Exception
    {
        // Setup mocks
        PluginConfigSnapshot config = getConfig();
        mockEvent.change = Suppliers.ofInstance(mockChange);
        mockEvent.author = Suppliers.ofInstance(mockAccount);

        mockEvent.comment = "This is the first line\nAnd the second line.";

        mockChange.project = "testproject";
        mockChange.branch = "master";
        mockChange.url = "https://change/";
        mockChange.owner = mockOwner;
        mockOwner.name = "Owner";

        mockAccount.name = "Unit Tester";

        // Test
        MessageGenerator messageGenerator;
        messageGenerator = MessageGeneratorFactory.newInstance(
                mockEvent, config);

        String expectedResult;
        expectedResult = "{\"text\": \"Unit Tester commented to Owner\\n>>>" +
                "testproject (master): This is the first line\n" +
                "And the second line. (https://change/)\"," +
                "\"channel\": \"#testchannel\",\"username\": \"testuser\"}\n";

        String actualResult;
        actualResult = messageGenerator.generate();

        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void generatesExpectedMessageForLongComment() throws Exception
    {
        // Setup mocks
        PluginConfigSnapshot config = getConfig();
        mockEvent.change = Suppliers.ofInstance(mockChange);
        mockEvent.author = Suppliers.ofInstance(mockAccount);

        mockEvent.comment = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Integer tristique ligula nec dapibus lobortis. Nulla venenatis, lacus quis vulputate volutpat, " +
                "sem neque ornare eros, vel sodales magna risus et diam. Maecenas ultricies justo dictum orci " +
                "scelerisque consequat a vel purus.";

        mockChange.project = "testproject";
        mockChange.branch = "master";
        mockChange.url = "https://change/";
        mockChange.owner = mockOwner;
        mockOwner.name = "Owner";

        mockAccount.name = "Unit Tester";

        // Test
        MessageGenerator messageGenerator;
        messageGenerator = MessageGeneratorFactory.newInstance(
                mockEvent, config);

        String expectedResult;
        expectedResult = "{\"text\": \"Unit Tester commented to Owner\\n>>>" +
                "testproject (master): " + mockEvent.comment.substring(0, 197) + "... (https://change/)\"," +
                "\"channel\": \"#testchannel\",\"username\": \"testuser\"}\n";

        String actualResult;
        actualResult = messageGenerator.generate();

        assertThat(actualResult, is(equalTo(expectedResult)));
    }

}
