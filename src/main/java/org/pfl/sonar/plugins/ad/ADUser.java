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
