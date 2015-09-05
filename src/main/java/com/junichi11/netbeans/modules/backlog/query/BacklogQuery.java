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

import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.nulabinc.backlog4j.Issue.PriorityType;
import com.nulabinc.backlog4j.Issue.ResolutionType;
import com.nulabinc.backlog4j.Issue.StatusType;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.api.option.GetIssuesCountParams;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.bugtracking.issuetable.ColumnDescriptor;
import org.netbeans.modules.bugtracking.spi.QueryProvider;

/**
 *
 * @author junichi11
 */
public class BacklogQuery {

    private final BacklogRepository repository;
    private QueryProvider.IssueContainer issueContainer;
    private BacklogQueryController controller;
    private String name;
    private boolean isSaved;
    private ColumnDescriptor[] columnDescriptors;
    private final GetIssuesParamsSupport getIssuesParamsSupport;
    private static final Logger LOGGER = Logger.getLogger(BacklogQuery.class.getName());

    public BacklogQuery(BacklogRepository repository) {
        this(repository, null, null);
    }

    public BacklogQuery(BacklogRepository repository, String name, String queryParam) {
        this.repository = repository;
        this.name = name;
        if (queryParam != null) {
            isSaved = true;
        }
        getIssuesParamsSupport = new GetIssuesParamsSupport(queryParam);
    }

    /**
     * Return BacklogRepository.
     *
     * @return BacklogRepository
     */
    public BacklogRepository getRepository() {
        return repository;
    }

    /**
     * Return display name.
     *
     * @return display name
     */
    public String getDisplayName() {
        return name;
    }

    /**
     * Return tooltip text.
     *
     * @return tooltip text
     */
    public String getTooltip() {
        return getDisplayName();
    }

    /**
     * Check whether this query is saved.
     *
     * @return {@code true} if query is saved, otherwise {@code false}
     */
    public boolean isSaved() {
        return isSaved;
    }

    /**
     * Set saved.
     *
     * @param isSaved
     */
    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    /**
     * Get all BacklogIssues for this query.
     *
     * @return BacklogIssues
     */
    public Collection<BacklogIssue> getAllIssues() {
        GetIssuesParams issuesParams = createGetIssuesParams();
        if (issuesParams == null) {
            return Collections.emptyList();
        }
        return getAllIssues(getGetIssuesParams(issuesParams));
    }

    /**
     * Get all BacklogIssues for GetIssuesParams.
     *
     * @param issuesParams GetIssuesParams
     * @return BacklogIssues
     */
    public Collection<BacklogIssue> getAllIssues(GetIssuesParams issuesParams) {
        return repository.getIssues(issuesParams, true);
    }

    /**
     * Get BacklogIssues for GetIssuesParams.
     *
     * @param issuesParams GetIssuesParams
     * @return BacklogIssues
     */
    public Collection<BacklogIssue> getIssues(GetIssuesParams issuesParams) {
        return repository.getIssues(issuesParams, false);
    }

    /**
     * Get an issue count.
     *
     * @param issuesCountParams
     * @return a issue count if it can be got, otherwise -1
     */
    public int getIssuesCount(GetIssuesCountParams issuesCountParams) {
        return repository.getIssuesCount(issuesCountParams);
    }

    /**
     * Get BacklogIssue for issue key.
     *
     * @param issueKey issue key.
     * @return BacklogIssue
     */
    public BacklogIssue getIssue(String issueKey) {
        return repository.getIssue(issueKey);
    }

    /**
     * Create GetIssuesParams.
     *
     * @return GetIssuesParams
     */
    private GetIssuesParams createGetIssuesParams() {
        Project project = repository.getProject();
        if (project == null) {
            LOGGER.log(Level.WARNING, "Can't get the project({0})", repository.getProjectKey()); // NOI18N
            return null;
        }
        return new GetIssuesParams(Collections.singletonList(project.getId()));
    }

    /**
     * Get GetIssuesParams.
     *
     * @return GetIssuesParams
     */
    protected GetIssuesParams getGetIssuesParams(GetIssuesParams issuesParams) {
        issuesParams = issuesParams
                .keyword(getKeyword())
                // general
                .statuses(getStatus())
                .categoryIds(getCategoryIds())
                .versionIds(getVersionIds())
                .milestoneIds(getMilestoneIds())
                .assignerIds(getAssigneeIds())
                .createdUserIds(getCreatedUserIds())
                .priorities(getPriorities())
                .resolutions(getResolutions())
                .issueTypeIds(getIssueTypeIds())
                .count(GetIssuesParamsSupport.ISSUE_COUNT)
                // date
                .createdSince(getCreatedSince())
                .createdUntil(getCreatedUntil())
                .updatedSince(getUpdatedSince())
                .updatedUntil(getUpdatedUntil())
                .startDateSince(getStartDateSince())
                .dueDateSince(getDueDateSince())
                .dueDateUntil(getDueDateUntil());
        // file
        if (isAttachment()) {
            issuesParams = issuesParams.attachment(true);
        }

        if (isSharedFile()) {
            issuesParams = issuesParams.sharedFile(true);
        }

        return issuesParams;
    }

    /**
     * Return query param. params is return value of
     * BacklogHttpClientImpl.getParamString().
     *
     * @return query param
     */
    public String getQueryParam() {
        return getIssuesParamsSupport.getQueryParam();
    }

    /**
     * Set query param from GetIssuesParams
     *
     * @param params GetIssuesParams
     */
    public void setQueryParam(GetIssuesParams params) {
        if (params == null) {
            return;
        }
        getIssuesParamsSupport.setQueryParam(params);
    }

    /**
     * Return kewword.
     *
     * @return keyword
     */
    public String getKeyword() {
        return getIssuesParamsSupport.getKeyword();
    }

    /**
     * Get StatusTypes.
     *
     * @return StatusType list
     */
    public List<StatusType> getStatus() {
        return getIssuesParamsSupport.getStatus();
    }

    /**
     * Get status ids.
     *
     * @return status ids
     */
    public List<Long> getStatusIds() {
        return getIssuesParamsSupport.getStatusIds();
    }

    /**
     * Get Priority ids.
     *
     * @return priority ids
     */
    public List<Long> getPriorityIds() {
        return getIssuesParamsSupport.getPriorityIds();
    }

    /**
     * Get PriorityTypes.
     *
     * @return PriorityType list
     */
    public List<PriorityType> getPriorities() {
        return getIssuesParamsSupport.getPriorities();
    }

    /**
     * Get category ids.
     *
     * @return category ids
     */
    public List<Long> getCategoryIds() {
        return getIssuesParamsSupport.getCategoryIds();
    }

    /**
     * Get assignee ids.
     *
     * @return get assinee ids
     */
    public List<Long> getAssigneeIds() {
        return getIssuesParamsSupport.getAssigneeIds();
    }

    /**
     * Get user ids.
     *
     * @return user ids
     */
    public List<Long> getCreatedUserIds() {
        return getIssuesParamsSupport.getCreatedUserIds();
    }

    /**
     * Get version ids.
     *
     * @return version ids
     */
    public List<Long> getVersionIds() {
        return getIssuesParamsSupport.getVersionIds();
    }

    /**
     * Get milestone ids.
     *
     * @return milestone ids
     */
    public List<Long> getMilestoneIds() {
        return getIssuesParamsSupport.getMilestoneIds();
    }

    /**
     * Get resolution ids.
     *
     * @return resolution ids
     */
    public List<Long> getResolutionIds() {
        return getIssuesParamsSupport.getResolutionIds();
    }

    /**
     * Get created since date.
     *
     * @return created since date
     */
    public String getCreatedSince() {
        return getIssuesParamsSupport.getCreatedSince();
    }

    /**
     * Get created until date.
     *
     * @return created until date
     */
    public String getCreatedUntil() {
        return getIssuesParamsSupport.getCreatedUntil();
    }

    /**
     * Get updated since date.
     *
     * @return updated since date
     */
    public String getUpdatedSince() {
        return getIssuesParamsSupport.getUpdatedSince();
    }

    /**
     * Get Updated until date.
     *
     * @return updated until date
     */
    public String getUpdatedUntil() {
        return getIssuesParamsSupport.getUpdatedUntil();
    }

    /**
     * Get start date since.
     *
     * @return start date since
     */
    public String getStartDateSince() {
        return getIssuesParamsSupport.getStartDateSince();
    }

    /**
     * Get start date until.
     *
     * @return start date until
     */
    public String getStartDateUntil() {
        return getIssuesParamsSupport.getStartDateUntil();
    }

    /**
     * Get due date since.
     *
     * @return due date since
     */
    public String getDueDateSince() {
        return getIssuesParamsSupport.getDueDateSince();
    }

    /**
     * Get due date until.
     *
     * @return due date until
     */
    public String getDueDateUntil() {
        return getIssuesParamsSupport.getDueDateUntil();
    }

    /**
     * Whether attachment is valid.
     *
     * @return {@code true} attachment is valid, otherwise {@code false}
     */
    public boolean isAttachment() {
        return getIssuesParamsSupport.isAttachment();
    }

    /**
     * Whether shared file is valid.
     *
     * @return {@code true} shared file is valid, otherwise {@code false}
     */
    public boolean isSharedFile() {
        return getIssuesParamsSupport.isSharedFile();
    }

    /**
     * Get ResolutionTypes.
     *
     * @return ResolutionType list
     */
    public List<ResolutionType> getResolutions() {
        return getIssuesParamsSupport.getResolutions();
    }

    /**
     * Get IssueType ids.
     *
     * @return IssueType ids
     */
    public List<Long> getIssueTypeIds() {
        return getIssuesParamsSupport.getIssueTypeIds();
    }

    /**
     * Set query name.
     *
     * @param name query name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Determines whether it is possible to remove a query.
     *
     * @return {@code true} if it's possible to remove it, otherwise
     * {@code false}
     */
    public boolean canRemove() {
        return true;
    }

    /**
     * Remove a qurey from the repository.
     */
    public void remove() {
        repository.removeQuery(this);
    }

    /**
     * Determines whether it is possible to rename a query.
     *
     * @return {@code true} if it's possible to rename it, otherwise
     * {@code false}
     */
    public boolean canRename() {
        return true;
    }

    /**
     * Rename a query.
     */
    public void rename() {
        // TODO implement?
    }

    /**
     * Get BacklogQueryController.
     *
     * @return BacklogQueryController
     */
    public BacklogQueryController getQueryController() {
        if (controller == null) {
            controller = createQueryController();
        }
        return controller;
    }

    /**
     * Create a new BacklogQueryController.
     *
     * @return BacklogQueryController
     */
    private BacklogQueryController createQueryController() {
        return new BacklogQueryController(repository, this);
    }

    /**
     * Refresh issues.
     */
    public void refresh() {
        try {
            if (issueContainer != null) {
                issueContainer.refreshingStarted();
                issueContainer.clear();
                for (BacklogIssue issue : getAllIssues()) {
                    issueContainer.add(issue);
                }
            }
        } finally {
            fireFinished();
        }
    }

    void fireFinished() {
        if (issueContainer != null) {
            issueContainer.refreshingFinished();
        }
    }

    void setIssueContainer(QueryProvider.IssueContainer<BacklogIssue> issueContainer) {
        this.issueContainer = issueContainer;
    }

    /**
     * Get ColumnDescriptors.
     *
     * @return ColumnDescriptors
     */
    public ColumnDescriptor[] getColumnDescriptors() {
        if (columnDescriptors == null) {
            columnDescriptors = BacklogIssue.getColumnDescriptors();
        }
        return columnDescriptors;
    }

}
