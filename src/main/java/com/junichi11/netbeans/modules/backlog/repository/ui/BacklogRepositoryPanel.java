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
package com.junichi11.netbeans.modules.backlog.repository.ui;

import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import com.junichi11.netbeans.modules.backlog.utils.UiUtils;
import com.nulabinc.backlog4j.BacklogAPIException;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.BacklogClientFactory;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.ResponseList;
import com.nulabinc.backlog4j.conf.BacklogConfigure;
import com.nulabinc.backlog4j.conf.BacklogJpConfigure;
import com.nulabinc.backlog4j.conf.BacklogToolConfigure;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class BacklogRepositoryPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -5508697702484903925L;

    private final ChangeSupport changeSupprt = new ChangeSupport(this);
    private ResponseList<Project> projects;
    private final DefaultComboBoxModel<Project> projectComboBoxModel = new DefaultComboBoxModel<>();

    private boolean isConnectionSuccessful;

    /**
     * Creates new form RepositoryPanel
     */
    public BacklogRepositoryPanel() {
        initComponents();
        init();
    }

    public BacklogRepositoryPanel(BacklogRepository repository) {
        this();
        if (repository == null) {
            return;
        }
        RepositoryInfo info = repository.getInfo();
        if (info == null) {
            return;
        }
        setBacklogDomain(repository.getBacklogDomain());
        setDisplayName(info.getDisplayName());
        setApiKey(repository.getApiKey());
        setSaceId(repository.getSpaceId());
        setProject(repository.getProject());
    }

    @NbBundle.Messages({
        "BacklogRepositoryPanel.tooltip.display.name.button=Set project name to display name."
    })
    private void init() {
        // add DocumentListener
        DefaultDocumentListener documentListener = new DefaultDocumentListener();
        ApiKeyDocumentLisntener apiKeyDocumentLisntener = new ApiKeyDocumentLisntener();
        nameTextField.getDocument().addDocumentListener(documentListener);
        spaceIdTextField.getDocument().addDocumentListener(documentListener);
        apiKeyTextField.getDocument().addDocumentListener(apiKeyDocumentLisntener);

        ListCellRenderer<? super Project> renderer = projectComboBox.getRenderer();
        projectComboBox.setRenderer(new ComboBoxListCellRenderer(renderer));
        projectComboBox.setModel(projectComboBoxModel);

        backlogComboBox.setModel(new DefaultComboBoxModel<>(BacklogUtils.BACKLOG_DOMAINS.toArray(new String[0])));
        backlogComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                isConnectionSuccessful = false;
                fireChange();
            }
        });

        // tooltip
        setDisplayNameButton.setToolTipText(Bundle.BacklogRepositoryPanel_tooltip_display_name_button());
    }

    public String getBacklogDomain() {
        String domain = (String) backlogComboBox.getSelectedItem();
        if (domain == null) {
            backlogComboBox.setSelectedItem(BacklogUtils.BACKLOG_JP);
            domain = BacklogUtils.BACKLOG_JP;
        }
        return domain;
    }

    public String getDisplayName() {
        return nameTextField.getText().trim();
    }

    public String getSpaceId() {
        return spaceIdTextField.getText().trim();
    }

    public String getApiKey() {
        return apiKeyTextField.getText().trim();
    }

    public String getProjectKey() {
        Project project = (Project) projectComboBoxModel.getSelectedItem();
        String projectKey = ""; // NOI18N
        if (project != null) {
            projectKey = project.getProjectKey();
        }
        return projectKey;
    }

    public Project getProject() {
        return (Project) projectComboBoxModel.getSelectedItem();
    }

    private void setBacklogDomain(String domain) {
        if (StringUtils.isEmpty(domain)) {
            Locale locale = Locale.getDefault();
            if (locale != null && locale == Locale.JAPAN) {
                backlogComboBox.setSelectedItem(BacklogUtils.BACKLOG_JP);
            } else {
                backlogComboBox.setSelectedItem(BacklogUtils.BACKLOGTOOL_COM);
            }
            return;
        }
        backlogComboBox.setSelectedItem(domain);
    }

    private void setDisplayName(String name) {
        nameTextField.setText(name);
    }

    private void setSaceId(String spaceId) {
        spaceIdTextField.setText(spaceId);
    }

    private void setApiKey(String apiKey) {
        apiKeyTextField.setText(apiKey);
    }

    private void setProject(Project project) {
        if (project != null) {
            projectComboBoxModel.addElement(project);
        }
    }

    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupprt.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupprt.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupprt.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backlogLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        spaceIdLabel = new javax.swing.JLabel();
        apiKeyLabel = new javax.swing.JLabel();
        backlogComboBox = new javax.swing.JComboBox<String>();
        nameTextField = new javax.swing.JTextField();
        spaceIdTextField = new javax.swing.JTextField();
        apiKeyTextField = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        projectLabel = new javax.swing.JLabel();
        projectComboBox = new javax.swing.JComboBox<Project>();
        setDisplayNameButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(backlogLabel, org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.backlogLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.nameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(spaceIdLabel, org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.spaceIdLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(apiKeyLabel, org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.apiKeyLabel.text")); // NOI18N

        nameTextField.setText(org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.nameTextField.text")); // NOI18N

        spaceIdTextField.setText(org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.spaceIdTextField.text")); // NOI18N

        apiKeyTextField.setText(org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.apiKeyTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(connectButton, org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.connectButton.text")); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.projectLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(setDisplayNameButton, org.openide.util.NbBundle.getMessage(BacklogRepositoryPanel.class, "BacklogRepositoryPanel.setDisplayNameButton.text")); // NOI18N
        setDisplayNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDisplayNameButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(setDisplayNameButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connectButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spaceIdLabel)
                            .addComponent(apiKeyLabel)
                            .addComponent(nameLabel)
                            .addComponent(projectLabel)
                            .addComponent(backlogLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(backlogComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(spaceIdTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(apiKeyTextField)
                            .addComponent(nameTextField)
                            .addComponent(projectComboBox, 0, 452, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backlogLabel)
                    .addComponent(backlogComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spaceIdLabel)
                    .addComponent(spaceIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(apiKeyLabel)
                    .addComponent(apiKeyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connectButton)
                    .addComponent(setDisplayNameButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectLabel)
                    .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages({
        "BacklogRepositoryPanel.connection.successful=Connection successful"
    })
    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        connectButton.setEnabled(false);
        String domain = getBacklogDomain();
        String spaceId = getSpaceId();
        String apiKey = getApiKey();
        if (StringUtils.isEmpty(Arrays.asList(domain, spaceId, apiKey))) {
            return;
        }
        isConnectionSuccessful = true;
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
                    throw new AssertionError();
            }
            BacklogClient backlog = new BacklogClientFactory(configure).newClient();
            // try to get projects
            projects = backlog.getProjects();
            projectComboBoxModel.removeAllElements();
            for (Project project : projects) {
                projectComboBoxModel.addElement(project);
            }
        } catch (BacklogAPIException ex) {
            isConnectionSuccessful = false;
            // show dialog
            UiUtils.showErrorDialog(ex.getMessage());
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (isConnectionSuccessful) {
            // show dialog
            UiUtils.showPlainDialog(Bundle.BacklogRepositoryPanel_connection_successful());
        }
        fireChange();
        connectButton.setEnabled(true);
    }//GEN-LAST:event_connectButtonActionPerformed

    private void setDisplayNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDisplayNameButtonActionPerformed
        Project project = (Project) projectComboBox.getSelectedItem();
        if (project == null) {
            return;
        }
        String projectName = String.format("%s(%s)", project.getName(), project.getProjectKey()); // NOI18N
        nameTextField.setText(projectName);
    }//GEN-LAST:event_setDisplayNameButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel apiKeyLabel;
    private javax.swing.JTextField apiKeyTextField;
    private javax.swing.JComboBox<String> backlogComboBox;
    private javax.swing.JLabel backlogLabel;
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<Project> projectComboBox;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JButton setDisplayNameButton;
    private javax.swing.JLabel spaceIdLabel;
    private javax.swing.JTextField spaceIdTextField;
    // End of variables declaration//GEN-END:variables

    private static class ComboBoxListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 2631602005613866475L;

        private final ListCellRenderer renderer;

        public ComboBoxListCellRenderer(ListCellRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String text = null;
            if (value instanceof Project) {
                Project project = (Project) value;
                text = String.format("%s(%s)", project.getName(), project.getProjectKey()); // NOI18N
            }
            return renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }

    }

    private class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }
    }

    private class ApiKeyDocumentLisntener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            isConnectionSuccessful = false;
            fireChange();
        }
    }

}
