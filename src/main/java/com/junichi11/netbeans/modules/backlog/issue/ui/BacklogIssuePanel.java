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
import com.junichi11.netbeans.modules.backlog.issue.BacklogAttachment;
import com.junichi11.netbeans.modules.backlog.issue.BacklogIssue;
import com.junichi11.netbeans.modules.backlog.query.BacklogSubtaskingQueryController;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.ui.AttributesListCellRenderer;
import com.junichi11.netbeans.modules.backlog.ui.IssueTableCellRenderer;
import com.junichi11.netbeans.modules.backlog.utils.BacklogImage;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import static com.junichi11.netbeans.modules.backlog.utils.BacklogUtils.DEFAULT_DATE_FORMAT;
import static com.junichi11.netbeans.modules.backlog.utils.BacklogUtils.DEFAULT_DATE_FORMAT_WITH_TIME;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import com.nulabinc.backlog4j.Attachment;
import com.nulabinc.backlog4j.BacklogAPIException;
import com.nulabinc.backlog4j.BacklogClient;
import com.nulabinc.backlog4j.Category;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.Issue.PriorityType;
import com.nulabinc.backlog4j.IssueComment;
import com.nulabinc.backlog4j.IssueType;
import com.nulabinc.backlog4j.Milestone;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Project;
import com.nulabinc.backlog4j.Resolution;
import com.nulabinc.backlog4j.ResponseList;
import com.nulabinc.backlog4j.Status;
import com.nulabinc.backlog4j.User;
import com.nulabinc.backlog4j.Version;
import com.nulabinc.backlog4j.api.option.AddCategoryParams;
import com.nulabinc.backlog4j.api.option.AddVersionParams;
import com.nulabinc.backlog4j.api.option.CreateIssueParams;
import com.nulabinc.backlog4j.api.option.UpdateIssueParams;
import com.nulabinc.backlog4j.internal.json.CategoryJSONImpl;
import com.nulabinc.backlog4j.internal.json.ResolutionJSONImpl;
import com.nulabinc.backlog4j.internal.json.UserJSONImpl;
import com.nulabinc.backlog4j.internal.json.VersionJSONImpl;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.bugtracking.issuetable.IssueTable;
import org.netbeans.modules.bugtracking.issuetable.QueryTableCellRenderer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.Cancellable;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class BacklogIssuePanel extends javax.swing.JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 4070385780716024165L;
    private static final String BACKLOG_JP_ISSUE_URL_FORMAT = "https://%s.backlog.jp/view/%s-%s"; // NOI18N
    private static final String BACKLOGTOOL_COM_ISSUE_URL_FORMAT = "https://%s.backlogtool.com/view/%s-%s"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(BacklogIssuePanel.class.getName());
    private static final RequestProcessor RP = new RequestProcessor(BacklogIssuePanel.class);
    private static final String BACKLOG_ATTACHMENT_SUFFIX = ".backlog-attachment"; // NOI18N

    private BacklogIssue issue;
    private final List<Long> attachmentIds = new ArrayList<>();
    private AttachmentsPanel attachmentsPanel;
    private AttachmentsPanel unsubmittedAttachmentsPanel;
    private CommentsPanel commentsPanel;
    private IssueTable subtaskingTable;
    private boolean isSubtaskingEnabled;
    private final ChangeListener attachmentDeletedListener;
    private final String repositoryId;

    // models
    private final DefaultComboBoxModel<Priority> priorityComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<IssueType> issueTypeComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<Status> statusComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<User> assigneeComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<Resolution> resolutionComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<Category> categoryListModel = new DefaultListModel<>();
    private final DefaultListModel<Version> versionListModel = new DefaultListModel<>();
    private final DefaultListModel<Version> milestoneListModel = new DefaultListModel<>();
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    // icon
    private static final Icon ERROR_ICON = BacklogImage.ERROR_16.getIcon();
    private static final Icon ICON = BacklogImage.ICON_32.getIcon();

    /**
     * Creates new form IssuePanel
     */
    public BacklogIssuePanel(String repositoryId) {
        this.repositoryId = repositoryId;
        this.attachmentDeletedListener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                List<Attachment> attachments = unsubmittedAttachmentsPanel.getAttachments();
                attachmentIds.clear();
                for (Attachment attachment : attachments) {
                    attachmentIds.add(attachment.getId());
                }
            }
        };
        initComponents();
        init();
    }

    public void setIssue(BacklogIssue issue) {
        this.issue = issue;
        if (issue != null) {
            isSubtaskingEnabled = BacklogUtils.isSubtaskingEnabled(issue.getRepository());
        }
    }

    public BacklogIssue getIssue() {
        return issue;
    }

    private void init() {
        DefaultDocumentListner documentListner = new DefaultDocumentListner();
        summaryTextField.getDocument().addDocumentListener(documentListner);
        startDatePicker.getEditor().getDocument().addDocumentListener(documentListner);
        startDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        dueDatePicker.getEditor().getDocument().addDocumentListener(documentListner);
        dueDatePicker.setFormats(BacklogUtils.DATE_FORMAT_YYYY_MM_DD);
        actualHoursTextField.getDocument().addDocumentListener(documentListner);
        estimatedHoursTextField.getDocument().addDocumentListener(documentListner);
        errorHeaderLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        setError(" "); // NOI18N

        // set renderer
        issueTypeComboBox.setRenderer(new AttributesListCellRenderer(issueTypeComboBox.getRenderer()));
        statusComboBox.setRenderer(new AttributesListCellRenderer(statusComboBox.getRenderer()));
        priorityComboBox.setRenderer(new AttributesListCellRenderer(priorityComboBox.getRenderer()));
        assigneeComboBox.setRenderer(new AttributesListCellRenderer(assigneeComboBox.getRenderer(), repositoryId));
        versionList.setCellRenderer(new AttributesListCellRenderer(versionList.getCellRenderer()));
        categoryList.setCellRenderer(new AttributesListCellRenderer(categoryList.getCellRenderer()));
        milestoneList.setCellRenderer(new AttributesListCellRenderer(milestoneList.getCellRenderer()));
        resolutionComboBox.setRenderer(new AttributesListCellRenderer(resolutionComboBox.getRenderer()));

        // attachments
        float alignmentX = Component.LEFT_ALIGNMENT;
        selectFilesButton.setAlignmentX(alignmentX);

        unsubmittedAttachmentsPanel = new AttachmentsPanel();
        unsubmittedAttachmentsPanel.setAlignmentX(alignmentX);
        unsubmittedAttachmentsPanel.addChangeListener(attachmentDeletedListener);
        mainAttachmentsPanel.add(unsubmittedAttachmentsPanel);

        // add existing attachments
        attachmentsPanel = new AttachmentsPanel();
        attachmentsPanel.addPropertyChangeListener(this);
        attachmentsPanel.setAlignmentX(alignmentX);
        mainAttachmentsPanel.add(attachmentsPanel);

        // comments
        commentsPanel = new CommentsPanel();
        commentsPanel.addPropertyChangeListener(this);
        mainCommentsPanel.add(commentsPanel);

        // header
        Font font = errorHeaderLabel.getFont();
        headerIssueKeyLabel.setFont(font.deriveFont((float) (font.getSize() * 1.5)));
        headerIssueKeyLabel.setIcon(ICON);
    }

    @NbBundle.Messages({
        "# {0} - count",
        "BacklogIssuePanel.label.subtasking=Subtasking({0})"
    })
    public void update(boolean updateComment) {
        assert issue != null;
        JScrollBar verticalScrollBar = mainScrollPane.getVerticalScrollBar();
        int value = verticalScrollBar.getValue();
        setSubmitButton();
        if (issue == null) {
            return;
        }
        // init comment
        commentTextArea.setText(""); // NOI18N

        attachmentIds.clear();

        // header
        setHeader();

        // get information from backlog api
        setAttributesInfo();

        // existing issue
        setExistingIssueInfo(updateComment);

        // can use chart?
        setChartComponentsEnabled();

        // comments
        setCommentComponentsEnabled(!issue.isNew());

        // subtasking
        if (isSubtaskingEnabled()) {
            setSubtaskTable(issue.getRepository());
            BacklogIssue parentIssue = issue.getParentIssue();
            if (parentIssue != null) {
                subtaskingTable.started();
                List<BacklogIssue> subissues = issue.getRepository().getBacklogSubissues(parentIssue);
                subtaskingCollapsibleSectionPanel.setLabel(Bundle.BacklogIssuePanel_label_subtasking(subissues.size()));
                if (issue != parentIssue) {
                    subtaskingTable.addNode(parentIssue.getIssueNode());
                }
                for (BacklogIssue subissue : subissues) {
                    subtaskingTable.addNode(subissue.getIssueNode());
                }
            }
        }
        if (issue.isNew()) {
            subtaskingCollapsibleSectionPanel.setVisible(false);
        } else {
            subtaskingCollapsibleSectionPanel.setVisible(isSubtaskingEnabled());
        }
        addSubtaskLinkButton.setEnabled(!issue.isNew());

        // wait for updating comments
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        verticalScrollBar.setValue(value);
    }

    public void scrollToTop() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JScrollBar verticalScrollBar = mainScrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMinimum());
            }
        });
    }

    @NbBundle.Messages({
        "BacklogIssuePanel.header.new.issue=New Issue",
        "# {0} - parant issue key",
        "BacklogIssuePanel.header.new.subtask=New Subtask of {0}"
    })
    private void setHeader() {
        if (issue.isNew()) {
            String parentIssueKey = issue.getSubtaskParentIssueKey();
            if (!StringUtils.isEmpty(parentIssueKey)) {
                // subtasking
                setHeaderIssueKey(Bundle.BacklogIssuePanel_header_new_subtask(parentIssueKey));
            } else {
                setHeaderIssueKey(Bundle.BacklogIssuePanel_header_new_issue());
            }
            return;
        }

        // existing issue
        Issue existingIssue = issue.getIssue();
        setHeaderIssueKey(existingIssue.getIssueKey() + " " + existingIssue.getSummary());
        setDateLabel(headerCreatedDateLabel, existingIssue.getCreated(), true);
        setDateLabel(headerStartDateViewLabel, existingIssue.getStartDate(), false);
        setDateLabel(headerDueDateViewLabel, existingIssue.getDueDate(), false);
        User createdUser = existingIssue.getCreatedUser();
        if (createdUser != null) {
            headerCreatedUserLinkButton.setText(createdUser.getName());
            BacklogData cache = BacklogData.create(issue.getRepository());
            Icon userIcon = cache.getUserIcon(createdUser);
            if (userIcon != null) {
                headerCreatedUserLinkButton.setIcon(userIcon);
            }
            // TODO add an action to send email
//            String mailAddress = createdUser.getMailAddress();
//            if (!StringUtils.isEmpty(mailAddress)) {
//            }
        }
        addSubtaskLinkButton.setVisible(isSubtaskingEnabled());
    }

    private void setDateLabel(JLabel label, Date date, boolean isCreated) {
        if (date == null) {
            label.setText("-"); // NOI18N
        } else {
            String dateString;
            if (isCreated) {
                dateString = DEFAULT_DATE_FORMAT_WITH_TIME.format(date);
            } else {
                dateString = DEFAULT_DATE_FORMAT.format(date);
            }
            label.setText(dateString);
        }
    }

    private void setAttributesInfo() {
        BacklogData data = BacklogData.create(issue.getRepository());
        setIssueTypes(data);
        setPriorities(data);
        setCategories(data, false);
        setVersions(data, false);
        setMilestones(data, false);
        setUsers(data);
        setStatus(data);
        setResolutions(data);
    }

    @NbBundle.Messages({
        "# {0} - count",
        "BacklogIssuePanel.label.comments=Comments({0})"
    })
    private void setExistingIssueInfo(boolean updateComments) {
        Issue existingIssue = issue.getIssue();
        if (existingIssue != null) {
            summaryTextField.setText(existingIssue.getSummary());
            descriptionEditorPane.setText(existingIssue.getDescription());
            setSelectedIssueType(existingIssue.getIssueType());
            setSelectedPriority(existingIssue.getPriority());
            setSelectedStatus(existingIssue.getStatus());
            setSelectedResolution(existingIssue.getResolution());
            setSelectedAssignee(existingIssue.getAssignee());
            setSelectedCategories(existingIssue.getCategory());
            setSelectedVersions(existingIssue.getVersions());
            setSelectedMilestones(existingIssue.getMilestone());
            startDatePicker.setDate(existingIssue.getStartDate());
            dueDatePicker.setDate(existingIssue.getDueDate());
            BigDecimal estimatedHours = existingIssue.getEstimatedHours();
            if (estimatedHours != null) {
                estimatedHoursTextField.setText(estimatedHours.toString());
            }
            BigDecimal actualHours = existingIssue.getActualHours();
            if (actualHours != null) {
                actualHoursTextField.setText(actualHours.toString());
            }

            // comments
            if (updateComments) {
                removeAllComments();
                setComments(existingIssue.getId());
            }

            // attachments
            List<BacklogAttachment> attachments = issue.getAttachments();
            attachmentsPanel.removeAllAttachments();
            for (BacklogAttachment attachment : attachments) {
                attachmentsPanel.addAttachment(attachment);
            }
        }
    }

    private void setComments(final long id) {
        RP.post(new Runnable() {
            @Override
            public void run() {
                BacklogClient backlogClient = issue.getRepository().createBacklogClient();
                if (backlogClient == null) {
                    return;
                }
                int commentCount = 0;
                try {
                    ResponseList<IssueComment> issueComments = backlogClient.getIssueComments(id);
                    for (IssueComment comment : issueComments) {
                        if (!StringUtils.isEmpty(comment.getContent())) {
                            addComment(comment);
                            commentCount++;
                        }
                    }
                } catch (BacklogAPIException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
                commentsCollapsibleSectionPanel.setLabel(Bundle.BacklogIssuePanel_label_comments(commentCount));
            }
        });
    }

    private void setCommentComponentsEnabled(boolean isEnabled) {
        commentTextArea.setEnabled(isEnabled);
        commentsCollapsibleSectionPanel.setVisible(isEnabled);
    }

    private void addComment(IssueComment comment) {
        commentsPanel.addComment(issue.getRepository(), comment);
    }

    private void removeAllComments() {
        commentsPanel.removeAllComments();
    }

    private void setChartComponentsEnabled() {
        boolean isChartEnabled = BacklogUtils.isChartEnabled(issue.getRepository());
        startDatePicker.setEnabled(isChartEnabled);
        estimatedHoursTextField.setEnabled(isChartEnabled);
        actualHoursTextField.setEnabled(isChartEnabled);
    }

    private void setHeaderIssueKey(String text) {
        headerIssueKeyLabel.setText(String.format("<html><b>%s</b>", text)); // NOI18N
    }

    private void setSubtaskTable(BacklogRepository repository) {
        if (subtaskingTable == null) {
            BacklogSubtaskingQueryController queryController = new BacklogSubtaskingQueryController();
            subtaskingTable = new IssueTable(repository.getID(), "Subtasking", queryController, BacklogIssue.DEFAULT_SUBTASKING_COLUMN_DESCRIPTORS, false);
            subtaskingTable.initColumns();
            IssueTableCellRenderer renderer = new IssueTableCellRenderer((QueryTableCellRenderer) subtaskingTable.getRenderer());
            subtaskingTable.setRenderer(renderer);
            mainSubtaskTablePanel.add(subtaskingTable.getComponent());
        }
    }

    private boolean isSubtaskingEnabled() {
        return isSubtaskingEnabled;
    }

    private void setSelectedPriority(Priority priority) {
        if (priority == null) {
            return;
        }
        priorityComboBoxModel.setSelectedItem(priority);
    }

    private void setSelectedIssueType(IssueType issueType) {
        if (issueType == null) {
            return;
        }
        issueTypeComboBoxModel.setSelectedItem(issueType);
    }

    private void setSelectedStatus(Status status) {
        if (status == null) {
            return;
        }
        statusComboBoxModel.setSelectedItem(status);
    }

    private void setSelectedResolution(Resolution resolution) {
        long resolutionId = -1;
        if (resolution != null) {
            resolutionId = resolution.getId();
        }
        int size = resolutionComboBoxModel.getSize();
        for (int i = 0; i < size; i++) {
            Resolution r = resolutionComboBoxModel.getElementAt(i);
            if (StringUtils.isEmpty(r.getName())) {
                if (resolutionId == -1) {
                    resolutionComboBoxModel.setSelectedItem(r);
                    break;
                }
                continue;
            }

            if (resolutionId == r.getId()) {
                resolutionComboBoxModel.setSelectedItem(r);
                break;
            }
        }
    }

    private void setSelectedAssignee(User user) {
        int size = assigneeComboBoxModel.getSize();
        if (user == null) {
            if (size > 0) {
                assigneeComboBox.setSelectedIndex(0);
            }
            return;
        }
        assigneeComboBoxModel.setSelectedItem(user);
    }

    private void setSelectedCategories(List<Category> categories) {
        int size = categoryListModel.getSize();
        if (categories.isEmpty()) {
            if (size > 0) {
                categoryList.setSelectedIndex(0);
            }
            return;
        }
        int categorySize = categories.size();
        int[] indices = new int[categorySize];
        for (int i = 0; i < categorySize; i++) {
            Category category = categories.get(i);
            long id = category.getId();
            for (int j = 0; j < size; j++) {
                Category c = categoryListModel.get(j);
                if (id == c.getId()) {
                    indices[i] = j;
                    break;
                }
            }
        }
        categoryList.setSelectedIndices(indices);
    }

    private void setSelectedVersions(List<Version> versions) {
        int size = versionListModel.getSize();
        if (versions.isEmpty()) {
            if (size > 0) {
                versionList.setSelectedIndex(0);
            }
            return;
        }
        int versionSize = versions.size();
        int[] indices = new int[versionSize];
        for (int i = 0; i < versionSize; i++) {
            Version version = versions.get(i);
            long id = version.getId();
            for (int j = 0; j < size; j++) {
                Version v = versionListModel.get(j);
                if (id == v.getId()) {
                    indices[i] = j;
                    break;
                }
            }
        }
        versionList.setSelectedIndices(indices);
    }

    private void setSelectedMilestones(List<Milestone> milestones) {
        int size = milestoneListModel.getSize();
        if (milestones.isEmpty()) {
            if (size > 0) {
                milestoneList.setSelectedIndex(0);
            }
            return;
        }
        int milestoneSize = milestones.size();
        int[] indices = new int[milestoneSize];
        for (int i = 0; i < milestoneSize; i++) {
            Milestone milestone = milestones.get(i);
            long id = milestone.getId();
            for (int j = 0; j < size; j++) {
                Version version = milestoneListModel.get(j);
                if (id == version.getId()) {
                    indices[i] = j;
                    break;
                }
            }
        }
        milestoneList.setSelectedIndices(indices);
    }

    @NbBundle.Messages({
        "BacklogIssuePanel.submitButton.add=Add",
        "BacklogIssuePanel.submitButton.submit=Submit"
    })
    private void setSubmitButton() {
        if (issue == null) {
            submitHeaderButton.setVisible(false);
            return;
        }
        submitHeaderButton.setVisible(true);
        if (issue.isNew()) {
            submitHeaderButton.setText(Bundle.BacklogIssuePanel_submitButton_add());
        } else {
            submitHeaderButton.setText(Bundle.BacklogIssuePanel_submitButton_submit());
        }
    }

    private void setIssueTypes(BacklogData data) {
        List<IssueType> issueTypes = data.getIssueTypes();
        issueTypeComboBoxModel.removeAllElements();
        for (IssueType issueType : issueTypes) {
            issueTypeComboBoxModel.addElement(issueType);
        }
        issueTypeComboBox.setModel(issueTypeComboBoxModel);
    }

    private void setCategories(BacklogData data, boolean force) {
        List<Category> categories = data.getCategories(force);
        categoryListModel.removeAllElements();
        categoryListModel.addElement(new CategoryJSONImpl());
        for (Category category : categories) {
            categoryListModel.addElement(category);
        }
        categoryList.setModel(categoryListModel);
    }

    private void setUsers(BacklogData data) {
        try{
            List<User> users = data.getUsers();
            assigneeComboBoxModel.removeAllElements();
            assigneeComboBoxModel.addElement(new UserJSONImpl());
            for (User user : users) {
                assigneeComboBoxModel.addElement(user);
            }
        }catch(Exception e){
            System.out.print(e);
            assigneeComboBox.setEnabled(false);
        }
        assigneeComboBox.setModel(assigneeComboBoxModel);
    }

    private void setVersions(BacklogData data, boolean force) {
        List<Version> versions = data.getVersions(force);
        versionListModel.removeAllElements();
        versionListModel.addElement(new VersionJSONImpl());
        for (Version version : versions) {
            versionListModel.addElement(version);
        }
        versionList.setModel(versionListModel);
    }

    private void setMilestones(BacklogData data, boolean force) {
        List<Version> versions = data.getVersions(force);
        milestoneListModel.removeAllElements();
        milestoneListModel.addElement(new VersionJSONImpl());
        for (Version version : versions) {
            milestoneListModel.addElement(version);
        }
        milestoneList.setModel(milestoneListModel);
    }

    private void setPriorities(BacklogData data) {
        List<Priority> priorities = data.getPriorities();
        priorityComboBoxModel.removeAllElements();
        for (Priority priority : priorities) {
            priorityComboBoxModel.addElement(priority);
            Issue.PriorityType priorityType = priority.getPriority();
            if (priorityType == PriorityType.Normal) {
                priorityComboBoxModel.setSelectedItem(priority);
            }
        }
        priorityComboBox.setModel(priorityComboBoxModel);
    }

    private void setResolutions(BacklogData data) {
        resolutionComboBox.setVisible(!issue.isNew());
        List<Resolution> resolutions = data.getResolutions();
        resolutionComboBoxModel.removeAllElements();
        resolutionComboBoxModel.addElement(new ResolutionJSONImpl());
        for (Resolution resolution : resolutions) {
            resolutionComboBoxModel.addElement(resolution);
        }
        resolutionComboBox.setModel(resolutionComboBoxModel);
    }

    private void setStatus(BacklogData data) {
        statusComboBox.setVisible(!issue.isNew());
        List<Status> status = data.getStatus();
        statusComboBoxModel.removeAllElements();
        for (Status s : status) {
            statusComboBoxModel.addElement(s);
        }
        statusComboBox.setModel(statusComboBoxModel);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public String getSummary() {
        return summaryTextField.getText().trim();
    }

    public Priority getPriority() {
        return (Priority) priorityComboBox.getSelectedItem();
    }

    public IssueType getIssueType() {
        return (IssueType) issueTypeComboBox.getSelectedItem();
    }

    public String getDescription() {
        return descriptionEditorPane.getText();
    }

    public Status getStatus() {
        return (Status) statusComboBox.getSelectedItem();
    }

    public Resolution getResolution() {
        return (Resolution) resolutionComboBox.getSelectedItem();
    }

    public List<Category> getCategories() {
        return categoryList.getSelectedValuesList();
    }

    public List<Long> getCategoryIds() {
        ArrayList<Long> categoryIds = new ArrayList<>();
        for (Category category : getCategories()) {
            categoryIds.add(category.getId());
        }
        return categoryIds;
    }

    public List<Version> getVersions() {
        return versionList.getSelectedValuesList();
    }

    public List<Long> getVersionIds() {
        ArrayList<Long> versionIds = new ArrayList<>();
        for (Version version : getVersions()) {
            versionIds.add(version.getId());
        }
        return versionIds;
    }

    public List<Version> getMilestones() {
        return milestoneList.getSelectedValuesList();
    }

    public List<Long> getMilestonIds() {
        ArrayList<Long> milestoneIds = new ArrayList<>();
        for (Version milestone : getMilestones()) {
            milestoneIds.add(milestone.getId());
        }
        return milestoneIds;
    }

    public User getAssignee() {
        return (User) assigneeComboBox.getSelectedItem();
    }

    public String getStartDate() {
        JFormattedTextField editor = startDatePicker.getEditor();
        if (editor == null) {
            return ""; // NOI18N
        }
        return editor.getText().trim();
    }

    public String getDueDate() {
        JFormattedTextField editor = dueDatePicker.getEditor();
        if (editor == null) {
            return ""; // NOI18N
        }
        return editor.getText().trim();
    }

    public String getActualHours() {
        return actualHoursTextField.getText().trim();
    }

    public String getEstimatedHours() {
        return estimatedHoursTextField.getText().trim();
    }

    public String getComment() {
        return commentTextArea.getText();
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    public void setError(String errorMessasge) {
        if (StringUtils.isEmpty(errorMessasge) || errorMessasge.trim().isEmpty()) {
            errorHeaderLabel.setIcon(null);
        } else {
            errorHeaderLabel.setIcon(ERROR_ICON);
        }
        errorHeaderLabel.setText(errorMessasge);
    }

    public void setSubmitButtonEnabled(boolean isEnabled) {
        submitHeaderButton.setEnabled(isEnabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainCommentsPanel = new javax.swing.JPanel();
        mainAttachmentsPanel = new javax.swing.JPanel();
        selectFilesButton = new javax.swing.JButton();
        mainSubtaskTablePanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        addSubtaskLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        refreshLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        showOnBrowserLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        headerIssueKeyLabel = new javax.swing.JLabel();
        headerCreatedLabel = new javax.swing.JLabel();
        headerCreatedDateLabel = new javax.swing.JLabel();
        headerStartDateLabel = new javax.swing.JLabel();
        headerStartDateViewLabel = new javax.swing.JLabel();
        headerDueDateLabel = new javax.swing.JLabel();
        headerDueDateViewLabel = new javax.swing.JLabel();
        headerCreatedByLabel = new javax.swing.JLabel();
        headerCreatedUserLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        jSeparator5 = new javax.swing.JSeparator();
        errorHeaderLabel = new javax.swing.JLabel();
        submitHeaderButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        mainScrollPane = new javax.swing.JScrollPane();
        mainPanel = new javax.swing.JPanel();
        priorityComboBox = new javax.swing.JComboBox<Priority>();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionEditorPane = new javax.swing.JEditorPane();
        estimatedHoursLabel = new javax.swing.JLabel();
        priorityLabel = new javax.swing.JLabel();
        resolutionComboBox = new javax.swing.JComboBox<Resolution>();
        jSeparator2 = new javax.swing.JSeparator();
        versionLabel = new javax.swing.JLabel();
        milestoneLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        categoryScrollPane = new javax.swing.JScrollPane();
        categoryList = new javax.swing.JList<Category>();
        resolutionLabel = new javax.swing.JLabel();
        assigneeLabel = new javax.swing.JLabel();
        assigneeComboBox = new javax.swing.JComboBox<User>();
        acturalHoursLabel = new javax.swing.JLabel();
        estimatedHoursTextField = new javax.swing.JTextField();
        actualHoursTextField = new javax.swing.JTextField();
        statrDateLabel = new javax.swing.JLabel();
        summaryTextField = new javax.swing.JTextField();
        summaryLabel = new javax.swing.JLabel();
        dueDateLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        attributeLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        versionScrollPane = new javax.swing.JScrollPane();
        versionList = new javax.swing.JList<Version>();
        categoryLabel = new javax.swing.JLabel();
        milestoneScrollPane = new javax.swing.JScrollPane();
        milestoneList = new javax.swing.JList<Version>();
        issueTypeComboBox = new javax.swing.JComboBox<IssueType>();
        commentLabel = new javax.swing.JLabel();
        commentScrollPane = new javax.swing.JScrollPane();
        commentTextArea = new javax.swing.JTextArea();
        commentsCollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();
        startDatePicker = new org.jdesktop.swingx.JXDatePicker();
        dueDatePicker = new org.jdesktop.swingx.JXDatePicker();
        addCategoryButton = new javax.swing.JButton();
        addVersionButton = new javax.swing.JButton();
        addMilestoneButton = new javax.swing.JButton();
        assignToMyselfLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        statusComboBox = new javax.swing.JComboBox<Status>();
        hoursEstimatedLabel = new javax.swing.JLabel();
        hoursActualLabel = new javax.swing.JLabel();
        attachmentsCollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();
        subtaskingCollapsibleSectionPanel = new org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel();

        mainCommentsPanel.setLayout(new javax.swing.BoxLayout(mainCommentsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        mainAttachmentsPanel.setLayout(new javax.swing.BoxLayout(mainAttachmentsPanel, javax.swing.BoxLayout.Y_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(selectFilesButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.selectFilesButton.text")); // NOI18N
        selectFilesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFilesButtonActionPerformed(evt);
            }
        });
        mainAttachmentsPanel.add(selectFilesButton);

        mainSubtaskTablePanel.setLayout(new javax.swing.BoxLayout(mainSubtaskTablePanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(addSubtaskLinkButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.addSubtaskLinkButton.text")); // NOI18N
        addSubtaskLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubtaskLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(refreshLinkButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.refreshLinkButton.text")); // NOI18N
        refreshLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(showOnBrowserLinkButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.showOnBrowserLinkButton.text")); // NOI18N
        showOnBrowserLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showOnBrowserLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(headerIssueKeyLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerIssueKeyLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerCreatedLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedDateLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerCreatedDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerStartDateLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerStartDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerStartDateViewLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerStartDateViewLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerDueDateLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerDueDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerDueDateViewLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerDueDateViewLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedByLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerCreatedByLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerCreatedUserLinkButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.headerCreatedUserLinkButton.text")); // NOI18N

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(errorHeaderLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.errorHeaderLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(submitHeaderButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.submitHeaderButton.text")); // NOI18N
        submitHeaderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitHeaderButtonActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(errorHeaderLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(submitHeaderButton))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(headerCreatedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerCreatedDateLabel)
                        .addGap(18, 18, 18)
                        .addComponent(headerStartDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerStartDateViewLabel)
                        .addGap(18, 18, 18)
                        .addComponent(headerDueDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerDueDateViewLabel)
                        .addGap(18, 18, 18)
                        .addComponent(headerCreatedByLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerCreatedUserLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(headerIssueKeyLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addSubtaskLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(showOnBrowserLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(showOnBrowserLinkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshLinkButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator5)
                        .addComponent(jSeparator3)
                        .addComponent(addSubtaskLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(headerIssueKeyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerCreatedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerCreatedDateLabel)
                    .addComponent(headerDueDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerDueDateViewLabel)
                    .addComponent(headerCreatedByLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerCreatedUserLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerStartDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerStartDateViewLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(errorHeaderLabel)
                    .addComponent(submitHeaderButton))
                .addContainerGap())
        );

        mainPanel.setAutoscrolls(true);

        descriptionScrollPane.setPreferredSize(new java.awt.Dimension(50, 23));

        descriptionEditorPane.setMinimumSize(new java.awt.Dimension(50, 23));
        descriptionEditorPane.setPreferredSize(new java.awt.Dimension(50, 23));
        descriptionScrollPane.setViewportView(descriptionEditorPane);

        org.openide.awt.Mnemonics.setLocalizedText(estimatedHoursLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.estimatedHoursLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(priorityLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.priorityLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(versionLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.versionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(milestoneLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.milestoneLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(statusLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.statusLabel.text")); // NOI18N

        categoryScrollPane.setViewportView(categoryList);

        org.openide.awt.Mnemonics.setLocalizedText(resolutionLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.resolutionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(assigneeLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.assigneeLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(acturalHoursLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.acturalHoursLabel.text")); // NOI18N

        estimatedHoursTextField.setText(org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.estimatedHoursTextField.text")); // NOI18N

        actualHoursTextField.setText(org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.actualHoursTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(statrDateLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.statrDateLabel.text")); // NOI18N

        summaryTextField.setText(org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.summaryTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(summaryLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.summaryLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dueDateLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.dueDateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typeLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.typeLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(attributeLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.attributeLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.descriptionLabel.text")); // NOI18N

        versionScrollPane.setViewportView(versionList);

        org.openide.awt.Mnemonics.setLocalizedText(categoryLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.categoryLabel.text")); // NOI18N

        milestoneScrollPane.setViewportView(milestoneList);

        org.openide.awt.Mnemonics.setLocalizedText(commentLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.commentLabel.text")); // NOI18N

        commentTextArea.setColumns(20);
        commentTextArea.setRows(5);
        commentScrollPane.setViewportView(commentTextArea);

        commentsCollapsibleSectionPanel.setContent(mainCommentsPanel);
        commentsCollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.commentsCollapsibleSectionPanel.label")); // NOI18N

        addCategoryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/junichi11/netbeans/modules/backlog/resources/add_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addCategoryButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.addCategoryButton.text")); // NOI18N
        addCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCategoryButtonActionPerformed(evt);
            }
        });

        addVersionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/junichi11/netbeans/modules/backlog/resources/add_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addVersionButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.addVersionButton.text")); // NOI18N
        addVersionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addVersionButtonActionPerformed(evt);
            }
        });

        addMilestoneButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/junichi11/netbeans/modules/backlog/resources/add_icon_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addMilestoneButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.addMilestoneButton.text")); // NOI18N
        addMilestoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMilestoneButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(assignToMyselfLinkButton, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.assignToMyselfLinkButton.text")); // NOI18N
        assignToMyselfLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assignToMyselfLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(hoursEstimatedLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.hoursEstimatedLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(hoursActualLabel, org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.hoursActualLabel.text")); // NOI18N

        attachmentsCollapsibleSectionPanel.setContent(mainAttachmentsPanel);
        attachmentsCollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.attachmentsCollapsibleSectionPanel.label")); // NOI18N

        subtaskingCollapsibleSectionPanel.setContent(mainSubtaskTablePanel);
        subtaskingCollapsibleSectionPanel.setExpanded(false);
        subtaskingCollapsibleSectionPanel.setLabel(org.openide.util.NbBundle.getMessage(BacklogIssuePanel.class, "BacklogIssuePanel.subtaskingCollapsibleSectionPanel.label")); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(subtaskingCollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(attachmentsCollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(summaryLabel)
                                    .addComponent(descriptionLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(descriptionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(summaryTextField)))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(attributeLabel)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(milestoneLabel)
                            .addComponent(versionLabel)
                            .addComponent(estimatedHoursLabel)
                            .addComponent(statrDateLabel)
                            .addComponent(dueDateLabel)
                            .addComponent(categoryLabel)
                            .addComponent(typeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dueDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(startDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(milestoneScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(versionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(issueTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(categoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(estimatedHoursTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hoursEstimatedLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addVersionButton)
                            .addComponent(addCategoryButton)
                            .addComponent(addMilestoneButton))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(acturalHoursLabel)
                            .addComponent(assigneeLabel)
                            .addComponent(resolutionLabel)
                            .addComponent(priorityLabel)
                            .addComponent(statusLabel))
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(priorityComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(statusComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(resolutionComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(assigneeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(assignToMyselfLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(actualHoursTextField)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(hoursActualLabel)))))
                        .addContainerGap())
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(commentLabel)
                        .addGap(18, 18, 18)
                        .addComponent(commentScrollPane)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(commentsCollapsibleSectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(subtaskingCollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(summaryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(summaryLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLabel)
                    .addComponent(descriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(issueTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(priorityLabel)
                    .addComponent(priorityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(statusLabel)
                        .addComponent(statusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(categoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addCategoryButton)
                    .addComponent(categoryLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addVersionButton)
                    .addComponent(versionLabel)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(resolutionLabel)
                        .addComponent(resolutionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(milestoneScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(milestoneLabel)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(assigneeLabel)
                                    .addComponent(assigneeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(assignToMyselfLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(estimatedHoursLabel)
                            .addComponent(acturalHoursLabel)
                            .addComponent(estimatedHoursTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actualHoursTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hoursEstimatedLabel)
                            .addComponent(hoursActualLabel)))
                    .addComponent(addMilestoneButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statrDateLabel)
                    .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dueDateLabel)
                    .addComponent(dueDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attachmentsCollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(commentLabel)
                    .addComponent(commentScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(commentsCollapsibleSectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainScrollPane.setViewportView(mainPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainScrollPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void assignToMyselfLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assignToMyselfLinkButtonActionPerformed
        assert SwingUtilities.isEventDispatchThread();
        BacklogData data = BacklogData.create(issue.getRepository());
        User myself = data.getMyself();
        if (myself != null) {
            setSelectedAssignee(myself);
        }
    }//GEN-LAST:event_assignToMyselfLinkButtonActionPerformed

    @NbBundle.Messages({
        "BacklogIssuePanel.label.add.category=Add Category"
    })
    private void addCategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryButtonActionPerformed
        // show dialog
        NotifyDescriptor.InputLine inputDialog = new NotifyDescriptor.InputLine("", Bundle.BacklogIssuePanel_label_add_category()); // NOI18N
        if (DialogDisplayer.getDefault().notify(inputDialog) == NotifyDescriptor.OK_OPTION) {
            BacklogRepository repository = issue.getRepository();
            BacklogClient backlogClient = repository.createBacklogClient();
            if (backlogClient == null) {
                return;
            }
            String inputText = inputDialog.getInputText();
            if (inputText.isEmpty()) {
                return;
            }
            Category addCategory = backlogClient.addCategory(new AddCategoryParams(repository.getProjectKey(), inputText));
            if (addCategory == null) {
                // TODO show dialog?
                return;
            }
            // update cache
            BacklogData data = BacklogData.create(repository);
            setCategories(data, true);
            Issue existingIssue = issue.getIssue();
            setSelectedCategories(existingIssue.getCategory());
        }
    }//GEN-LAST:event_addCategoryButtonActionPerformed

    private void showOnBrowserLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showOnBrowserLinkButtonActionPerformed
        showInBrowser();
    }//GEN-LAST:event_showOnBrowserLinkButtonActionPerformed

    private void addVersionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addVersionButtonActionPerformed
        addVersionMilestone();
    }//GEN-LAST:event_addVersionButtonActionPerformed

    private void addMilestoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMilestoneButtonActionPerformed
        addVersionMilestone();
    }//GEN-LAST:event_addMilestoneButtonActionPerformed

    private void submitHeaderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitHeaderButtonActionPerformed
        final JButton button = (JButton) evt.getSource();
        RP.post(new Runnable() {

            @Override
            public void run() {
                setSubmitButtonEnabled(false);
                try {
                    final String buttonText = button.getText();
                    if (buttonText.equals(Bundle.BacklogIssuePanel_submitButton_add())) {
                        addIssue();
                    } else {
                        updateIssue();
                    }
                } finally {
                    setSubmitButtonEnabled(true);
                }
            }
        });
    }//GEN-LAST:event_submitHeaderButtonActionPerformed

    private void refreshLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshLinkButtonActionPerformed
        refresh();
    }//GEN-LAST:event_refreshLinkButtonActionPerformed

    private void selectFilesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFilesButtonActionPerformed
        selectFiles();
    }//GEN-LAST:event_selectFilesButtonActionPerformed

    private void addSubtaskLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubtaskLinkButtonActionPerformed
        if (issue == null) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                BacklogRepository backlogRepository = issue.getRepository();
                BacklogIssue parent = issue;
                if (issue.isChild()) {
                    parent = issue.getParentIssue();
                }
                if (parent == null) {
                    return;
                }
                backlogRepository.createNewSubissue(parent);
            }
        });
    }//GEN-LAST:event_addSubtaskLinkButtonActionPerformed

    @NbBundle.Messages({
        "BacklogIssuePanel.label.select.file=Select File",
        "BacklogIssuePanel.message.uploading.attachments=Uploading files"
    })
    private void selectFiles() {
        // attachment
        RP.post(new Runnable() {

            @Override
            public void run() {
                submitHeaderButton.setEnabled(false);
                // TODO add FileFilter
                File[] attachments = new FileChooserBuilder(BacklogIssuePanel.class.getName() + BACKLOG_ATTACHMENT_SUFFIX)
                        .setTitle(Bundle.BacklogIssuePanel_label_select_file())
                        .setFilesOnly(true)
                        .showMultiOpenDialog();
                if (attachments == null) {
                    submitHeaderButton.setEnabled(true);
                    fireChange();
                    return;
                }

                // show progress bar
                ProgressHandle handle = ProgressHandleFactory.createHandle(
                        Bundle.BacklogIssuePanel_message_uploading_attachments(),
                        new Cancellable() {
                            @Override
                            public boolean cancel() {
                                return true;
                            }
                        });
                try {
                    handle.start(attachments.length);
                    int progressCount = 0;
                    for (File attachment : attachments) {
                        attachment = FileUtil.normalizeFile(attachment);
                        String fileName = attachment.getName();
                        try {
                            handle.progress(fileName);
                            Attachment backlogAttachment = issue.postAttachmentFile(attachment, "");
                            if (backlogAttachment == null) {
                                // TODO set error?
                                break;
                            }
                            handle.progress(++progressCount);
                            unsubmittedAttachmentsPanel.addAttachment(backlogAttachment, true);
                            attachmentIds.add(backlogAttachment.getId());
                        } catch (FileNotFoundException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage());
                        }
                    }
                } finally {
                    handle.finish();
                    submitHeaderButton.setEnabled(true);
                    fireChange();
                }
            }
        });
    }

    private void refresh() {
        RP.post(new Runnable() {

            @Override
            public void run() {
                // #3
                Runnable refresh = new Runnable() {
                    @Override
                    public void run() {
                        refreshLinkButton.setEnabled(false);
                        // show progress bar
                        ProgressHandle handle = ProgressHandleFactory.createHandle("Refreshing..."); // NOI18N
                        try {
                            handle.start();
                            issue.refresh();
                            update(true);
                        } finally {
                            refreshLinkButton.setEnabled(true);
                            handle.finish();
                        }
                    }
                };

                if (SwingUtilities.isEventDispatchThread()) {
                    refresh.run();
                } else {
                    SwingUtilities.invokeLater(refresh);
                }
            }
        });
    }

    @NbBundle.Messages({
        "BacklogIssuePanel.message.add.issue.success=Issue has been added.",
        "BacklogIssuePanel.message.add.issue.fail=Failure."
    })
    private void addIssue() {
        CreateIssueParams issueParams = createCreateIssueParams();
        if (issueParams == null) {
            return;
        }
        final Issue newIssue = issue.addIssue(issueParams);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (newIssue != null) {
                    unsubmittedAttachmentsPanel.removeAllAttachments();
                    StatusDisplayer.getDefault().setStatusText(Bundle.BacklogIssuePanel_message_add_issue_success());
                } else {
                    StatusDisplayer.getDefault().setStatusText(Bundle.BacklogIssuePanel_message_add_issue_fail());
                }
                // update issue panel
                update(false);
            }
        });

    }

    @NbBundle.Messages({
        "BacklogIssuePanel.message.update.issue.success=Issue has been updated.",
        "BacklogIssuePanel.message.update.issue.fail=Failure."
    })
    private void updateIssue() {
        UpdateIssueParams issueParams = createUpdateIssueParams();
        final Issue updateIssue = issue.updateIssue(issueParams);
        final boolean hasComment = isCommentUpdated();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (updateIssue != null) {
                    unsubmittedAttachmentsPanel.removeAllAttachments();
                    StatusDisplayer.getDefault().setStatusText(Bundle.BacklogIssuePanel_message_update_issue_success());
                } else {
                    StatusDisplayer.getDefault().setStatusText(Bundle.BacklogIssuePanel_message_update_issue_fail());
                }
                update(hasComment);
            }
        });

    }

    private void showInBrowser() {
        Issue existingIssue = issue.getIssue();
        if (existingIssue == null) {
            return;
        }
        BacklogRepository repository = issue.getRepository();
        String spaceId = repository.getSpaceId();
        String projectKey = repository.getProjectKey();
        String url = getIssueUrl(spaceId, projectKey, existingIssue.getKeyId());
        if (url == null) {
            return;
        }
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(url));
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    @NbBundle.Messages({
        "BacklogIssuePanel.label.add.version.milestone=Add version/milestone"
    })
    private void addVersionMilestone() {
        NotifyDescriptor.InputLine inputLine = new NotifyDescriptor.InputLine("", Bundle.BacklogIssuePanel_label_add_version_milestone()); // NOI18N
        if (DialogDisplayer.getDefault().notify(inputLine) == NotifyDescriptor.OK_OPTION) {
            String inputText = inputLine.getInputText();
            if (StringUtils.isEmpty(inputText)) {
                return;
            }
            BacklogRepository repository = issue.getRepository();
            BacklogClient backlogClient = repository.createBacklogClient();
            if (backlogClient == null) {
                return;
            }
            try {
                Version addVersion = backlogClient.addVersion(new AddVersionParams(repository.getProjectKey(), inputText));
                if (addVersion == null) {
                    LOGGER.log(Level.WARNING, "Can't add version ({0})", inputText); // NOI18N
                    return;
                }
                BacklogData data = BacklogData.create(repository);
                setVersions(data, true);
                setMilestones(data, false); // already reloaded in setVersions
                Issue existingIssue = issue.getIssue();
                setSelectedVersions(existingIssue.getVersions());
                setSelectedMilestones(existingIssue.getMilestone());
            } catch (BacklogAPIException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    private CreateIssueParams createCreateIssueParams() {
        Project project = issue.getRepository().getProject();
        if (project == null) {
            return null;
        }
        CreateIssueParams issueParams = new CreateIssueParams(project.getId(), getSummary(), getIssueType().getId(), getPriority().getPriority())
                .description(getDescription())
                .categoryIds(getCategoryIds())
                .versionIds(getVersionIds())
                .milestoneIds(getMilestonIds());
        // actual and estimated hours
        String actualHours = getActualHours();
        if (!StringUtils.isEmpty(actualHours)) {
            issueParams = issueParams.actualHours(Float.valueOf(actualHours));
        }
        String estimatedHours = getEstimatedHours();
        if (!StringUtils.isEmpty(estimatedHours)) {
            issueParams = issueParams.estimatedHours(Float.valueOf(estimatedHours));
        }

        // date
        String dueDate = getDueDate();
        if (!StringUtils.isEmpty(dueDate)) {
            issueParams = issueParams.dueDate(BacklogUtils.toApiDateFormat(dueDate));
        }
        String startDate = getStartDate();
        if (!StringUtils.isEmpty(startDate)) {
            issueParams = issueParams.startDate(BacklogUtils.toApiDateFormat(startDate));
        }

        // assignee
        User assignee = getAssignee();
        if (assignee != null && !StringUtils.isEmpty(assignee.getName())) {
            issueParams = issueParams.assigneeId(assignee.getId());
        }

        // attachments
        if (!attachmentIds.isEmpty()) {
            issueParams = issueParams.attachmentIds(attachmentIds);
        }

        // subtask?
        if (isSubtaskingEnabled()) {
            if (issue.isNew() && !StringUtils.isEmpty(issue.getSubtaskParentIssueKey())) {
                BacklogIssue parentIssue = issue.getParentIssue();
                issueParams = issueParams.parentIssueId(parentIssue.getIssue().getId());
            }
        }
        return issueParams;
    }

    private UpdateIssueParams createUpdateIssueParams() {
        UpdateIssueParams issueParams = new UpdateIssueParams(issue.getIssueKey())
                .summary(getSummary())
                .description(getDescription())
                .issueTypeId(getIssueType().getId())
                .priority(getPriority().getPriority())
                .categoryIds(getCategoryIds())
                .versionIds(getVersionIds())
                .milestoneIds(getMilestonIds());

        // resolution
        // if we change resolution to empty, set null value
        Resolution resolution = getResolution();
        if (!StringUtils.isEmpty(resolution.getName())) {
            issueParams = issueParams.resolution(resolution.getResolution());
        } else {
            issueParams = issueParams.resolution(null);
        }

        // comment
        String comment = getComment();
        if (!StringUtils.isEmpty(comment)) {
            issueParams = issueParams.comment(comment);
        }

        // check status
        // Can't change to the same status
        Issue existingIssue = issue.getIssue();
        Issue.StatusType originalStatus = existingIssue.getStatus().getStatus();
        Issue.StatusType newStatus = getStatus().getStatus();
        if (originalStatus != newStatus) {
            issueParams = issueParams.status(newStatus);
        }

        // hours
        String actualHours = getActualHours();
        if (!StringUtils.isEmpty(actualHours)) {
            issueParams = issueParams.actualHours(Float.valueOf(actualHours));
        }
        String estimatedHours = getEstimatedHours();
        if (!StringUtils.isEmpty(estimatedHours)) {
            issueParams = issueParams.estimatedHours(Float.valueOf(estimatedHours));
        }

        // date
        String dueDate = getDueDate();
        String startDate = getStartDate();
        issueParams = issueParams.dueDate(BacklogUtils.toApiDateFormat(dueDate))
                .startDate(BacklogUtils.toApiDateFormat(startDate));

        // assignee
        // set 0 to id if change an assignee to empty
        User assignee = getAssignee();
        if (assignee != null) {
            if (!StringUtils.isEmpty(assignee.getName())) {
                issueParams = issueParams.assigneeId(assignee.getId());
            } else {
                issueParams = issueParams.assigneeId(0);
            }
        }

        // XXX check attachments count?
        // attachments
        if (!attachmentIds.isEmpty()) {
            issueParams = issueParams.attachmentIds(attachmentIds);
        }
        return issueParams;
    }

    private boolean isCommentUpdated() {
        String comment = getComment();
        return !StringUtils.isEmpty(comment);
    }

    @CheckForNull
    private String getIssueUrl(String spaceId, String projectKey, long issueKeyId) {
        BacklogRepository repository = issue.getRepository();
        String backlogDomain = repository.getBacklogDomain();
        String urlFormat = getBacklogIssueUrlFormat(backlogDomain);
        if (urlFormat == null) {
            return null;
        }
        return String.format(urlFormat, spaceId, projectKey, issueKeyId);
    }

    private static String getBacklogIssueUrlFormat(String backlogDomain) {
        if (null != backlogDomain) {
            switch (backlogDomain) {
                case BacklogUtils.BACKLOGTOOL_COM:
                    return BACKLOGTOOL_COM_ISSUE_URL_FORMAT;
                case BacklogUtils.BACKLOG_JP:
                    return BACKLOG_JP_ISSUE_URL_FORMAT;
                default:
                    break;
            }
        }
        LOGGER.log(Level.WARNING, "Invalid backlog domain: {0}", backlogDomain);
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField actualHoursTextField;
    private javax.swing.JLabel acturalHoursLabel;
    private javax.swing.JButton addCategoryButton;
    private javax.swing.JButton addMilestoneButton;
    private org.netbeans.modules.bugtracking.commons.LinkButton addSubtaskLinkButton;
    private javax.swing.JButton addVersionButton;
    private org.netbeans.modules.bugtracking.commons.LinkButton assignToMyselfLinkButton;
    private javax.swing.JComboBox<User> assigneeComboBox;
    private javax.swing.JLabel assigneeLabel;
    private org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel attachmentsCollapsibleSectionPanel;
    private javax.swing.JLabel attributeLabel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JList<Category> categoryList;
    private javax.swing.JScrollPane categoryScrollPane;
    private javax.swing.JLabel commentLabel;
    private javax.swing.JScrollPane commentScrollPane;
    private javax.swing.JTextArea commentTextArea;
    private org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel commentsCollapsibleSectionPanel;
    private javax.swing.JEditorPane descriptionEditorPane;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JLabel dueDateLabel;
    private org.jdesktop.swingx.JXDatePicker dueDatePicker;
    private javax.swing.JLabel errorHeaderLabel;
    private javax.swing.JLabel estimatedHoursLabel;
    private javax.swing.JTextField estimatedHoursTextField;
    private javax.swing.JLabel headerCreatedByLabel;
    private javax.swing.JLabel headerCreatedDateLabel;
    private javax.swing.JLabel headerCreatedLabel;
    private org.netbeans.modules.bugtracking.commons.LinkButton headerCreatedUserLinkButton;
    private javax.swing.JLabel headerDueDateLabel;
    private javax.swing.JLabel headerDueDateViewLabel;
    private javax.swing.JLabel headerIssueKeyLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel headerStartDateLabel;
    private javax.swing.JLabel headerStartDateViewLabel;
    private javax.swing.JLabel hoursActualLabel;
    private javax.swing.JLabel hoursEstimatedLabel;
    private javax.swing.JComboBox<IssueType> issueTypeComboBox;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JPanel mainAttachmentsPanel;
    private javax.swing.JPanel mainCommentsPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JPanel mainSubtaskTablePanel;
    private javax.swing.JLabel milestoneLabel;
    private javax.swing.JList<Version> milestoneList;
    private javax.swing.JScrollPane milestoneScrollPane;
    private javax.swing.JComboBox<Priority> priorityComboBox;
    private javax.swing.JLabel priorityLabel;
    private org.netbeans.modules.bugtracking.commons.LinkButton refreshLinkButton;
    private javax.swing.JComboBox<Resolution> resolutionComboBox;
    private javax.swing.JLabel resolutionLabel;
    private javax.swing.JButton selectFilesButton;
    private org.netbeans.modules.bugtracking.commons.LinkButton showOnBrowserLinkButton;
    private org.jdesktop.swingx.JXDatePicker startDatePicker;
    private javax.swing.JLabel statrDateLabel;
    private javax.swing.JComboBox<Status> statusComboBox;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton submitHeaderButton;
    private org.netbeans.modules.bugtracking.commons.CollapsibleSectionPanel subtaskingCollapsibleSectionPanel;
    private javax.swing.JLabel summaryLabel;
    private javax.swing.JTextField summaryTextField;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JList<Version> versionList;
    private javax.swing.JScrollPane versionScrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case BacklogIssue.PROP_COMMENT_QUOTE:
                addQuoteComment(commentsPanel.getQuoteComment());
                break;
            case BacklogIssue.PROP_COMMENT_DELETED:
                deleteComment(commentsPanel.getDeletedComment());
                break;
            case BacklogIssue.PROP_COMMENT_EDITED:
                editComment(commentsPanel.getEditedComment());
                break;
            case AttachmentPanel.PROP_ATTACHMENT_DELETED:
                Attachment attachment = (Attachment) event.getOldValue();
                deleteAttachment(attachment);
                break;
            default:
                break;
        }
        commentsPanel.resetChangedPanels();
    }

    private void addQuoteComment(final String quoteComment) {
        if (quoteComment != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String existingText = commentTextArea.getText();
                    StringBuilder sb = new StringBuilder();
                    if (!StringUtils.isEmpty(existingText)) {
                        sb.append(existingText).append("\n"); // NOI18N
                    }
                    sb.append(StringUtils.toQuoteComment(quoteComment));
                    commentTextArea.setText(sb.toString());

                    scrollToCommentArea();
                }
            });
        }
    }

    private void deleteComment(IssueComment comment) {
        // XXX delet comment is still not supported by api v2
    }

    private void editComment(final IssueComment comment) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // show dialog
                String content = EditIssuePanel.showDialog(comment);
                if (content == null) {
                    return;
                }
                IssueComment updateIssueComment = issue.updateIssueComment(comment, content);
                if (updateIssueComment != null) {
                    update(true);
                }
            }
        });
    }

    private void deleteAttachment(Attachment attachment) {
        Attachment a = issue.deleteIssueAttachment(attachment.getId());
        if (a != null) {
            attachmentsPanel.removeAttachment(attachment);
            update(false);
        }
    }

    private void scrollToCommentArea() {
        int commentHeight = commentScrollPane.getHeight();
        int commentsHeight = commentsCollapsibleSectionPanel.getHeight();
        JScrollBar verticalScrollBar = mainScrollPane.getVerticalScrollBar();
        int maximum = verticalScrollBar.getMaximum();
        verticalScrollBar.setValue(maximum - (commentsHeight + commentHeight + 50)); // 50: empty space
    }

    //~ Inner class
    private class DefaultDocumentListner implements DocumentListener {

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
}
