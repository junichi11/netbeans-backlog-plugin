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
package com.junichi11.netbeans.modules.backlog.ui;

import com.nulabinc.backlog4j.Category;
import com.nulabinc.backlog4j.IssueType;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Resolution;
import com.nulabinc.backlog4j.Status;
import com.nulabinc.backlog4j.User;
import com.nulabinc.backlog4j.Version;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import com.junichi11.netbeans.modules.backlog.Backlog.FileType;
import com.junichi11.netbeans.modules.backlog.utils.BacklogImage;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class AttributesListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 8440399918914460498L;

    private final ListCellRenderer renderer;

    public AttributesListCellRenderer(ListCellRenderer renderer) {
        this.renderer = renderer;
    }

    @NbBundle.Messages({
        "AttributesListCellRenderer.file.type.attached=Attached",
        "AttributesListCellRenderer.file.type.shared=Shared"
    })
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String text = null;
        if (value instanceof IssueType) {
            IssueType issueType = (IssueType) value;
            text = issueType.getName();
        } else if (value instanceof Status) {
            Status status = (Status) value;
            text = status.getName();
        } else if (value instanceof Priority) {
            Priority priority = (Priority) value;
            text = priority.getName();
            if (text == null) {
                text = " "; // NOI18N
            }
            JLabel label = (JLabel) renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            BacklogImage priorityIcon = BacklogImage.getPriorityIcon(priority);
            if (priorityIcon != null) {
                label.setIcon(priorityIcon.getIcon());
            }
            return label;
        } else if (value instanceof User) {
            User user = (User) value;
            text = user.getName();
        } else if (value instanceof Category) {
            Category category = (Category) value;
            text = category.getName();
        } else if (value instanceof Version) {
            Version version = (Version) value;
            text = version.getName();
        } else if (value instanceof Resolution) {
            Resolution resolution = (Resolution) value;
            text = resolution.getName();
        } else if (value instanceof FileType) {
            FileType fileType = (FileType) value;
            switch (fileType) {
                case NONE:
                    text = " "; // NOI18N
                    break;
                case ATTACHED:
                    text = Bundle.AttributesListCellRenderer_file_type_attached();
                    break;
                case SHARED:
                    text = Bundle.AttributesListCellRenderer_file_type_shared();
                    break;
                default:
                    throw new AssertionError();
            }
        }
        if (text == null) {
            text = " "; // NOI18N
        }
        return renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }

}
