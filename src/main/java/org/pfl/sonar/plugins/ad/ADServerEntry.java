/*
 * Sonar AD Plugin
 * Copyright (C) Jiji Sasidharan
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

/**
 * VO to hold ADServer properties
 * 
 * @author Jiji Sasidharan
 */
public class ADServerEntry implements Comparable<ADServerEntry> {
	private final int priority;
	private final int weight;
	private final String host;
	private final int port;

	/**
	 * ADServer
	 * 
	 * @param priority
	 * @param weight
	 * @param host
	 * @param port
	 */
	public ADServerEntry(int priority, int weight, String host, int port) {
		this.priority = priority;
		this.weight = weight;
		this.host = host;
		this.port = port;
	}

	/**
	 * compareTo
	 */
	public int compareTo(ADServerEntry o) {
		if (this.priority == o.priority) {
			if (this.weight == o.weight) {
				return getUrl().compareTo(o.getUrl());
			} else {
				return Integer.valueOf(o.weight).compareTo(this.weight);
			}
		}
		return Integer.valueOf(this.priority).compareTo(o.priority);
	}

	/**
	 * Returns priority
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Returns weight
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * Returns host
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns port
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns the LDAP url.
	 * @return
	 */
	public String getUrl() {
		return "ldap://" + host + ":" + port;
	}
	
	/**
	 * Return the string representation of ADServerEntry object.
	 */
	public String toString() {
		return "("+ priority +"-" + weight+ ")" + getUrl();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) 
			return false;
		return toString().equals(obj.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
