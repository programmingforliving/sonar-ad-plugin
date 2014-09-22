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
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Testcase for {@link ADUser}
 * @author Jiji Sasidharan
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ADUser.class})
public class ADUserTest {
	
	@SuppressWarnings("unchecked")
	public NamingEnumeration<SearchResult> createMock(List<String> groups) throws Exception {
		NamingEnumeration<SearchResult> searchResultsEnum = mock(NamingEnumeration.class);
		SearchResult searchResult = mock(SearchResult.class);
		when(searchResultsEnum.hasMore()).thenReturn(true, false);
		when(searchResultsEnum.next()).thenReturn(searchResult);

		Attributes attributes = new BasicAttributes();
		if (groups != null) {
			for (String grp : groups) {
				Attribute memberOf = attributes.get("memberOf");
				if (memberOf == null) {
					attributes.put("memberOf", grp);
				} else {
					memberOf.add(grp);
				}
			}
		}
		attributes.put("CN", "CN=user,OU=mycompany,OU=IE");
		attributes.put("atr1", "val1");
		when(searchResult.getAttributes()).thenReturn(attributes);
		
		return searchResultsEnum;
	}

	/**
	 * Scenario:
	 *   a) The user has memberOf attribute in AD.
	 *   
	 * @throws Exception
	 */
	@Test
	public void testWithMemberOf() throws Exception {
		String userName = "manager1";
		List<String> grpList = Arrays.asList("CN=Manager,OU=mycompany,OU=IE", 
				                             "CN=User,OU=mycompany,OU=IE");
		NamingEnumeration<SearchResult> searchResultEnum = createMock(grpList);
		ADUser adUser = new ADUser(userName);
		adUser.populate(searchResultEnum);
		assertEquals("Username is not set", adUser.getUserName(), userName);
		assertNotNull("Groups are not retrieved", adUser.getGroups());
		assertEquals("Groups are not retrieved", adUser.getGroups().size(), 2);
		assertEquals("Groups are not retrieved", adUser.getGroups().toArray()[0], "Manager");
		assertEquals("Groups are not retrieved", adUser.getGroups().toArray()[1], "User");
	}

	/**
	 * Scenario:
	 *   a) memberOf attribute is not present for user.
	 *   
	 * @throws Exception
	 */
	@Test
	public void testWithMemberOfMissing() throws Exception {
		String userName = "developer1";
		NamingEnumeration<SearchResult> searchResultEnum = createMock(null);
		ADUser adUser = new ADUser(userName);
		adUser.populate(searchResultEnum);
		assertEquals("Username is not set", adUser.getUserName(), userName);
		assertNull("Groups retrieval logic failed", adUser.getGroups());
	}

	/**
	 * Scenario:
	 *   a) memberOf attribute is empty.
	 *   
	 * @throws Exception
	 */
	@Test
	public void testWithMemberOfEmpty() throws Exception {
		String userName = "developer1";
		NamingEnumeration<SearchResult> searchResultEnum = createMock(Arrays.asList(""));
		ADUser adUser = new ADUser(userName);
		adUser.populate(searchResultEnum);
		assertEquals("Username is not set", adUser.getUserName(), userName);
		assertNotNull("Groups retrieval logic failed", adUser.getGroups());
		assertEquals("Groups retrieval logic failed", adUser.getGroups().size(), 0);
	}
}
