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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.TreeSet;

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
 * Testcase for ADSecurityRealm
 * @author Jiji Sasidharan
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ADSettings.class, InetAddress.class})
public class ADSecurityRealmTest {

    @Rule
    private ExpectedException thrown = ExpectedException.none();

    private void setupMocks(final boolean hasProviders, final boolean hostHasFQN) throws Exception {
        whenNew(ADSettings.class).withAnyArguments().thenAnswer(new Answer<ADSettings>() {
            public ADSettings answer(InvocationOnMock invocation)
                    throws Throwable {
                ADSettings adSettings = spy(new ADSettings((Settings) invocation.getArguments()[0]));
                TreeSet<ADServerEntry> providers = null;
                if (hasProviders)
                    providers = new TreeSet<ADServerEntry>(Arrays.asList((new ADServerEntry(0, 1, "ldap.mycompany.com", 389))));
                doReturn(providers).when(adSettings).fetchProviderList(anyString());
                return adSettings;
            }
        });

        mockStatic(InetAddress.class);
        InetAddress mockInetAddress = mock(InetAddress.class);
        when(InetAddress.getLocalHost()).thenReturn(mockInetAddress);
        if (hostHasFQN)
            when(mockInetAddress.getCanonicalHostName()).thenReturn("mycomp.ad.mycompany.com");
        else
            when(mockInetAddress.getCanonicalHostName()).thenReturn("mycomputer");
    }

    /**
     * Scenario
     *   LDAP Providers are not available for authentication.
     */
    @Test
    public void testADSecurityRealmInitWithNoProviders() throws Exception {
        setupMocks(false, true);

        thrown.expect(ADPluginException.class);
        thrown.expectMessage(CoreMatchers.startsWith("Failed to retrieve srv records for"));

        ADSecurityRealm realm = new ADSecurityRealm(new ADSettings(new Settings()));
        realm.init();
        fail("Failed to throw exception incase of no providers.");
    }

    /**
     * Scenario
     *   LDAP Providers are available for authentication - happy path!
     */
    @Test
    public void testADSecurityRealmInitWithProviders() throws Exception {
        setupMocks(true, true);
        ADSecurityRealm realm = new ADSecurityRealm(new ADSettings(new Settings()));
        realm.init();
        assertEquals("Wrong SecurityRealm Name", realm.getName(), Constants.SECURITY_REALM_NAME);
        assertEquals("Wrong GroupsProvider implemenation returned", realm.getGroupsProvider().getClass(), ADGroupsProvider.class);
        assertEquals("Wrong UsersProvider implemenation returned", realm.getUsersProvider().getClass(), ADUsersProvider.class);
        assertEquals("Wrong SecurityRealm Name", realm.doGetAuthenticator().getClass(), ADAuthenticator.class);
    }

    /**
     * Scenario
     *   LDAP Providers are available for authentication - happy path
     */
    @Test
    public void testADSecurityRealmInitWithMissingDomainInHostName() throws Exception {
        setupMocks(true, false);
        thrown.expect(ADPluginException.class);
        thrown.expectMessage(CoreMatchers.startsWith("Failed to retrieve srv records for"));

        ADSecurityRealm realm = new ADSecurityRealm(new ADSettings(new Settings()));
        realm.init();
        fail();
    }
}
