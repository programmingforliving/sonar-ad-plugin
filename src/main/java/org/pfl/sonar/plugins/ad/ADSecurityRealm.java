/*
 * Sonar AD Plugin
 * Copyright (C) Jiji Sasidharan
 * http://programmingforliving.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
    public String getName() {
        return Constants.SECURITY_REALM_NAME;
    }

    /**
     * Initialize the AD Security Realm.
     */
    public void init() {
        adSettings.load();
        LOG.info("ADSecurityRealm initialized successfully.");
    }

    /**
     * Returns the active directory authenticator.
     * 
     * @return {@link Authenticator} implementation associated with this realm
     */
    public Authenticator doGetAuthenticator() {
        return new ADAuthenticator(adSettings);
    }

    /**
     * Return ExternalUsersProvider. 
     * 
     * @return {@link ExternalUsersProvider} associated with this realm, null
     *         if not supported
     */
    public ExternalUsersProvider getUsersProvider() {
        return new ADUsersProvider(adSettings);
    }

    /**
     * Return ExternalGroupsProvider. 
     * 
     * @return {@link ExternalGroupsProvider} associated with this realm, null
     *         if not supported
     */
    public ExternalGroupsProvider getGroupsProvider() {
        return new ADGroupsProvider(adSettings);
    }
}
