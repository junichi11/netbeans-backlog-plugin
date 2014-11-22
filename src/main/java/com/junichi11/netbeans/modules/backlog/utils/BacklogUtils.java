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
package com.junichi11.netbeans.modules.backlog.utils;

import com.nulabinc.backlog4j.Project;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.junichi11.netbeans.modules.backlog.repository.BacklogRepository;

/**
 *
 * @author junichi11
 */
public final class BacklogUtils {

    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy/MM/dd"; // NOI18N
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss"; // NOI18N
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
    public static final DateFormat DEFAULT_DATE_FORMAT_WITH_TIME = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);

    private BacklogUtils() {
    }

    /**
     * Check whether date string format is "yyyy/MM/dd".
     *
     * @param date date string
     * @return {@code true} format is "yyyy/MM/dd", otherwise {@code false}
     */
    public static boolean isDateFormat(String date) {
        if (date == null) {
            return false;
        }
        DEFAULT_DATE_FORMAT.setLenient(false);
        boolean isDateFormat;
        try {
            Date toDate = DEFAULT_DATE_FORMAT.parse(date);
            String toString = DEFAULT_DATE_FORMAT.format(toDate);
            isDateFormat = date.equals(toString);
        } catch (ParseException ex) {
            isDateFormat = false;
        }
        return isDateFormat;
    }

    /**
     * Replace to date format for API. From "/" to "-". e.g. 2014/10/11 ->
     * 2014-10-11
     *
     * @param date date string
     * @return formatted api date string
     */
    public static String toApiDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return ""; // NOI18N
        }
        return date.replaceAll("/", "-"); // NOI18N
    }

    public static boolean isChartEnabled(BacklogRepository repository) {
        Project project = repository.getProject();
        boolean isChartEnabled = project == null ? false : project.isChartEnabled();
        return isChartEnabled;
    }

    public static boolean isSubtaskingEnabled(BacklogRepository repository) {
        Project project = repository.getProject();
        boolean isSubtaskingEnabled = project == null ? false : project.isSubtaskingEnabled();
        return isSubtaskingEnabled;
    }
}
