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

import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
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

    // A ThreadLocal instance to hold the logged in user details local to the thread.
    private static final ThreadLocal<ADUser> LOGGED_IN_USER_HOLDER = new ThreadLocal<ADUser>();

    public static DirContext open(ADSettings adSettings, String userName, String password) {
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
                LOGGED_IN_USER_HOLDER.remove();
                ldapCtx = new InitialLdapContext(env, null);
                LOG.trace("User succesfully bound to AD {}", ldapCtx);
                try {
                    SearchControls sCtrl = new SearchControls();
                    sCtrl.setSearchScope(SearchControls.SUBTREE_SCOPE);

                    NamingEnumeration<SearchResult> answer = ldapCtx.search(adSettings.getDnsDomainDN(),
                            "(&(userPrincipalName=" + usrPrincipal + "))", sCtrl);

                    ADUser user = new ADUser(userName);
                    user.populate(answer);
                    LOGGED_IN_USER_HOLDER.set(user);
                } catch (NamingException e) {
                    LOG.warn("Failed to retrieve the attributes of {}. Error: {}", userName, e.getMessage());
                    LOG.trace("Use search failed", e);
                }
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

    /**
     * Retrieve the logged in user object from ThreadLocal and returns it.
     * @return
     */
    public static ADUser getLoggedInUser() {
        return LOGGED_IN_USER_HOLDER.get();
    }
}
