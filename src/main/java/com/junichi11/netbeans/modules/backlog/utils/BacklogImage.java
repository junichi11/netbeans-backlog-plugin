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

import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.Priority;
import java.awt.Image;
import javax.swing.Icon;
import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.util.ImageUtilities;

/**
 *
 * @author junichi11
 */
public enum BacklogImage {

    ICON_16("icon_16.png"), // NOI18N
    ICON_32("icon_32.png"), // NOI18N
    PRIORITY_HIGH("priority_high.png"), // NOI18N
    PRIORITY_NORMAL("priority_normal.png"), // NOI18N
    PRIORITY_LOW("priority_low.png"), // NOI18N
    ERROR_16("error_icon_16.png"); // NOI18N

    private final String name;
    private static final String RESOUCE_PATH_FORMAT = "com/junichi11/netbeans/modules/backlog/resources/%s"; // NOI18N

    private BacklogImage(String name) {
        this.name = name;
    }

    public Image getImage() {
        return ImageUtilities.loadImage(getImagePath(), true);
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon(getImagePath(), true);
    }

    public String getImagePath() {
        return String.format(RESOUCE_PATH_FORMAT, name);
    }

    @CheckForNull
    public static final BacklogImage getPriorityIcon(Priority priority) {
        if (priority == null) {
            return null;
        }
        Issue.PriorityType priorityType = priority.getPriority();
        if (priorityType == null) {
            return null;
        }
        switch (priority.getPriority()) {
            case High:
                return PRIORITY_HIGH;
            case Normal:
                return PRIORITY_NORMAL;
            case Low:
                return PRIORITY_LOW;
            default:
                throw new AssertionError();
        }
    }
}
