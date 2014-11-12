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

import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;

/**
 *
 * @author junichi11
 */
public class DatePanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1070212794909865480L;
    private final boolean isChartEnabled;

    /**
     * Creates new form DatePanel
     */
    public DatePanel(boolean isChartEnabled) {
        this.isChartEnabled = isChartEnabled;
        initComponents();
        init();
    }

    private void init() {
        setChartComponentsEnabled();
        // TODO add document listener
        createdSinceDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        createdUntilDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        updatedSinceDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        updatedUntilDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        startDateSinceDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        startDateUntilDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        dueDateSinceDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        dueDateUntilDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
    }

    private void setChartComponentsEnabled() {
        startDateSinceDatePicker.setEnabled(isChartEnabled);
        startDateUntilDatePicker.setEnabled(isChartEnabled);
    }

    public void clear() {
        createdSinceDatePicker.getEditor().setText(""); // NOI18N
        createdUntilDatePicker.getEditor().setText(""); // NOI18N
        updatedSinceDatePicker.getEditor().setText(""); // NOI18N
        updatedUntilDatePicker.getEditor().setText(""); // NOI18N
        startDateSinceDatePicker.getEditor().setText(""); // NOI18N
        startDateUntilDatePicker.getEditor().setText(""); // NOI18N
        dueDateSinceDatePicker.getEditor().setText(""); // NOI18N
        dueDateUntilDatePicker.getEditor().setText(""); // NOI18N
    }

    public void setCreatedSince(String date) {
        createdSinceDatePicker.getEditor().setText(date);
    }

    public void setCreatedUntil(String date) {
        createdUntilDatePicker.getEditor().setText(date);
    }

    public void setUpdatedSince(String date) {
        updatedSinceDatePicker.getEditor().setText(date);
    }

    public void setUpdatedUntil(String date) {
        updatedUntilDatePicker.getEditor().setText(date);
    }

    public void setStartDateSince(String date) {
        startDateSinceDatePicker.getEditor().setText(date);
    }

    public void setStartDateUntil(String date) {
        startDateUntilDatePicker.getEditor().setText(date);
    }

    public void setDueDateSince(String date) {
        dueDateSinceDatePicker.getEditor().setText(date);
    }

    public void setDueDateUntil(String date) {
        dueDateUntilDatePicker.getEditor().setText(date);
    }

    public String getCreatedSince() {
        return createdSinceDatePicker.getEditor().getText().trim();
    }

    public String getCreatedUntil() {
        return createdUntilDatePicker.getEditor().getText().trim();
    }

    public String getUpdatedSince() {
        return updatedSinceDatePicker.getEditor().getText().trim();
    }

    public String getUpdatedUntil() {
        return updatedUntilDatePicker.getEditor().getText().trim();
    }

    public String getStartDateSince() {
        return startDateSinceDatePicker.getEditor().getText().trim();
    }

    public String getStartDateUntil() {
        return startDateUntilDatePicker.getEditor().getText().trim();
    }

    public String getDueDateSince() {
        return dueDateSinceDatePicker.getEditor().getText().trim();
    }

    public String getDueDateUntil() {
        return dueDateUntilDatePicker.getEditor().getText().trim();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createdLabel = new javax.swing.JLabel();
        createdSinceDatePicker = new org.jdesktop.swingx.JXDatePicker();
        createdHyphenLabel = new javax.swing.JLabel();
        createdUntilDatePicker = new org.jdesktop.swingx.JXDatePicker();
        updaetdLabel = new javax.swing.JLabel();
        updatedSinceDatePicker = new org.jdesktop.swingx.JXDatePicker();
        updatedHyphenLabel = new javax.swing.JLabel();
        updatedUntilDatePicker = new org.jdesktop.swingx.JXDatePicker();
        dueDateLabel = new javax.swing.JLabel();
        dueDateSinceDatePicker = new org.jdesktop.swingx.JXDatePicker();
        dueDateHyphenLabel = new javax.swing.JLabel();
        dueDateUntilDatePicker = new org.jdesktop.swingx.JXDatePicker();
        startDateLabel = new javax.swing.JLabel();
        startDateSinceDatePicker = new org.jdesktop.swingx.JXDatePicker();
        startDateHyphenLabel = new javax.swing.JLabel();
        startDateUntilDatePicker = new org.jdesktop.swingx.JXDatePicker();

        org.openide.awt.Mnemonics.setLocalizedText(createdLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.createdLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(createdHyphenLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.createdHyphenLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(updaetdLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.updaetdLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(updatedHyphenLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.updatedHyphenLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dueDateLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.dueDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dueDateHyphenLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.dueDateHyphenLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(startDateLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.startDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(startDateHyphenLabel, org.openide.util.NbBundle.getMessage(DatePanel.class, "DatePanel.startDateHyphenLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updaetdLabel)
                    .addComponent(createdLabel)
                    .addComponent(startDateLabel)
                    .addComponent(dueDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dueDateSinceDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(createdSinceDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updatedSinceDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(startDateSinceDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(updatedHyphenLabel)
                            .addComponent(createdHyphenLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(createdUntilDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(updatedUntilDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startDateHyphenLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startDateUntilDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(dueDateHyphenLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dueDateUntilDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createdLabel)
                    .addComponent(createdSinceDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createdHyphenLabel)
                    .addComponent(createdUntilDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updaetdLabel)
                    .addComponent(updatedSinceDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatedUntilDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatedHyphenLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDateLabel)
                    .addComponent(startDateSinceDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startDateHyphenLabel)
                    .addComponent(startDateUntilDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dueDateSinceDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dueDateHyphenLabel)
                    .addComponent(dueDateUntilDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dueDateLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel createdHyphenLabel;
    private javax.swing.JLabel createdLabel;
    private org.jdesktop.swingx.JXDatePicker createdSinceDatePicker;
    private org.jdesktop.swingx.JXDatePicker createdUntilDatePicker;
    private javax.swing.JLabel dueDateHyphenLabel;
    private javax.swing.JLabel dueDateLabel;
    private org.jdesktop.swingx.JXDatePicker dueDateSinceDatePicker;
    private org.jdesktop.swingx.JXDatePicker dueDateUntilDatePicker;
    private javax.swing.JLabel startDateHyphenLabel;
    private javax.swing.JLabel startDateLabel;
    private org.jdesktop.swingx.JXDatePicker startDateSinceDatePicker;
    private org.jdesktop.swingx.JXDatePicker startDateUntilDatePicker;
    private javax.swing.JLabel updaetdLabel;
    private javax.swing.JLabel updatedHyphenLabel;
    private org.jdesktop.swingx.JXDatePicker updatedSinceDatePicker;
    private org.jdesktop.swingx.JXDatePicker updatedUntilDatePicker;
    // End of variables declaration//GEN-END:variables
}
