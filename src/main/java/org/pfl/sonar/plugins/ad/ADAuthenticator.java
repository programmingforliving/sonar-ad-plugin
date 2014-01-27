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
