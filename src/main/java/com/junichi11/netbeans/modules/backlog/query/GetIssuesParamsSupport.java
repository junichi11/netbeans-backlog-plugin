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

import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.Issue.PriorityType;
import com.nulabinc.backlog4j.Issue.ResolutionType;
import com.nulabinc.backlog4j.Issue.StatusType;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import com.nulabinc.backlog4j.http.BacklogHttpClientImpl;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public final class GetIssuesParamsSupport {

    /**
     * Count per request.
     */
    public static final int ISSUE_COUNT = 100;

    private final List<Long> assigneeIds = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();
    private final List<Long> createdUserIds = new ArrayList<>();
    private final List<Long> issueTypeIds = new ArrayList<>();
    private final List<Long> milestoneIds = new ArrayList<>();
    private final List<Long> priorityIds = new ArrayList<>();
    private final List<Long> projectIds = new ArrayList<>();
    private final List<Long> resolutionIds = new ArrayList<>();
    private final List<Long> statusIds = new ArrayList<>();
    private final List<Long> versionIds = new ArrayList<>();
    private String keyword;
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

    private String queryParam;

    public GetIssuesParamsSupport(@NonNull GetIssuesParams params) {
        this.queryParam = new BacklogHttpClientImpl().getParamsString(true, params);
        parseQueryParam();
    }

    public GetIssuesParamsSupport(String queryParam) {
        this.queryParam = queryParam;
        parseQueryParam();
    }

    /**
     * Get query parameter string.
     *
     * @return query parameter
     */
    public synchronized String getQueryParam() {
        return queryParam;
    }

    /**
     * Set query param from GetIssuesParams
     *
     * @param params GetIssuesParams
     */
    public synchronized void setQueryParam(GetIssuesParams params) {
        if (params == null) {
            return;
        }
        // since backlog4j 2.1.3
        queryParam = new BacklogHttpClientImpl().getParamsString(true, params);
        parseQueryParam();
    }

    /**
     * Parse query param.
     */
    private synchronized void parseQueryParam() {
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
            String key = ""; // NOI18N
            String value = ""; // NOI18N
            try {
                key = URLDecoder.decode(split[0], "UTF-8"); // NOI18N
                value = URLDecoder.decode(split[1], "UTF-8"); // NOI18N
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            }
            switch (key) {
                case "projectId[]": // NOI18N
                    projectIds.add(Long.decode(value));
                    break;
                case "statusId[]": // NOI18N
                    statusIds.add(Long.decode(value));
                    break;
                case "priorityId[]": // NOI18N
                    priorityIds.add(Long.decode(value));
                    break;
                case "categoryId[]": // NOI18N
                    categoryIds.add(Long.decode(value));
                    break;
                case "assigneeId[]": // NOI18N
                    assigneeIds.add(Long.decode(value));
                    break;
                case "versionId[]": // NOI18N
                    versionIds.add(Long.decode(value));
                    break;
                case "createdUserId[]": // NOI18N
                    createdUserIds.add(Long.decode(value));
                    break;
                case "milestoneId[]": // NOI18N
                    milestoneIds.add(Long.decode(value));
                    break;
                case "resolutionId[]": // NOI18N
                    resolutionIds.add(Long.decode(value));
                    break;
                case "issueTypeId[]": // NOI18N
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
     * Create a new GetIssuesParams from parameters of the original one.
     *
     * @return GetIssuesParams
     */
    public synchronized GetIssuesParams newGetIssuesParams() {
        GetIssuesParams issuesParams = new GetIssuesParams(getProjectIds());
        issuesParams.keyword(getKeyword())
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
                .count(ISSUE_COUNT)
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

    public List<Long> getAssigneeIds() {
        return assigneeIds;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public List<Long> getCreatedUserIds() {
        return createdUserIds;
    }

    public List<Long> getIssueTypeIds() {
        return issueTypeIds;
    }

    public List<Long> getMilestoneIds() {
        return milestoneIds;
    }

    public List<Long> getPriorityIds() {
        return priorityIds;
    }

    public List<PriorityType> getPriorities() {
        List<Issue.PriorityType> list = new ArrayList<>(priorityIds.size());
        for (Long priorityId : priorityIds) {
            PriorityType.valueOf(Integer.parseInt(priorityId.toString()));
        }
        return list;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public List<Long> getResolutionIds() {
        return resolutionIds;
    }

    public List<ResolutionType> getResolutions() {
        List<Issue.ResolutionType> list = new ArrayList<>(resolutionIds.size());
        for (Long resolutionId : resolutionIds) {
            list.add(Issue.ResolutionType.valueOf(Integer.parseInt(resolutionId.toString())));
        }
        return list;
    }

    public List<Long> getStatusIds() {
        return statusIds;
    }

    public List<StatusType> getStatus() {
        List<Issue.StatusType> list = new ArrayList<>(statusIds.size());
        for (Long statusId : statusIds) {
            list.add(StatusType.valueOf(Integer.parseInt(statusId.toString())));
        }
        return list;
    }

    public List<Long> getVersionIds() {
        return versionIds;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCreatedSince() {
        return createdSince;
    }

    public String getCreatedUntil() {
        return createdUntil;
    }

    public String getUpdatedSince() {
        return updatedSince;
    }

    public String getUpdatedUntil() {
        return updatedUntil;
    }

    public String getStartDateSince() {
        return startDateSince;
    }

    public String getStartDateUntil() {
        return startDateUntil;
    }

    public String getDueDateSince() {
        return dueDateSince;
    }

    public String getDueDateUntil() {
        return dueDateUntil;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public boolean isSharedFile() {
        return sharedFile;
    }

    private void clearAllQueryParams() {
        keyword = ""; // NOI18N
        statusIds.clear();
        projectIds.clear();
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
