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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
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
@PrepareForTest({ADSettings.class})
public class ADSecurityRealmTest {
	
	@Rule
	private ExpectedException thrown = ExpectedException.none(); 
	
	private void setupMocks(final boolean hasProviders) throws Exception {
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
	}
	
	/**
	 * Scenario
	 * a) LDAP Providers are not available for authentication. 
	 */
	@Test
	public void testADSecurityRealmInitWithNoProviders() throws Exception {
		setupMocks(false);
		
		thrown.expect(ADPluginException.class);
		thrown.expectMessage(CoreMatchers.startsWith("Failed to retrieve srv records for"));

		ADSecurityRealm realm = new ADSecurityRealm(new ADSettings(new Settings()));
		realm.init();
		fail("Failed to throw exception incase of no providers.");
	}

	/**
	 * Scenario
	 * a) LDAP Providers are available for authentication - happy path 
	 */
	@Test
	public void testADSecurityRealmInitWithProviders() throws Exception {
		setupMocks(true);
		String hostName = "ailsonfire.53647650-25f9-0132-5601-76bec1757a7f_df3d214d406d"; InetAddress.getLocalHost().getCanonicalHostName();
		if (hostName.indexOf(".") == -1) {
			// if the host name doesn't return the FQN,
			// then this test would throw exception
			thrown.expect(ADPluginException.class);
			thrown.expectMessage(CoreMatchers.startsWith("Failed to retrieve srv records for"));
		}
		ADSecurityRealm realm = new ADSecurityRealm(new ADSettings(new Settings()));
		realm.init();
		assertEquals("Wrong SecurityRealm Name", realm.getName(), Constants.SECURITY_REALM_NAME);
		assertEquals("Wrong GroupsProvider implemenation returned", realm.getGroupsProvider().getClass(), ADGroupsProvider.class);
		assertEquals("Wrong UsersProvider implemenation returned", realm.getUsersProvider().getClass(), ADUsersProvider.class);
		assertEquals("Wrong SecurityRealm Name", realm.doGetAuthenticator().getClass(), ADAuthenticator.class);
	}
}
