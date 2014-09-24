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
