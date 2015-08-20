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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.startsWith;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.config.Settings;

/**
 * Test Case for ADSettings
 * 
 * @author Jiji Sasidharan
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ADSettings.class, InetAddress.class})
public class ADSettingsTest {

    @Rule
    private ExpectedException thrown = ExpectedException.none();

    /**
     * Setup necessary mock objects/stubs for InitialDirContext and the InitialDirContext.getAttributes.
     *
     * @param domain
     * @param providers
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected void setupMocks(String domain, final List<String> providers) throws Exception {
        InitialDirContext dirCtx = mock(InitialDirContext.class);
        whenNew(InitialDirContext.class).withNoArguments().thenReturn(dirCtx);

        Attributes attributes = mock(Attributes.class);
        String searchStr = "dns:/_ldap._tcp." + (domain == null ? "" : domain);
        when(dirCtx.getAttributes(startsWith(searchStr), aryEq(new String[]{"srv"}))).thenReturn(attributes);
        when(dirCtx.getAttributes(not(startsWith(searchStr)), aryEq(new String[]{"srv"}))).thenReturn(null);

        final NamingEnumeration<Attribute> attrEnum = mock(NamingEnumeration.class);
        when(attributes.getAll()).thenAnswer(new Answer<NamingEnumeration<Attribute>>() {
            public NamingEnumeration<Attribute> answer(InvocationOnMock invocation)
                    throws Throwable {
                return attrEnum;
            }
        });

        if (providers == null || providers.isEmpty()) {
            when(attrEnum.hasMore()).thenReturn(false);
        } else {
            final Iterator<String> providerIt = providers.iterator();
            when(attrEnum.hasMore()).thenAnswer(new Answer<Boolean>() {
                public Boolean answer(InvocationOnMock invocation)
                        throws Throwable {
                    return providerIt.hasNext();
                }
            });

            when(attrEnum.next()).thenAnswer(new Answer<Attribute>() {
                public Attribute answer(InvocationOnMock invocation)
                        throws Throwable {
                    Attribute attr = mock(Attribute.class);
                    when(attr.get()).thenReturn(providerIt.next());
                    return attr;
                }
            });
        }

        mockStatic(InetAddress.class);
        InetAddress mockInetAddress = mock(InetAddress.class);
        when(InetAddress.getLocalHost()).thenReturn(mockInetAddress);
        when(mockInetAddress.getCanonicalHostName()).thenReturn("mycomp.users.mycompany.com");
    }

    /**
     * Scenario:
     *   a) Domain is provided in sonar.properties.
     *   b) No srv records available for the domain.
     * @throws Exception
     */
    @Test
    public void testWithExternallyProvidedDomainWithNoProviders() throws Exception {
        final String externallyProvidedDomain = "users.mycompany.com";
        setupMocks(externallyProvidedDomain, null);

        thrown.expect(ADPluginException.class);
        thrown.expectMessage(CoreMatchers.startsWith("Failed to retrieve srv records for"));

        Settings settings = new Settings();
        settings.setProperty("sonar.ad.domain", externallyProvidedDomain);
        new ADSettings(settings).load();;
        // load should throw the ADPluginException
        fail("ADPluginException is not thrown on the event of no providers ");
    }

    /**
     * Scenario:
     *   a) Domain is provided in sonar.properties.
     *   b) one srv records available for the domain.
     * @throws Exception
     */
    @Test
    public void testWithExternallyProvidedDomainWithProviders1() throws Exception {
        final String externallyProvidedDomain = "users.mycompany.com";
        final String externallyProvidedDomainDN = "DC=users,DC=mycompany,DC=com";
        setupMocks(externallyProvidedDomain, Arrays.asList("0 100 389 ldap.mycompany.com"));

        Settings settings = new Settings();
        settings.setProperty("sonar.ad.domain", externallyProvidedDomain);
        ADSettings adSettings = new ADSettings(settings);
        adSettings.load();

        assertEquals("fetchProviderList failed.", adSettings.getProviderList().size(), 1);
        Iterator<ADServerEntry> providers = adSettings.getProviderList().iterator();
        assertEquals("fetchProviderList failed.", providers.next(), new ADServerEntry(0, 100, "ldap.mycompany.com", 389));
        assertEquals("Domain identifcation failed.", adSettings.getDnsDomain(), externallyProvidedDomain);
        assertEquals("DomainDN construction failed.", adSettings.getDnsDomainDN(), externallyProvidedDomainDN);
    }

    /**
     * Scenario:
     *   a) Domain is provided in sonar.properties.
     *   b) two srv records available for the domain.
     * @throws Exception
     */
    @Test
    public void testWithExternallyProvidedDomainWithProviders2() throws Exception {
        final String externallyProvidedDomain = "users.mycompany.com";
        final String externallyProvidedDomainDN = "DC=users,DC=mycompany,DC=com";
        setupMocks(externallyProvidedDomain, Arrays.asList("0 1 389 ldap1.mycompany.com", "1 1 389 ldap2.mycompany.com"));

        Settings settings = new Settings();
        settings.setProperty("sonar.ad.domain", externallyProvidedDomain);
        ADSettings adSettings = new ADSettings(settings);
        adSettings.load();

        assertEquals("fetchProviderList failed.", adSettings.getProviderList().size(), 2);
        Iterator<ADServerEntry> providers = adSettings.getProviderList().iterator();
        assertEquals("fetchProviderList order failed.", providers.next(), new ADServerEntry(0,  1, "ldap1.mycompany.com", 389));
        assertEquals("fetchProviderList order failed.", providers.next(), new ADServerEntry(1,  1, "ldap2.mycompany.com", 389));
        assertEquals("Domain identifcation failed.", adSettings.getDnsDomain(), externallyProvidedDomain);
        assertEquals("DomainDN construction failed.", adSettings.getDnsDomainDN(), externallyProvidedDomainDN);
    }

    /**
     * Scenario:
     *   a) auto-discovery of domain.
     *   b) two srv records available for the domain.
     * @throws Exception
     */
    @Test
    public void testAutoDiscoveryWithProviders() throws Exception {
        String hostName = "mycomp.users.mycompany.com";
        String domainName = hostName.substring(hostName.indexOf('.') + 1);
        String domainNameDN = "DC=" + domainName.replace(".", ",DC=");
        setupMocks(domainName, Arrays.asList("0 1 389 ldap1.mycompany.com", "1 1 389 ldap2.mycompany.com"));

        ADSettings adSettings = new ADSettings(new Settings());
        adSettings.load();

        assertEquals("fetchProviderList failed.", adSettings.getProviderList().size(), 2);
        Iterator<ADServerEntry> providers = adSettings.getProviderList().iterator();
        assertEquals("fetchProviderList order failed.", providers.next(), new ADServerEntry(0,  1, "ldap1.mycompany.com", 389));
        assertEquals("fetchProviderList order failed.", providers.next(), new ADServerEntry(1,  1, "ldap2.mycompany.com", 389));
        assertEquals("Domain identifcation failed.", adSettings.getDnsDomain(), domainName);
        assertEquals("DomainDN construction failed.", adSettings.getDnsDomainDN(), domainNameDN);
    }

    /**
     * Scenario:
     *   a) auto-discovery of domain.
     *   b) No srv records available for the domain.
     * @throws Exception
     */
    @Test
    public void testAutoDiscoveryWithNoProviders() throws Exception {
        setupMocks(null, null);

        thrown.expect(ADPluginException.class);
        thrown.expectMessage(CoreMatchers.startsWith("Failed to retrieve srv records for"));

        new ADSettings(new Settings()).load();
        fail("ADPluginException is not thrown on the event of no providers ");
    }

    /**
     * Scenario:
     *   a) auto-discovery of domain.
     *   b) No srv records available for the domain.
     *   c) two srv records available for sub domain
     * @throws Exception
     */
    @Test
    public void testSubdomainAutoDiscoveryWithProviders() throws Exception {
        String hostName = "mycomp.users.mycompany.com";
        String domainName = hostName.substring(hostName.indexOf('.') + 1);
        String domainNameDN = "DC=" + domainName.replace(".", ",DC=");
        String subDomainName = domainName.substring(domainName.indexOf('.') + 1);
        String subDomainNameDN = "DC=" + subDomainName.replace(".", ",DC=");
        setupMocks(subDomainName, Arrays.asList("0 1 389 ldap1.mycompany.com", "1 1 389 ldap2.mycompany.com"));

        ADSettings adSettings = new ADSettings(new Settings());
        adSettings.load();

        assertEquals("SubDomain fetchProviderList failed.", adSettings.getProviderList().size(), 2);
        Iterator<ADServerEntry> providers = adSettings.getProviderList().iterator();
        assertEquals("SubDomain fetchProviderList order failed.", providers.next(), new ADServerEntry(0,  1, "ldap1.mycompany.com", 389));
        assertEquals("SubDomain fetchProviderList order failed.", providers.next(), new ADServerEntry(1,  1, "ldap2.mycompany.com", 389));
        assertEquals("SubDomain search failed.", adSettings.getDnsDomain(), subDomainName);
        assertEquals("SubDomainDN construction failed.", adSettings.getDnsDomainDN(), subDomainNameDN);
        assertNotSame("SubDomainDN construction failed.", adSettings.getDnsDomainDN(), domainNameDN);
    }
}
