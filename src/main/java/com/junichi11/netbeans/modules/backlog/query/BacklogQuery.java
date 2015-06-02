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

import com.junichi11.netbeans.modules.backlog.BacklogData;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import com.nulabinc.backlog4j.Issue.PriorityType;
import com.nulabinc.backlog4j.Issue.ResolutionType;
import com.nulabinc.backlog4j.Issue.StatusType;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.Resolution;
import com.nulabinc.backlog4j.Status;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import com.nulabinc.backlog4j.http.BacklogHttpClientImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
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
    private String queryParam;
    private String keyword;
    private boolean isSaved;
    private ColumnDescriptor[] columnDescriptors;
    private final List<Long> assigneeIds = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();
    private final List<Long> createdUserIds = new ArrayList<>();
    private final List<Long> issueTypeIds = new ArrayList<>();
    private final List<Long> milestoneIds = new ArrayList<>();
    private final List<Long> priorityIds = new ArrayList<>();
    private final List<Long> resolutionIds = new ArrayList<>();
    private final List<Long> statusIds = new ArrayList<>();
    private final List<Long> versionIds = new ArrayList<>();
    private String createdSince;
    private String createdUntil;
    private String updatedSince;
    private String updatedUntil;
    private String startDateSince;
    private String startDateUntil;
    private String dueDateSince;
    private String dueDateUntil;
    private boolean attachment;
    private boolean sharedFile;
    private static final Logger LOGGER = Logger.getLogger(BacklogQuery.class.getName());

    public BacklogQuery(BacklogRepository repository) {
        this(repository, null, null);
    }

    public BacklogQuery(BacklogRepository repository, String name, String queryParam) {
        this.repository = repository;
        this.name = name;
        this.queryParam = queryParam;
        if (queryParam != null) {
            isSaved = true;
        }
        parseQueryParam();
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
     * Get issues for this query.
     *
     * @return BacklogIssues
     */
    public Collection<BacklogIssue> getIssues() {
        GetIssuesParams issuesParams = createGetIssuesParams();
        if (issuesParams == null) {
            return Collections.emptyList();
        }
        return getIssues(getGetIssuesParams(issuesParams));
    }

    /**
     * Get BacklogIssues for GetIssuesParams.
     *
     * @param issuesParams GetIssuesParams
     * @return BacklogIssues
     */
    public Collection<BacklogIssue> getIssues(GetIssuesParams issuesParams) {
        return repository.getIssues(issuesParams);
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
                .versionIds(getVersionIds())
                .milestoneIds(getMilestoneIds())
                .assignerIds(getAssigneeIds())
                .createdUserIds(getCreatedUserIds())
                .priorities(getPriorities())
                .resolutions(getResolutions())
                .issueTypeIds(getIssueTypeIds())
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
        return queryParam;
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
        // since backlog4j 2.1.3
        queryParam = new BacklogHttpClientImpl().getParamsString(true, params);
        parseQueryParam();
    }

    /**
     * Return kewword.
     *
     * @return keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Get StatusTypes.
     *
     * @return StatusType list
     */
    public List<StatusType> getStatus() {
        BacklogData data = BacklogData.create(repository);
        List<Status> status = data.getStatus();
        List<StatusType> list = new ArrayList<>(statusIds.size());
        for (Long statusId : statusIds) {
            for (Status s : status) {
                if (statusId == s.getId()) {
                    list.add(s.getStatus());
                    break;
                }
            }
        }
        return list;
    }

    /**
     * Get status ids.
     *
     * @return status ids
     */
    public List<Long> getStatusIds() {
        return statusIds;
    }

    /**
     * Get Priority ids.
     *
     * @return priority ids
     */
    public List<Long> getPriorityIds() {
        return priorityIds;
    }

    /**
     * Get PriorityTypes.
     *
     * @return PriorityType list
     */
    public List<PriorityType> getPriorities() {
        BacklogData data = BacklogData.create(repository);
        List<Priority> priorities = data.getPriorities();
        List<PriorityType> list = new ArrayList<>(priorityIds.size());
        for (Long priorityId : priorityIds) {
            for (Priority p : priorities) {
                if (priorityId == p.getId()) {
                    list.add(p.getPriority());
                    break;
                }
            }
        }
        return list;
    }

    /**
     * Get category ids.
     *
     * @return category ids
     */
    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    /**
     * Get assignee ids.
     *
     * @return get assinee ids
     */
    public List<Long> getAssigneeIds() {
        return assigneeIds;
    }

    /**
     * Get user ids.
     *
     * @return user ids
     */
    public List<Long> getCreatedUserIds() {
        return createdUserIds;
    }

    /**
     * Get version ids.
     *
     * @return version ids
     */
    public List<Long> getVersionIds() {
        return versionIds;
    }

    /**
     * Get milestone ids.
     *
     * @return milestone ids
     */
    public List<Long> getMilestoneIds() {
        return milestoneIds;
    }

    /**
     * Get resolution ids.
     *
     * @return resolution ids
     */
    public List<Long> getResolutionIds() {
        return resolutionIds;
    }

    /**
     * Get created since date.
     *
     * @return created since date
     */
    public String getCreatedSince() {
        return createdSince;
    }

    /**
     * Get created until date.
     *
     * @return created until date
     */
    public String getCreatedUntil() {
        return createdUntil;
    }

    /**
     * Get updated since date.
     *
     * @return updated since date
     */
    public String getUpdatedSince() {
        return updatedSince;
    }

    /**
     * Get Updated until date.
     *
     * @return updated until date
     */
    public String getUpdatedUntil() {
        return updatedUntil;
    }

    /**
     * Get start date since.
     *
     * @return start date since
     */
    public String getStartDateSince() {
        return startDateSince;
    }

    /**
     * Get start date until.
     *
     * @return start date until
     */
    public String getStartDateUntil() {
        return startDateUntil;
    }

    /**
     * Get due date since.
     *
     * @return due date since
     */
    public String getDueDateSince() {
        return dueDateSince;
    }

    /**
     * Get due date until.
     *
     * @return due date until
     */
    public String getDueDateUntil() {
        return dueDateUntil;
    }

    /**
     * Whether attachment is valid.
     *
     * @return {@code true} attachment is valid, otherwise {@code false}
     */
    public boolean isAttachment() {
        return attachment;
    }

    /**
     * Whether shared file is valid.
     *
     * @return {@code true} shared file is valid, otherwise {@code false}
     */
    public boolean isSharedFile() {
        return sharedFile;
    }

    /**
     * Get ResolutionTypes.
     *
     * @return ResolutionType list
     */
    public List<ResolutionType> getResolutions() {
        BacklogData data = BacklogData.create(repository);
        List<Resolution> resolutions = data.getResolutions();
        List<ResolutionType> list = new ArrayList<>(resolutionIds.size());
        for (Long resolutionId : resolutionIds) {
            for (Resolution r : resolutions) {
                if (resolutionId == r.getId()) {
                    list.add(r.getResolution());
                    break;
                }
            }
        }
        return list;
    }

    /**
     * Get IssueType ids.
     *
     * @return IssueType ids
     */
    public List<Long> getIssueTypeIds() {
        return issueTypeIds;
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
                for (BacklogIssue issue : getIssues()) {
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
     * Parse query param.
     */
    private void parseQueryParam() {
        clearAllQueryParams();
        if (StringUtils.isEmpty(queryParam)) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(queryParam, "&"); // NOI18N
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String[] split = token.split("="); // NOI18N
            if (split.length != 2) {
                continue;
            }
            String key = split[0];
            String value = split[1];
            switch (key) {
                case "statusId%5B%5D": // NOI18N
                    statusIds.add(Long.decode(value));
                    break;
                case "priorityId%5B%5D": // NOI18N
                    priorityIds.add(Long.decode(value));
                    break;
                case "categoryId%5B%5D": // NOI18N
                    categoryIds.add(Long.decode(value));
                    break;
                case "assigneeId%5B%5D": // NOI18N
                    assigneeIds.add(Long.decode(value));
                    break;
                case "versionId%5B%5D": // NOI18N
                    versionIds.add(Long.decode(value));
                    break;
                case "createdUserId%5B%5D": // NOI18N
                    createdUserIds.add(Long.decode(value));
                    break;
                case "milestoneId%5B%5D": // NOI18N
                    milestoneIds.add(Long.decode(value));
                    break;
                case "resolutionId%5B%5D": // NOI18N
                    resolutionIds.add(Long.decode(value));
                    break;
                case "issueTypeId%5B%5D": // NOI18N
                    issueTypeIds.add(Long.decode(value));
                    break;
                case "keyword": // NOI18N
                    keyword = value;
                    break;
                case "createdSince": // NOI18N
                    createdSince = value;
                    break;
                case "createdUntil": // NOI18N
                    createdUntil = value;
                    break;
                case "updatedSince": // NOI18N
                    updatedSince = value;
                    break;
                case "updatedUntil": // NOI18N
                    updatedUntil = value;
                    break;
                case "startDateSince": // NOI18N
                    startDateSince = value;
                    break;
                case "startDateUntil": // NOI18N
                    startDateUntil = value;
                    break;
                case "dueDateSince": // NOI18N
                    dueDateSince = value;
                    break;
                case "dueDateUntil": // NOI18N
                    dueDateUntil = value;
                    break;
                case "attachment": // NOI18N
                    attachment = Boolean.valueOf(value);
                    break;
                case "sharedFile": // NOI18N
                    sharedFile = Boolean.valueOf(value);
                    break;
                default:
                    break;
            }
        }
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

    /**
     * Clear all query params.
     */
    private void clearAllQueryParams() {
        keyword = ""; // NOI18N
        statusIds.clear();
        priorityIds.clear();
        categoryIds.clear();
        assigneeIds.clear();
        versionIds.clear();
        createdUserIds.clear();
        milestoneIds.clear();
        resolutionIds.clear();
        issueTypeIds.clear();
        createdSince = ""; // NOI18N
        createdUntil = ""; // NOI18N
        updatedSince = ""; // NOI18N
        updatedUntil = ""; // NOI18N
        startDateSince = ""; // NOI18N
        startDateUntil = ""; // NOI18N
        dueDateSince = ""; // NOI18N
        dueDateUntil = ""; // NOI18N
        attachment = false;
        sharedFile = false;
    }

}
