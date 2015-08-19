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

import com.junichi11.netbeans.modules.backlog.BacklogConfig;
import com.junichi11.netbeans.modules.backlog.BacklogConnector;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import static com.junichi11.netbeans.modules.backlog.utils.BacklogUtils.DEFAULT_DATE_FORMAT;
import com.nulabinc.backlog4j.Attachment;
import com.nulabinc.backlog4j.AttachmentData;
import com.nulabinc.backlog4j.BacklogAPIException;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.IssueComment;
import com.nulabinc.backlog4j.IssueType;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Resolution;
import com.nulabinc.backlog4j.ResponseList;
import com.nulabinc.backlog4j.User;
import com.nulabinc.backlog4j.api.option.CreateIssueParams;
import com.nulabinc.backlog4j.api.option.UpdateIssueCommentParams;
import com.nulabinc.backlog4j.api.option.UpdateIssueParams;
import com.nulabinc.backlog4j.internal.file.AttachmentDataImpl;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.api.RepositoryManager;
import org.netbeans.modules.bugtracking.api.Util;
import org.netbeans.modules.bugtracking.commons.UIUtils;
import org.netbeans.modules.bugtracking.issuetable.ColumnDescriptor;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.netbeans.modules.bugtracking.spi.IssueProvider;
import org.netbeans.modules.bugtracking.spi.IssueScheduleInfo;
import org.netbeans.modules.bugtracking.spi.IssueScheduleProvider;
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
    private String subtaskParentIssueKey;
    private IssueScheduleInfo scheduleInfo;
    private final List<IssueComment> comments = Collections.synchronizedList(new ArrayList<IssueComment>());
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
    public static final String LABEL_NAME_PARENT_CHILD = "backlog.issue.parent.child"; // NOI18N
    public static final ColumnDescriptor<String>[] DEFAULT_SUBTASKING_COLUMN_DESCRIPTORS = getSubtaskingColumnDescriptors();

    public static final String PROP_COMMENT_DELETED = "backlog.comment.deleted"; // NOI18N
    public static final String PROP_COMMENT_QUOTE = "backlog.comment.quote"; // NOI18N
    public static final String PROP_COMMENT_EDITED = "backlog.comment.edited"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(BacklogIssue.class.getName());

    public BacklogIssue(BacklogRepository repository) {
        this.repository = repository;
    }

    public BacklogIssue(BacklogRepository repository, Issue issue) {
        this.repository = repository;
        setIssue(issue);
    }

    public BacklogIssue(BacklogRepository repository, String parentIssueKey) {
        this(repository);
        String projectKey = repository.getProjectKey();
        String regex = String.format("\\A%s-\\d+\\z", projectKey); // NOI18N
        if (parentIssueKey.matches(regex)) {
            this.subtaskParentIssueKey = parentIssueKey;
        }
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
    @NbBundle.Messages({
        "BacklogIssue.LBL.assignee=Assignee",
        "BacklogIssue.LBL.created=Created",
        "BacklogIssue.LBL.dueDate=Due date",
        "BacklogIssue.LBL.createdBy=Created by",
        "BacklogIssue.LBL.startDate=Start date",
        "BacklogIssue.LBL.status=Status"
    })
    public String getTooltip() {
        StringBuilder sb = new StringBuilder();
        String displayName = getDisplayName();
        sb.append("<html>") // NOI18N
                .append("<b>").append(displayName).append("</b>"); // NOI18N
        if (!isNew()) {
            sb.append("<hr>"); // NOI18N
            User assignee = getAssignee();
            User createdUser = getCreatedUser();
            Date created = getCreated();
            Date dueDate = getDueDate();
            Date startDate = getStartDate();
            com.nulabinc.backlog4j.Status issueStatus = getIssueStatus();
            if (createdUser != null) {
                sb.append(Bundle.BacklogIssue_LBL_createdBy()).append(": ").append(createdUser.getName()).append("<br>"); // NOI18N
            }
            if (assignee != null) {
                sb.append(Bundle.BacklogIssue_LBL_assignee()).append(": ").append(assignee.getName()).append("<br>"); // NOI18N
            }
            if (issueStatus != null) {
                sb.append(Bundle.BacklogIssue_LBL_status()).append(": ").append(issueStatus.getName()).append("<br>"); // NOI18N
            }
            if (created != null) {
                sb.append(Bundle.BacklogIssue_LBL_created()).append(": ").append(DEFAULT_DATE_FORMAT.format(created)).append("<br>"); // NOI18N
            }
            if (startDate != null) {
                sb.append(Bundle.BacklogIssue_LBL_startDate()).append(": ").append(DEFAULT_DATE_FORMAT.format(startDate)).append("<br>"); // NOI18N
            }
            if (dueDate != null) {
                sb.append(Bundle.BacklogIssue_LBL_dueDate()).append(": ").append(DEFAULT_DATE_FORMAT.format(dueDate)).append("<br>"); // NOI18N
            }
        }
        sb.append("</html>"); // NOI18N
        return sb.toString();
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
     * Get start date.
     *
     * @return Start date if issue is not {@code null} and chart is enabled,
     * otherwise {@code null}
     */
    public Date getStartDate() {
        if (issue == null) {
            return null;
        }
        return issue.getStartDate();
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
     * Get assignee.
     *
     * @return Assignee if issue is not {@code null}, {@code null} otherwise
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
        if (backlogClient == null) {
            return;
        }
        try {
            setIssue(backlogClient.getIssue(id));
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        fireStatusChange();
    }

    public void refreshIssue(Issue issue) {
        setIssue(issue);
    }

    /**
     * Get issue status.
     *
     * @return status
     */
    public Status getStatus() {
        return BacklogConfig.getInstance().getStatus(this);
    }

    public void setStatus(Status status) {
        BacklogConfig.getInstance().setStatus(this, status);
        fireStatusChange();
    }

    public List<IssueComment> getIssueComments() {
        if (issue == null) {
            return Collections.emptyList();
        }
        return comments;
    }

    private void refreshIssueComments() {
        if (issue == null) {
            return;
        }
        comments.clear();
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient != null) {
            try {
                ResponseList<IssueComment> issueComments = backlogClient.getIssueComments(issue.getId());
                comments.addAll(issueComments);
            } catch (BacklogAPIException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    public long getLastUpdatedTime() {
        Date updated = this.getUpdated();
        if (updated != null) {
            long time = updated.getTime();
            for (IssueComment issueComment : getIssueComments()) {
                Date commentUpdated = issueComment.getUpdated();
                if (commentUpdated != null) {
                    long commentTime = commentUpdated.getTime();
                    if (time < commentTime) {
                        time = commentTime;
                    }
                }
            }
            return time;
        }
        return -1L;
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

    void fireScheduleChange() {
        changeSupport.firePropertyChange(IssueScheduleProvider.EVENT_ISSUE_SCHEDULE_CHANGED, null, null);
    }

    public BacklogIssue getParentIssue() {
        if (subtaskParentIssueKey != null) {
            BacklogIssue parent = repository.getIssue(subtaskParentIssueKey);
            if (parent != null) {
                return parent;
            }
        }
        return repository.getParentIssue(this);
    }

    public boolean isParent() {
        if (issue == null) {
            return false;
        }
        long parentIssueId = issue.getParentIssueId();
        return parentIssueId <= 0;
    }

    public boolean isChild() {
        if (issue == null) {
            return false;
        }
        long parentIssueId = issue.getParentIssueId();
        return parentIssueId > 0;
    }

    public String getSubtaskParentIssueKey() {
        return subtaskParentIssueKey;
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
        if (backlogClient == null) {
            return null;
        }
        Issue createdIssue = null;
        try {
            createdIssue = backlogClient.createIssue(issueParams);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        if (createdIssue != null) {
            setIssue(createdIssue);
            repository.addIssue(this);
            fireChange();
            fireDataChange();
            fireScheduleChange();
            fireStatusChange();
            ((BacklogIssueController) getController()).setChanged(false);
        }
        subtaskParentIssueKey = null;
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
        if (backlogClient == null) {
            return null;
        }
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
            scheduleInfo = createScheduleInfo();
            fireScheduleChange();
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
        if (backlogClient == null) {
            return Collections.emptyList();
        }
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
        if (backlogClient == null) {
            return null;
        }
        AttachmentData attachmentData = null;
        try {
            attachmentData = backlogClient.downloadIssueAttachment(issue.getId(), attachmentId);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return attachmentData;
    }

    public Attachment deleteIssueAttachment(long attachmentId) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return null;
        }
        Attachment attachment = null;
        try {
            attachment = backlogClient.deleteIssueAttachment(issue.getId(), attachmentId);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return attachment;
    }

    /**
     * Update IssueComment.
     *
     * @param comment IssueComment
     * @param content new content
     * @return Updated IssueComment if update is successful, otherwise
     * {@code null}
     */
    @CheckForNull
    public IssueComment updateIssueComment(IssueComment comment, String content) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return null;
        }
        IssueComment updatedIssueComment = null;
        try {
            UpdateIssueCommentParams updateIssueCommentParams = new UpdateIssueCommentParams(issue.getId(), comment.getId(), content);
            updatedIssueComment = backlogClient.updateIssueComment(updateIssueCommentParams);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        return updatedIssueComment;
    }

    /**
     * Get subissue ids.
     *
     * @return subissue ids
     */
    public List<String> getSubissueIds() {
        return repository.getSubissueIds(this);
    }

    /**
     * Set an existing issue.
     *
     * @param issue an existing issue
     */
    private void setIssue(Issue issue) {
        this.issue = issue;
        this.summary = issue.getSummary();
        // XXX many requests may be posted for getting comments
        // Use notification?
//        refreshIssueComments();
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
        if (backlogClient == null) {
            return null;
        }
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
     * @param resolveAsFixed {@code true} Resolve an issue as FIXED when changes
     * are committed by vcs, {@code false} otherwise
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

    // schedule
    public void setSchedule(IssueScheduleInfo scheduleInfo) {
        // XXX change the due date?
        // open issue panel
        Repository repo = RepositoryManager.getInstance().getRepository(BacklogConnector.ID, getRepository().getID());
        Util.openIssue(repo, getKeyId());
    }

    public IssueScheduleInfo getSchedule() {
        com.nulabinc.backlog4j.Status issueStatus = getIssueStatus();
        if (issueStatus == null || issueStatus.getStatus() == Issue.StatusType.Closed) {
            return null;
        }
        if (scheduleInfo == null) {
            scheduleInfo = createScheduleInfo();
        }
        return scheduleInfo;
    }

    private IssueScheduleInfo createScheduleInfo() {
        Date startDate = getStartDate();
        Date dueDate = getDueDate();
        if (startDate != null) {
            if (dueDate == null) {
                return new IssueScheduleInfo(startDate, 1);
            } else {
                // interval
                long start = startDate.getTime();
                long due = dueDate.getTime();
                // XXX check cast
                int interval = (int) ((due - start) / (1000 * 60 * 60 * 24));
                return new IssueScheduleInfo(startDate, interval);
            }
        } else if (dueDate != null) {
            return new IssueScheduleInfo(dueDate, 1);
        }
        return null;
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

    @NbBundle.Messages({
        "BacklogIssue.column.descriptor.issueType.displayName=Issue Type",
        "BacklogIssue.column.descriptor.summary.displayName=Summary",
        "BacklogIssue.column.descriptor.priority.displayName=Priority",
        "BacklogIssue.column.descriptor.created.displayName=Created",
        "BacklogIssue.column.descriptor.dueDate.displayName=Due Date",
        "BacklogIssue.column.descriptor.updated.displayName=Updated",
        "BacklogIssue.column.descriptor.registeredBy.displayName=Registered by",
        "BacklogIssue.column.descriptor.assignee.displayName=Assignee",
        "BacklogIssue.column.descriptor.status.displayName=Status",
        "BacklogIssue.column.descriptor.attachment.displayName=Attachment",
        "BacklogIssue.column.descriptor.sharedFile.displayName=Shared File"
    })
    public static ColumnDescriptor<String>[] getColumnDescriptors() {
        List<ColumnDescriptor<String>> descriptors = new LinkedList<>();
        JTable table = new JTable();
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ISSUE_TYPE, String.class, Bundle.BacklogIssue_column_descriptor_issueType_displayName(), Bundle.BacklogIssue_column_descriptor_issueType_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ID, String.class, "ID", "ID", UIUtils.getColumnWidthInPixels(6, table)));
        descriptors.add(new ColumnDescriptor<>(IssueNode.LABEL_NAME_SUMMARY, String.class, Bundle.BacklogIssue_column_descriptor_summary_displayName(), Bundle.BacklogIssue_column_descriptor_summary_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_PRIORITY, String.class, Bundle.BacklogIssue_column_descriptor_priority_displayName(), Bundle.BacklogIssue_column_descriptor_priority_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_CREATED, String.class, Bundle.BacklogIssue_column_descriptor_created_displayName(), Bundle.BacklogIssue_column_descriptor_created_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_DUE_DATE, String.class, Bundle.BacklogIssue_column_descriptor_dueDate_displayName(), Bundle.BacklogIssue_column_descriptor_dueDate_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_UPDATED, String.class, Bundle.BacklogIssue_column_descriptor_updated_displayName(), Bundle.BacklogIssue_column_descriptor_updated_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_REGISTERED_BY, String.class, Bundle.BacklogIssue_column_descriptor_registeredBy_displayName(), Bundle.BacklogIssue_column_descriptor_registeredBy_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ASSIGNEE, String.class, Bundle.BacklogIssue_column_descriptor_assignee_displayName(), Bundle.BacklogIssue_column_descriptor_assignee_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_STATUS, String.class, Bundle.BacklogIssue_column_descriptor_status_displayName(), Bundle.BacklogIssue_column_descriptor_status_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_ATTACHMENT, String.class, Bundle.BacklogIssue_column_descriptor_attachment_displayName(), Bundle.BacklogIssue_column_descriptor_attachment_displayName()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_SHARED_FILE, String.class, Bundle.BacklogIssue_column_descriptor_sharedFile_displayName(), Bundle.BacklogIssue_column_descriptor_sharedFile_displayName()));
        return descriptors.toArray(new ColumnDescriptor[descriptors.size()]);
    }

    @NbBundle.Messages({
        "BacklogIssue.column.descriptor.parentChild.displayName=P/C",
        "BacklogIssue.column.descriptor.parentChild.shortDiscription=Parent/Child"
    })
    public static ColumnDescriptor<String>[] getSubtaskingColumnDescriptors() {
        List<ColumnDescriptor<String>> descriptors = new LinkedList<>(Arrays.asList(getColumnDescriptors()));
        descriptors.add(new ColumnDescriptor<>(LABEL_NAME_PARENT_CHILD, String.class, Bundle.BacklogIssue_column_descriptor_parentChild_displayName(), Bundle.BacklogIssue_column_descriptor_parentChild_shortDiscription()));
        return descriptors.toArray(new ColumnDescriptor[descriptors.size()]);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
