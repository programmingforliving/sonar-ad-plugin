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

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.Settings;
import org.sonar.api.security.Authenticator;
import org.sonar.api.security.Authenticator.Context;

/**
 * @author Jiji Sasidharan
 */
public class ADAuthenticatorTest {

	
	@Test
	public void testDoAuthenticate() {
		Settings settings = new Settings();
		ADSettings adSettings = new ADSettings(settings);
		ADSecurityRealm adSecurityRealm = new ADSecurityRealm(adSettings);
		adSecurityRealm.init();
		
		Authenticator authenticator = adSecurityRealm.doGetAuthenticator();
		
		assertNotNull(authenticator);
		assertEquals(authenticator.getClass(), ADAuthenticator.class);
//		try {
//			HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//			Context loginContext = new Context("52336", "jan13@AIB", request);
//			authenticator.doAuthenticate(loginContext);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
	}
}
