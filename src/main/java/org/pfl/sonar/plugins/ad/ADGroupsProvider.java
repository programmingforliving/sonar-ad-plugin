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

import java.util.Collection;

import org.sonar.api.security.ExternalGroupsProvider;

/**
 * Retrieve the groups for a given user.
 */
public class ADGroupsProvider extends ExternalGroupsProvider {

	/**
	 * ADGroupsProvider
	 * 
	 * @param adSettings  The AD settings
	 */
	public ADGroupsProvider(ADSettings adSettings) {
	}

	/**
	 * Returns the groups associated with the user.
	 * 
	 * @username The user name.
	 */
	@Override
	public Collection<String> doGetGroups(String username) {
		return null;
	}
}
