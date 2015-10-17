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
import com.junichi11.netbeans.modules.backlog.query.BacklogQuery;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider.Status;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public final class BacklogConfig {

    private static final BacklogConfig INSTANCE = new BacklogConfig();

    // query
    private static final String QUERY = "query"; // NOI18N
    private static final String QUERY_PARAMS = "query.params"; // NOI18N

    // status
    private static final String STATUS = "status"; // NOI18N
    //  [status]::[last updated]
    private static final String STATUS_FORMAT = "%s::%s"; // NOI18N
    private static final String STATUS_DELIMITER = "::"; // NOI18N

    // template
    private static final String TEMPLATE = "template"; // NOI18N
    private static final String DEFAULT_TEMPLATE_NAME = "default"; // NOI18N

    private static final String QUERY_MAX_ISSUE_COUNT = "query.max.issue.count"; // NOI18N

    private BacklogConfig() {
    }

    public static BacklogConfig getInstance() {
        return INSTANCE;
    }

    public Status getStatus(BacklogIssue issue) {
        BacklogRepository repository = issue.getRepository();
        Preferences preferences = getPreferences().node(repository.getID()).node(STATUS);
        String statusTime = preferences.get(issue.getKeyId(), null);
        if (statusTime == null) {
            return Status.INCOMING_NEW;
        }

        String[] split = statusTime.split(STATUS_DELIMITER);
        if (split.length != 2) {
            return Status.INCOMING_NEW;
        }

        // TODO CONFLICT, OUTGOING_NEW, OUTGOING_MODIFIED
        Status status = Status.valueOf(split[0]);
        long lastUpdated = Long.parseLong(split[1]);
        if (status == Status.SEEN) {
            long lastUpdatedTime = issue.getLastUpdatedTime();
            if (lastUpdatedTime != -1L) {
                if (lastUpdated < lastUpdatedTime) {
                    setStatus(issue, Status.INCOMING_MODIFIED);
                    return Status.INCOMING_MODIFIED;
                }
            }
        }
        return status;
    }

    public void setStatus(BacklogIssue issue, Status status) {
        long lastUpdatedTime = issue.getLastUpdatedTime();
        if (lastUpdatedTime != -1L) {
            BacklogRepository repository = issue.getRepository();
            Preferences preferences = getPreferences().node(repository.getID()).node(STATUS);
            preferences.put(issue.getKeyId(), String.format(STATUS_FORMAT, status.name(), lastUpdatedTime));
        }
    }

    /**
     * Return saved query names.
     *
     * @param repository repository
     * @return saved query names
     */
    public String[] getQueryNames(BacklogRepository repository) {
        Preferences preferences = getPreferences().node(repository.getID()).node(QUERY);
        try {
            return preferences.childrenNames();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new String[0];
    }

    /**
     * Return parameters for specified name.
     *
     * @param repository repository
     * @param queryName query name
     * @return query parameters if name exists, otherwise {@code null}
     */
    public String getQueryParams(BacklogRepository repository, String queryName) {
        Preferences preferences = getPreferences().node(repository.getID()).node(QUERY).node(queryName);
        return preferences.get(QUERY_PARAMS, null);
    }

    /**
     * Save parameters for specified query.
     *
     * @param repository repository
     * @param query query
     */
    public void setQueryParams(BacklogRepository repository, BacklogQuery query) {
        String id = repository.getID();
        Preferences preferences = getPreferences().node(id).node(QUERY).node(query.getDisplayName());
        preferences.put(QUERY_PARAMS, query.getQueryParam());
    }

    /**
     * Remove configurations for specified query.
     *
     * @param repository repository
     * @param query query
     */
    public void removeQuery(BacklogRepository repository, BacklogQuery query) {
        String displayName = query.getDisplayName();
        if (StringUtils.isEmpty(displayName)) {
            return;
        }
        Preferences preferences = getPreferences().node(repository.getID()).node(QUERY).node(displayName);
        try {
            preferences.removeNode();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Return the max issue count for a query.
     *
     * @param repository BacklogRepository
     * @param queryName a query name
     * @return the max issue count (default value is 20)
     */
    public int getMaxIssueCount(BacklogRepository repository, String queryName) {
        String id = repository.getID();
        Preferences preferences = getPreferences().node(id).node(QUERY).node(queryName);
        return preferences.getInt(QUERY_MAX_ISSUE_COUNT, 20);
    }

    /**
     * Set the max issue count for a query.
     *
     * @param repository BacklogRepository
     * @param query BacklogQuery
     */
    public void setMaxIssueCount(BacklogRepository repository, BacklogQuery query) {
        String id = repository.getID();
        Preferences preferences = getPreferences().node(id).node(QUERY).node(query.getDisplayName());
        preferences.putInt(QUERY_MAX_ISSUE_COUNT, query.getMaxIssueCount());
    }

    /**
     * Get the template for specified name.
     *
     * @param name the template name
     * @return the template
     */
    @NbBundle.Messages("BacklogConfig.default.template=#### Overview description\n"
            + "\n"
            + "#### Steps to reproduce\n"
            + "\n"
            + "1. \n"
            + "2. \n"
            + "3. \n"
            + "\n"
            + "#### Actual results\n"
            + "\n"
            + "#### Expected results\n")
    public String getTemplate(String name) {
        return getPreferences().node(TEMPLATE).get(name, Bundle.BacklogConfig_default_template());
    }

    /**
     * Set template.
     *
     * @param name the template name
     * @param template the template
     */
    public void setTemplate(String name, String template) {
        getPreferences().node(TEMPLATE).put(name, template);
    }

    /**
     * Remove a template. <b>NOTE:</b> Can't remove the default template. But
     * default template will be initialized.
     *
     * @param name the template name
     */
    public void removeTemplate(String name) {
        getPreferences().node(TEMPLATE).remove(name);
    }

    /**
     * Get all template names.
     *
     * @return all template names
     */
    public String[] getTemplateNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add(DEFAULT_TEMPLATE_NAME);
        Preferences preferences = getPreferences().node(TEMPLATE);
        try {
            String[] childrenNames = preferences.keys();
            names.addAll(Arrays.asList(childrenNames));
            return names.toArray(new String[childrenNames.length + 1]);
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return names.toArray(new String[1]);
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(BacklogConfig.class);
    }
}
