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
package com.junichi11.netbeans.modules.backlog;

import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssueFinder;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssuePriorityProvider;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssueProvider;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssueScheduleProvider;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssueStatusProvider;
import com.junichi11.netbeans.modules.backlog.query.BacklogQuery;
import com.junichi11.netbeans.modules.backlog.query.BacklogQueryProvider;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepositoryProvider;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.spi.BugtrackingSupport;
import org.openide.util.RequestProcessor;

/**
 * Backlog integration
 *
 * @author junichi11
 */
public final class Backlog {

    public enum FileType {

        NONE, ATTACHED, SHARED;
    }

    private static final Backlog INSTANCE = new Backlog();
    private BugtrackingSupport<BacklogRepository, BacklogQuery, BacklogIssue> bugtrackingSupport;
    private RequestProcessor rp;
    private BacklogIssueProvider issueProvider;
    private BacklogIssueStatusProvider issueStatusProvider;
    private BacklogIssuePriorityProvider issuePriorityProvider;
    private BacklogIssueScheduleProvider issueScheduleProvider;
    private BacklogIssueFinder issueFinder;
    private BacklogQueryProvider queryProvider;
    private BacklogRepositoryProvider repositoryProvider;
    private IssueNode.ChangesProvider<BacklogIssue> changesProvider;

    private Backlog() {
    }

    public static Backlog getInstance() {
        return INSTANCE;
    }

    public BugtrackingSupport<BacklogRepository, BacklogQuery, BacklogIssue> getBugtrackingSupport() {
        if (bugtrackingSupport == null) {
            bugtrackingSupport = new BugtrackingSupport<>(getRepositoryProvider(), getQueryProvider(), getIssueProvider());
        }
        return bugtrackingSupport;
    }

    public RequestProcessor getRequestProcessor() {
        if (rp == null) {
            rp = new RequestProcessor("Backlog", 1, true); // NOI18N
        }
        return rp;
    }

    public BacklogIssueProvider getIssueProvider() {
        if (issueProvider == null) {
            issueProvider = new BacklogIssueProvider();
        }
        return issueProvider;
    }

    public BacklogIssueStatusProvider getIssueStatusProvider() {
        if (issueStatusProvider == null) {
            issueStatusProvider = new BacklogIssueStatusProvider();
        }
        return issueStatusProvider;
    }

    public BacklogIssuePriorityProvider getIssuePriorityProvider() {
        if (issuePriorityProvider == null) {
            issuePriorityProvider = new BacklogIssuePriorityProvider();
        }
        return issuePriorityProvider;
    }

    public BacklogIssueScheduleProvider getIssueScheduleProvider() {
        if (issueScheduleProvider == null) {
            issueScheduleProvider = new BacklogIssueScheduleProvider();
        }
        return issueScheduleProvider;
    }

    public BacklogIssueFinder getIssueFinder() {
        if (issueFinder == null) {
            issueFinder = new BacklogIssueFinder();
        }
        return issueFinder;
    }

    public BacklogQueryProvider getQueryProvider() {
        if (queryProvider == null) {
            queryProvider = new BacklogQueryProvider();
        }
        return queryProvider;
    }

    public BacklogRepositoryProvider getRepositoryProvider() {
        if (repositoryProvider == null) {
            repositoryProvider = new BacklogRepositoryProvider();
        }
        return repositoryProvider;
    }

    public IssueNode.ChangesProvider<BacklogIssue> getChangesProvider() {
        if (changesProvider == null) {
            changesProvider = new IssueNode.ChangesProvider<BacklogIssue>() {
                @Override
                public String getRecentChanges(BacklogIssue issue) {
                    return issue.getRecentChanges();
                }
            };
        }
        return changesProvider;
    }
}
