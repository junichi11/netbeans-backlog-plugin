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

import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.junichi11.netbeans.modules.backlog.BacklogConnector;
import com.junichi11.netbeans.modules.backlog.repository.ui.BacklogRepositoryPanel;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.api.RepositoryManager;
import org.netbeans.modules.bugtracking.spi.RepositoryController;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class BacklogRepositoryController implements RepositoryController, ChangeListener {

    private final BacklogRepository repository;
    private BacklogRepositoryPanel panel;
    private String errorMessage;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public BacklogRepositoryController(BacklogRepository repository) {
        this.repository = repository;
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public boolean isValid() {
        validate();
        return errorMessage == null;
    }

    @NbBundle.Messages({
        "# {0} - name",
        "BacklogRepositoryController.error.message.empty={0} must be set.",
        "BacklogRepositoryController.error.message.already.exists=Repository name already exists.",
        "BacklogRepositoryController.error.message.connection=Please press connect button to get your projects.",
        "LBL.DisplayName=Display name",
        "LBL.SpaceId=Space ID",
        "LBL.ApiKey=Api key",
        "LBL.ProjectKey=Project key"
    })
    private void validate() {
        // space id
        String spaceId = getPanel().getSpaceId();
        if (StringUtils.isEmpty(spaceId)) {
            errorMessage = Bundle.BacklogRepositoryController_error_message_empty(Bundle.LBL_SpaceId());
            return;
        }

        // api key
        String apiKey = getPanel().getApiKey();
        if (StringUtils.isEmpty(apiKey)) {
            errorMessage = Bundle.BacklogRepositoryController_error_message_empty(Bundle.LBL_ApiKey());
            return;
        }

        // press connect button?
        if (!getPanel().isConnectionSuccessful()) {
            errorMessage = Bundle.BacklogRepositoryController_error_message_connection();
            return;
        }

        // select project?
        if (StringUtils.isEmpty(getPanel().getProjectKey())) {
            errorMessage = Bundle.BacklogRepositoryController_error_message_empty(Bundle.LBL_ProjectKey());
            return;
        }

        // display name
        String displayName = getPanel().getDisplayName();
        // is empty?
        if (StringUtils.isEmpty(displayName)) {
            errorMessage = Bundle.BacklogRepositoryController_error_message_empty(Bundle.LBL_DisplayName());
            return;
        }
        // is unique?
        Collection<Repository> repositories = RepositoryManager.getInstance().getRepositories(BacklogConnector.ID);
        String id = repository.getID();
        for (Repository repo : repositories) {
            if (id != null || repo.getId().equals(id)) {
                continue;
            }
            if (displayName.equals(repo.getDisplayName())) {
                errorMessage = Bundle.BacklogRepositoryController_error_message_already_exists();
                return;
            }
        }

        errorMessage = null;
    }

    @Override
    public void populate() {
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void applyChanges() {
        // set RepositoryInfo
        BacklogRepositoryPanel p = getPanel();
        BacklogRepositoryInfo repositoryInfo = new BacklogRepositoryInfo()
                .setDisplayName(p.getDisplayName())
                .setApiKey(p.getApiKey())
                .setSpaceId(p.getSpaceId())
                .setProjectKey(p.getProjectKey())
                .setProject(p.getProject());
        repository.setRepositoryInfo(repositoryInfo);
    }

    @Override
    public void cancelChanges() {
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private BacklogRepositoryPanel getPanel() {
        if (panel == null) {
            RepositoryInfo info = repository.getInfo();
            if (info != null) {
                // existing repository
                panel = new BacklogRepositoryPanel(repository);
            } else {
                // new repository
                panel = new BacklogRepositoryPanel();
            }
            panel.addChangeListener(this);
        }
        return panel;
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChange();
    }

}
