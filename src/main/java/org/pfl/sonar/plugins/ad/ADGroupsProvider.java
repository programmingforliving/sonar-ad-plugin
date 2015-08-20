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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.security.ExternalGroupsProvider;

/**
 * Retrieve the groups for a given user.
 *
 * @author Jiji_Sasidharan
 */
public class ADGroupsProvider extends ExternalGroupsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ADGroupsProvider.class);

    /**
     * ADGroupsProvider
     *
     * @param adSettings  The AD settings
     */
    public ADGroupsProvider(ADSettings adSettings) {
    }

    /**
     * Returns the groups associated with the user.
     *
     * @username The user name.
     */
    @Override
    public Collection<String> doGetGroups(String username) {
        ADUser user = ContextUtil.getLoggedInUser();
        if (user != null && username.equals(user.getUserName())) {
            LOG.debug("{} belongs to groups: {}", username, user.getGroups());
            return user.getGroups();
        }
        return null;
    }
}
