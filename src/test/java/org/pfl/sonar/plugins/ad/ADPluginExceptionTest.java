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

import org.junit.Test;

/**
 * Test case for {@link ADPluginException}
 * @author Jiji Sasidharan
 */
public class ADPluginExceptionTest {

	/**
	 * Scenario:
	 *   a) Exception is thrown with error message
	 */
	@Test
	public void testADPluginExceptionWithMessage() {
		String message = "some error message";
		ADPluginException exception = new ADPluginException(message);
		assertNotNull("Exception message is not set", exception.getMessage());
		assertEquals("Exception message is incorrect", exception.getMessage(), message);
	}

	/**
	 * Scenario:
	 *  a) Exception is thrown with error message and another exception
	 */
	@Test
	public void testADPluginExceptionWithMessageAndCauseException() {
		String message = "some error message";
		Throwable t = new Throwable("rootcause");
		ADPluginException exception = new ADPluginException(message, t);
		assertNotNull("Exception message is missing", exception.getMessage());
		assertEquals("Exception message is incorrect", exception.getMessage(), message);
		assertNotNull("Chained exception is missing", exception.getCause());
	}

	/**
	 * Scenario:
	 *   a) Exception is thrown with no message (null 
	 */
	@Test
	public void testADPluginExceptionWithNullMessage() {
		ADPluginException exception = new ADPluginException(null);
		assertNull("Exception message is not set", exception.getMessage());
	}

	/**
	 * Scenario:
	 *  a) Exception is thrown with error message and null exception
	 */
	@Test
	public void testADPluginExceptionWithMessageAndNullCauseException() {
		String message = "some error message";
		ADPluginException exception = new ADPluginException(message, null);
		assertNotNull("Exception message is missing", exception.getMessage());
		assertEquals("Exception message is incorrect", exception.getMessage(), message);
		assertNull("Chained exception is missing", exception.getCause());
	}

	/**
	 * Scenario:
	 *  a) Exception is thrown with Null error message and null exception
	 */
	@Test
	public void testADPluginExceptionWithNullMessageAndNullCauseException() {
		ADPluginException exception = new ADPluginException(null, null);
		assertNull("Exception message is missing", exception.getMessage());
		assertNull("Chained exception is missing", exception.getCause());
	}
}
