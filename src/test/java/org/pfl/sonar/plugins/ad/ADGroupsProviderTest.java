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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test case for {@link ADGroupsProvider}
 * 
 * @author Jiji Sasidharan
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ADUser.class, ContextUtil.class})
public class ADGroupsProviderTest {

	/**
	 * Setup mock objects
	 * @param userName
	 * @param groups
	 */
	private void setupMocks(String userName, Collection<String> groups) {
		ADUser adUser = spy(new ADUser(userName));
		doReturn(groups).when(adUser).getGroups();
		
		mockStatic(ContextUtil.class);
		when(ContextUtil.getLoggedInUser()).thenReturn(adUser);
	}
	
	/**
	 * Scenario:
	 *    a) User logged in with 2 groups
	 */
	@Test
	public void testWithValidUserAndGroups() {
		String userName = "user";
		List<String> groups = Arrays.asList("Manager", "Developer");
		setupMocks(userName, groups);
		
		ADGroupsProvider groupsProvider = new ADGroupsProvider(null);
		Collection<String> resultGrps = groupsProvider.doGetGroups(userName);
		assertNotNull("groups returned shouldn't be null", resultGrps);
		assertEquals("Groups are not returned correctly", resultGrps.size(), groups.size());
		assertEquals("Groups are not returned correctly", resultGrps, groups);
	}

	/**
	 * Scenario:
	 *   a) The user name in GetGroups request is different from logged in user 
	 */
	@Test
	public void testWithInvalidUserAndGroups() {
		String userName = "user1";
		List<String> groups = Arrays.asList("Manager", "Developer");
		setupMocks(userName, groups);
		
		ADGroupsProvider groupsProvider = new ADGroupsProvider(null);
		Collection<String> resultGrps = groupsProvider.doGetGroups("user2");
		assertNull("Groups returned should be null.", resultGrps);
	}

	/**
	 * Scenario:
	 *   a) Logged in user has zero groups 
	 */
	@Test
	public void testWithInvalidUserAndZeroGroups() {
		String userName = "user";
		List<String> groups = Arrays.asList();
		setupMocks(userName, groups);
		
		ADGroupsProvider groupsProvider = new ADGroupsProvider(null);
		Collection<String> resultGrps = groupsProvider.doGetGroups(userName);
		assertNotNull("Groups returned should not be null.", resultGrps);
		assertEquals("Groups are not returned correctly", resultGrps.size(), groups.size());
		assertEquals("Groups are not returned correctly", resultGrps, groups);
	}

	/**
	 * Scenario:
	 *   a) The user is not logged on 
	 */
	@Test
	public void testWithUserNotLoggedon() {
		Collection<String> resultGrps = new ADGroupsProvider(null).doGetGroups("user");
		assertNull("Groups returned should be null.", resultGrps);
	}
}
