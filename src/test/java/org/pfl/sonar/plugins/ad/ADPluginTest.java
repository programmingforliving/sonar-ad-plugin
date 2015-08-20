/**
 * Copyright (C) 2013 Jiji Sasidharan (http://www.programmingforliving.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pfl.sonar.plugins.ad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

/**
 * Test case for {@link ADPlugin}
 *
 * @author Jiji Sasidharan
 */
public class ADPluginTest {

    /**
     * Check the plugin is configured properly.
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void testExtensionList() {
        List extensionList = new ADPlugin().getExtensions();
        assertNotNull("Plugin Extension list shouldn't be null", extensionList);
        assertEquals("Plugin extensions size is not 2", extensionList.size(), 2);
        assertEquals("First element in plugin extension is not ADSecurityRealm", extensionList.get(0), ADSecurityRealm.class);
        assertEquals("Second element in plugin extension is not ADSettings", extensionList.get(1), ADSettings.class);
    }
}
