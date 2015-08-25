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

import com.junichi11.netbeans.modules.backlog.BacklogConfig;
import com.junichi11.netbeans.modules.backlog.BacklogConnector;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.options.BacklogOptions;
import com.junichi11.netbeans.modules.backlog.query.AssignedToMeQuery;
import com.junichi11.netbeans.modules.backlog.query.BacklogQuery;
import com.junichi11.netbeans.modules.backlog.query.CreatedByMeQuery;
import com.junichi11.netbeans.modules.backlog.query.DefaultQuery;
import com.junichi11.netbeans.modules.backlog.query.NotificationsQuery;
import com.junichi11.netbeans.modules.backlog.utils.BacklogImage;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import com.nulabinc.backlog4j.BacklogAPIException;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.BacklogClientFactory;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.Notification;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.ResponseList;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import com.nulabinc.backlog4j.conf.BacklogConfigure;
import com.nulabinc.backlog4j.conf.BacklogJpConfigure;
import com.nulabinc.backlog4j.conf.BacklogToolConfigure;
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
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.api.RepositoryManager;
import org.netbeans.modules.bugtracking.api.Util;
import org.netbeans.modules.bugtracking.spi.RepositoryController;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.spi.RepositoryProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class BacklogRepository {

    private static final Image ICON = BacklogImage.ICON_16.getImage();
    static final String PROPERTY_BACKLOG_DOMAIN = "backlogdomain"; // NOI18N
    static final String PROPERTY_API_KEY = "apikey"; // NOI18N
    static final String PROPERTY_SPACE_ID = "spaceid"; // NOI18N
    static final String PROPERTY_PROJECT_KEY = "projectkey"; // NOI18N
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private RepositoryInfo info;
    private RepositoryController controller;
    private Project project;
    private Set<BacklogQuery> queries = null;
    // for empty domain
    private boolean showingRepositoryDialog = false;

    // default queries
    private BacklogQuery assignedToMeQuery;
    private BacklogQuery createdByMeQuery;
    private final BacklogQuery notificationQuery = new NotificationsQuery(this);

    // key id, issue
    private final Map<String, BacklogIssue> issueCache = Collections.synchronizedMap(new HashMap<String, BacklogIssue>());
    // XXX for subtask
    private BacklogIssue subtaskParentIssue;
    private static final Logger LOGGER = Logger.getLogger(BacklogRepository.class.getName());

    public BacklogRepository() {
    }

    public BacklogRepository(RepositoryInfo info) {
        this.info = info;
    }

    @CheckForNull
    public BacklogClient createBacklogClient() {
        // XXX client will keep waiting a connection if connection was interrupted.
        // So create a new client each time.
        // try to create BacklogClient
        return createBacklogClient(getBacklogDomain(), getSpaceId(), getApiKey());
    }

    /**
     * Create a new BacklogClient.
     *
     * @param spaceId space id
     * @param apiKey API key
     * @return BacklogClient
     */
    @NbBundle.Messages({
        "BacklogRepository.backlog.api.error=Can't connect to a backlog account",
        "BacklogRepository.backlog.domain.error=Backlog domain is not set. Please select backlog.jp or baclogtool.com"
    })
    BacklogClient createBacklogClient(String domain, String spaceId, String apiKey) {
        if (StringUtils.isEmpty(domain)) {
            // open panel
            if (!showingRepositoryDialog) {
                showingRepositoryDialog = true;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        NotifyDescriptor.Message message = new NotifyDescriptor.Message(Bundle.BacklogRepository_backlog_domain_error(), NotifyDescriptor.ERROR_MESSAGE);
                        if (DialogDisplayer.getDefault().notify(message) == NotifyDescriptor.OK_OPTION) {
                            Repository repo = RepositoryManager.getInstance().getRepository(BacklogConnector.ID, getID());
                            if (repo != null) {
                                Util.edit(repo);
                            }
                        }
                    }
                });
            }
            return null;
        }
        try {
            BacklogConfigure configure;
            switch (domain) {
                case BacklogUtils.BACKLOG_JP:
                    configure = new BacklogJpConfigure(spaceId).apiKey(apiKey);
                    break;
                case BacklogUtils.BACKLOGTOOL_COM:
                    configure = new BacklogToolConfigure(spaceId).apiKey(apiKey);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Unsupported domain: {0}", domain);
                    return null;
            }
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
        // XXX for subtask
        if (subtaskParentIssue != null) {
            String issueKey = subtaskParentIssue.getIssueKey();
            subtaskParentIssue = null;
            return new BacklogIssue(this, issueKey);
        }
        return new BacklogIssue(this);
    }

    public BacklogIssue createIssue(String summary, String description) {
        String projectKey = getProjectKey();
        String regex = String.format("\\A%s-\\d+\\z", projectKey); // NOI18N
        if (summary.matches(regex)) {
            // subtask
            return new BacklogIssue(this, summary);
        }
        return createIssue();
    }

    /**
     * Create a new BacklogIssue. New issue is cached.
     *
     * @param issue an issue
     * @return backlog issue
     */
    public synchronized BacklogIssue createIssue(Issue issue, boolean isRefresh) {
        // use cache
        String keyId = String.valueOf(issue.getKeyId());
        BacklogIssue backlogIssue = issueCache.get(keyId);
        if (backlogIssue != null) {
            // #27
            if (isRefresh) {
                backlogIssue.refreshIssue(issue);
            }
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
    public synchronized void addIssue(BacklogIssue issue) {
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
        Project p = getProject();
        if (p == null) {
            return Collections.emptyList();
        }
        List<BacklogIssue> backlogIssues = new ArrayList<>(keyIds.length);
        for (String keyId : keyIds) {
            BacklogIssue backlogIssue;
            synchronized (this) {
                backlogIssue = issueCache.get(keyId);
            }
            if (backlogIssue != null) {
                backlogIssues.add(backlogIssue);
                continue;
            }
            // get issue
            BacklogClient client = createBacklogClient();
            if (client == null) {
                return Collections.emptyList();
            }
            String issueKey = String.format("%s-%s", p.getProjectKey(), keyId);
            try {
                Issue issue = client.getIssue(issueKey);
                if (issue != null) {
                    backlogIssue = createIssue(issue, false);
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
     * @param isRefresh {@code true} if clear an issue cache, otherwise
     * {@code false}
     * @return BacklogIssues
     */
    public Collection<BacklogIssue> getIssues(GetIssuesParams issuesParams, boolean isRefresh) {
        Project p = getProject();
        if (p == null || issuesParams == null) {
            return Collections.emptyList();
        }
        BacklogClient backlogClient = createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        List<BacklogIssue> backlogIssues = new ArrayList<>();
        try {
            ResponseList<Issue> issues = backlogClient.getIssues(issuesParams);
            for (Issue issue : issues) {
                BacklogIssue backlogIssue = createIssue(issue, isRefresh);
                backlogIssues.add(backlogIssue);
            }
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
        return backlogIssues;
    }

    /**
     * Get Notifications.
     *
     * @return Notifications
     */
    public Collection<Notification> getNotifications() {
        Project p = getProject();
        if (p == null) {
            return Collections.emptyList();
        }
        BacklogClient backlogClient = createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        List<Notification> notifications = new ArrayList<>();
        try {
            ResponseList<Notification> responses = backlogClient.getNotifications();
            notifications.addAll(responses);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
        return notifications;
    }

    /**
     * Mark as read a notification.
     *
     * @param id notification identifer
     */
    public void markAsReadNotification(long id) {
        Project p = getProject();
        if (p == null) {
            return;
        }
        BacklogClient backlogClient = createBacklogClient();
        if (backlogClient == null) {
            return;
        }
        try {
            backlogClient.markAsReadNotification(id);
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
    }

    /**
     * Reset notification count.
     *
     */
    public void resetNotificationCount() {
        Project p = getProject();
        if (p == null) {
            return;
        }
        BacklogClient backlogClient = createBacklogClient();
        if (backlogClient == null) {
            return;
        }
        try {
            backlogClient.resetNotificationCount();
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
    }

    /**
     * Get BacklogIssue for Notification.
     *
     * @param notification Notification
     * @param isRefresh
     * @return BacklogIssue
     */
    public BacklogIssue getIssue(@NonNull Notification notification, boolean isRefresh) {
        Issue issue = notification.getIssue();
        return createIssue(issue, isRefresh);
    }

    /**
     * Get BacklogIssue.
     *
     * @param issueKey issue key
     * @return BacklogIssue
     */
    @CheckForNull
    public BacklogIssue getIssue(String issueKey) {
        if (issueKey == null) {
            return null;
        }
        // try to get from cache
        synchronized (this) {
            for (BacklogIssue backlogIssue : issueCache.values()) {
                if (issueKey.equals(backlogIssue.getIssueKey())) {
                    return backlogIssue;
                }
            }
        }
        BacklogClient backlogClient = createBacklogClient();
        if (backlogClient == null) {
            return null;
        }
        try {
            Issue issue = backlogClient.getIssue(issueKey);
            if (issue != null) {
                return createIssue(issue, false);
            }
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
        return null;
    }

    /**
     * Get BacklogIssue.
     *
     * @param issueId issue id (not issue key id)
     * @return BacklogIssue if issue exists, otherwise {@code null}
     */
    @CheckForNull
    public BacklogIssue getIssue(long issueId) {
        BacklogClient backlogClient = createBacklogClient();
        if (backlogClient == null) {
            return null;
        }
        try {
            Issue issue = backlogClient.getIssue(issueId);
            if (issue != null) {
                return createIssue(issue, false);
            }
        } catch (BacklogAPIException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
        }
        return null;
    }

    /**
     * Get subissue ids.
     *
     * @param parentIssue parent BacklogIssue
     * @return subissue ids
     */
    public List<String> getSubissueIds(BacklogIssue parentIssue) {
        List<Issue> children = getSubissues(parentIssue);
        ArrayList<String> ids = new ArrayList<>(children.size());
        for (Issue child : children) {
            ids.add(String.valueOf(child.getKeyId()));
        }
        return ids;
    }

    /**
     * Get subissues.
     *
     * @param parentIssue parent BacklogIssue
     * @return subissues
     */
    public List<Issue> getSubissues(BacklogIssue parentIssue) {
        Project p = getProject();
        if (p == null || !p.isSubtaskingEnabled()) {
            return Collections.emptyList();
        }
        BacklogClient backlogClient = createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        GetIssuesParams issuesParams = new GetIssuesParams(Collections.singletonList(p.getId()))
                .parentChildType(GetIssuesParams.ParentChildType.Child)
                .parentIssueIds(Collections.singletonList(parentIssue.getIssue().getId()));
        try {
            return backlogClient.getIssues(issuesParams);
        } catch (BacklogAPIException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Get subissues from a parent issue.
     *
     * @param parentIssue parent BacklogIssue
     * @return BacklogIssues
     */
    public List<BacklogIssue> getBacklogSubissues(BacklogIssue parentIssue) {
        List<Issue> subissues = getSubissues(parentIssue);
        ArrayList<BacklogIssue> backlogSubissues = new ArrayList<>(subissues.size());
        for (Issue subissue : subissues) {
            backlogSubissues.add(createIssue(subissue, false));
        }
        return backlogSubissues;
    }

    /**
     * Get parent issue.
     *
     * @param childIssue child BacklogIssue
     * @return parent issue if it exists, otherwise {@code null}
     */
    public BacklogIssue getParentIssue(BacklogIssue childIssue) {
        if (childIssue == null || childIssue.getIssue() == null) {
            return null;
        }
        long parentIssueId = childIssue.getIssue().getParentIssueId();
        if (parentIssueId <= 0) {
            return childIssue;
        }

        // check cache
        synchronized (this) {
            for (BacklogIssue issue : issueCache.values()) {
                Issue i = issue.getIssue();
                if (i.getId() == parentIssueId) {
                    return issue;
                }
            }
        }

        // get issue from online
        return getIssue(parentIssueId);
    }

    public void createNewSubissue(BacklogIssue subtaskParentIssue) {
        setSubtaskParentIssue(subtaskParentIssue);
        Repository repository = RepositoryManager.getInstance().getRepository(BacklogConnector.ID, getID());
        Util.createNewIssue(repository);
    }

    private void setSubtaskParentIssue(BacklogIssue subtaskParentIssue) {
        this.subtaskParentIssue = subtaskParentIssue;
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
            if (options.isNotificationsQuery()) {
                addQuery(getNotificationQuery());
            }

            // add user queries
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
        getQueries().add(query);
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
        getQueries().remove(query);
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
        setDefaultQuery(getNotificationQuery(), options.isNotificationsQuery());
        fireQueryListChanged();
    }

    private void setDefaultQuery(BacklogQuery query, boolean isEnabled) {
        if (isEnabled) {
            if (!getQueries().contains(query)) {
                getQueries().add(query);
            }
        } else if (getQueries().contains(query)) {
            getQueries().remove(query);
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
        Project p = getProject();
        if (p == null) {
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
        issuesParams = new GetIssuesParams(Collections.singletonList(p.getId()))
                .keyword(criteria);
        issues.addAll(getIssues(issuesParams, false));
        return issues;
    }

    /**
     * Get Backlog domain.
     *
     * @return backlog domain.
     */
    public String getBacklogDomain() {
        return getPropertyValue(PROPERTY_BACKLOG_DOMAIN);
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
            if (client == null) {
                return null;
            }
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
        String url;
        String backlogDomain = repositoryInfo.getBacklogDomain();
        if (backlogDomain != null) {
            switch (backlogDomain) {
                case BacklogUtils.BACKLOG_JP:
                    url = String.format("https://%s.backlog.jp/", repositoryInfo.getSpaceId()); // NOI18N
                    break;
                case BacklogUtils.BACKLOGTOOL_COM:
                    url = String.format("https://%s.backlogtool.com/", repositoryInfo.getSpaceId()); // NOI18N
                    break;
                default:
                    throw new AssertionError();
            }
        } else {
            url = ""; // NOI18N
        }
        info = createRepositoryInfo(repositoryInfo, url, null, null, null, null);
        setProperties(repositoryInfo);

        project = repositoryInfo.getProject();
    }

    private void setProperties(BacklogRepositoryInfo repositoryInfo) {
        if (info != null) {
            info.putValue(PROPERTY_BACKLOG_DOMAIN, repositoryInfo.getBacklogDomain());
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

    /**
     * Get NotificationQuery.
     *
     * @return CreatedByMeQuery
     */
    private BacklogQuery getNotificationQuery() {
        return notificationQuery;
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
