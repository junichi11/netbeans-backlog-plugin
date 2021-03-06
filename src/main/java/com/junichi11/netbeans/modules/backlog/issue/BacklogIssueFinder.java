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
package com.junichi11.netbeans.modules.backlog.issue;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.bugtracking.spi.IssueFinder;

/**
 *
 * @author junichi11
 */
public class BacklogIssueFinder implements IssueFinder {

    private final Pattern issuePattern;
    private static final int[] EMPTY_INT_ARRAY = new int[0];

    public BacklogIssueFinder(String projectKey) {
        if (projectKey != null) {
            issuePattern = Pattern.compile(projectKey + "-\\d+"); // NOI18N
        } else {
            issuePattern = null;
        }
    }

    @Override
    public int[] getIssueSpans(CharSequence text) {
        if (issuePattern == null) {
            return EMPTY_INT_ARRAY;
        }
        Matcher matcher = issuePattern.matcher(text);
        int startPosition = 0;
        int textLength = text.length();
        ArrayList<IssueSpan> issueSpans = new ArrayList<>();
        // find
        while (startPosition < textLength) {
            if (!matcher.find(startPosition)) {
                break;
            }
            startPosition = matcher.end();
            issueSpans.add(new IssueSpan(matcher.start(), matcher.end()));
        }

        // to int array
        if (!issueSpans.isEmpty()) {
            int[] spans = new int[issueSpans.size() * 2];
            for (int i = 0; i < issueSpans.size(); i++) {
                IssueSpan issueSpan = issueSpans.get(i);
                int j = 2 * i;
                spans[j] = issueSpan.getStart();
                spans[j + 1] = issueSpan.getEnd();
            }
            return spans;
        }
        return EMPTY_INT_ARRAY;
    }

    @Override
    public String getIssueId(String issueHyperlinkText) {
        int indexOfHyphen = issueHyperlinkText.indexOf("-"); // NOI18N
        if (indexOfHyphen != -1) {
            int startPosition = indexOfHyphen + 1;
            int endPosition = issueHyperlinkText.length();
            if (startPosition <= endPosition) {
                return issueHyperlinkText.substring(startPosition, endPosition);
            }
        }
        return ""; // NOI18N
    }

    private static final class IssueSpan {

        private final int start;
        private final int end;

        public IssueSpan(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
