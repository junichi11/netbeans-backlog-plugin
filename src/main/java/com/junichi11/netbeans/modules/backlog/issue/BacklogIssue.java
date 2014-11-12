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

import com.nulabinc.backlog4j.Attachment;
import com.nulabinc.backlog4j.AttachmentData;
import com.nulabinc.backlog4j.BacklogAPIException;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.IssueType;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Resolution;
import com.nulabinc.backlog4j.ResponseList;
import com.nulabinc.backlog4j.User;
import com.nulabinc.backlog4j.api.option.CreateIssueParams;
import com.nulabinc.backlog4j.api.option.UpdateIssueParams;
import com.nulabinc.backlog4j.internal.file.AttachmentDataImpl;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import org.netbeans.api.annotations.common.CheckForNull;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import org.netbeans.modules.bugtracking.commons.UIUtils;
import org.netbeans.modules.bugtracking.issuetable.ColumnDescriptor;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.netbeans.modules.bugtracking.spi.IssueProvider;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider.Status;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class BacklogIssue {

    private Issue issue;
    private String summary;
    private BacklogIssueController controller;
    private IssueNode node;
    private final BacklogRepository repository;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public static final String LABEL_NAME_ID = "backlog.issue.id"; // NOI18N
    public static final String LABEL_NAME_PRIORITY = "backlog.issue.priority"; // NOI18N
    public static final String LABEL_NAME_ISSUE_TYPE = "backlog.issue.type"; // NOI18N
    public static final String LABEL_NAME_STATUS = "backlog.issue.status"; // NOI18N
    public static final String LABEL_NAME_CREATED = "backlog.issue.created"; // NOI18N
    public static final String LABEL_NAME_DUE_DATE = "backlog.issue.due.date"; // NOI18N
    public static final String LABEL_NAME_UPDATED = "backlog.issue.updated"; // NOI18N
    public static final String LABEL_NAME_REGISTERED_BY = "backlog.issue.registered.by"; // NOI18N
    public static final String LABEL_NAME_ASSIGNEE = "backlog.issue.assignee"; // NOI18N
    public static final String LABEL_NAME_ATTACHMENT = "backlog.issue.attachment"; // NOI18N
    public static final String LABEL_NAME_SHARED_FILE = "backlog.issue.shared.file"; // NOI18N

    private static final Logger LOGGER = Logger.getLogger(BacklogIssue.class.getName());

    public BacklogIssue(BacklogRepository repository) {
        this.repository = repository;
    }

    public BacklogIssue(BacklogRepository repository, Issue issue) {
        this.repository = repository;
        setIssue(issue);
    }

    public BacklogRepository getRepository() {
        return repository;
    }

    /**
     * Get display name.
     *
     * @return String for display
     */
    @NbBundle.Messages({
        "BacklogIssue.new.issue.display.name=New Issue"
    })
    public String getDisplayName() {
        if (isNew()) {
            return Bundle.BacklogIssue_new_issue_display_name();
        }
        String issueKey = getIssueKey();
        if (issueKey == null) {
            return summary;
        }
        return String.format("[%s] %s", issueKey, summary); // NOI18N
    }

    /**
     * Get tooltip.
     *
     * @return String for tooltip
     */
    public String getTooltip() {
        return getDisplayName();
    }

    /**
     * Get issue id.
     *
     * @return issue id if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public String getID() {
        if (issue != null) {
            return String.valueOf(issue.getId());
        }
        return null;
    }

    /**
     * Get issue key.
     *
     * @return issue key if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public String getIssueKey() {
        if (issue != null) {
            return issue.getIssueKey();
        }
        return null;
    }

    /**
     * Get issue key id.
     *
     * @return issue key id if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public String getKeyId() {
        if (issue != null) {
            return String.valueOf(issue.getKeyId());
        }
        return null;
    }

    /**
     * Get summary.
     *
     * @return summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Get priority.
     *
     * @return priority if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public Priority getPriority() {
        if (issue == null) {
            return null;
        }
        return issue.getPriority();
    }

    /**
     * Get issue type.
     *
     * @return issue type if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public IssueType getIssueType() {
        if (issue == null) {
            return null;
        }
        return issue.getIssueType();
    }

    /**
     * Get issue status.
     *
     * @return issue status if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public com.nulabinc.backlog4j.Status getIssueStatus() {
        if (issue == null) {
            return null;
        }
        return issue.getStatus();
    }

    /**
     * Get created date.
     *
     * @return Created date if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public Date getCreated() {
        if (issue == null) {
            return null;
        }
        return issue.getCreated();
    }

    /**
     * Get updated date.
     *
     * @return Updated date if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public Date getUpdated() {
        if (issue == null) {
            return null;
        }
        return issue.getUpdated();
    }

    /**
     * Get due date.
     *
     * @return Due date if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public Date getDueDate() {
        if (issue == null) {
            return null;
        }
        return issue.getDueDate();
    }

    /**
     * Get created user.
     *
     * @return Created user if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public User getCreatedUser() {
        if (issue == null) {
            return null;
        }
        return issue.getCreatedUser();
    }

    /**
     * Get assingnee.
     *
     * @return Assingnee if issue is not {@code null}, {@code null} otherwise
     */
    @CheckForNull
    public User getAssignee() {
        if (issue == null) {
            return null;
        }
        return issue.getAssignee();
    }

    /**
     * Whether an issue has attachments.
     *
     * @return {@code true} if an issue has attachments, {@code false} otherwise
     */
    public boolean hasAttachment() {
        if (issue == null) {
            return false;
        }
        return !issue.getAttachments().isEmpty();
    }

    /**
     * Whether an issue has shared files.
     *
     * @return {@code true} if an issue has shared files, {@code false}
     * otherwise
     */
    public boolean hasSharedFile() {
        if (issue == null) {
            return false;
        }
        return !issue.getSharedFiles().isEmpty();
    }

    /**
     * Whether current issue is new.
     *
     * @return {@code true} if this don't have existing issue, {@code false}
     * otherwise
     */
    public boolean isNew() {
        return issue == null;
    }

    /**
     * Refresh issue. Get newer issue with API.
     */
    public void refresh() {
        if (issue == null) {
            return;
        }
        long id = issue.getId();
        BacklogClient backlogClient = repository.createBacklogClient();
        try {
            issue = backlogClient.getIssue(id);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    /**
     * Get issue status.
     *
     * @return status
     */
    public Status getStatus() {
        // TODO
        return Status.SEEN;
    }

    /**
     * Get recent changes.
     *
     * @return changes
     */
    public String getRecentChanges() {
        // TODO
        return "";
    }

    void fireChange() {
        changeSupport.firePropertyChange(IssueController.PROP_CHANGED, null, null);
    }

    void fireDataChange() {
        changeSupport.firePropertyChange(IssueProvider.EVENT_ISSUE_DATA_CHANGED, null, null);
    }

    void fireStatusChange() {
        changeSupport.firePropertyChange(IssueStatusProvider.EVENT_STATUS_CHANGED, null, null);
    }

    /**
     * Add an issue.
     *
     * @param issueParams An issue params for adding
     * @return Created issue if it's successful to add an issue, {@code null}
     * otherwise
     */
    @CheckForNull
    public Issue addIssue(CreateIssueParams issueParams) {
        BacklogClient backlogClient = repository.createBacklogClient();
        Issue createdIssue = null;
        try {
            createdIssue = backlogClient.createIssue(issueParams);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        if (createdIssue != null) {
            setIssue(createdIssue);
            fireChange();
            fireDataChange();
//            fireStatusChange();
            ((BacklogIssueController) getController()).setChanged(false);
        }
        return createdIssue;
    }

    /**
     * Update an issue.
     *
     * @param issueParams An issue params for updating
     * @return Created issue if it's successful to update an issue, {@code null}
     * otherwise
     */
    @CheckForNull
    public Issue updateIssue(UpdateIssueParams issueParams) {
        BacklogClient backlogClient = repository.createBacklogClient();
        Issue updatedIssue = null;
        try {
            updatedIssue = backlogClient.updateIssue(issueParams);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        if (updatedIssue != null) {
            setIssue(updatedIssue);
            fireChange();
            fireDataChange();
            fireStatusChange();
            ((BacklogIssueController) getController()).setChanged(false);
        }
        return updatedIssue;
    }

    /**
     * Get attachments.
     *
     * @return Attachment list
     */
    public List<BacklogAttachment> getAttachments() {
        BacklogClient backlogClient = repository.createBacklogClient();
        try {
            long issueId = issue.getId();
            ResponseList<Attachment> issueAttachments = backlogClient.getIssueAttachments(issueId);
            ArrayList<BacklogAttachment> backlogIssueAttachments = new ArrayList<>();
            for (Attachment issueAttachment : issueAttachments) {
                backlogIssueAttachments.add(new BacklogAttachment(issueAttachment, this));
            }
            return backlogIssueAttachments;
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Get attachment data with specified id.
     *
     * @param attachmentId attachment id
     * @return attachment data
     */
    public AttachmentData getAttachmentData(long attachmentId) {
        BacklogClient backlogClient = repository.createBacklogClient();
        AttachmentData attachmentData = null;
        try {
            attachmentData = backlogClient.downloadIssueAttachment(issue.getId(), attachmentId);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return attachmentData;
    }

    /**
     * Set an existing issue.
     *
     * @param issue an existing issue
     */
    private void setIssue(Issue issue) {
        this.issue = issue;
        this.summary = issue.getSummary();
    }

    /**
     * Get an issue.
     *
     * @return an issue
     */
    public Issue getIssue() {
        return issue;
    }

    /**
     * Post an attachment file. If the file is not attached to an issue, the
     * file will be removed after one hour.
     *
     * @param file
     * @param description
     * @return
     * @throws FileNotFoundException
     */
    @CheckForNull
    public Attachment postAttachmentFile(File file, String description) throws FileNotFoundException {
        BacklogClient backlogClient = repository.createBacklogClient();
        try {
            return backlogClient.postAttachment(new AttachmentDataImpl(file.getName(), new FileInputStream(file)));
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    /**
     * Get issue controller.
     *
     * @return issue controller
     */
    public IssueController getController() {
        if (controller == null) {
            controller = new BacklogIssueController(this);
        }
        return controller;
    }

    /**
     * Add comment when changes are committed by vcs(e.g. git).
     *
     * @param comment comment
     * @param resolveAsFixed {@code true} Resolve an issue as FIXED when
     * chnanges are committed by vcs, {@code false} otherwise
     */
    public void addComment(String comment, boolean resolveAsFixed) {
        // TODO add comment?
        if (resolveAsFixed) {
            // update issue
            refresh();
            UpdateIssueParams updateIssueParams = new UpdateIssueParams(issue.getIssueKey());
            Resolution resolution = issue.getResolution();
            if (resolution != null) {
                Issue.ResolutionType resolutionType = resolution.getResolution();
                if (resolutionType == Issue.ResolutionType.Fixed) {
                    LOGGER.log(Level.INFO, "Resolution is already FIXED."); // NOI18N
                    return;
                }
            }
            updateIssueParams = updateIssueParams.resolution(Issue.ResolutionType.Fixed);
            Issue updatedIssue = updateIssue(updateIssueParams);
            if (updatedIssue != null) {
                setIssue(updatedIssue);
            }
        }
    }

    /**
     * Get issue node. Use an issue node to add to an issue table.
     *
     * @return issue node
     */
    public IssueNode getIssueNode() {
        if (node == null) {
            node = createIssueNode();
        }
        return node;
    }

    private IssueNode createIssueNode() {
        return new BacklogIssueNode(this);
    }

    public static ColumnDescriptor[] getColumnDescriptors(BacklogRepository repository) {
        List<ColumnDescriptor<String>> descriptors = new LinkedList<>();
        JTable table = new JTable();
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ISSUE_TYPE, String.class, "Issue Type", "Issue Type"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ID, String.class, "ID", "ID", UIUtils.getColumnWidthInPixels(6, table)));
        descriptors.add(new ColumnDescriptor<>(IssueNode.LABEL_NAME_SUMMARY, String.class, "Summary", "Summary"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_PRIORITY, String.class, "Priority", "Priority"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_CREATED, String.class, "Created", "Created"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_DUE_DATE, String.class, "Due Date", "Due Date"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_UPDATED, String.class, "Updated", "Updated"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_REGISTERED_BY, String.class, "Registered by", "Registered by"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ASSIGNEE, String.class, "Assignee", "Assignee"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_STATUS, String.class, "Status", "Status"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ATTACHMENT, String.class, "Attachment", "Attachment"));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_SHARED_FILE, String.class, "Shared File", "Shared File"));
        return descriptors.toArray(new ColumnDescriptor[descriptors.size()]);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
