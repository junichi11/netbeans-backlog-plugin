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
package com.junichi11.netbeans.modules.backlog.query.ui;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import com.junichi11.netbeans.modules.backlog.Backlog.FileType;
import com.junichi11.netbeans.modules.backlog.query.BacklogQuery;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import java.awt.Container;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class BacklogQueryPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -2602864183602734990L;
    private final GeneralPanel generalPanel;
    private final DatePanel datePanel;
    private final BacklogRepository repository;
    private final BacklogQuery query;

    /**
     * Creates new form BacklogQueryPanel
     */
    public BacklogQueryPanel(BacklogRepository repository, BacklogQuery query, JComponent tableComponent) {
        this.repository = repository;
        this.query = query;
        initComponents();
        setQueryName();
        generalPanel = new GeneralPanel(repository);
        generalPanel.reset();
        datePanel = new DatePanel(BacklogUtils.isChartEnabled(repository));
        mainGeneralPanel.add(generalPanel);
        mainDatePanel.add(datePanel);
        Font font = keywordLabel.getFont();
        queryNameLabel.setFont(font.deriveFont((float) (font.getSize() * 1.5)));
        // issue table
        mainIssueTablePanel.add(tableComponent);
        update();
    }

    public GeneralPanel getGeneralPanel() {
        return generalPanel;
    }

    public DatePanel getDatePanel() {
        return datePanel;
    }

    public String getKeyword() {
        return keywordTextField.getText();
    }

    public void addSearchButtonActionListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void removeSearchButtonActionListener(ActionListener listener) {
        searchButton.removeActionListener(listener);
    }

    public void addSaveButtonActionListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public void removeSaveButtonActionListener(ActionListener listener) {
        saveButton.removeActionListener(listener);
    }

    public boolean isSearchButton(Object button) {
        return button == searchButton;
    }

    public boolean isSaveButton(Object button) {
        return button == saveButton;
    }

    public void setKeyword(String keyword) {
        keywordTextField.setText(keyword);
    }

    public final void update() {
        setQueryName();
        if (query.isSaved()) {
            setKeyword(query.getKeyword());
            // GeneralPanel
            generalPanel.setSelectedStatus(query.getStatusIds());
            generalPanel.setSelectedPriority(query.getPriorityIds());
            generalPanel.setSelectedCategory(query.getCategoryIds());
            generalPanel.setSelectedAssignee(query.getAssigneeIds());
            generalPanel.setSelectedVersion(query.getVersionIds());
            generalPanel.setSelectedMilestone(query.getMilestoneIds());
            generalPanel.setSelectedRegisteredBy(query.getCreatedUserIds());
            generalPanel.setSelectedIssueType(query.getIssueTypeIds());
            generalPanel.setSelectedResolution(query.getResolutionIds());
            // file
            List<FileType> fileTypes = new ArrayList<>();
            if (query.isAttachment()) {
                fileTypes.add(FileType.ATTACHED);
            }
            if (query.isSharedFile()) {
                fileTypes.add(FileType.SHARED);
            }
            generalPanel.setSelectedFiles(fileTypes);
            // DatePanel
            datePanel.setCreatedSince(toSlashDateFormat(query.getCreatedSince()));
            datePanel.setCreatedUntil(toSlashDateFormat(query.getCreatedUntil()));
            datePanel.setUpdatedSince(toSlashDateFormat(query.getUpdatedSince()));
            datePanel.setUpdatedUntil(toSlashDateFormat(query.getUpdatedUntil()));
            datePanel.setStartDateSince(toSlashDateFormat(query.getStartDateSince()));
            datePanel.setStartDateUntil(toSlashDateFormat(query.getStartDateUntil()));
            datePanel.setDueDateSince(toSlashDateFormat(query.getDueDateSince()));
            datePanel.setDueDateUntil(toSlashDateFormat(query.getDueDateUntil()));
        }
    }

    private static String toSlashDateFormat(String date) {
        return date.replace("-", "/"); // NOI18N
    }

    @NbBundle.Messages({
        "BacklogQueryPanel.new.query.name=New Query"
    })
    private void setQueryName() {
        if (query == null) {
            setQueryName(Bundle.BacklogQueryPanel_new_query_name());
            return;
        }
        String displayName = query.getDisplayName();
        if (StringUtils.isEmpty(displayName)) {
            setQueryName(Bundle.BacklogQueryPanel_new_query_name());
        } else {
            setQueryName(displayName);
        }
    }

    private void setQueryName(String name) {
        queryNameLabel.setText(String.format("<html><b>%s</b>", name)); // NOI18N
    }

    private void clear() {
        setKeyword(""); // NOI18N
        generalPanel.clear();
        datePanel.clear();
    }

    public void setSaveButtonEnabled(boolean isEnabled) {
        saveButton.setEnabled(isEnabled);
    }

    public void scrollToBottom() {
        if (!generalCollapsibleSectionPanel.isExpanded() || !dateCollapsibleSectionPanel.isExpanded()) {
            return;
        }

        Container parent = getParent();
        if (parent == null) {
            return;
        }
        parent = parent.getParent();
        parent = parent.getParent(); // viewport
        parent = parent.getParent(); // scroll pane
        if (parent instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) parent;
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainGeneralPanel = new javax.swing.JPanel();
        mainDatePanel = new javax.swing.JPanel();
        queryNameLabel = new javax.swing.JLabel();
        generalCollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();
        keywordLabel = new javax.swing.JLabel();
        keywordTextField = new javax.swing.JTextField();
        dateCollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();
        searchButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        mainIssueTablePanel = new javax.swing.JPanel();

        mainGeneralPanel.setLayout(new javax.swing.BoxLayout(mainGeneralPanel, javax.swing.BoxLayout.LINE_AXIS));

        mainDatePanel.setLayout(new javax.swing.BoxLayout(mainDatePanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(queryNameLabel, org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.queryNameLabel.text")); // NOI18N

        generalCollapsibleSectionPanel.setContent(mainGeneralPanel);
        generalCollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.generalCollapsibleSectionPanel.label")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(keywordLabel, org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.keywordLabel.text")); // NOI18N

        keywordTextField.setText(org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.keywordTextField.text")); // NOI18N

        dateCollapsibleSectionPanel.setContent(mainDatePanel);
        dateCollapsibleSectionPanel.setExpanded(false);
        dateCollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.dateCollapsibleSectionPanel.label")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(searchButton, org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.searchButton.text")); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(saveButton, org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.saveButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(clearButton, org.openide.util.NbBundle.getMessage(BacklogQueryPanel.class, "BacklogQueryPanel.clearButton.text")); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        mainIssueTablePanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateCollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(keywordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(keywordTextField))
                    .addComponent(generalCollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(searchButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearButton))
                            .addComponent(queryNameLabel))
                        .addGap(0, 154, Short.MAX_VALUE))
                    .addComponent(mainIssueTablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(queryNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(resetButton)
                    .addComponent(clearButton)
                    .addComponent(saveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keywordLabel)
                    .addComponent(keywordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(generalCollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateCollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainIssueTablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
    }//GEN-LAST:event_searchButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        generalPanel.reset(query);
        update();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clear();
    }//GEN-LAST:event_clearButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel dateCollapsibleSectionPanel;
    private org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel generalCollapsibleSectionPanel;
    private javax.swing.JLabel keywordLabel;
    private javax.swing.JTextField keywordTextField;
    private javax.swing.JPanel mainDatePanel;
    private javax.swing.JPanel mainGeneralPanel;
    private javax.swing.JPanel mainIssueTablePanel;
    private javax.swing.JLabel queryNameLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables
}
