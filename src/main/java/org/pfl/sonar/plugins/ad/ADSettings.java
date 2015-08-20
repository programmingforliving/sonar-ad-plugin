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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.ServerExtension;
import org.sonar.api.config.Settings;

/**
 * ADSettings - Setting for Active Directory.
 * 
 * This class holds 
 * <ul>
 *  <li>Domain Name for Active Directory Authentication</li>
 *  <li>List of AD Authentication Provider configurations</li>
 * </ul>
 * The above configurations are automatically detected based on the FQN of the host where 
 * the Sonar is running. The Domain name can also be configured in sonar.properties in case
 * the FQN of the host doesn't have the authentication domain name in it.
 * 
 * @author Jiji_Sasidharan
 */
public class ADSettings implements ServerExtension {
    
    private static final Logger LOG = LoggerFactory.getLogger(ADSettings.class);

    private String dnsDomain;
    private String dnsDomainDN;
    private Set<ADServerEntry> providerList;
    
    /**
     * Constructor
     * @param settings
     */
    @Properties(
        @Property(key=Constants.CONFIG_OVERRIDE_AD_DOMAIN, name="AD domain to search for", defaultValue="")
    )
    public ADSettings(Settings settings) {
    	dnsDomain = settings.getString(Constants.CONFIG_OVERRIDE_AD_DOMAIN);
    }
    
    /**
     * Load all the settings
     */
    public void load() {
    	doAutoDiscovery();
    }
    
    /**
     * This method automatically identifies the Active Directory settings. 
     * If domain is given in sonar.properties, then the authentication server details 
     * are fetched based on that. If domain is not available in sonar properties, then 
     * this method take the FQN of the host and derive domain name and authentication server
     * list from it.
     */
    protected void doAutoDiscovery() {
        try {
            Set<ADServerEntry> providerList = null;
            String hostName = null;
            String adDomain = getDnsDomain();
            boolean domainNotProvided = (adDomain == null || adDomain.isEmpty());
        	if (domainNotProvided) {
        		hostName = InetAddress.getLocalHost().getCanonicalHostName();
	            LOG.trace("Host Name: {}", hostName);
	            int index = -1;
	            while ((index = hostName.indexOf(".", index + 1)) > -1) {
	            	adDomain = hostName.substring(index + 1);
	                try {
	                    LOG.trace("Searching provider list for domain '{}'", adDomain);
	                    providerList = fetchProviderList(adDomain);
	                    if (providerList != null && providerList.size() > 0) {
	                    	break;
	                    }
	                } catch (Exception e) {
	                    LOG.warn("Coulnt find any providers for domain '{}", adDomain);
	                }
	            };
        	} else {
        		providerList = fetchProviderList(adDomain);
        	}

        	if (providerList == null || providerList.isEmpty()) {
                LOG.error("Autodiscovery couldn't find any srv records for {}", 
                		(domainNotProvided ? hostName : adDomain));
                throw new ADPluginException("Failed to retrieve srv records for " + 
                		(domainNotProvided ? hostName : adDomain));
            }
        	
        	setDnsDomain(adDomain);
        	setDnsDomainDN("DC=" + adDomain.replace(".", ",DC="));
            setProviderList(providerList);
        } catch (UnknownHostException e) {
            LOG.error("Failed to detect domain. Error: " + e.getMessage());
            throw new ADPluginException("Failed to detect the domain ", e);
        }

        LOG.debug("DNS Domain   : {}", getDnsDomain());
        LOG.debug("Provider List: {}", getProviderList());
        LOG.info("AutoDiscovery completed successfully.");
    }

    /**
     * Fetch list of LDAP server configurations for a given domain
     *  
     * @param domainName
     * @return
     */
    protected Set<ADServerEntry> fetchProviderList(String domainName) {
        // find provider list.
        Set<ADServerEntry> providerList = new TreeSet<ADServerEntry>();
        try {
            DirContext dirContext = new InitialDirContext();
            Attributes srvAttrs = dirContext.getAttributes("dns:/_ldap._tcp." + domainName, new String[] { "srv" });
            for (NamingEnumeration<? extends Attribute> srvEnum = srvAttrs.getAll(); srvEnum.hasMore();) {
                String srvRecord = (String) srvEnum.next().get();
                LOG.trace("Processing srv record - {}", srvRecord);
                // srv record format: 0 100 389 xyzdc.yourcompany.com.
                String[] srvRecordTokens = srvRecord.split(" ");
                
                String host = srvRecordTokens[3];
                if (host.endsWith(".")) {
                    host = host.substring(0, host.length() - 1);
                }
                ADServerEntry adServer = new ADServerEntry(Integer.valueOf(srvRecordTokens[0]), 
                        Integer.valueOf(srvRecordTokens[1]), 
                        host, 
                        Integer.valueOf(srvRecordTokens[2]));
                providerList.add(adServer);
            }
            LOG.debug("Provider List: {}", providerList);
        } catch (NamingException e) {
            LOG.error("Failed to retrieve src records for domain: '{}'. Error: {}", domainName, e.getMessage());
            throw new ADPluginException("Failed to retrieve src records for domain: " + domainName, e);
        }
        return providerList;
    }
    
    /**
     * Return dnsDomain. 
     * @return the dnsDomain
     */
    public String getDnsDomain() {
        return dnsDomain;
    }

    /**
     * Set dnsDomain.
     * @param dnsDomain the dnsDomain to set
     */
    private void setDnsDomain(String dnsDomain) {
        this.dnsDomain = dnsDomain;
    }

    /**
     * Return providerList. 
     * @return the providerList
     */
    public Set<ADServerEntry> getProviderList() {
        return providerList;
    }

    /**
     * Set providerList.
     * @param providerList the providerList to set
     */
    private void setProviderList(Set<ADServerEntry> providerList) {
        this.providerList = providerList;
    }

	/**
	 * Returns dnsDomainDN
	 * @return the dnsDomainDN
	 */
	public String getDnsDomainDN() {
		return dnsDomainDN;
	}

	/**
	 * Set dnsDomainDN
	 * @param dnsDomainDN the dnsDomainNS to set
	 */
	private void setDnsDomainDN(String dnsDomainDN) {
		this.dnsDomainDN = dnsDomainDN;
	}
}
