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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.config.Settings;

/**
 * Test case for ContextUtil
 * @author Jiji Sasidharan
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ContextUtil.class, InitialLdapContext.class})
public class ContextUtilTest {

    @SuppressWarnings("unchecked")
    private ADSettings getMocks(final String domainName, final String userName, final String password) throws Exception {
        Settings settings = new Settings();
        settings.setProperty("sonar.ad.domain", domainName);
        ADSettings adSettings = spy(new ADSettings(settings));
        doReturn(new TreeSet<ADServerEntry>(Arrays.asList((new ADServerEntry(0, 1, "ldap.mycompany.com", 389)))))
            .when(adSettings, "fetchProviderList", domainName);

        final InitialLdapContext dirCtx = mock(InitialLdapContext.class);
        whenNew(InitialLdapContext.class).withAnyArguments().thenAnswer(new Answer<InitialLdapContext>() {
            public InitialLdapContext answer(InvocationOnMock invocation)
                    throws Throwable {
                Properties env = (Properties) invocation.getArguments()[0];
                if (env.get(Constants.SECURITY_PRINCIPAL).equals(userName + "@" + domainName) &&
                    env.get(Constants.SECURITY_CREDENTIALS).equals(password))
                    return dirCtx;
                throw new Exception("bind failed for user " + userName);
            }
        });

        when(dirCtx.search(anyString(), anyString(), isA(SearchControls.class))).thenReturn(null);

        whenNew(ADUser.class).withArguments(userName)
                             .thenAnswer(new Answer<ADUser>() {
                                public ADUser answer(InvocationOnMock invocation)
                                        throws Throwable {
                                     ADUser user = spy (new ADUser(userName));
                                     doNothing().when(user).populate(any(NamingEnumeration.class));
                                     return user;
                                 }
                             });
        adSettings.load();
        return adSettings;
    }

    /**
     * Scenario:
     *   Open Context with right credentials.
     *
     * @throws Exception
     */
    @Test
    public void testOpenWithRightCredentials() throws Exception {
        String user = "user", password = "secret";
        ADSettings adSettings = getMocks("ad.mycompany.com", user, password);
        DirContext ctx = ContextUtil.open(adSettings, user, password);
        assertNotNull(ctx);
        assertNotNull(ContextUtil.getLoggedInUser());
        assertEquals(ContextUtil.getLoggedInUser().getUserName(), user);
    }

    /**
     * Scenario:
     *   Open context with wrong credentials.
     *
     * @throws Exception
     */
    @Test
    public void testOpenWithWrongCredentials() throws Exception {
        String user = "user", password = "secret";
        ADSettings adSettings = getMocks("ad.mycompany.com", user, password);
        DirContext ctx = ContextUtil.open(adSettings, user, "not secret");
        assertNull(ctx);
        assertNull(ContextUtil.getLoggedInUser());
    }

    /**
     * Scenario:
     *   Make sure user principal is correctly assembled.
     *
     * @throws Exception
     */
    @Test
    public void testOpenWithWrongPrincipal() throws Exception {
        String user = "user", password = "secret";
        ADSettings adSettings = getMocks("ad.mycompany.com", user, password);
        doReturn("wrong domain").when(adSettings).getDnsDomain();
        DirContext ctx = ContextUtil.open(adSettings, user, password);
        assertNull(ctx);
        assertNull(ContextUtil.getLoggedInUser());
    }

    /**
     * Scenario:
     *   1) Make sure user principal is correctly assembled.
     *
     * @throws Exception
     */
    @Test
    public void testDirContextClose() throws Exception {
        DirContext dirContext = mock(DirContext.class);
        ContextUtil.close(dirContext);
        verify(dirContext).close();
    }
}
