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
import org.sonar.api.ServerExtension;
import org.sonar.api.config.Settings;

/**
 * ADSettings - Setting for Active Directory.
 * 
 * @author Jiji_Sasidharan
 *
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
    public ADSettings(Settings settings) {
        // We may use settings in future. :)
    }
    
    /**
     * Load all the settings
     */
    public void load() {
        doAutoDiscovery();
    }
    
    /**
     * This method automatically identifies the Active Directory settings. 
     */
    private void doAutoDiscovery() {
        //find domain
        try {
            String hostName = InetAddress.getLocalHost().getCanonicalHostName();
            LOG.trace("Host Name: {}", hostName);

            String domainName = null;
            Set<ADServerEntry> providerList = null;

            int index = -1;
            while ((index = hostName.indexOf(".", index + 1)) > -1) {
                domainName = hostName.substring(index + 1);
                try {
                    LOG.trace("Searching provider list for domain '{}'", domainName);
                    providerList = fetchProviderList(domainName);
                    break;
                } catch (Exception e) {
                    // ignore the exception for now.
                }
            };
            
            setDnsDomain(domainName);
            if (providerList == null || providerList.isEmpty()) {
                LOG.error("Autodiscovery couldn't find any srv records for {}", hostName);
                throw new ADPluginException("Failed to retrieve srv records for" + hostName);
            }
            setProviderList(providerList);
        } catch (UnknownHostException e) {
            LOG.error("Failed to detect domain. Error: " + e.getMessage());
            throw new ADPluginException("Failed to detect the domain ", e);
        }
        
        //construct domain DN
        StringBuilder domainDNBuilder = new StringBuilder();
        for (String dom : getDnsDomain().split("[.]")) {
            domainDNBuilder.append(",").append("DC=").append(dom);
        }
        setDnsDomainDN(domainDNBuilder.toString().substring(1));
        LOG.debug("DNS Domain   : {}", getDnsDomain());
        LOG.debug("DNS Domain DN: {}", getDnsDomainDN());
        LOG.debug("Provider List: {}", getProviderList());
        LOG.info("AutoDiscovery completed successfully.");
    }

    private Set<ADServerEntry> fetchProviderList(String domainName) {
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
            setProviderList(providerList);
            LOG.debug("Provider List: {}", getProviderList());
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
     * Return dnsDomainDN. 
     * @return the dnsDomainDN
     */
    public String getDnsDomainDN() {
        return dnsDomainDN;
    }

    /**
     * Set dnsDomainDN.
     * @param dnsDomainDN the dnsDomainDN to set
     */
    private void setDnsDomainDN(String dnsDomainDN) {
        this.dnsDomainDN = dnsDomainDN;
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
}
