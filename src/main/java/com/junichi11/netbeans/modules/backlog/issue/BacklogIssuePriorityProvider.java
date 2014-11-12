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

import com.nulabinc.backlog4j.Issue.PriorityType;
import com.nulabinc.backlog4j.Priority;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import com.junichi11.netbeans.modules.backlog.utils.BacklogImage;
import org.netbeans.modules.bugtracking.spi.IssuePriorityInfo;
import org.netbeans.modules.bugtracking.spi.IssuePriorityProvider;
import org.openide.util.ImageUtilities;

/**
 *
 * @author junichi11
 */
public class BacklogIssuePriorityProvider implements IssuePriorityProvider<BacklogIssue> {

    @Override
    public String getPriorityID(BacklogIssue issue) {
        // TODO implement
        Priority priority = issue.getPriority();
        if (priority == null) {
            return ""; // NOI18N
        }
        return String.valueOf(priority.getId());
    }

    @Override
    public IssuePriorityInfo[] getPriorityInfos() {
        // TODO implement
        List<IssuePriorityInfo> issuePriorityInfos = new ArrayList<>();
        for (PriorityType priorityType : PriorityType.values()) {
            Image image = null;
            switch (priorityType) {
                case High:
                    image = ImageUtilities.loadImage(BacklogImage.PRIORITY_HIGH.getImagePath(), true);
                    break;
                case Normal:
                    image = ImageUtilities.loadImage(BacklogImage.PRIORITY_NORMAL.getImagePath(), true);
                    break;
                case Low:
                    image = ImageUtilities.loadImage(BacklogImage.PRIORITY_LOW.getImagePath(), true);
                    break;
                default:
                    break;
            }
            IssuePriorityInfo isuePriorityInfo;
            if (image != null) {
                isuePriorityInfo = new IssuePriorityInfo(String.valueOf(priorityType.getIntValue()), priorityType.name(), image);
            } else {
                isuePriorityInfo = new IssuePriorityInfo(String.valueOf(priorityType.getIntValue()), priorityType.name());
            }
            issuePriorityInfos.add(isuePriorityInfo);
        }
        return issuePriorityInfos.toArray(new IssuePriorityInfo[issuePriorityInfos.size()]);
    }

}
