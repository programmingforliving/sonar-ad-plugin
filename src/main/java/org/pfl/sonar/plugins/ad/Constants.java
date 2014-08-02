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
