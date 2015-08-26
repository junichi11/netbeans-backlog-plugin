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
package com.junichi11.netbeans.modules.backlog.issue.ui;

import com.junichi11.netbeans.modules.backlog.BacklogData;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import com.junichi11.netbeans.modules.backlog.utils.UiUtils;
import com.nulabinc.backlog4j.IssueComment;
import com.nulabinc.backlog4j.Notification;
import com.nulabinc.backlog4j.User;
import java.util.Date;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CommentPanel extends javax.swing.JPanel {

    public enum Status {
        Quote,
        Edited,
        Deleted,
        Notify,
        None
    }

    private static final long serialVersionUID = 4522741935313865234L;
    private IssueComment comment;
    private Status status = Status.None;

    /**
     * Creates new form CommentPanel
     */
    private CommentPanel() {
        initComponents();
    }

    public CommentPanel(BacklogRepository repository, IssueComment comment) {
        this.comment = comment;
        initComponents();

        setUser(repository, comment.getCreatedUser());
        setCreatedDate(comment.getCreated());
        setUpdatedDate(comment.getUpdated());
        setContent(comment.getContent());

        setNotifications(comment, repository);

        // disable
        BacklogData cache = BacklogData.create(repository);
        User myself = cache.getMyself();
        if (!comment.getCreatedUser().equals(myself)) {
            notifyLinkButton.setEnabled(false);
            editLinkButton.setEnabled(false);
        }

        // TODO delete comment is still not supported by api v2
        deleteLinkButton.setEnabled(false);
    }

    private void setUser(BacklogRepository repository, User user) {
        BacklogData cache = BacklogData.create(repository);
        Icon userIcon = cache.getUserIcon(user);
        if (userIcon != null) {
            userLinkButton.setIcon(userIcon);
        } else {
            userLinkButton.setIcon(null);
        }
        userLinkButton.setText(user.getName());
    }

    private void setCreatedDate(Date date) {
        if (date != null) {
            createdDateLabel.setText(BacklogUtils.DEFAULT_DATE_FORMAT_WITH_TIME.format(date));
        }
    }

    private void setUpdatedDate(Date date) {
        if (date != null) {
            updatedDateLabel.setText(BacklogUtils.DEFAULT_DATE_FORMAT_WITH_TIME.format(date));
        }
    }

    private void setContent(String content) {
        if (content == null) {
            contentTextPane.setText(""); // NOI18N
        } else if (content.isEmpty()) {
            // TODO show change log?
            contentTextPane.setText(""); // NOI18N
        } else {
            contentTextPane.setText(content);
        }
    }

    private void setNotifications(IssueComment comment, BacklogRepository repository) {
        List<Notification> notifications = comment.getNotifications();
        BacklogData data = BacklogData.create(repository);
        for (Notification notification : notifications) {
            User user = notification.getUser();
            Icon userIcon = data.getUserIcon(user);
            if (userIcon != null) {
                JLabel userLabel = new JLabel(userIcon);
                userLabel.setToolTipText(user.getName());
                notificationUsersPanel.add(userLabel);
            }
        }
    }

    public IssueComment getComment() {
        return comment;
    }

    public String getSelectedText() {
        return contentTextPane.getSelectedText();
    }

    public Status getStatus() {
        return status;
    }

    void resetProperties() {
        status = Status.None;
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
        createdDateLabel = new javax.swing.JLabel();
        updatedLabel = new javax.swing.JLabel();
        updatedDateLabel = new javax.swing.JLabel();
        userLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        editLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        jSeparator1 = new javax.swing.JSeparator();
        contentTextPane = new javax.swing.JTextPane();
        quoteLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        deleteLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        notifyLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        notificationSentToLabel = new javax.swing.JLabel();
        notificationUsersPanel = new javax.swing.JPanel();

        org.openide.awt.Mnemonics.setLocalizedText(createdLabel, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.createdLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(createdDateLabel, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.createdDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(updatedLabel, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.updatedLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(updatedDateLabel, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.updatedDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(userLinkButton, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.userLinkButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editLinkButton, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.editLinkButton.text")); // NOI18N
        editLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinkButtonActionPerformed(evt);
            }
        });

        contentTextPane.setEditable(false);
        contentTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 5, 3, 5));

        org.openide.awt.Mnemonics.setLocalizedText(quoteLinkButton, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.quoteLinkButton.text")); // NOI18N
        quoteLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quoteLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteLinkButton, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.deleteLinkButton.text")); // NOI18N
        deleteLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(notifyLinkButton, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.notifyLinkButton.text")); // NOI18N
        notifyLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notifyLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(notificationSentToLabel, org.openide.util.NbBundle.getMessage(CommentPanel.class, "CommentPanel.notificationSentToLabel.text")); // NOI18N

        notificationUsersPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentTextPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addComponent(createdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createdDateLabel)
                        .addGap(18, 18, 18)
                        .addComponent(updatedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updatedDateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(notifyLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(quoteLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1)
                    .addComponent(notificationUsersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(notificationSentToLabel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createdDateLabel)
                    .addComponent(updatedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatedDateLabel)
                    .addComponent(userLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quoteLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(notifyLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationSentToLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notificationUsersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void quoteLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quoteLinkButtonActionPerformed
        status = Status.Quote;
        firePropertyChange(BacklogIssue.PROP_COMMENT_QUOTE, null, null);
    }//GEN-LAST:event_quoteLinkButtonActionPerformed

    @NbBundle.Messages({
        "CommentPanel.message.delete.issue=Do you really want to delete this comment?"
    })
    private void deleteLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLinkButtonActionPerformed
        if (!UiUtils.showQuestionDialog(Bundle.CommentPanel_message_delete_issue())) {
            return;
        }
        status = Status.Deleted;
        firePropertyChange(BacklogIssue.PROP_COMMENT_DELETED, null, null);
    }//GEN-LAST:event_deleteLinkButtonActionPerformed

    private void editLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLinkButtonActionPerformed
        status = Status.Edited;
        firePropertyChange(BacklogIssue.PROP_COMMENT_EDITED, null, null);
    }//GEN-LAST:event_editLinkButtonActionPerformed

    private void notifyLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notifyLinkButtonActionPerformed
        status = Status.Notify;
        firePropertyChange(BacklogIssue.PROP_COMMENT_NOTIFY, null, null);
    }//GEN-LAST:event_notifyLinkButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane contentTextPane;
    private javax.swing.JLabel createdDateLabel;
    private javax.swing.JLabel createdLabel;
    private org.netbeans.modules.bugtracking.commons.LinkButton deleteLinkButton;
    private org.netbeans.modules.bugtracking.commons.LinkButton editLinkButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel notificationSentToLabel;
    private javax.swing.JPanel notificationUsersPanel;
    private org.netbeans.modules.bugtracking.commons.LinkButton notifyLinkButton;
    private org.netbeans.modules.bugtracking.commons.LinkButton quoteLinkButton;
    private javax.swing.JLabel updatedDateLabel;
    private javax.swing.JLabel updatedLabel;
    private org.netbeans.modules.bugtracking.commons.LinkButton userLinkButton;
    // End of variables declaration//GEN-END:variables
}
