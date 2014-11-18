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
package com.junichi11.netbeans.modules.backlog.utils;

import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author junichi11
 */
public class StringUtilsTest {

    public StringUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isEmpty method, of class StringUtils.
     */
    @Test
    public void testIsEmpty_String() {
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty((String) null));

        assertFalse(StringUtils.isEmpty("test"));
        assertFalse(StringUtils.isEmpty(" "));
    }

    /**
     * Test of isEmpty method, of class StringUtils.
     */
    @Test
    public void testIsEmpty_List() {
        assertTrue(StringUtils.isEmpty(Arrays.asList("")));
        assertTrue(StringUtils.isEmpty(Arrays.asList((String) null)));
        assertTrue(StringUtils.isEmpty(Arrays.asList("", "")));
        assertTrue(StringUtils.isEmpty(Arrays.asList((String) null, "")));

        assertFalse(StringUtils.isEmpty(Arrays.asList("", "test")));
        assertFalse(StringUtils.isEmpty(Arrays.asList((String) null, "test")));
        assertFalse(StringUtils.isEmpty(Arrays.asList("test1", "test2")));
    }

    /**
     * Test of toQuoteComment method, of class StringUtils.
     */
    @Test
    public void testToQuoteComment() {
        assertEquals(null, StringUtils.toQuoteComment(null));
        assertEquals("> test\n", StringUtils.toQuoteComment("test"));
        assertEquals("> line1\n> line2\n", StringUtils.toQuoteComment("line1\nline2"));
        assertEquals("> line1\n> line2\n", StringUtils.toQuoteComment("line1\nline2\n"));
    }

}
