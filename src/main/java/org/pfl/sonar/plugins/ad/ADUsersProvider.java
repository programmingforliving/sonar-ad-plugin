/*
 * Sonar AD Plugin
 * Copyright (C) 2012-2014 Jiji Sasidharan
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
