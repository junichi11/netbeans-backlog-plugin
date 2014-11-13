/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package com.junichi11.netbeans.modules.backlog.issue;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author junichi11
 */
public class BacklogIssueFinderTest {

    private BacklogIssueFinder finder;

    public BacklogIssueFinderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        finder = new BacklogIssueFinder("TEST");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getIssueSpans method, of class BacklogIssueFinder.
     */
    @Test
    public void testGetIssueSpans() {
        int[] result = finder.getIssueSpans("TEST-7");
        assertArrayEquals(new int[]{0, 6}, result);
        result = finder.getIssueSpans("[[TEST-7]]");
        assertArrayEquals(new int[]{2, 8}, result);
        result = finder.getIssueSpans("It's [[TEST-7]] and TEST-15");
        assertArrayEquals(new int[]{7, 13, 20, 27}, result);
        result = finder.getIssueSpans("TEST-1TEST-2");
        assertArrayEquals(new int[]{0, 6, 6, 12}, result);

        result = finder.getIssueSpans("[[test-7]]");
        assertArrayEquals(new int[0], result);
        result = finder.getIssueSpans("TEST100");
        assertArrayEquals(new int[0], result);

        finder = new BacklogIssueFinder(null);
        result = finder.getIssueSpans("TEST-1");
        assertArrayEquals(new int[0], result);
    }

    /**
     * Test of getIssueId method, of class BacklogIssueFinder.
     */
    @Test
    public void testGetIssueId() {
        assertEquals("1", finder.getIssueId("TEST-1"));
        assertEquals("", finder.getIssueId("TEST1"));
    }

}
