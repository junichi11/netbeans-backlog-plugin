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

import com.junichi11.netbeans.modules.backlog.Backlog.FileType;
import com.junichi11.netbeans.modules.backlog.BacklogData;
import com.junichi11.netbeans.modules.backlog.query.BacklogQuery;
import com.junichi11.netbeans.modules.backlog.query.QueryUtils;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.ui.AttributesListCellRenderer;
import com.junichi11.netbeans.modules.backlog.utils.StringUtils;
import com.nulabinc.backlog4j.Category;
import com.nulabinc.backlog4j.Issue;
import com.nulabinc.backlog4j.Issue.PriorityType;
import com.nulabinc.backlog4j.Issue.ResolutionType;
import com.nulabinc.backlog4j.Issue.StatusType;
import com.nulabinc.backlog4j.IssueType;
import com.nulabinc.backlog4j.Priority;
import com.nulabinc.backlog4j.Resolution;
import com.nulabinc.backlog4j.Status;
import com.nulabinc.backlog4j.User;
import com.nulabinc.backlog4j.Version;
import com.nulabinc.backlog4j.internal.json.CategoryJSONImpl;
import com.nulabinc.backlog4j.internal.json.IssueTypeJSONImpl;
import com.nulabinc.backlog4j.internal.json.PriorityJSONImpl;
import com.nulabinc.backlog4j.internal.json.ResolutionJSONImpl;
import com.nulabinc.backlog4j.internal.json.StatusJSONImpl;
import com.nulabinc.backlog4j.internal.json.UserJSONImpl;
import com.nulabinc.backlog4j.internal.json.VersionJSONImpl;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class GeneralPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 6142512073117694599L;
    private final DefaultListModel<Status> statusListModel = new DefaultListModel<>();
    private final DefaultListModel<Priority> priorityListModel = new DefaultListModel<>();
    private final DefaultListModel<Version> versionListModel = new DefaultListModel<>();
    private final DefaultListModel<User> assigneeListModel = new DefaultListModel<>();
    private final DefaultListModel<Category> categoryListModel = new DefaultListModel<>();
    private final DefaultListModel<User> registeredByListModel = new DefaultListModel<>();
    private final DefaultListModel<Version> milestoneListModel = new DefaultListModel<>();
    private final DefaultListModel<Resolution> resolutionListModel = new DefaultListModel<>();
    private final DefaultListModel<IssueType> issueTypeListModel = new DefaultListModel<>();
    private final DefaultListModel<FileType> fileListModel = new DefaultListModel<>();
    private final BacklogRepository repository;

    private static final List<Long> NOT_CLOSED_STATUS = new ArrayList<>(StatusType.values().length);

    static {
        for (StatusType value : QueryUtils.NOT_CLOSED_STATUS) {
            long id = value.getIntValue();
            NOT_CLOSED_STATUS.add(id);
        }
    }

    /**
     * Creates new form GeneralPanel
     */
    public GeneralPanel(BacklogRepository repository) {
        this.repository = repository;
        initComponents();
        init();
    }

    private void init() {
        issueTypeList.setModel(issueTypeListModel);
        issueTypeList.setCellRenderer(new AttributesListCellRenderer(issueTypeList.getCellRenderer()));
        statusList.setModel(statusListModel);
        statusList.setCellRenderer(new AttributesListCellRenderer(statusList.getCellRenderer()));
        priorityList.setModel(priorityListModel);
        priorityList.setCellRenderer(new AttributesListCellRenderer(priorityList.getCellRenderer()));
        assigneeList.setModel(assigneeListModel);
        assigneeList.setCellRenderer(new AttributesListCellRenderer(assigneeList.getCellRenderer()));
        registeredByList.setModel(registeredByListModel);
        registeredByList.setCellRenderer(new AttributesListCellRenderer(registeredByList.getCellRenderer()));
        versionList.setModel(versionListModel);
        versionList.setCellRenderer(new AttributesListCellRenderer(versionList.getCellRenderer()));
        categoryList.setModel(categoryListModel);
        categoryList.setCellRenderer(new AttributesListCellRenderer(categoryList.getCellRenderer()));
        milestoneList.setModel(milestoneListModel);
        milestoneList.setCellRenderer(new AttributesListCellRenderer(milestoneList.getCellRenderer()));
        resolutionList.setModel(resolutionListModel);
        resolutionList.setCellRenderer(new AttributesListCellRenderer(resolutionList.getCellRenderer()));
        fileList.setModel(fileListModel);
        fileList.setCellRenderer(new AttributesListCellRenderer(fileList.getCellRenderer()));
    }

    void clear() {
        setAttributes();
    }

    void reset() {
        reset(null);
    }

    void reset(BacklogQuery query) {
        setAttributes();
        if (query != null) {
        }
    }

    void setComponentsEnabled(boolean isEnabled) {
        setComponentsEnabled(this, isEnabled);
    }

    private void setComponentsEnabled(Container container, boolean isEnabled) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(isEnabled);
            if (component instanceof Container) {
                setComponentsEnabled((Container) component, isEnabled);
            }
        }
    }

    private void setAttributes() {
        BacklogData data = BacklogData.create(repository);
        setIssueType(data.getIssueTypes());
        setPriority(data.getPriorities());
        setStatus(data.getStatus());
        setCategory(data.getCategories());
        setVersion(data.getVersions());
        setMilestone(data.getVersions());
        setAssignee(data.getProjectUsers());
        setResisteredBy(data.getProjectUsers());
        setResolution(data.getResolutions());
        setFile();
    }

    private void setIssueType(List<IssueType> issueTypes) {
        if (issueTypes == null) {
            return;
        }
        issueTypeListModel.removeAllElements();
        issueTypeListModel.addElement(new IssueTypeJSONImpl());
        for (IssueType issueType : issueTypes) {
            issueTypeListModel.addElement(issueType);
        }
    }

    private void setPriority(List<Priority> priorities) {
        if (priorities == null) {
            return;
        }
        priorityListModel.removeAllElements();
        priorityListModel.addElement(new PriorityJSONImpl());
        for (Priority priority : priorities) {
            priorityListModel.addElement(priority);
        }
    }

    private void setStatus(List<Status> status) {
        if (status == null) {
            return;
        }
        statusListModel.removeAllElements();
        statusListModel.addElement(new StatusJSONImpl());
        for (Status s : status) {
            statusListModel.addElement(s);
        }
    }

    private void setCategory(List<Category> categories) {
        if (categories == null) {
            return;
        }
        categoryListModel.removeAllElements();
        categoryListModel.addElement(new CategoryJSONImpl());
        categoryListModel.addElement(new NoCategory());
        for (Category category : categories) {
            categoryListModel.addElement(category);
        }
    }

    private void setVersion(List<Version> versions) {
        if (versions == null) {
            return;
        }
        versionListModel.removeAllElements();
        versionListModel.addElement(new VersionJSONImpl());
        versionListModel.addElement(new NoVersion());
        for (Version version : versions) {
            versionListModel.addElement(version);
        }
    }

    private void setMilestone(List<Version> milestones) {
        if (milestones == null) {
            return;
        }
        milestoneListModel.removeAllElements();
        milestoneListModel.addElement(new VersionJSONImpl());
        milestoneListModel.addElement(new NoMilestone());
        for (Version milestone : milestones) {
            milestoneListModel.addElement(milestone);
        }
    }

    private void setResolution(List<Resolution> resolutions) {
        if (resolutions == null) {
            return;
        }
        resolutionListModel.removeAllElements();
        resolutionListModel.addElement(new ResolutionJSONImpl());
        resolutionListModel.addElement(new NoResolution());
        for (Resolution resolution : resolutions) {
            resolutionListModel.addElement(resolution);
        }
    }

    private void setAssignee(List<User> assignees) {
        if (assignees == null) {
            return;
        }
        assigneeListModel.removeAllElements();
        assigneeListModel.addElement(new UserJSONImpl());
        assigneeListModel.addElement(new UnassignedUser());
        for (User assignee : assignees) {
            assigneeListModel.addElement(assignee);
        }
    }

    private void setResisteredBy(List<User> users) {
        if (users == null) {
            return;
        }
        registeredByListModel.removeAllElements();
        registeredByListModel.addElement(new UserJSONImpl());
        for (User user : users) {
            registeredByListModel.addElement(user);
        }
    }

    private void setFile() {
        fileListModel.removeAllElements();
        for (FileType fileType : FileType.values()) {
            fileListModel.addElement(fileType);
        }
    }

    public List<StatusType> getStatus() {
        List<Status> selectedValues = statusList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        List<StatusType> status = new ArrayList<>(selectedValues.size());
        for (Status selectedValue : selectedValues) {
            Issue.StatusType statusType = selectedValue.getStatusType();
            if (statusType != null) {
                status.add(statusType);
            }
        }
        return status;
    }

    public List<PriorityType> getPriorities() {
        List<Priority> selectedValues = priorityList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        List<PriorityType> priorities = new ArrayList<>(selectedValues.size());
        for (Priority selectedValue : selectedValues) {
            PriorityType priority = selectedValue.getPriorityType();
            if (priority != null) {
                priorities.add(priority);
            }
        }
        return priorities;
    }

    public List<ResolutionType> getResolutions() {
        List<Resolution> selectedValues = resolutionList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        List<ResolutionType> resolutions = new ArrayList<>(selectedValues.size());
        for (Resolution selectedValue : selectedValues) {
            ResolutionType resolution = selectedValue.getResolutionType();
            if (selectedValue.getName() != null && resolution != null) {
                resolutions.add(resolution);
            }
        }
        return resolutions;
    }

    public List<Long> getCategoryIds() {
        List<Category> selectedValues = categoryList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        ArrayList<Long> categories = new ArrayList<>(selectedValues.size());
        for (Category selectedValue : selectedValues) {
            if (StringUtils.isEmpty(selectedValue.getName())) {
                continue;
            }
            categories.add(selectedValue.getId());
        }
        return categories;
    }

    public List<Long> getVersionIds() {
        List<Version> selectedValues = versionList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        ArrayList<Long> versionIds = new ArrayList<>(selectedValues.size());
        for (Version selectedValue : selectedValues) {
            if (StringUtils.isEmpty(selectedValue.getName())) {
                continue;
            }
            versionIds.add(selectedValue.getId());
        }
        return versionIds;
    }

    public List<Long> getMilestoneIds() {
        List<Version> selectedValues = milestoneList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        ArrayList<Long> milestoneIds = new ArrayList<>(selectedValues.size());
        for (Version selectedValue : selectedValues) {
            if (StringUtils.isEmpty(selectedValue.getName())) {
                continue;
            }
            milestoneIds.add(selectedValue.getId());
        }
        return milestoneIds;
    }

    public List<Long> getIssueTypeIds() {
        List<IssueType> selectedValues = issueTypeList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        ArrayList<Long> issuetypeIds = new ArrayList<>(selectedValues.size());
        for (IssueType selectedValue : selectedValues) {
            if (StringUtils.isEmpty(selectedValue.getName())) {
                continue;
            }
            issuetypeIds.add(selectedValue.getId());
        }
        return issuetypeIds;
    }

    public List<Long> getAssignerIds() {
        List<User> selectedValues = assigneeList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        ArrayList<Long> assigneeIds = new ArrayList<>(selectedValues.size());
        for (User selectedValue : selectedValues) {
            if (StringUtils.isEmpty(selectedValue.getName())) {
                continue;
            }
            assigneeIds.add(selectedValue.getId());
        }
        return assigneeIds;
    }

    public List<Long> getCreatedUserIds() {
        List<User> selectedValues = registeredByList.getSelectedValuesList();
        if (selectedValues == null) {
            return Collections.emptyList();
        }
        ArrayList<Long> registeredbyIds = new ArrayList<>(selectedValues.size());
        for (User selectedValue : selectedValues) {
            if (StringUtils.isEmpty(selectedValue.getName())) {
                continue;
            }
            registeredbyIds.add(selectedValue.getId());
        }
        return registeredbyIds;
    }

    public boolean isAttached() {
        List<FileType> selectedValuesList = fileList.getSelectedValuesList();
        return selectedValuesList.contains(FileType.ATTACHED);
    }

    public boolean isShared() {
        List<FileType> selectedValuesList = fileList.getSelectedValuesList();
        return selectedValuesList.contains(FileType.SHARED);
    }

    public void setSelectedStatus(List<Long> statusIds) {
        int size = statusIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = statusIds.get(i);
            for (int j = 0; j < statusListModel.getSize(); j++) {
                Status status = statusListModel.get(j);
                if (StringUtils.isEmpty(status.getName())) {
                    continue;
                }
                if (id == status.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        statusList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedPriority(List<Long> priorityIds) {
        int size = priorityIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = priorityIds.get(i);
            for (int j = 0; j < priorityListModel.getSize(); j++) {
                Priority priority = priorityListModel.get(j);
                if (StringUtils.isEmpty(priority.getName())) {
                    continue;
                }
                if (id == priority.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        priorityList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedCategory(List<Long> categoryIds) {
        int size = categoryIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = categoryIds.get(i);
            for (int j = 0; j < categoryListModel.getSize(); j++) {
                Category category = categoryListModel.get(j);
                if (StringUtils.isEmpty(category.getName())) {
                    continue;
                }
                if (id == category.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        categoryList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedAssignee(List<Long> assigneeIds) {
        int size = assigneeIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = assigneeIds.get(i);
            for (int j = 0; j < assigneeListModel.getSize(); j++) {
                User assignee = assigneeListModel.get(j);
                if (StringUtils.isEmpty(assignee.getName())) {
                    continue;
                }
                if (id == assignee.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        assigneeList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedVersion(List<Long> versionIds) {
        int size = versionIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = versionIds.get(i);
            for (int j = 0; j < versionListModel.getSize(); j++) {
                Version version = versionListModel.get(j);
                if (StringUtils.isEmpty(version.getName())) {
                    continue;
                }
                if (id == version.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        versionList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedMilestone(List<Long> versionIds) {
        int size = versionIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = versionIds.get(i);
            for (int j = 0; j < milestoneListModel.getSize(); j++) {
                Version version = milestoneListModel.get(j);
                if (StringUtils.isEmpty(version.getName())) {
                    continue;
                }
                if (id == version.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        milestoneList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedRegisteredBy(List<Long> registeredbyIds) {
        int size = registeredbyIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = registeredbyIds.get(i);
            for (int j = 0; j < registeredByListModel.getSize(); j++) {
                User registeredby = registeredByListModel.get(j);
                if (StringUtils.isEmpty(registeredby.getName())) {
                    continue;
                }
                if (id == registeredby.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        registeredByList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedResolution(List<Long> resolutionIds) {
        int size = resolutionIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = resolutionIds.get(i);
            for (int j = 0; j < resolutionListModel.getSize(); j++) {
                Resolution resolution = resolutionListModel.get(j);
                if (StringUtils.isEmpty(resolution.getName())) {
                    continue;
                }
                if (id == resolution.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        resolutionList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedIssueType(List<Long> issuetypeIds) {
        int size = issuetypeIds.size();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Long id = issuetypeIds.get(i);
            for (int j = 0; j < issueTypeListModel.getSize(); j++) {
                IssueType issuetype = issueTypeListModel.get(j);
                if (StringUtils.isEmpty(issuetype.getName())) {
                    continue;
                }
                if (id == issuetype.getId()) {
                    indicesList.add(j);
                    break;
                }
                // XXX already was removed
            }
        }
        issueTypeList.setSelectedIndices(toIntArray(indicesList));
    }

    public void setSelectedFiles(List<FileType> fileTypes) {
        List<Integer> indicesList = new ArrayList<>();
        for (FileType fileType : fileTypes) {
            for (int i = 0; i < fileListModel.getSize(); i++) {
                if (fileType == fileListModel.get(i)) {
                    indicesList.add(i);
                    break;
                }
            }
            fileList.setSelectedValue(fileType, false);
        }
        fileList.setSelectedIndices(toIntArray(indicesList));
    }

    private static int[] toIntArray(List<Integer> items) {
        int size = items.size();
        int[] intArray = new int[size];
        for (int i = 0; i < size; i++) {
            intArray[i] = items.get(i);
        }
        return intArray;
    }

    private User getMyself() {
        BacklogData data = BacklogData.create(repository);
        return data.getMyself();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusLabel = new javax.swing.JLabel();
        statusScrollPane = new javax.swing.JScrollPane();
        statusList = new javax.swing.JList<Status>();
        priorityLabel = new javax.swing.JLabel();
        priorityScrollPane = new javax.swing.JScrollPane();
        priorityList = new javax.swing.JList<Priority>();
        categoryLabel = new javax.swing.JLabel();
        categoryScrollPane = new javax.swing.JScrollPane();
        categoryList = new javax.swing.JList<Category>();
        assigneeLabel = new javax.swing.JLabel();
        assigneeScrollPane = new javax.swing.JScrollPane();
        assigneeList = new javax.swing.JList<User>();
        versionLabel = new javax.swing.JLabel();
        versionScrollPane = new javax.swing.JScrollPane();
        versionList = new javax.swing.JList<Version>();
        registeredByLabel = new javax.swing.JLabel();
        registeredByScrollPane = new javax.swing.JScrollPane();
        registeredByList = new javax.swing.JList<User>();
        milestoneLabel = new javax.swing.JLabel();
        milestoneScrollPane = new javax.swing.JScrollPane();
        milestoneList = new javax.swing.JList<Version>();
        resolutionLabel = new javax.swing.JLabel();
        resolutionScrollPane = new javax.swing.JScrollPane();
        resolutionList = new javax.swing.JList<Resolution>();
        issueTypeLabel = new javax.swing.JLabel();
        issueTypeScrollPane = new javax.swing.JScrollPane();
        issueTypeList = new javax.swing.JList<IssueType>();
        fileLabel = new javax.swing.JLabel();
        fileScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList<FileType>();
        assigneeMeLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        registeredByMeLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        notClosedLinkButton = new org.netbeans.modules.bugtracking.commons.LinkButton();

        org.openide.awt.Mnemonics.setLocalizedText(statusLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.statusLabel.text")); // NOI18N

        statusScrollPane.setViewportView(statusList);

        org.openide.awt.Mnemonics.setLocalizedText(priorityLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.priorityLabel.text")); // NOI18N

        priorityScrollPane.setViewportView(priorityList);

        org.openide.awt.Mnemonics.setLocalizedText(categoryLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.categoryLabel.text")); // NOI18N

        categoryScrollPane.setViewportView(categoryList);

        org.openide.awt.Mnemonics.setLocalizedText(assigneeLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.assigneeLabel.text")); // NOI18N

        assigneeScrollPane.setViewportView(assigneeList);

        org.openide.awt.Mnemonics.setLocalizedText(versionLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.versionLabel.text")); // NOI18N

        versionScrollPane.setViewportView(versionList);

        org.openide.awt.Mnemonics.setLocalizedText(registeredByLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.registeredByLabel.text")); // NOI18N

        registeredByScrollPane.setViewportView(registeredByList);

        org.openide.awt.Mnemonics.setLocalizedText(milestoneLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.milestoneLabel.text")); // NOI18N

        milestoneScrollPane.setViewportView(milestoneList);

        org.openide.awt.Mnemonics.setLocalizedText(resolutionLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.resolutionLabel.text")); // NOI18N

        resolutionScrollPane.setViewportView(resolutionList);

        org.openide.awt.Mnemonics.setLocalizedText(issueTypeLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.issueTypeLabel.text")); // NOI18N

        issueTypeScrollPane.setViewportView(issueTypeList);

        org.openide.awt.Mnemonics.setLocalizedText(fileLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.fileLabel.text")); // NOI18N

        fileScrollPane.setViewportView(fileList);

        org.openide.awt.Mnemonics.setLocalizedText(assigneeMeLinkButton, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.assigneeMeLinkButton.text")); // NOI18N
        assigneeMeLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assigneeMeLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(registeredByMeLinkButton, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.registeredByMeLinkButton.text")); // NOI18N
        registeredByMeLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registeredByMeLinkButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(notClosedLinkButton, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.notClosedLinkButton.text")); // NOI18N
        notClosedLinkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notClosedLinkButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(priorityScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(priorityLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(notClosedLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(assigneeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(assigneeMeLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(categoryLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(categoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(assigneeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(registeredByLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(registeredByMeLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(versionLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(versionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(registeredByScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resolutionLabel)
                    .addComponent(milestoneLabel)
                    .addComponent(milestoneScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(resolutionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(issueTypeLabel)
                    .addComponent(fileLabel)
                    .addComponent(issueTypeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(fileScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusLabel)
                            .addComponent(categoryLabel)
                            .addComponent(versionLabel)
                            .addComponent(milestoneLabel)
                            .addComponent(issueTypeLabel)
                            .addComponent(notClosedLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(statusScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(categoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(versionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(milestoneScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(issueTypeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(priorityLabel)
                            .addComponent(assigneeLabel)
                            .addComponent(registeredByLabel)
                            .addComponent(resolutionLabel)
                            .addComponent(fileLabel)
                            .addComponent(assigneeMeLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(registeredByMeLinkButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(registeredByScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(assigneeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(resolutionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(fileScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(priorityScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void assigneeMeLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assigneeMeLinkButtonActionPerformed
        User myself = getMyself();
        if (myself != null) {
            assigneeList.setSelectedValue(myself, false);
        }
    }//GEN-LAST:event_assigneeMeLinkButtonActionPerformed

    private void registeredByMeLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registeredByMeLinkButtonActionPerformed
        User myself = getMyself();
        if (myself != null) {
            registeredByList.setSelectedValue(myself, false);
        }
    }//GEN-LAST:event_registeredByMeLinkButtonActionPerformed

    private void notClosedLinkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notClosedLinkButtonActionPerformed
        setSelectedStatus(NOT_CLOSED_STATUS);
    }//GEN-LAST:event_notClosedLinkButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel assigneeLabel;
    private javax.swing.JList<User> assigneeList;
    private org.netbeans.modules.bugtracking.commons.LinkButton assigneeMeLinkButton;
    private javax.swing.JScrollPane assigneeScrollPane;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JList<Category> categoryList;
    private javax.swing.JScrollPane categoryScrollPane;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JList<FileType> fileList;
    private javax.swing.JScrollPane fileScrollPane;
    private javax.swing.JLabel issueTypeLabel;
    private javax.swing.JList<IssueType> issueTypeList;
    private javax.swing.JScrollPane issueTypeScrollPane;
    private javax.swing.JLabel milestoneLabel;
    private javax.swing.JList<Version> milestoneList;
    private javax.swing.JScrollPane milestoneScrollPane;
    private org.netbeans.modules.bugtracking.commons.LinkButton notClosedLinkButton;
    private javax.swing.JLabel priorityLabel;
    private javax.swing.JList<Priority> priorityList;
    private javax.swing.JScrollPane priorityScrollPane;
    private javax.swing.JLabel registeredByLabel;
    private javax.swing.JList<User> registeredByList;
    private org.netbeans.modules.bugtracking.commons.LinkButton registeredByMeLinkButton;
    private javax.swing.JScrollPane registeredByScrollPane;
    private javax.swing.JLabel resolutionLabel;
    private javax.swing.JList<Resolution> resolutionList;
    private javax.swing.JScrollPane resolutionScrollPane;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JList<Status> statusList;
    private javax.swing.JScrollPane statusScrollPane;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JList<Version> versionList;
    private javax.swing.JScrollPane versionScrollPane;
    // End of variables declaration//GEN-END:variables

    //~ inner classes
    private static class UnassignedUser extends UserJSONImpl {

        @Override
        public long getId() {
            return -1L;
        }

        @NbBundle.Messages("UnassignedUser.name=Unassigned")
        @Override
        public String getName() {
            return Bundle.UnassignedUser_name();
        }

    }

    private static class NoCategory extends CategoryJSONImpl {

        @Override
        public long getId() {
            return -1L;
        }

        @NbBundle.Messages("NoCategory.name=No Category")
        @Override
        public String getName() {
            return Bundle.NoCategory_name();
        }
    }

    private static class NoVersion extends VersionJSONImpl {

        @Override
        public long getId() {
            return -1L;
        }

        @NbBundle.Messages("NoVersion.name=No Version")
        @Override
        public String getName() {
            return Bundle.NoVersion_name();
        }
    }

    private static class NoMilestone extends VersionJSONImpl {

        @Override
        public long getId() {
            return -1L;
        }

        @NbBundle.Messages("NoMilestone.name=No Milestone")
        @Override
        public String getName() {
            return Bundle.NoMilestone_name();
        }
    }

    private static class NoResolution extends ResolutionJSONImpl {

        @Override
        public long getId() {
            return -1L;
        }

        @NbBundle.Messages("NoResolution.name=No Resolution")
        @Override
        public String getName() {
            return Bundle.NoResolution_name();
        }

        @Override
        public ResolutionType getResolutionType() {
            return ResolutionType.NotSet;
        }

    }

}
