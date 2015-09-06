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
package com.junichi11.netbeans.modules.backlog.query;

import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepositoryInfo;
import com.junichi11.netbeans.modules.backlog.utils.BacklogUtils;
import com.nulabinc.backlog4j.Issue.PriorityType;
import com.nulabinc.backlog4j.Issue.ResolutionType;
import com.nulabinc.backlog4j.Issue.StatusType;
import com.nulabinc.backlog4j.api.option.GetIssuesParams;
import com.nulabinc.backlog4j.http.BacklogHttpClientImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author junichi11
 */
public class BacklogQueryTest {

    private BacklogRepository repository;
    private BacklogQuery query;
    private BacklogQuery savedQuery;

    // default queries
    private AssignedToMeQuery assignedToMeQuery;
    private CreatedByMeQuery createdByMeQuery;

    public BacklogQueryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        repository = new BacklogRepository();
        BacklogRepositoryInfo repositoryInfo = new BacklogRepositoryInfo()
                .setBacklogDomain(BacklogUtils.BACKLOG_JP)
                .setApiKey("test")
                .setSpaceId("junichi")
                .setDisplayName("test name")
                .setProjectKey("testProjectKey");
        repository.setRepositoryInfo(repositoryInfo);
        query = new BacklogQuery(repository);
        savedQuery = new BacklogQuery(repository, "saved query", "keyword=test&categoryId[]=1", 100);
        assignedToMeQuery = new AssignedToMeQuery(repository);
        createdByMeQuery = new CreatedByMeQuery(repository);
    }

    @After
    public void tearDown() {
        repository = null;
    }

    /**
     * Test of getDisplayName method, of class BacklogQuery.
     */
    @Test
    public void testGetDisplayName() {
        assertEquals(query.getDisplayName(), null);
        query.setName("My Query");
        assertEquals("My Query", query.getDisplayName());
    }

    /**
     * Test of getTooltip method, of class BacklogQuery.
     */
    @Test
    public void testGetTooltip() {
        assertEquals(query.getTooltip(), null);
        query.setName("My query");
        assertEquals("My query", query.getTooltip());
    }

    /**
     * Test of isSaved method, of class BacklogQuery.
     */
    @Test
    public void testIsSaved() {
        Assert.assertFalse(query.isSaved());
        Assert.assertFalse(assignedToMeQuery.isSaved());
        Assert.assertFalse(createdByMeQuery.isSaved());

        Assert.assertTrue(savedQuery.isSaved());
    }

    /**
     * Test of setSaved method, of class BacklogQuery.
     */
    @Test
    public void testSetSaved() {
        query.setSaved(true);
        Assert.assertTrue(query.isSaved());
        query.setSaved(false);
        Assert.assertFalse(query.isSaved());
    }

    /**
     * Test of getIssues method, of class BacklogQuery.
     */
    @Test
    public void testGetIssues_0args() {
    }

    /**
     * Test of getQueryParam method, of class BacklogQuery.
     */
    @Test
    public void testGetQueryParam() {
        assertEquals(null, query.getQueryParam());
        assertEquals("keyword=test&categoryId[]=1", savedQuery.getQueryParam());
    }

    /**
     * Test of setQueryParam method, of class BacklogQuery.
     */
    @Test
    public void testSetQueryParam() {
        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.assignerIds(Arrays.asList(1L, 2L))
                .attachment(true)
                .sharedFile(false)
                .categoryIds(Arrays.asList(3L));
        String paramString = new BacklogHttpClientImpl().getParamsString(true, params);
        query.setQueryParam(params);
        assertEquals(paramString, query.getQueryParam());
    }

    /**
     * Test of getKeyword method, of class BacklogQuery.
     */
    @Test
    public void testGetKeyword() {
        assertEquals("", query.getKeyword());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.keyword("test");
        query.setQueryParam(params);
        assertEquals("test", query.getKeyword());

        savedQuery = new BacklogQuery(repository, "test", "keyword=test", 100);
        assertEquals("test", savedQuery.getKeyword());
    }

    /**
     * Test of getStatus method, of class BacklogQuery.
     */
    @Test
    public void testGetStatus() {
    }

    /**
     * Test of getStatusIds method, of class BacklogQuery.
     */
    @Test
    public void testGetStatusIds() {
        assertEquals(Collections.emptyList(), query.getStatusIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.statuses(Arrays.asList(StatusType.Closed, StatusType.InProgress));
        query.setQueryParam(params);
        ArrayList<Long> types = new ArrayList<>();
        types.add((long) StatusType.Closed.getIntValue());
        types.add((long) StatusType.InProgress.getIntValue());
        assertEquals(types, query.getStatusIds());

        savedQuery = new BacklogQuery(repository, "test", "statusId[]=1&statusId[]=2", 100);
        types.clear();
        types.add(1L);
        types.add(2L);
        assertEquals(types, savedQuery.getStatusIds());
    }

    /**
     * Test of getPriorityIds method, of class BacklogQuery.
     */
    @Test
    public void testGetPriorityIds() {
        assertEquals(Collections.emptyList(), query.getPriorityIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.priorities(Arrays.asList(PriorityType.High, PriorityType.Normal));
        query.setQueryParam(params);
        ArrayList<Long> types = new ArrayList<>();
        types.add((long) PriorityType.High.getIntValue());
        types.add((long) PriorityType.Normal.getIntValue());
        assertEquals(types, query.getPriorityIds());

        savedQuery = new BacklogQuery(repository, "test", "priorityId[]=1", 100);
        types.clear();
        types.add(1L);
        assertEquals(types, savedQuery.getPriorityIds());
    }

    /**
     * Test of getPriorities method, of class BacklogQuery.
     */
    @Test
    public void testGetPriorities() {
    }

    /**
     * Test of getCategoryIds method, of class BacklogQuery.
     */
    @Test
    public void testGetCategoryIds() {
        assertEquals(Collections.emptyList(), query.getCategoryIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.categoryIds(Arrays.asList(1L, 2L, 3L));
        query.setQueryParam(params);
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, query.getCategoryIds());

        savedQuery = new BacklogQuery(repository, "test", "categoryId[]=1&categoryId[]=2&categoryId[]=3", 100);
        ids.clear();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, savedQuery.getCategoryIds());
    }

    /**
     * Test of getAssigneeIds method, of class BacklogQuery.
     */
    @Test
    public void testGetAssigneeIds() {
        assertEquals(Collections.emptyList(), query.getAssigneeIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.assignerIds(Arrays.asList(4L, 5L));
        query.setQueryParam(params);
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(4L);
        ids.add(5L);
        assertEquals(ids, query.getAssigneeIds());

        savedQuery = new BacklogQuery(repository, "test", "assigneeId[]=1&assigneeId[]=2&assigneeId[]=3", 100);
        ids.clear();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, savedQuery.getAssigneeIds());
    }

    /**
     * Test of getCreatedUserIds method, of class BacklogQuery.
     */
    @Test
    public void testGetCreatedUserIds() {
        assertEquals(Collections.emptyList(), query.getCreatedUserIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.createdUserIds(Arrays.asList(1L, 2L, 3L));
        query.setQueryParam(params);
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, query.getCreatedUserIds());

        savedQuery = new BacklogQuery(repository, "test", "createdUserId[]=1&createdUserId[]=2&createdUserId[]=3", 100);
        ids.clear();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, savedQuery.getCreatedUserIds());
    }

    /**
     * Test of getVersionIds method, of class BacklogQuery.
     */
    @Test
    public void testGetVersionIds() {
        assertEquals(Collections.emptyList(), query.getVersionIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.versionIds(Arrays.asList(1L, 2L, 3L));
        query.setQueryParam(params);
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, query.getVersionIds());

        savedQuery = new BacklogQuery(repository, "test", "versionId[]=1&versionId[]=2&versionId[]=3", 100);
        ids.clear();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, savedQuery.getVersionIds());
    }

    /**
     * Test of getMilestoneIds method, of class BacklogQuery.
     */
    @Test
    public void testGetMilestoneIds() {
        assertEquals(Collections.emptyList(), query.getMilestoneIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.milestoneIds(Arrays.asList(1L, 2L, 3L));
        query.setQueryParam(params);
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, query.getMilestoneIds());

        savedQuery = new BacklogQuery(repository, "test", "milestoneId[]=1&milestoneId[]=2&milestoneId[]=3", 100);
        ids.clear();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, savedQuery.getMilestoneIds());
    }

    /**
     * Test of getResolutionIds method, of class BacklogQuery.
     */
    @Test
    public void testGetResolutionIds() {
        assertEquals(Collections.emptyList(), query.getResolutionIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.resolutions(Arrays.asList(ResolutionType.CannotReproduce, ResolutionType.WontFix, ResolutionType.Duplication));
        query.setQueryParam(params);
        ArrayList<Long> types = new ArrayList<>();
        types.add((long) ResolutionType.CannotReproduce.getIntValue());
        types.add((long) ResolutionType.WontFix.getIntValue());
        types.add((long) ResolutionType.Duplication.getIntValue());
        assertEquals(types, query.getResolutionIds());

        savedQuery = new BacklogQuery(repository, "test", "resolutionId[]=1&resolutionId[]=2&resolutionId[]=3", 100);
        types.clear();
        types.add(1L);
        types.add(2L);
        types.add(3L);
        assertEquals(types, savedQuery.getResolutionIds());
    }

    /**
     * Test of getCreatedSince method, of class BacklogQuery.
     */
    @Test
    public void testGetCreatedSince() {
        assertEquals("", query.getCreatedSince());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.createdSince("2014-11-11");
        query.setQueryParam(params);
        assertEquals("2014-11-11", query.getCreatedSince());

        savedQuery = new BacklogQuery(repository, "test", "createdSince=2014-11-11", 100);
        assertEquals("2014-11-11", savedQuery.getCreatedSince());
    }

    /**
     * Test of getCreatedUntil method, of class BacklogQuery.
     */
    @Test
    public void testGetCreatedUntil() {
        assertEquals("", query.getCreatedUntil());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.createdUntil("2014-11-12");
        query.setQueryParam(params);
        assertEquals("2014-11-12", query.getCreatedUntil());

        savedQuery = new BacklogQuery(repository, "test", "createdUntil=2014-11-12", 100);
        assertEquals("2014-11-12", savedQuery.getCreatedUntil());
    }

    /**
     * Test of getUpdatedSince method, of class BacklogQuery.
     */
    @Test
    public void testGetUpdatedSince() {
        assertEquals("", query.getUpdatedSince());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.updatedSince("2014-11-13");
        query.setQueryParam(params);
        assertEquals("2014-11-13", query.getUpdatedSince());

        savedQuery = new BacklogQuery(repository, "test", "updatedSince=2014-11-13", 100);
        assertEquals("2014-11-13", savedQuery.getUpdatedSince());
    }

    /**
     * Test of getUpdatedUntil method, of class BacklogQuery.
     */
    @Test
    public void testGetUpdatedUntil() {
        assertEquals("", query.getUpdatedUntil());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.updatedUntil("2014-11-14");
        query.setQueryParam(params);
        assertEquals("2014-11-14", query.getUpdatedUntil());

        savedQuery = new BacklogQuery(repository, "test", "updatedUntil=2014-11-14", 100);
        assertEquals("2014-11-14", savedQuery.getUpdatedUntil());
    }

    /**
     * Test of getStartDateSince method, of class BacklogQuery.
     */
    @Test
    public void testGetStartDateSince() {
        assertEquals("", query.getStartDateSince());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.startDateSince("2014-11-11");
        query.setQueryParam(params);
        assertEquals("2014-11-11", query.getStartDateSince());

        savedQuery = new BacklogQuery(repository, "test", "startDateSince=2014-11-11", 100);
        assertEquals("2014-11-11", savedQuery.getStartDateSince());
    }

    /**
     * Test of getStartDateUntil method, of class BacklogQuery.
     */
    @Test
    public void testGetStartDateUntil() {
        assertEquals("", query.getStartDateUntil());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.startDateUntil("2014-11-12");
        query.setQueryParam(params);
        assertEquals("2014-11-12", query.getStartDateUntil());

        savedQuery = new BacklogQuery(repository, "test", "startDateUntil=2014-11-12", 100);
        assertEquals("2014-11-12", savedQuery.getStartDateUntil());
    }

    /**
     * Test of getDueDateSince method, of class BacklogQuery.
     */
    @Test
    public void testGetDueDateSince() {
        assertEquals("", query.getDueDateSince());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.dueDateSince("2014-11-11");
        query.setQueryParam(params);
        assertEquals("2014-11-11", query.getDueDateSince());

        savedQuery = new BacklogQuery(repository, "test", "dueDateSince=2014-11-11", 100);
        assertEquals("2014-11-11", savedQuery.getDueDateSince());
    }

    /**
     * Test of getDueDateUntil method, of class BacklogQuery.
     */
    @Test
    public void testGetDueDateUntil() {
        assertEquals("", query.getDueDateUntil());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.dueDateUntil("2014-12-12");
        query.setQueryParam(params);
        assertEquals("2014-12-12", query.getDueDateUntil());

        savedQuery = new BacklogQuery(repository, "test", "startDateUntil=2014-11-11", 100);
        assertEquals("2014-11-11", savedQuery.getStartDateUntil());
    }

    /**
     * Test of isAttachment method, of class BacklogQuery.
     */
    @Test
    public void testIsAttachment() {
        assertEquals(false, query.isAttachment());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.attachment(true);
        query.setQueryParam(params);
        assertEquals(true, query.isAttachment());

        params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.attachment(false);
        query.setQueryParam(params);
        assertEquals(false, query.isAttachment());

        savedQuery = new BacklogQuery(repository, "test", "attachment=true", 100);
        assertEquals(true, savedQuery.isAttachment());
        savedQuery = new BacklogQuery(repository, "test", "attachment=false", 100);
        assertEquals(false, savedQuery.isAttachment());
    }

    /**
     * Test of isSharedFile method, of class BacklogQuery.
     */
    @Test
    public void testIsSharedFile() {
        assertEquals(false, query.isSharedFile());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.sharedFile(true);
        query.setQueryParam(params);
        assertEquals(true, query.isSharedFile());

        params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.sharedFile(false);
        query.setQueryParam(params);
        assertEquals(false, query.isSharedFile());

        savedQuery = new BacklogQuery(repository, "test", "sharedFile=true", 100);
        assertEquals(true, savedQuery.isSharedFile());
        savedQuery = new BacklogQuery(repository, "test", "sharedFile=false", 100);
        assertEquals(false, savedQuery.isSharedFile());
    }

    /**
     * Test of getResolutions method, of class BacklogQuery.
     */
    @Test
    public void testGetResolutions() {
    }

    /**
     * Test of getIssueTypeIds method, of class BacklogQuery.
     */
    @Test
    public void testGetIssueTypeIds() {
        assertEquals(Collections.emptyList(), query.getIssueTypeIds());

        GetIssuesParams params = new GetIssuesParams(Collections.singletonList(1L));
        params = params.issueTypeIds(Arrays.asList(1L, 2L, 3L));
        query.setQueryParam(params);
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, query.getIssueTypeIds());

        savedQuery = new BacklogQuery(repository, "test", "issueTypeId[]=1&issueTypeId[]=2&issueTypeId[]=3", 100);
        ids.clear();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        assertEquals(ids, savedQuery.getIssueTypeIds());
    }

    /**
     * Test of getIssues method, of class BacklogQuery.
     */
    @Test
    public void testGetIssues_GetIssuesParams() {
    }

    /**
     * Test of getIssue method, of class BacklogQuery.
     */
    @Test
    public void testGetIssue() {
    }

    /**
     * Test of setName method, of class BacklogQuery.
     */
    @Test
    public void testSetName() {
        query.setName("test");
        assertEquals("test", query.getDisplayName());
    }

    /**
     * Test of canRemove method, of class BacklogQuery.
     */
    @Test
    public void testCanRemove() {
        Assert.assertTrue(query.canRemove());
        Assert.assertFalse(assignedToMeQuery.canRemove());
        Assert.assertFalse(createdByMeQuery.canRemove());
    }

    /**
     * Test of remove method, of class BacklogQuery.
     */
    @Test
    public void testRemove() {
    }

    /**
     * Test of canRename method, of class BacklogQuery.
     */
    @Test
    public void testCanRename() {
        Assert.assertTrue(query.canRename());
        Assert.assertFalse(assignedToMeQuery.canRename());
        Assert.assertFalse(createdByMeQuery.canRename());
    }

    /**
     * Test of rename method, of class BacklogQuery.
     */
    @Test
    public void testRename() {
    }

    /**
     * Test of getQueryController method, of class BacklogQuery.
     */
    @Test
    public void testGetQueryController() {
        BacklogQueryController controller1 = query.getQueryController();
        BacklogQueryController controller2 = query.getQueryController();
        Assert.assertNotNull(controller1);
        Assert.assertNotNull(controller2);
        assertEquals(controller1, controller2);
    }

    /**
     * Test of refresh method, of class BacklogQuery.
     */
    @Test
    public void testRefresh() {
    }

    /**
     * Test of getColumnDescriptors method, of class BacklogQuery.
     */
    @Test
    public void testGetColumnDescriptors() {
    }

}
