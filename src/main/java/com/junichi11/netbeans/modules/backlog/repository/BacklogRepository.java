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
package com.junichi11.netbeans.modules.backlog.repository;

import com.nulabinc.backlog4j.BacklogAPIException;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.BacklogClientFactory;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import com.nulabinc.backlog4j.conf.BacklogConfigure;
import com.nulabinc.backlog4j.conf.BacklogJpConfigure;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import com.junichi11.netbeans.modules.backlog.BacklogConfig;
import com.junichi11.netbeans.modules.backlog.BacklogConnector;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.options.BacklogOptions;
import com.junichi11.netbeans.modules.backlog.query.AssignedToMeQuery;
import com.junichi11.netbeans.modules.backlog.query.BacklogQuery;
import com.junichi11.netbeans.modules.backlog.query.CreatedByMeQuery;
import com.junichi11.netbeans.modules.backlog.query.DefaultQuery;
import com.junichi11.netbeans.modules.backlog.utils.BacklogImage;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import com.nulabinc.backlog4j.ResponseList;
import org.netbeans.modules.bugtracking.spi.RepositoryController;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.spi.RepositoryProvider;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class BacklogRepository {

    private static final Image ICON = BacklogImage.ICON_16.getImage();
    static final String PROPERTY_API_KEY = "apikey"; // NOI18N
    static final String PROPERTY_SPACE_ID = "spaceid"; // NOI18N
    static final String PROPERTY_PROJECT_KEY = "projectkey"; // NOI18N
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private RepositoryInfo info;
    private RepositoryController controller;
    private Project project;
    private Set<BacklogQuery> queries = null;

    // default queries
    private BacklogQuery assignedToMeQuery;
    private BacklogQuery createdByMeQuery;

    private final Map<String, BacklogIssue> issueCache = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(BacklogRepository.class.getName());

    public BacklogRepository() {
    }

    public BacklogRepository(RepositoryInfo info) {
        this.info = info;
    }

    public BacklogClient createBacklogClient() {
        // XXX client will keep waiting a connection if connection was interrupted.
        // So create a new client each time.
        // try to create BacklogClient
        return createBacklogClient(getSpaceId(), getApiKey());
    }

    /**
     * Create a new BacklogClient.
     *
     * @param spaceId space id
     * @param apiKey API key
     * @return BacklogClient
     */
    @NbBundle.Messages({
        "BacklogRepository.backlog.api.error=Can't connect to backlog account"
    })
    BacklogClient createBacklogClient(String spaceId, String apiKey) {
        try {
            BacklogConfigure configure = new BacklogJpConfigure(spaceId).apiKey(apiKey);
            return new BacklogClientFactory(configure).newClient();
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.WARNING, "{0}:{1}", new Object[]{Bundle.BacklogRepository_backlog_api_error(), ex.getMessage()}); // NOI18N
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Image getIcon() {
        return ICON;
    }

    public RepositoryInfo getInfo() {
        return info;
    }

    public String getID() {
        if (info == null) {
            return null;
        }
        return info.getID();
    }

    public BacklogQuery createQuery() {
        return new BacklogQuery(this);
    }

    public BacklogIssue createIssue() {
        return new BacklogIssue(this);
    }

    /**
     * Create a new BacklogIssue. New issue is cached.
     *
     * @param issue an issue
     * @return backlog issue
     */
    public BacklogIssue createIssue(Issue issue) {
        // use cache
        String keyId = String.valueOf(issue.getKeyId());
        BacklogIssue backlogIssue = issueCache.get(keyId);
        if (backlogIssue != null) {
            return backlogIssue;
        }
        backlogIssue = new BacklogIssue(this, issue);
        issueCache.put(keyId, backlogIssue);
        return backlogIssue;
    }

    /**
     * Add BacklogIssue to repository cache.
     *
     * @param issue BacklogIssue
     */
    public void addIssue(BacklogIssue issue) {
        if (issue == null) {
            return;
        }
        String keyId = issue.getKeyId();
        if (StringUtils.isEmpty(keyId)) {
            return;
        }
        BacklogIssue cachedIssue = issueCache.get(keyId);
        if (cachedIssue != null) {
            return;
        }
        issueCache.put(keyId, issue);
    }

    /**
     * Get BacklogIssues for specified key ids.
     *
     * @param keyIds issue key ids
     * @return issue list for issue keys
     */
    public List<BacklogIssue> getIssues(String... keyIds) {
        List<BacklogIssue> backlogIssues = new ArrayList<>(keyIds.length);
        for (String keyId : keyIds) {
            BacklogIssue backlogIssue = issueCache.get(keyId);
            if (backlogIssue != null) {
                backlogIssues.add(backlogIssue);
                continue;
            }
            // get issue
            BacklogClient client = createBacklogClient();
            String issueKey = String.format("%s-%s", project.getProjectKey(), keyId);
            try {
                Issue issue = client.getIssue(issueKey);
                if (issue != null) {
                    backlogIssue = createIssue(issue);
                    backlogIssues.add(backlogIssue);
                }
            } catch (BacklogAPIException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
        return backlogIssues;
    }

    /**
     * Get BacklogIssues.
     *
     * @param issuesParams GetIssuesParams
     * @return BacklogIssues
     */
    public Collection<BacklogIssue> getIssues(GetIssuesParams issuesParams) {
        Project p = getProject();
        if (p == null || issuesParams == null) {
            return Collections.emptyList();
        }
        BacklogClient backlogClient = createBacklogClient();
        List<BacklogIssue> backlogIssues = new ArrayList<>();
        try {
            ResponseList<Issue> issues = backlogClient.getIssues(issuesParams);
            for (Issue issue : issues) {
                BacklogIssue backlogIssue = createIssue(issue);
                backlogIssues.add(backlogIssue);
            }
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
        return backlogIssues;
    }

    /**
     * Get BacklogIssue.
     *
     * @param issueKey issue key
     * @return BacklogIssue
     */
    @CheckForNull
    public BacklogIssue getIssue(String issueKey) {
        BacklogClient backlogClient = createBacklogClient();
        try {
            Issue issue = backlogClient.getIssue(issueKey);
            if (issue != null) {
                return createIssue(issue);
            }
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
        return null;
    }

    /**
     * Get all saved queries.
     *
     * @return queries
     */
    public Collection<BacklogQuery> getQueries() {
        if (queries == null) {
            queries = new HashSet<>();

            // add default queries
            BacklogOptions options = BacklogOptions.getInstance();
            if (options.isAssignedToMeQuery()) {
                addQuery(getAssignedToMeQuery());
            }
            if (options.isCreatedByMeQuery()) {
                addQuery(getCreatedByMeQuery());
            }

            // add user quereis
            String[] queryNames = BacklogConfig.getInstance().getQueryNames(this);
            for (String queryName : queryNames) {
                String queryParams = BacklogConfig.getInstance().getQueryParams(this, queryName);
                if (StringUtils.isEmpty(queryParams)) {
                    continue;
                }
                BacklogQuery backlogQuery = new BacklogQuery(this, queryName, queryParams);
                backlogQuery.setSaved(true);
                addQuery(backlogQuery);
            }
        }
        return queries;
    }

    /**
     * Add a query.
     *
     * @param query a query
     */
    public void addQuery(BacklogQuery query) {
        queries.add(query);
    }

    /**
     * Remove a query.
     *
     * @param query a query
     */
    public void removeQuery(BacklogQuery query) {
        // remove configurations
        if (!(query instanceof DefaultQuery)) {
            removeQueryConfig(query);
        }
        queries.remove(query);
        fireQueryListChanged();
    }

    private void removeQueryConfig(BacklogQuery query) {
        BacklogConfig.getInstance().removeQuery(this, query);
    }

    /**
     * Save a query.
     *
     * @param query a query
     */
    public void saveQuery(BacklogQuery query) {
        String displayName = query.getDisplayName();
        if (StringUtils.isEmpty(displayName)) {
            return;
        }
        BacklogConfig.getInstance().setQueryParams(this, query);
        addQuery(query);
        fireQueryListChanged();
    }

    public void optionsChanged() {
        BacklogOptions options = BacklogOptions.getInstance();
        setDefaultQuery(getAssignedToMeQuery(), options.isAssignedToMeQuery());
        setDefaultQuery(getCreatedByMeQuery(), options.isCreatedByMeQuery());
        fireQueryListChanged();
    }

    private void setDefaultQuery(BacklogQuery query, boolean isEnabled) {
        if (isEnabled) {
            if (!queries.contains(query)) {
                queries.add(query);
            }
        } else {
            if (queries.contains(query)) {
                queries.remove(query);
            }
        }
    }

    void removed() {
        // remove all queries
        for (BacklogQuery query : getQueries()) {
            removeQueryConfig(query);
        }
        if (queries != null) {
            queries.clear();
        }
    }

    /**
     * Get RepositoryController.
     *
     * @return RepositoryController
     */
    public RepositoryController getController() {
        if (controller == null) {
            controller = new BacklogRepositoryController(this);
        }
        return controller;
    }

    /**
     * Determines whether it is possible to attach files to an Issue for
     * repository.
     *
     * @return {@code true} in case it is possible to attach files, otherwise
     * {@code false}
     */
    public boolean canAttachFiles() {
        // TODO
        return true;
    }

    /**
     * Runs a query against the bugtracking repository to get all issues for
     * which applies that the ID equals to or the summary contains the given
     * criteria string.
     *
     * @param criteria
     * @return collection of issues
     */
    public Collection<BacklogIssue> simpleSearch(String criteria) {
        if (project == null) {
            return Collections.emptyList();
        }
        List<BacklogIssue> issues = new ArrayList<>();
        // search with id number (issue key)
        if (criteria.matches("\\d*")) { // NOI18N
            String projectKey = getProjectKey();
            String issueKey = String.format("%s-%s", projectKey, criteria); // NOI18N
            BacklogIssue issue = getIssue(issueKey);
            if (issue != null) {
                issues.add(issue);
            }
        }
        // search as a keyword
        GetIssuesParams issuesParams;
        issuesParams = new GetIssuesParams(Collections.singletonList(project.getId()))
                .keyword(criteria);
        issues.addAll(getIssues(issuesParams));
        return issues;
    }

    /**
     * Get API key.
     *
     * @return API key.
     */
    public String getApiKey() {
        return getPropertyValue(PROPERTY_API_KEY);
    }

    /**
     * Get space id.
     *
     * @return space id
     */
    public String getSpaceId() {
        return getPropertyValue(PROPERTY_SPACE_ID);
    }

    /**
     * Get project key
     *
     * @return project key
     */
    public String getProjectKey() {
        return getPropertyValue(PROPERTY_PROJECT_KEY);
    }

    /**
     * Get project.
     *
     * @return project
     */
    @NbBundle.Messages({
        "BacklogRepository.project.error=Can't get project. Please try to check internet connection."
    })
    @CheckForNull
    public Project getProject() {
        if (project != null) {
            return project;
        }

        // get project with project key
        String projectKey = getProjectKey();
        if (StringUtils.isEmpty(projectKey)) {
            return null;
        }
        if (project == null) {
            BacklogClient client = createBacklogClient();
            try {
                project = client.getProject(projectKey);
            } catch (BacklogAPIException ex) {
                LOGGER.log(Level.WARNING, "{0}({1})", new Object[]{ex.getMessage(), Bundle.BacklogRepository_project_error()});
            }
        }
        return project;
    }

    private String getPropertyValue(String propertyName) {
        if (info != null) {
            return info.getValue(propertyName);
        }
        return ""; // NOI18N
    }

    public void setRepositoryInfo(BacklogRepositoryInfo repositoryInfo) {
        info = createRepositoryInfo(repositoryInfo, null, null, null, null, null);
        setProperties(repositoryInfo);

        project = repositoryInfo.getProject();
    }

    private void setProperties(BacklogRepositoryInfo repositoryInfo) {
        if (info != null) {
            info.putValue(PROPERTY_API_KEY, repositoryInfo.getApiKey());
            info.putValue(PROPERTY_SPACE_ID, repositoryInfo.getSpaceId());
            info.putValue(PROPERTY_PROJECT_KEY, repositoryInfo.getProjectKey());
        }
    }

    private RepositoryInfo createRepositoryInfo(BacklogRepositoryInfo backlogRepositoryInfo, String url, String user, String httpUser, char[] password, char[] httpPassword) {
        String displayName = backlogRepositoryInfo.getDisplayName();
        String id = info != null ? info.getID() : displayName + System.currentTimeMillis();
        RepositoryInfo repositoryInfo = new RepositoryInfo(
                id,
                BacklogConnector.ID,
                url,
                displayName, // display name
                displayName, // tooltip
                user,
                httpUser,
                password,
                httpPassword
        );
        return repositoryInfo;
    }

    /**
     * Get AssignedToMeQuery.
     *
     * @return AssignedToMeQuery
     */
    private BacklogQuery getAssignedToMeQuery() {
        if (assignedToMeQuery == null) {
            assignedToMeQuery = new AssignedToMeQuery(this);
        }
        return assignedToMeQuery;
    }

    /**
     * Get CreatedByMeQuery.
     *
     * @return CreatedByMeQuery
     */
    private BacklogQuery getCreatedByMeQuery() {
        if (createdByMeQuery == null) {
            createdByMeQuery = new CreatedByMeQuery(this);
        }
        return createdByMeQuery;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void fireQueryListChanged() {
        propertyChangeSupport.firePropertyChange(RepositoryProvider.EVENT_QUERY_LIST_CHANGED, null, null);
    }

    private void fireUnsubmittedIssueChanged() {
        propertyChangeSupport.firePropertyChange(RepositoryProvider.EVENT_UNSUBMITTED_ISSUES_CHANGED, null, null);
    }
}
