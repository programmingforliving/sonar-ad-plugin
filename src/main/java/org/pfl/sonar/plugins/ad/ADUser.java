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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Active Directory User object.
 * 
 * @author Jiji Sasidharan
 */
@SuppressWarnings("rawtypes")
public class ADUser {

	private static final Logger LOG = LoggerFactory.getLogger(ADUser.class);

	private String userName;
	private Collection<String> groups;
	
	public ADUser(String userName) {
		this.userName = userName; 
	}
	
	/**
	 * Populate the user attributes from AD SearchResult
	 * @param userAttributeEnumeration
	 * @throws NamingException
	 */
	public void populate(NamingEnumeration<SearchResult> userAttributeEnumeration) 
			throws NamingException {
    	while (userAttributeEnumeration.hasMore()) {
    		SearchResult result = userAttributeEnumeration.next();
			NamingEnumeration attrs = result.getAttributes().getAll();
    		while (attrs.hasMore()) {
    			Attribute attr = (Attribute)attrs.next();
    			if ("memberOf".equals(attr.getID())) {
    				LOG.debug("{}", attr);
    				groups = getAtrributeValues(attr.getAll());
    			}
    		}
    	}
	}
	
	/**
	 * Extract attribute values. 
	 * 
	 * Note: This method extract the value of CN=
	 *  
	 * @param attrValues
	 * @return
	 * @throws NamingException
	 */
	private Collection<String> getAtrributeValues(NamingEnumeration attrValues) throws NamingException {
		List<String> valueList = new ArrayList<String>();
		while (attrValues.hasMoreElements()) {
			String attrValue = (String)attrValues.next();
			LOG.debug("Processing {}", attrValue);
			if (!attrValue.isEmpty()) {
				valueList.add(attrValue.substring(3, attrValue.indexOf(",")));
			}
		}
		return valueList;
	}

	/**
	 * Returns userName
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Returns groups
	 * @return the groups
	 */
	public Collection<String> getGroups() {
		return groups;
	}
	
}
