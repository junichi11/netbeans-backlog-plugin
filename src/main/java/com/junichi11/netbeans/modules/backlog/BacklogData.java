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

import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.nulabinc.backlog4j.BacklogAPIException;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.Category;
import com.nulabinc.backlog4j.DiskUsage;
import com.nulabinc.backlog4j.IssueType;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Resolution;
import com.nulabinc.backlog4j.Status;
import com.nulabinc.backlog4j.User;
import com.nulabinc.backlog4j.Version;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

/**
 * Data cache.
 *
 * @author junichi11
 */
public final class BacklogData {

    private BacklogPlan plan;
    private List<Category> categories;
    private List<IssueType> issueTypes;
    private List<Priority> priorities;
    private List<Resolution> resolutions;
    private List<Status> status;
    private List<User> users;
    private List<Version> versions;
    private User myself;
    private final Map<User, Icon> userIcons = new HashMap<>();
    private final BacklogRepository repository;
    private static final Map<String, BacklogData> DATA = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(BacklogData.class.getName());

    private BacklogData(BacklogRepository repository) {
        this.repository = repository;
    }

    public static BacklogData create(@NonNull BacklogRepository repository) {
        String id = repository.getID();
        BacklogData data = DATA.get(id);
        if (data != null) {
            return data;
        }
        data = new BacklogData(repository);
        DATA.put(id, data);
        return data;
    }

    /**
     * Get Priorities.
     *
     * @return Priorities
     */
    public List<Priority> getPriorities() {
        return getPriorities(false);
    }

    /**
     * Get Priorities.
     *
     * @param isForce {@code true} if you want to reload data, {@code false} if
     * you want to use cache data
     * @return Priorities
     */
    public List<Priority> getPriorities(boolean isForce) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        if (priorities == null || isForce) {
            priorities = backlogClient.getPriorities();
        }
        return priorities;
    }

    /**
     * Get IssueTypes.
     *
     * @return IssueTypes
     */
    public List<IssueType> getIssueTypes() {
        return getIssueTypes(false);
    }

    /**
     * Get IssueTypes.
     *
     * @param isForce {@code true} if you want to reload data, {@code false} if
     * you want to use cache data
     * @return IssueTypes
     */
    public List<IssueType> getIssueTypes(boolean isForce) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        if (issueTypes == null || isForce) {
            issueTypes = backlogClient.getIssueTypes(repository.getProjectKey());
        }
        return issueTypes;
    }

    /**
     * Get Users.
     *
     * @return Users
     */
    public List<User> getUsers() {
        return getUsers(false);
    }

    /**
     * Get user icon. Icon size is 16x16.
     *
     * @param user
     * @return user icon if it was got, otherwise {@code null}
     */
    @CheckForNull
    public Icon getUserIcon(User user) {
        Icon icon = userIcons.get(user);
        if (icon != null) {
            return icon;
        }
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient != null) {
            try {
                com.nulabinc.backlog4j.Icon userIcon = backlogClient.getUserIcon(user.getId());
                if (userIcon != null) {
                    try {
                        // resize to 16x16
                        BufferedImage image = ImageIO.read(userIcon.getContent());
                        Image resizedImage = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(resizedImage);
                        userIcons.put(user, icon);
                        return icon;
                    } catch (IOException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage());
                    }
                }
            } catch (BacklogAPIException ex) {
                LOGGER.log(Level.WARNING, "{0}:{1}", new Object[]{repository.getProjectKey(), ex.getMessage()}); // NOI18N
            }
        }
        return null;
    }

    /**
     * Get Users.
     *
     * @param isForce {@code true} if you want to reload data, {@code false} if
     * you want to use cache data
     * @return Users
     */
    public List<User> getUsers(boolean isForce) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        if (users == null || isForce) {
            users = backlogClient.getUsers();
        }
        return users;
    }

    /**
     * Get Categories.
     *
     * @return Categories
     */
    public List<Category> getCategories() {
        return getCategories(false);
    }

    /**
     * Get Categories.
     *
     * @param isForce {@code true} if you want to reload data, {@code false} if
     * you want to use cache data
     * @return Categories
     */
    public List<Category> getCategories(boolean isForce) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        if (categories == null || isForce) {
            categories = backlogClient.getCategories(repository.getProjectKey());
        }
        return categories;
    }

    /**
     * Get Versions.
     *
     * @return Versions
     */
    public List<Version> getVersions() {
        return getVersions(false);
    }

    /**
     * Get Versions.
     *
     * @param isForce {@code true} if you want to reload data, {@code false} if
     * you want to use cache data
     * @return Versions
     */
    public List<Version> getVersions(boolean isForce) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        if (versions == null || isForce) {
            versions = backlogClient.getVersions(repository.getProjectKey());
        }
        return versions;
    }

    /**
     * Get Status.
     *
     * @return Status
     */
    public List<Status> getStatus() {
        return getStatus(false);
    }

    /**
     * Get Status.
     *
     * @param isForce {@code true} if you want to reload data, {@code false} if
     * you want to use cache data
     * @return Status
     */
    public List<Status> getStatus(boolean isForce) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        if (status == null || isForce) {
            status = backlogClient.getStatuses();
        }
        return status;
    }

    /**
     * Get Resolutions.
     *
     * @return Resolutions
     */
    public List<Resolution> getResolutions() {
        return getResolutions(false);
    }

    /**
     * Get Resolutions.
     *
     * @param isForce {@code true} if you want to reload data, {@code false} if
     * you want to use cache data
     * @return Resolutions
     */
    public List<Resolution> getResolutions(boolean isForce) {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return Collections.emptyList();
        }
        if (resolutions == null || isForce) {
            resolutions = backlogClient.getResolutions();
        }
        return resolutions;
    }

    /**
     * Get Myself.
     *
     * @return Myself
     */
    public User getMyself() {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return null;
        }
        if (myself == null) {
            myself = backlogClient.getMyself();
        }
        return myself;
    }

    /**
     * Get Backlog Plan.
     *
     * @return Backlog Plan
     */
    public BacklogPlan getPlan() {
        BacklogClient backlogClient = repository.createBacklogClient();
        if (backlogClient == null) {
            return BacklogPlan.FREE;
        }
        if (plan == null) {
            DiskUsage diskUsage = backlogClient.getSpaceDiskUsage();
            long capacity = diskUsage.getCapacity();
            plan = BacklogPlan.valueOfCapacity(capacity);
        }
        return plan;
    }
}
