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

/**
 * Constants - List out all the constants used in the plugin
 *
 * @author Jiji_Sasidharan
 */
public interface Constants {

    public String SECURITY_REALM_NAME = "AD";

    public String PROVIDER_URL            = "java.naming.provider.url";
    public String FACTORY_INITIAL         = "java.naming.factory.initial";
    public String SECURITY_AUTHENTICATION = "java.naming.security.authentication";
    public String SECURITY_PRINCIPAL      = "java.naming.security.principal";
    public String SECURITY_CREDENTIALS    = "java.naming.security.credentials";
    public String REFERRAL                = "java.naming.referral";

    public String LDAP_CTX_FACTORY               = "com.sun.jndi.ldap.LdapCtxFactory";
    public String SECURITY_AUTHENTICATION_SIMPLE = "simple";
    public String REFERRAL_FOLLOW                = "follow";

    public String CONFIG_OVERRIDE_AD_DOMAIN = "sonar.ad.domain";
}
