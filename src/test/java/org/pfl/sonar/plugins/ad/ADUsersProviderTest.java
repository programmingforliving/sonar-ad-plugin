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
