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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sonar.api.SonarPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ADPlugin - Sonar Active Directory Plugin class.
 *
 * @author Jiji_Sasidharan
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ADPlugin extends SonarPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(ADPlugin.class);

    /*
     * (non-Javadoc)
     *
     * @see org.sonar.api.Plugin#getExtensions()
     */
    public List getExtensions() {
        List extensionList = new ArrayList();
        extensionList.add(ADSecurityRealm.class);
        extensionList.add(ADSettings.class);
        LOG.trace("ExtensionList: {}", extensionList);
        return Collections.unmodifiableList(extensionList);
    }
}
