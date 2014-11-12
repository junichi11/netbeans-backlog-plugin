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
package com.junichi11.netbeans.modules.backlog;

import org.netbeans.api.annotations.common.CheckForNull;

/**
 *
 * @author junichi11
 */
public enum BacklogPlan {

    FREE {

                @Override
                public long getSize() {
                    return FREE_PLAN_SIZE;
                }

                @Override
                public long getProjectCount() {
                    return 1L;
                }

                @Override
                public long getUserCount() {
                    return 10L;
                }

                @Override
                public long getAttachmentCount() {
                    return 1L;
                }

                @Override
                public long getAttachmentMaxSize() {
                    return 5L * MEGA;
                }
            },
    BASIC {

                @Override
                public long getSize() {
                    return BASIC_PLAN_SIZE;
                }

                @Override
                public long getProjectCount() {
                    return 5L;
                }

                @Override
                public long getUserCount() {
                    return 30L;
                }

                @Override
                public long getAttachmentCount() {
                    return 10L;
                }

                @Override
                public long getAttachmentMaxSize() {
                    return 10L * MEGA;
                }
            },
    PREMIUM {

                @Override
                public long getSize() {
                    return PREMIUM_PLAN_SIZE;
                }

                @Override
                public long getProjectCount() {
                    return 100L;
                }

                @Override
                public long getUserCount() {
                    // infinity
                    return Long.MAX_VALUE;
                }

                @Override
                public long getAttachmentCount() {
                    return 30L;
                }

                @Override
                public long getAttachmentMaxSize() {
                    return 10L * MEGA;
                }
            },
    MAX {

                @Override
                public long getSize() {
                    return MAX_PLAN_SIZE;
                }

                @Override
                public long getProjectCount() {
                    // infinity
                    return Long.MAX_VALUE;
                }

                @Override
                public long getUserCount() {
                    // infinity
                    return Long.MAX_VALUE;
                }

                @Override
                public long getAttachmentCount() {
                    return 50L;
                }

                @Override
                public long getAttachmentMaxSize() {
                    return 10L * MEGA;
                }
            };

    private static final long MEGA = 1024L * 1024L;
    private static final long GIGA = MEGA * 1024L;
    private static final long FREE_PLAN_SIZE = 100 * MEGA;
    private static final long BASIC_PLAN_SIZE = 1 * GIGA;
    private static final long PREMIUM_PLAN_SIZE = 30 * GIGA;
    private static final long MAX_PLAN_SIZE = 100 * GIGA;

    public abstract long getSize();

    public abstract long getProjectCount();

    public abstract long getUserCount();

    public abstract long getAttachmentCount();

    public abstract long getAttachmentMaxSize();

    @CheckForNull
    public static BacklogPlan valueOfCapacity(long capacity) {
        if (capacity == FREE_PLAN_SIZE) {
            return FREE;
        } else if (capacity == BASIC_PLAN_SIZE) {
            return BASIC;
        } else if (capacity == PREMIUM_PLAN_SIZE) {
            return PREMIUM;
        } else if (capacity >= MAX_PLAN_SIZE) {
            return MAX;
        }
        return null;
    }

}
