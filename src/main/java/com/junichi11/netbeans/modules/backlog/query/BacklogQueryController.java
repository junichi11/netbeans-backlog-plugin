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
package com.junichi11.netbeans.modules.backlog.query;

import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Collections;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.CheckForNull;
import com.junichi11.netbeans.modules.backlog.Backlog;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.ui.IssueTableCellRenderer;
import com.junichi11.netbeans.modules.backlog.query.ui.BacklogQueryPanel;
import com.junichi11.netbeans.modules.backlog.query.ui.DatePanel;
import com.junichi11.netbeans.modules.backlog.query.ui.GeneralPanel;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import org.netbeans.modules.bugtracking.commons.SaveQueryPanel;
import org.netbeans.modules.bugtracking.issuetable.IssueTable;
import org.netbeans.modules.bugtracking.issuetable.QueryTableCellRenderer;
import org.netbeans.modules.bugtracking.spi.QueryController;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class BacklogQueryController implements QueryController, ActionListener {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private BacklogQueryPanel panel;
    private IssueTable issueTable;

    private final BacklogRepository repository;
    private final BacklogQuery query;

    public BacklogQueryController(BacklogRepository repository, BacklogQuery query) {
        this.repository = repository;
        this.query = query;
    }

    @Override
    public boolean providesMode(QueryMode qm) {
        if (query instanceof DefaultQuery) {
            return false;
        }
        if (qm == QueryMode.VIEW) {
            return false;
        }
        return true;
    }

    @Override
    public JComponent getComponent(QueryMode qm) {
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void opened() {
        if (query.isSaved()) {
            search();
        }
    }

    @Override
    public void closed() {
    }

    @Override
    public boolean saveChanges(String string) {
        return true;
    }

    @Override
    public boolean discardUnsavedChanges() {
        return true;
    }

    @Override
    public boolean isChanged() {
        // TODO
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener instanceof IssueTable) {
            // don't use recent changes column
            return;
        }
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener instanceof IssueTable) {
            // don't use recent changes column
            return;
        }
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    void firePropertyChange() {
        propertyChangeSupport.firePropertyChange(QueryController.PROP_CHANGED, null, null);
    }

    public BacklogRepository getRepository() {
        return repository;
    }

    private void saveQuery() {
        repository.saveQuery(query);
    }

    private BacklogQueryPanel getPanel() {
        if (panel == null) {
            // don't use recent changes column
            issueTable = new IssueTable(repository.getID(), query.getDisplayName(), this, query.getColumnDescriptors(), false);
            issueTable.setRenderer(new IssueTableCellRenderer((QueryTableCellRenderer) issueTable.getRenderer()));
            issueTable.initColumns();
            panel = new BacklogQueryPanel(repository, query, issueTable.getComponent());
            panel.addSearchButtonActionListener(this);
            panel.addSaveButtonActionListener(this);
        }
        return panel;
    }

    @NbBundle.Messages({
        "# {0} - count",
        "BacklogQueryController.label.matching.issues=There are {0} issues matching this query.",
        "BacklogQueryController.label.matching.issue=There is 1 issue matching this query.",
        "BacklogQueryController.label.matching.no.issue=There is no issue matching this query."
    })
    private void search() {
        // clear node
        issueTable.started();
        GetIssuesParams issuesParams = createIssuesParams();
        if (issuesParams == null) {
            return;
        }
        Collection<BacklogIssue> issues = query.getIssues(issuesParams);
        int issueCount = issues.size();
        String text = Bundle.BacklogQueryController_label_matching_no_issue();
        if (issueCount == 1) {
            text = Bundle.BacklogQueryController_label_matching_issue();
        } else if (issueCount > 1) {
            text = Bundle.BacklogQueryController_label_matching_issues(issueCount);
        }
        StatusDisplayer.getDefault().setStatusText(text);
        for (BacklogIssue issue : issues) {
            issueTable.addNode(issue.getIssueNode());
        }
    }

    @NbBundle.Messages({
        "BacklogQueryController.message.error.empty.name=Query name must be set.",
        "BacklogQueryController.message.error.already.exists=Already exists.",
        "BacklogQueryController.message.saved=Query has been saved."
    })
    private void save() {
        GetIssuesParams issuesParams = createIssuesParams();
        if (issuesParams == null) {
            return;
        }
        if (!query.isSaved()) {
            SaveQueryPanel.QueryNameValidator validator = new SaveQueryPanel.QueryNameValidator() {
                @Override
                public String isValid(String name) {
                    if (StringUtils.isEmpty(name)) {
                        return Bundle.BacklogQueryController_message_error_empty_name();
                    }
                    Collection<BacklogQuery> queries = repository.getQueries();
                    for (BacklogQuery query : queries) {
                        if (query.getDisplayName().equals(name)) {
                            return Bundle.BacklogQueryController_message_error_already_exists();
                        }
                    }
                    return null;
                }
            };
            // if cancel button is pressed, null value will be returned
            String queryName = SaveQueryPanel.show(validator, HelpCtx.DEFAULT_HELP);
            if (StringUtils.isEmpty(queryName)) {
                return;
            }
            query.setName(queryName);
        }
        query.setQueryParam(issuesParams);
        saveQuery();
        query.setSaved(true);
        getPanel().update();
        StatusDisplayer.getDefault().setStatusText(Bundle.BacklogQueryController_message_saved());
        query.refresh();
        firePropertyChange();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getPanel().isSearchButton(e.getSource())) {
            search();
            getPanel().scrollToBottom();
        } else if (getPanel().isSaveButton(e.getSource())) {
            RequestProcessor rp = Backlog.getInstance().getRequestProcessor();
            rp.post(new Runnable() {

                @Override
                public void run() {
                    getPanel().setEnabled(false);
                    save();
                    getPanel().setEnabled(true);
                }
            });
        }
    }

    @CheckForNull
    private GetIssuesParams createIssuesParams() {
        Project project = repository.getProject();
        if (project == null) {
            return null;
        }
        GeneralPanel generalPanel = getPanel().getGeneralPanel();
        DatePanel datePanel = getPanel().getDatePanel();
        GetIssuesParams issuesParams = new GetIssuesParams(Collections.singletonList(project.getId()));
        issuesParams = issuesParams
                .keyword(getPanel().getKeyword())
                // general
                .statuses(generalPanel.getStatus())
                .priorities(generalPanel.getPriories())
                .categoryIds(generalPanel.getCategoryIds())
                .assignerIds(generalPanel.getAssignerIds())
                .versionIds(generalPanel.getVersionIds())
                .createdUserIds(generalPanel.getCreatedUserIds())
                .milestoneIds(generalPanel.getMilestoneIds())
                .resolutions(generalPanel.getResolutions())
                .issueTypeIds(generalPanel.getIssueTypeIds())
                // date
                .createdSince(BacklogUtils.toApiDateFormat(datePanel.getCreatedSince()))
                .createdUntil(BacklogUtils.toApiDateFormat(datePanel.getCreatedUntil()))
                .updatedSince(BacklogUtils.toApiDateFormat(datePanel.getUpdatedSince()))
                .updatedUntil(BacklogUtils.toApiDateFormat(datePanel.getUpdatedUntil()))
                .startDateSince(BacklogUtils.toApiDateFormat(datePanel.getStartDateSince()))
                .startDateUntil(BacklogUtils.toApiDateFormat(datePanel.getStartDateUntil()))
                .dueDateSince(BacklogUtils.toApiDateFormat(datePanel.getDueDateSince()))
                .dueDateUntil(BacklogUtils.toApiDateFormat(datePanel.getDueDateUntil()));
        // file
        if (getPanel().getGeneralPanel().isAttached()) {
            issuesParams = issuesParams.attachment(true);
        }
        if (getPanel().getGeneralPanel().isShared()) {
            issuesParams = issuesParams.sharedFile(true);
        }
        return issuesParams;
    }
}
