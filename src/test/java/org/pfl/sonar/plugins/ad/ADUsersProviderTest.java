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

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mock;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.security.ExternalUsersProvider.Context;
import org.sonar.api.security.UserDetails;

/**
 * Testcase for {@link ADUsersProvider}
 * 
 * @author Jiji Sasidharan
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ADUsersProvider.class})
public class ADUsersProviderTest {

	/**
	 * Scenario:
	 *   a) The default Sonar admin user 'admin' query should return null.
	 */
	@Test
	public void testAdmin() {
		ADUsersProvider adUsersProvider = new ADUsersProvider(null);
		UserDetails user = adUsersProvider.doGetUserDetails(new Context("admin", mock(HttpServletRequest.class)));
		assertNull("Default user(admin) check failed", user);
	}
	
	/**
	 * Scenario:
	 *   a) Any non default user query should return.
	 */
	@Test
	public void testAnyUserOtherThanAdmin() {
		String userName = "user";
		ADUsersProvider adUsersProvider = new ADUsersProvider(null);
		UserDetails user = adUsersProvider.doGetUserDetails(new Context(userName, mock(HttpServletRequest.class)));
		assertNotNull("Non default user query failed", user);
		assertEquals("User name deosn't match", user.getName(), userName);
		assertEquals("Wrong email address", user.getEmail(), "");
	}
}
