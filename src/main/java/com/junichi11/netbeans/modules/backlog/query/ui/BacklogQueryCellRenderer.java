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
package com.junichi11.netbeans.modules.backlog.query.ui;

import com.nulabinc.backlog4j.IssueType;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssueNode.IssueTypeProperty;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssueNode.PriorityProperty;
import com.junichi11.netbeans.modules.backlog.utils.BacklogImage;
import org.netbeans.modules.bugtracking.issuetable.QueryTableCellRenderer;

/**
 *
 * @author junichi11
 */
public class BacklogQueryCellRenderer implements TableCellRenderer {

    private final QueryTableCellRenderer defaultRenderer;

    public BacklogQueryCellRenderer(QueryTableCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof PriorityProperty) {
            PriorityProperty priority = (PriorityProperty) value;
            BacklogImage priorityIcon = BacklogImage.getPriorityIcon(priority.getPriority());
            if (priorityIcon != null) {
                label.setIcon(priorityIcon.getIcon());
                return label;
            }
        }

        if (value instanceof IssueTypeProperty) {
            IssueTypeProperty type = (IssueTypeProperty) value;
            IssueType issueType = type.getIssueType();
            if (issueType != null) {
                String colorString = issueType.getColor().getStrValue();
                Color color = Color.decode(colorString);
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                } else {
                    label.setBackground(color);
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        }
        return label;
    }

}
