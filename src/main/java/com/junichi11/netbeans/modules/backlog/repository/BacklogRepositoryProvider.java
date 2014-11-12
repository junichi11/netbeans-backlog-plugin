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

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.query.BacklogQuery;
import org.netbeans.modules.bugtracking.spi.RepositoryController;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.spi.RepositoryProvider;

/**
 *
 * @author junichi11
 */
public class BacklogRepositoryProvider implements RepositoryProvider<BacklogRepository, BacklogQuery, BacklogIssue> {

    @Override
    public RepositoryInfo getInfo(BacklogRepository repository) {
        return repository.getInfo();
    }

    @Override
    public Image getIcon(BacklogRepository repository) {
        return repository.getIcon();
    }

    /**
     * Returns issues with the given IDs.
     *
     * @param repository repository
     * @param ids key ids
     * @return issues
     */
    @Override
    public Collection<BacklogIssue> getIssues(BacklogRepository repository, String... ids) {
        return repository.getIssues(ids);
    }

    @Override
    public void removed(BacklogRepository repository) {
        repository.removed();
        BacklogRepositoryManager.getInstance().remove(repository);
    }

    @Override
    public RepositoryController getController(BacklogRepository repository) {
        return repository.getController();
    }

    @Override
    public BacklogQuery createQuery(BacklogRepository repository) {
        return repository.createQuery();
    }

    @Override
    public BacklogIssue createIssue(BacklogRepository repository) {
        return repository.createIssue();
    }

    @Override
    public BacklogIssue createIssue(BacklogRepository repository, String summary, String description) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<BacklogQuery> getQueries(BacklogRepository repository) {
        return repository.getQueries();
    }

    /**
     * Runs a query against the bugtracking repository to get all issues for
     * which applies that the ID equals to or the summary contains the given
     * criteria string.
     *
     * @param repository repository
     * @param criteria
     * @return collection of issues
     */
    @Override
    public Collection<BacklogIssue> simpleSearch(BacklogRepository repository, String criteria) {
        return repository.simpleSearch(criteria);
    }

    @Override
    public boolean canAttachFiles(BacklogRepository repository) {
        return repository.canAttachFiles();
    }

    @Override
    public void removePropertyChangeListener(BacklogRepository repository, PropertyChangeListener listener) {
        repository.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(BacklogRepository repository, PropertyChangeListener listener) {
        repository.addPropertyChangeListener(listener);
    }

}
