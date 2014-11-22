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

import com.nulabinc.backlog4j.Attachment;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeListener;
import com.junichi11.netbeans.modules.backlog.issue.BacklogAttachment;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class AttachmentsPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 3089442505594772323L;
    private final List<AttachmentPanel> attachmentPanels = Collections.synchronizedList(new ArrayList<AttachmentPanel>());
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form UnsubmittedAttachmentsPanel
     */
    public AttachmentsPanel() {
        initComponents();
    }

    public List<Attachment> getAttachments() {
        ArrayList<Attachment> attachments = new ArrayList<>(attachmentPanels.size());
        synchronized (attachmentPanels) {
            for (AttachmentPanel attachmentPanel : attachmentPanels) {
                attachments.add(attachmentPanel.getAttachment());
            }
        }
        return attachments;
    }

    /**
     *
     * @param attachment
     * @param isReady
     */
    @NbBundle.Messages({
        "AttachmentsPanel.message.ready.to.be.attached=Ready to be attached"
    })
    public void addAttachment(Attachment attachment, boolean isReady) {
        if (attachment == null) {
            return;
        }
        AttachmentPanel newPanel = new AttachmentPanel(attachment);
        if (isReady) {
            newPanel.setStatus(Bundle.AttachmentsPanel_message_ready_to_be_attached());
        }
        newPanel.addPropertyChangeListener(this);
        attachmentPanels.add(newPanel);
        add(newPanel);
    }

    public void addAttachment(BacklogAttachment attachment) {
        if (attachment == null) {
            return;
        }
        AttachmentPanel newPanel = new AttachmentPanel(attachment);
        newPanel.addPropertyChangeListener(this);
        attachmentPanels.add(newPanel);
        add(newPanel);
    }

    public void removeAllAttachments() {
        synchronized (attachmentPanels) {
            for (AttachmentPanel attachment : attachmentPanels) {
                removeAttachment(attachment);
            }
            attachmentPanels.clear();
        }
    }

    private void removeAttachment(AttachmentPanel attachment) {
        if (attachment == null) {
            return;
        }
        attachment.removePropertyChangeListener(this);
        remove(attachment);
    }

    void removeAttachment(Attachment attachment) {
        AttachmentPanel removedPanel = null;
        synchronized (attachmentPanels) {
            for (AttachmentPanel attachmentPanel : attachmentPanels) {
                Attachment a = attachmentPanel.getAttachment();
                if (a.getId() == attachment.getId()) {
                    removedPanel = attachmentPanel;
                    break;
                }
            }
            attachmentPanels.remove(removedPanel);
            removeAttachment(removedPanel);
        }
    }

    void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AttachmentPanel.PROP_ATTACHMENT_DELETED)) {
            synchronized (attachmentPanels) {
                for (Iterator<AttachmentPanel> iterator = attachmentPanels.iterator(); iterator.hasNext();) {
                    AttachmentPanel attachment = iterator.next();
                    if (attachment.isDeleted()) {
                        Attachment a = attachment.getAttachment();
                        if (!attachment.isUnsubmitted()) {
                            firePropertyChange(AttachmentPanel.PROP_ATTACHMENT_DELETED, a, null);
                        } else {
                            iterator.remove();
                            removeAttachment(attachment);
                        }
                        break;
                    }
                }
            }
            fireChange();
        }
    }

    private void fireChange() {
        changeSupport.fireChange();
    }
}
