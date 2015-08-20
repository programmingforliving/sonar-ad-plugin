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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.UserDetails;

/**
 * UserDetails provider.
 *
 * @author Jiji_Sasidharan
 */
public class ADUsersProvider extends ExternalUsersProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ADUsersProvider.class);

    public ADUsersProvider(ADSettings adSettings) {
        //we might use ADSetting in future
    }

    /**
     * Returns the UserDetails.
     */
    public UserDetails doGetUserDetails(Context context) {
        LOG.debug("Retrieving the user details for '{}'", context.getUsername());
        UserDetails userDetails = null;
        // Bypass AD authentication for default user 'admin'
        if ("admin".equals(context.getUsername())) {
            LOG.debug("Bypassing AD to allow Sonar default user 'admin' "
                    + "to authenticate against Sonar internal database. ");
        } else {
            userDetails = new UserDetails();
            userDetails.setName(context.getUsername());
            userDetails.setEmail("");
        }
        return userDetails;
    }
}
