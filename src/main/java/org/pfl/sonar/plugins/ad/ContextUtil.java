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

import java.util.Properties;

import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides utility methods to open and close LDAP Context.
 * 
 * @author Jiji Sasidharan
 */
public class ContextUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ContextUtil.class);

	public static DirContext open(ADSettings adSettings, String userName,
			String password) {
		String usrPrincipal = userName + "@" + adSettings.getDnsDomain();
		Properties env = new Properties();
		env.put(Constants.FACTORY_INITIAL, Constants.LDAP_CTX_FACTORY);
		env.put(Constants.SECURITY_AUTHENTICATION, Constants.SECURITY_AUTHENTICATION_SIMPLE);
		env.put(Constants.SECURITY_PRINCIPAL, usrPrincipal);
		env.put(Constants.SECURITY_CREDENTIALS, password);
		env.put(Constants.REFERRAL, Constants.REFERRAL_FOLLOW);
        DirContext ldapCtx = null;
        for(ADServerEntry provider : adSettings.getProviderList()) {
            env.put(Constants.PROVIDER_URL, provider.getUrl());
            try {
            	LOG.trace("AD Connect props: {}", env);
				ldapCtx = new InitialLdapContext(env, null);
				LOG.trace("User succesfully bound to AD {}", ldapCtx);
				break;
			} catch (Exception e) {
				LOG.warn("AD bind failed for {}. Error: {}", provider, e.getMessage());
				LOG.trace("AD bind failed", e);
			}
        }
        return ldapCtx;
	}

	/**
	 * Cose an Ldap directory context.
	 * @param dirCtx
	 */
	public static void close(DirContext dirCtx) {
		try {
			if (dirCtx != null)
				dirCtx.close();
		} catch (Exception e) {
			// ignore the exception
			LOG.warn("Error while closing the context", e);
		}
	}
}
