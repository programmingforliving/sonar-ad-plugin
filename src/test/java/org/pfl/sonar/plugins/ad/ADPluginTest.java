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

import java.util.List;

import org.junit.Test;

/**
 * Test case for {@link ADPlugin}
 * 
 * @author Jiji Sasidharan
 */
public class ADPluginTest {

	/**
	 * Check the plugin is configured properly.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testExtensionList() {
		List extensionList = new ADPlugin().getExtensions();
		assertNotNull("Plugin Extension list shouldn't be null", extensionList);
		assertEquals("Plugin extensions size is not 2", extensionList.size(), 2);
		assertEquals("First element in plugin extension is not ADSecurityRealm", extensionList.get(0), ADSecurityRealm.class);
		assertEquals("Second element in plugin extension is not ADSettings", extensionList.get(1), ADSettings.class);
	}
}
