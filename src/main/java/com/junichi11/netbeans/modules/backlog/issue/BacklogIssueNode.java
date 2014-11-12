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

import com.nulabinc.backlog4j.IssueType;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Status;
import com.nulabinc.backlog4j.User;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.junichi11.netbeans.modules.backlog.Backlog;
import com.junichi11.netbeans.modules.backlog.BacklogConnector;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.issuetable.IssueNode.SummaryProperty;
import org.openide.nodes.Node.Property;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class BacklogIssueNode extends IssueNode<BacklogIssue> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);

    public BacklogIssueNode(BacklogIssue issue) {
        super(
                BacklogConnector.ID,
                issue.getRepository().getID(),
                issue,
                Backlog.getInstance().getIssueProvider(),
                Backlog.getInstance().getIssueStatusProvider(),
                Backlog.getInstance().getChangesProvider());
    }

    @Override
    protected Property<?>[] getProperties() {
        return new Property<?>[]{
            new IssueTypeProperty(),
            new IDProperty(),
            new SummaryProperty(),
            new PriorityProperty(),
            new CreatedProperty(),
            new DueDateProperty(),
            new UpdatedProperty(),
            new RegisteredByProperty(),
            new AssigneeProperty(),
            new StatusProperty(),
            new AttachmentProperty(),
            new SharedFileProperty()
        };
    }

    @NbBundle.Messages({
        "BacklogIssueNode.id.displayName=ID",
        "BacklogIssueNode.id.shortDescription=ID",})
    private class IDProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public IDProperty() {
            super(BacklogIssue.LABEL_NAME_ID, String.class, Bundle.BacklogIssueNode_id_displayName(), Bundle.BacklogIssueNode_id_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getIssueData().getKeyId();
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.priority.displayName=Priority",
        "BacklogIssueNode.priority.shortDescription=Priority",})
    public class PriorityProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public PriorityProperty() {
            super(BacklogIssue.LABEL_NAME_PRIORITY, String.class, Bundle.BacklogIssueNode_priority_displayName(), Bundle.BacklogIssueNode_priority_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            Priority priority = getIssueData().getPriority();
            if (priority == null) {
                return ""; // NOI18N
            }
            return priority.getName();
        }

        public Priority getPriority() {
            return getIssueData().getPriority();
        }

    }

    @NbBundle.Messages({
        "BacklogIssueNode.issueType.displayName=Issue Type",
        "BacklogIssueNode.issueType.shortDescription=Issue Type",})
    public class IssueTypeProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public IssueTypeProperty() {
            super(BacklogIssue.LABEL_NAME_ISSUE_TYPE, String.class, Bundle.BacklogIssueNode_issueType_displayName(), Bundle.BacklogIssueNode_issueType_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            IssueType issueType = getIssueData().getIssueType();
            if (issueType == null) {
                return ""; // NOI18N
            }
            return issueType.getName();
        }

        public IssueType getIssueType() {
            return getIssueData().getIssueType();
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.created.displayName=Created",
        "BacklogIssueNode.created.shortDescription=Created",})
    private class CreatedProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public CreatedProperty() {
            super(BacklogIssue.LABEL_NAME_CREATED, String.class, Bundle.BacklogIssueNode_created_displayName(), Bundle.BacklogIssueNode_created_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            Date created = getIssueData().getCreated();
            if (created == null) {
                return ""; // NOI18N
            }
            return DATE_FORMAT.format(created);
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.dueDate.displayName=Due date",
        "BacklogIssueNode.dueDate.shortDescription=Due date",})
    private class DueDateProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public DueDateProperty() {
            super(BacklogIssue.LABEL_NAME_DUE_DATE, String.class, Bundle.BacklogIssueNode_dueDate_displayName(), Bundle.BacklogIssueNode_dueDate_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            Date duedate = getIssueData().getDueDate();
            if (duedate == null) {
                return ""; // NOI18N
            }
            return DATE_FORMAT.format(duedate);
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.updated.displayName=Updated",
        "BacklogIssueNode.updated.shortDescription=Updated",})
    private class UpdatedProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public UpdatedProperty() {
            super(BacklogIssue.LABEL_NAME_UPDATED, String.class, Bundle.BacklogIssueNode_updated_displayName(), Bundle.BacklogIssueNode_updated_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            Date updated = getIssueData().getUpdated();
            if (updated == null) {
                return ""; // NOI18N
            }
            return DATE_FORMAT.format(updated);
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.registeredBy.displayName=Registered By",
        "BacklogIssueNode.registeredBy.shortDescription=Registered By",})
    private class RegisteredByProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public RegisteredByProperty() {
            super(BacklogIssue.LABEL_NAME_REGISTERED_BY, String.class, Bundle.BacklogIssueNode_registeredBy_displayName(), Bundle.BacklogIssueNode_registeredBy_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            User user = getIssueData().getCreatedUser();
            if (user == null) {
                return ""; // NOI18N
            }
            return user.getName();
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.assignee.displayName=Assignee",
        "BacklogIssueNode.assignee.shortDescription=Assignee",})
    private class AssigneeProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public AssigneeProperty() {
            super(BacklogIssue.LABEL_NAME_ASSIGNEE, String.class, Bundle.BacklogIssueNode_assignee_displayName(), Bundle.BacklogIssueNode_assignee_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            User user = getIssueData().getAssignee();
            if (user == null) {
                return ""; // NOI18N
            }
            return user.getName();
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.attachment.displayName=Attachment",
        "BacklogIssueNode.attachment.shortDescription=Attachment",})
    private class AttachmentProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public AttachmentProperty() {
            super(BacklogIssue.LABEL_NAME_ATTACHMENT, String.class, Bundle.BacklogIssueNode_attachment_displayName(), Bundle.BacklogIssueNode_attachment_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            // XXX better view?
            return getIssueData().hasAttachment() ? "○" : ""; // NOI18N
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.sharedFile.displayName=Shared File",
        "BacklogIssueNode.sharedFile.shortDescription=Shared File",})
    private class SharedFileProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public SharedFileProperty() {
            super(BacklogIssue.LABEL_NAME_SHARED_FILE, String.class, Bundle.BacklogIssueNode_sharedFile_displayName(), Bundle.BacklogIssueNode_sharedFile_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return getIssueData().hasSharedFile() ? "○" : ""; // NOI18N
        }
    }

    @NbBundle.Messages({
        "BacklogIssueNode.status.displayName=Status",
        "BacklogIssueNode.status.shortDescription=Status",})
    public class StatusProperty extends IssueNode<BacklogIssue>.IssueProperty<String> {

        public StatusProperty() {
            super(BacklogIssue.LABEL_NAME_STATUS, String.class, Bundle.BacklogIssueNode_status_displayName(), Bundle.BacklogIssueNode_status_shortDescription());
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            Status status = getIssueData().getIssueStatus();
            if (status == null) {
                return ""; // NOI18N
            }
            return status.getName();
        }

        public Status getStatus() {
            return getIssueData().getIssueStatus();
        }
    }
}
