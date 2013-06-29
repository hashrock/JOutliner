/*
 * Copyright (c) 2001, Maynard Demmon
 * Copyright (c) 2001, Organic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 * 
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *
 *  - Neither the name of Organic nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.util.crawler;

import java.io.*;
import java.util.*;

public class FileExtensionFilter implements FileFilter {

	// Constants
	public static final String EXTENSION_SEPERATOR = ".";
	public static final String WILDCARD_CHARACTER = "*";


	// Declare Fields
	private ArrayList extensionList = null;
	private boolean anyExtensionWillDo = false;


	// Constructors
	public FileExtensionFilter() {}
	
	public FileExtensionFilter(String extension) {
		String[] temp = {extension};
		setExtensionList(new ArrayList(Arrays.asList(temp)));
	}
	
	public FileExtensionFilter(String[] extensionList) {
		setExtensionList(new ArrayList(Arrays.asList(extensionList)));
	}


	// Accessors
	public ArrayList getExtensionList() {return extensionList;}
	public void setExtensionList(ArrayList extensionList) {
		this.extensionList = extensionList;
		
		// Check extensionList for wildcard character.
		anyExtensionWillDo = false;
		for (int i = 0; i < extensionList.size(); i++) {
			String extension = (String) extensionList.get(i);
			if (extension.equals(WILDCARD_CHARACTER)) {
				anyExtensionWillDo = true;
				break;
			}
		}		
	}
	
	
	// FileFilter Interface
	public boolean isValid(File file) {
	
		// Get the extension of the file
		String filename = file.getName();
		int seperatorIndex = filename.lastIndexOf(EXTENSION_SEPERATOR);
		String extension = "";
		if (seperatorIndex > 0) {
			extension = filename.substring(seperatorIndex + 1,filename.length());
		}
		
		// Check the extension against the list of valid extensions. If the list is null
		// then we assume that all extensions are invalid.
		if (extensionList == null) {
			return false;
		}
		
		// Check for special extension "*" which indicates any extension.
		if (anyExtensionWillDo) {
			return true;
		}
		
		for (int i = 0; i < extensionList.size(); i++) {
			String validExtension = (String) extensionList.get(i);
			if (validExtension.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		
		return false;
	}
}