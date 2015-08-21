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
    @Override
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
    @Override
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
