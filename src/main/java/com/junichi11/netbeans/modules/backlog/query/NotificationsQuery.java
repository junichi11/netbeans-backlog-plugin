/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package com.junichi11.netbeans.modules.backlog.query;

import com.junichi11.netbeans.modules.backlog.BacklogConfig;
import com.junichi11.netbeans.modules.backlog.BacklogConnector;
import com.junichi11.netbeans.modules.backlog.BacklogData;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.utils.UiUtils;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.IssueComment;
import com.nulabinc.backlog4j.Notification;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.api.RepositoryManager;
import org.netbeans.modules.bugtracking.api.Util;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider.Status;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class NotificationsQuery extends BacklogQuery implements DefaultQuery {

    private final List<Notification> notifications = Collections.synchronizedList(new ArrayList<Notification>());

    public NotificationsQuery(BacklogRepository repository) {
        super(repository);
    }

    @NbBundle.Messages("NotificationsQuery.displayName=Notifications")
    @Override
    public String getDisplayName() {
        return Bundle.NotificationsQuery_displayName();
    }

    @Override
    public String getTooltip() {
        return getDisplayName();
    }

    @Override
    @NbBundle.Messages({
        "# {0} - content",
        "NotificationsQuery.notification.comment=Comment: {0}"
    })
    public Collection<BacklogIssue> getIssues(boolean isRefresh) {
        final BacklogRepository repository = getRepository();
        List<BacklogIssue> issues = new ArrayList<>();
        if (isRefresh) {
            notifications.clear();
            notifications.addAll(repository.getNotifications());
        }
        for (final Notification notification : notifications) {
            if (notification.isResourceAlreadyRead()) {
                continue;
            }
            final BacklogIssue issue = repository.getIssue(notification, isRefresh);
            if (!issues.contains(issue)) {
                BacklogConfig.getInstance().setStatus(issue, Status.INCOMING_MODIFIED);
                issues.add(issue);
            }
            // icon
            BacklogData data = BacklogData.create(repository);
            Icon senderIcon = data.getUserIcon(notification.getSender());

            IssueComment comment = notification.getComment();
            NotificationDisplayer.getDefault().notify(
                    getTitle(notification),
                    senderIcon,
                    Bundle.NotificationsQuery_notification_comment(comment.getContent()),
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Repository repo = RepositoryManager.getInstance().getRepository(BacklogConnector.ID, repository.getID());
                    Util.openIssue(repo, issue.getKeyId());
                    repository.markAsReadNotification(notification.getId());
                }
            });
        }
        return issues;
    }

    @NbBundle.Messages({
        "# {0} - title",
        "NotificationsQuery.notification.title=Backlog: {0}",
        "NotificationsQuery.reason.assigned=Assigned",
        "NotificationsQuery.reason.commented=Commented",
        "NotificationsQuery.reason.fileAttached=File Attached",
        "NotificationsQuery.reason.issueCreated=Issue Created",
        "NotificationsQuery.reason.issueUpdated=Issue Updated ",
        "NotificationsQuery.reason.projectUserAdded=Project User Added",
        "NotificationsQuery.reason.other=Other",
        "# {0} - sender",
        "NotificationsQuery.notification.sender=Sender:{0}"
    })
    private static String getTitle(Notification notification) {
        StringBuilder sb = new StringBuilder();
        Project project = notification.getProject();
        Issue issue = notification.getIssue();
        sb.append(project.getProjectKey()).append("-").append(issue.getKeyId()).append(" "); // NOI18N
        Notification.Reason reason = notification.getReason();
        switch (reason) {
            case Assigned:
                sb.append(Bundle.NotificationsQuery_reason_assigned());
                break;
            case Commented:
                sb.append(Bundle.NotificationsQuery_reason_commented());
                break;
            case FileAttached:
                sb.append(Bundle.NotificationsQuery_reason_fileAttached());
                break;
            case IssueCreated:
                sb.append(Bundle.NotificationsQuery_reason_issueCreated());
                break;
            case IssueUpdated:
                sb.append(Bundle.NotificationsQuery_reason_issueUpdated());
                break;
            case ProjectUserAdded:
                sb.append(Bundle.NotificationsQuery_reason_projectUserAdded());
                break;
            case Other:
                sb.append(Bundle.NotificationsQuery_reason_other());
                break;
            default:
                throw new AssertionError();
        }
        User sender = notification.getSender();
        if (sender != null) {
            sb.append(" ").append(Bundle.NotificationsQuery_notification_sender(sender.getName())); // NOI18N
        }
        return Bundle.NotificationsQuery_notification_title(sb.toString());
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public boolean canRemove() {
        // XXX delete action is not set to disable
        return false;
    }

    @Override
    public void remove() {
        // XXX delete action is not set to disable
        UiUtils.showOptions();
    }

}
