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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sonar.api.SonarPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ADPlugin - Sonar Active Directory Plugin class 
 * 
 * @author Jiji_Sasidharan
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ADPlugin extends SonarPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(ADPlugin.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.sonar.api.Plugin#getExtensions()
     */
    public List getExtensions() {
        List extensionList = new ArrayList();
        extensionList.add(ADSecurityRealm.class);
        extensionList.add(ADSettings.class);
        LOG.trace("ExtensionList: " + extensionList);
        return Collections.unmodifiableList(extensionList);
    }
}
