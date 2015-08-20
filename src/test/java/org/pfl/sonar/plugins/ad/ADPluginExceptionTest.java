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
