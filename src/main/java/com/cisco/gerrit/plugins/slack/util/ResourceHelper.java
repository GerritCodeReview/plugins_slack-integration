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

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Simple helper class to load resources via the current classloader. */
public final class ResourceHelper {
  // Made private to prevent instantiation.
  private ResourceHelper() {}

  /**
   * Loads the named resource as an InputStream from the current classloader.
   *
   * @param name The named resource.
   * @return The named resource as an InputStream, null if not found.
   * @throws IOException In the event of an IO error
   */
  public static InputStream loadNamedResourceAsStream(String name) throws IOException {
    return name != null ? ResourceHelper.class.getClassLoader().getResourceAsStream(name) : null;
  }

  /**
   * Loads the named resource as a String from the current classloader.
   *
   * @param name The named resource.
   * @return The named resource as a String, null if not found.
   * @throws IOException In the event of an IO error
   */
  public static String loadNamedResourceAsString(String name) throws IOException {
    InputStream inputStream = ResourceHelper.loadNamedResourceAsStream(name);
    return inputStream != null
        ? CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        : null;
  }
}
