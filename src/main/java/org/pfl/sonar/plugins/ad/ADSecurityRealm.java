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
import org.sonar.api.security.Authenticator;
import org.sonar.api.security.ExternalGroupsProvider;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.SecurityRealm;

/**
 * ADSecurityRealm - SecurityRealm implementation for Sonar AD Plugin.
 *
 * @author Jiji_Sasidharan
 */
public class ADSecurityRealm extends SecurityRealm {

    private static final Logger LOG = LoggerFactory.getLogger(ADSecurityRealm.class);

    private ADSettings adSettings;

    public ADSecurityRealm(ADSettings adSettings) {
        this.adSettings = adSettings;
    }

    /**
     * Returns SecurityRealm name for Sonar AD Plugin.
     *
     * The Sonar AD Plugin should be configured in <code>sonar.properties</code>
     * with <code>sonar.security.realm: AD </code>
     *
     * @return SecurityRealm name
     * @see Constants#SECURITY_REALM_NAME
     */
    @Override
    public String getName() {
        return Constants.SECURITY_REALM_NAME;
    }

    /**
     * Initialize the AD Security Realm.
     */
    @Override
    public void init() {
        adSettings.load();
        LOG.info("ADSecurityRealm initialized successfully.");
    }

    /**
     * Returns the active directory authenticator.
     *
     * @return {@link Authenticator} implementation associated with this realm
     */
    @Override
    public Authenticator doGetAuthenticator() {
        return new ADAuthenticator(adSettings);
    }

    /**
     * Return ExternalUsersProvider.
     *
     * @return {@link ExternalUsersProvider} associated with this realm, null
     *         if not supported
     */
    @Override
    public ExternalUsersProvider getUsersProvider() {
        return new ADUsersProvider(adSettings);
    }

    /**
     * Return ExternalGroupsProvider.
     *
     * @return {@link ExternalGroupsProvider} associated with this realm, null
     *         if not supported
     */
    @Override
    public ExternalGroupsProvider getGroupsProvider() {
        return new ADGroupsProvider(adSettings);
    }
}
