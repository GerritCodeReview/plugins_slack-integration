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

package com.cisco.gerrit.plugins.slack.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResourceHelperTest {
  private static final String RESOURCE_NAME = "test.properties";
  private static final String NON_EXISTING_RESOURCE_NAME = "invalid.properties";

  @Test
  public void testLoadNamedResourceAsStream() throws Exception {
    assertNotNull(ResourceHelper.loadNamedResourceAsStream(RESOURCE_NAME));
  }

  @Test
  public void loadNullResourceAsStreamReturnsNull() throws Exception {
    assertNull(ResourceHelper.loadNamedResourceAsStream(null));
  }

  @Test
  public void loadNonExistingNamedResourceAsStreamReturnsNull() throws Exception {
    assertNull(ResourceHelper.loadNamedResourceAsStream(NON_EXISTING_RESOURCE_NAME));
  }

  @Test
  public void testLoadNamedResourceAsString() throws Exception {
    String resource = ResourceHelper.loadNamedResourceAsString(RESOURCE_NAME);

    assertNotNull(resource);
    assertTrue(resource.length() > 0);
  }

  @Test
  public void loadNullNamedResourceAsStringReturnsNull() throws Exception {
    assertNull(ResourceHelper.loadNamedResourceAsString(null));
  }

  @Test
  public void loadNonExistingNamedResourceAsStringReturnsNull() throws Exception {
    assertNull(ResourceHelper.loadNamedResourceAsString(NON_EXISTING_RESOURCE_NAME));
  }
}
