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
import com.organic.maynard.io.*;
import java.util.*;

/**
 * Handles the contents of each file processed by a crawler.
 */
public class FileContentsHandler implements FileHandler {
	// Constants
	public static final int MODE_UNKNOWN = -1;
	public static final int MODE_BIG_CHUNK = 1;
	public static final int MODE_ARRAYS = 2;
	
	// Declare Fields
	private String lineEnding = null;
	private boolean lineEndingAtEnd = true;
	private int processMode = MODE_UNKNOWN;
	private String openEncoding = "UTF-8";
	private String saveEncoding = "UTF-8";
	
	
	// Constructors
	public FileContentsHandler(String lineEnding) {
		this(lineEnding, true);
	}
	
	public FileContentsHandler(String lineEnding, boolean lineEndingAtEnd) {
		this(lineEnding, lineEndingAtEnd, MODE_BIG_CHUNK, "UTF-8", "UTF-8");
	}
	
	public FileContentsHandler(
		String lineEnding, 
		boolean lineEndingAtEnd, 
		int processMode, 
		String openEncoding, 
		String saveEncoding
	) {
		setLineEnding(lineEnding);
		setLineEndingAtEnd(lineEndingAtEnd);
		setProcessMode(processMode);
		setOpenEncoding(openEncoding);
		setSaveEncoding(saveEncoding);
	}
	
	
	// Accessors
	public String getLineEnding() {
		return lineEnding;
	}
	
	public void setLineEnding(String lineEnding) {
		this.lineEnding = lineEnding;
	}
	
	public boolean getLineEndingAtEnd() {
		return lineEndingAtEnd;
	}
	
	public void setLineEndingAtEnd(boolean lineEndingAtEnd) {
		this.lineEndingAtEnd = lineEndingAtEnd;
	}
	
	public int getProcessMode() {
		return processMode;
	}
	
	public void setProcessMode(int processMode) {
		this.processMode = processMode;
	}
	
	public String getOpenEncoding() {
		return openEncoding;
	}
	
	public void setOpenEncoding(String openEncoding) {
		this.openEncoding = openEncoding;
	}
	
	public String getSaveEncoding() {
		return saveEncoding;
	}
	
	public void setSaveEncoding(String saveEncoding) {
		this.saveEncoding = saveEncoding;
	}
	
	
	// FileHandler Interface
	public void handleFile(File file) {
		if (getProcessMode() == MODE_BIG_CHUNK) {
			String contents = FileTools.readFileToString(file, getOpenEncoding(), lineEnding);
			contents = processContents(file, contents);
			
			// If contents are null then don't write anything out.
			if (contents == null) {
				return;
			}
			
			// Clean last line ending if neccessary.
			int lineEndingLength = lineEnding.length();
			int contentsLength = contents.length();
			if (!lineEndingAtEnd && (contentsLength >= lineEndingLength)) {
				contents = contents.substring(0, contentsLength - lineEndingLength);
			}
			
			try {
				FileTools.dumpStringToFile(file, contents, getSaveEncoding());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else if (getProcessMode() == MODE_ARRAYS) {
			ArrayList lines = new ArrayList();
			ArrayList lineEndings = new ArrayList();
			FileTools.readFileToArrayOfLines(file, getOpenEncoding(), lines, lineEndings);
			
			boolean modified = processContents(file, lines, lineEndings);
			
			if (modified) {
				FileTools.dumpArrayOfLinesToFile(file, getSaveEncoding(), lines, lineEndings);
			}
		} else {
			System.out.println("Error: Unknown process mode.");
		}
	}
	
	protected String processContents(File file, String contents) {
		System.out.println("Contents: " + contents);
		return contents;
	}
	
	protected boolean processContents(File file, ArrayList lines, ArrayList lineEndings) {
		return false;
	}
}