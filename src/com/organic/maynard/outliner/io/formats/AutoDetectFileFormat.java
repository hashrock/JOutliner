/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.outliner.io.formats;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.io.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.Replace;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2004/01/30 00:12:42 $
 */

public class AutoDetectFileFormat extends AbstractFileFormat implements OpenFileFormat, JoeReturnCodes {
	
	// Constructors
	public AutoDetectFileFormat() {}
	
	
	// OpenFileFormat Interface
	public boolean supportsComments() {return true;}
	public boolean supportsEditability() {return true;}
	public boolean supportsMoveability() {return true;}
	public boolean supportsAttributes() {return true;}
	public boolean supportsDocumentAttributes() {return true;}
	
	public int open(JoeTree tree, DocumentInfo docInfo, InputStream stream) {
		FileFormatManager manager = Outliner.fileFormatManager;
		
		String extension = getExtension(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH));
		OpenFileFormat format = null;
		String format_name = null;
		
		if (PropertyContainerUtil.getPropertyAsBoolean(docInfo, DocumentInfo.KEY_IMPORTED)) {
			format_name = manager.getImportFileFormatNameForExtension(extension);
			format = manager.getImportFormat(format_name);
		} else {
			format_name = manager.getOpenFileFormatNameForExtension(extension);
			format = manager.getOpenFormat(format_name);
		}
		
		System.out.println("EXTENSION: " + extension);
		System.out.println("FORMAT_NAME: " + format_name);
		PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT, format.getName());
		
		return format.open(tree, docInfo, stream);
	}
	
	private static String getExtension(String filename) {
		int index = filename.lastIndexOf(Preferences.EXTENSION_SEPARATOR);
		if (index == -1) {
			return null;
		} else {
			return filename.substring(index + 1, filename.length());
		}
	}
}