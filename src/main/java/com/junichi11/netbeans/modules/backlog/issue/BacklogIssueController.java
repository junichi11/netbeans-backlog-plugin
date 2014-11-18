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

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.junichi11.netbeans.modules.backlog.issue.ui.BacklogIssuePanel;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
final class BacklogIssueController implements IssueController, ChangeListener {

    private BacklogIssuePanel panel;
    private boolean isChanged;

    public BacklogIssueController(BacklogIssue issue) {
        getPanel().setIssue(issue);
        getPanel().update(true);
        getPanel().scrollToTop();
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void opened() {
        validate();
    }

    @Override
    public void closed() {
    }

    @Override
    public boolean saveChanges() {
        // TODO
        return true;
    }

    @Override
    public boolean discardUnsavedChanges() {
        // TODO
        return true;
    }

    @Override
    public boolean isChanged() {
        // TODO
        return false;
    }

    void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPanel().getIssue().addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPanel().getIssue().removePropertyChangeListener(listener);
    }

    private BacklogIssuePanel getPanel() {
        if (panel == null) {
            panel = new BacklogIssuePanel();
            panel.addChangeListener(this);
        }
        return panel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof BacklogIssuePanel) {
            validate();
        }
    }

    @NbBundle.Messages({
        "# {0} - name",
        "BacklogIssueController.empty.error={0} must be set",
        "BacklogIssueController.summary=Summary",
        "BacklogIssueController.date.format.error=Enter in YYYY/MM/DD format (e.g. 2014/09/06)",
        "BacklogIssueController.hours.format.error=Number must be set to actual and estimated hours(e.g. 1, 0.25, 36)"
    })
    void validate() {
        BacklogIssuePanel p = getPanel();
        p.setSubmitButtonEnabled(false);
        setChanged(false);
        // summary
        String summary = p.getSummary();
        if (StringUtils.isEmpty(summary)) {
            p.setError(Bundle.BacklogIssueController_empty_error(Bundle.BacklogIssueController_summary()));
            return;
        }

        // date
        String startDate = p.getStartDate();
        String dueDate = p.getDueDate();
        if (!isDateValid(startDate) || !isDateValid(dueDate)) {
            p.setError(Bundle.BacklogIssueController_date_format_error());
            return;
        }

        // hours
        String actualHours = p.getActualHours();
        String estimatedHours = p.getEstimatedHours();
        if (!isHoursValid(actualHours) || !isHoursValid(estimatedHours)) {
            p.setError(Bundle.BacklogIssueController_hours_format_error());
            return;
        }

        // everything ok
        setChanged(true);
        p.setSubmitButtonEnabled(true);
        p.setError(" "); // NOI18N
    }

    private boolean isDateValid(String date) {
        if (!StringUtils.isEmpty(date) && !BacklogUtils.isDateFormat(date)) {
            return false;
        }
        return true;
    }

    private boolean isHoursValid(String hours) {
        if (StringUtils.isEmpty(hours)) {
            return true;
        }
        boolean isFloat = true;
        Float f = null;
        try {
            f = Float.valueOf(hours);
        } catch (NumberFormatException ex) {
            isFloat = false;
        }
        if (isFloat == false || f == null || f.isInfinite() || f.isNaN()) {
            return false;
        }
        return true;
    }

}
