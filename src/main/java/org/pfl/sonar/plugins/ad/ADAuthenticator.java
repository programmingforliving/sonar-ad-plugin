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

import javax.naming.directory.DirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.security.Authenticator;

/**
 * Authenticator for Sonar AD Plugin.
 *
 * @author Jiji_Sasidharan
 */
public class ADAuthenticator extends Authenticator {

    private static final Logger LOG = LoggerFactory.getLogger(ADAuthenticator.class);

    private ADSettings adSettings;

    /**
     * Constructor
     * @param adSettings Active Directory settings
     */
    public ADAuthenticator(ADSettings adSettings) {
        this.adSettings = adSettings;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sonar.api.security.Authenticator#doAuthenticate(org.sonar.api.security.Authenticator.Context)
     */
    @Override
    public boolean doAuthenticate(Context context) {
        String userName = context.getUsername();
        String password = context.getPassword();
        LOG.trace("adSettings: {}", adSettings);
        DirContext dirCtx = ContextUtil.open(adSettings, userName, password);
        boolean isAuthenticated = dirCtx != null;
        LOG.debug("User '{}' authenticated? - {}", userName, isAuthenticated);
        ContextUtil.close(dirCtx);
        return isAuthenticated;
    }
}
