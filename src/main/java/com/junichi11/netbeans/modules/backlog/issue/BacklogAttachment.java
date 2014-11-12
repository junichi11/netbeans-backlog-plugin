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

import com.nulabinc.backlog4j.Attachment;
import com.nulabinc.backlog4j.AttachmentData;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Date;
import org.netbeans.modules.bugtracking.commons.AttachmentsPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public class BacklogAttachment extends AttachmentsPanel.AbstractAttachment {

    private final Attachment attachment;
    private final BacklogIssue issue;

    public BacklogAttachment(Attachment attachment, BacklogIssue issue) {
        this.attachment = attachment;
        this.issue = issue;
    }

    @Override
    protected void getAttachementData(OutputStream outputStream) {
        if (issue == null) {
            return;
        }
        AttachmentData attachmentData = issue.getAttachmentData(attachment.getId());
        if (attachmentData == null) {
            return;
        }

        try (InputStream inputStream = new BufferedInputStream(attachmentData.getContent())) {
            int bufferSize = 1024 * 4;
            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            try {
                outputStream.close();
            } catch (IOException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }
    }

    @Override
    protected String getContentType() {
        return URLConnection.guessContentTypeFromName(getFilename());
    }

    @Override
    public boolean isPatch() {
        return false;
    }

    @Override
    public String getDesc() {
        return attachment.getName();
    }

    @Override
    public String getFilename() {
        return attachment.getName();
    }

    @Override
    public Date getDate() {
        return attachment.getCreated();
    }

    @Override
    public String getAuthor() {
        return attachment.getCreatedUser().getName();
    }

    @Override
    public String getAuthorName() {
        return attachment.getCreatedUser().getName();
    }

    public Attachment getAttachment() {
        return attachment;
    }

}
