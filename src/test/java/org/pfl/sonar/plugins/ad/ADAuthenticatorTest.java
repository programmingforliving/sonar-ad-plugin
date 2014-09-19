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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.config.Settings;
import org.sonar.api.security.Authenticator;
import org.sonar.api.security.Authenticator.Context;

/**
 * Testcase for ADAuthenticator
 * 
 * @author Jiji Sasidharan
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ADAuthenticator.class, ContextUtil.class})
public class ADAuthenticatorTest {
	
	@SuppressWarnings("unchecked")
	private ADSecurityRealm getMock(final String domainName, final String userName, final String password) throws Exception {
		Settings settings = new Settings();
		settings.setProperty("sonar.ad.domain", domainName);
		ADSettings adSettings = spy(new ADSettings(settings));
		doReturn(new TreeSet<ADServerEntry>(Arrays.asList((new ADServerEntry(0, 1, "ldap.mycompany.com", 389)))))
			.when(adSettings, "fetchProviderList", domainName);
		
		final InitialLdapContext dirCtx = mock(InitialLdapContext.class);  
		whenNew(InitialLdapContext.class).withAnyArguments().thenAnswer(new Answer<InitialLdapContext>() {
			public InitialLdapContext answer(InvocationOnMock invocation)
					throws Throwable {
				Properties env = (Properties) invocation.getArguments()[0];
				if (env.get(Constants.SECURITY_PRINCIPAL).equals(userName + "@" + domainName) &&
					env.get(Constants.SECURITY_CREDENTIALS).equals(password)) 
					return dirCtx;
				throw new Exception("bind failed for user " + userName);
			}
		});

		when(dirCtx.search(anyString(), anyString(), isA(SearchControls.class))).thenReturn(null);

		whenNew(ADUser.class).withArguments(userName)
							 .thenAnswer(new Answer<ADUser>() {
								public ADUser answer(InvocationOnMock invocation)
										throws Throwable {
									 ADUser user = spy (new ADUser(userName));
									 doNothing().when(user).populate(any(NamingEnumeration.class));
									 return user;
								 } 
							 });

		ADSecurityRealm adSecurityRealm = new ADSecurityRealm(adSettings);
		adSecurityRealm.init();
		return adSecurityRealm;
	}

	/**
	 * Scenario:
	 *   1) Correct password
	 * @throws Exception
	 */
	@Test
	public void testDoAuthenticate() throws Exception {
		String domainName = "ad.mycompany.com";
		String userName = "user", password = "secret";
		
		ADSecurityRealm adSecurityRealm = getMock(domainName, userName, password);
		Authenticator authenticator = adSecurityRealm.doGetAuthenticator();
		assertEquals(authenticator.getClass(), ADAuthenticator.class);

		boolean logged = authenticator.doAuthenticate(new Context(userName, password, mock(HttpServletRequest.class)));
		assertTrue(logged);
		assertNotNull(ContextUtil.getLoggedInUser());
	}

	/**
	 * Scenario:
	 *   1) Wrong password
	 * @throws Exception
	 */
	@Test
	public void testDoAuthenticateWithWrongPassword() throws Exception {
		String domainName = "ad.mycompany.com";
		String userName = "user", password = "secret";
		
		ADSecurityRealm adSecurityRealm = getMock(domainName, userName, password);
		Authenticator authenticator = adSecurityRealm.doGetAuthenticator();
		assertEquals(authenticator.getClass(), ADAuthenticator.class);

		boolean logged = authenticator.doAuthenticate(new Context(userName, "not secret", mock(HttpServletRequest.class)));
		assertTrue(!logged);
		assertNull(ContextUtil.getLoggedInUser());
	}

	/**
	 * Scenario:
	 *   1) Wrong user name
	 * @throws Exception
	 */
	@Test
	public void testDoAuthenticateWithWrongUserName() throws Exception {
		String domainName = "ad.mycompany.com";
		String userName = "user", password = "secret";
		
		ADSecurityRealm adSecurityRealm = getMock(domainName, userName, password);
		Authenticator authenticator = adSecurityRealm.doGetAuthenticator();
		assertEquals(authenticator.getClass(), ADAuthenticator.class);

		boolean logged = authenticator.doAuthenticate(new Context("not valid", password, mock(HttpServletRequest.class)));
		assertTrue(!logged);
		assertNull(ContextUtil.getLoggedInUser());
	}
}
