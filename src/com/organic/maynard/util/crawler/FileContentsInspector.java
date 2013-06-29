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

import java.util.*;
import java.io.*;
import com.organic.maynard.io.*;

public class FileContentsInspector implements FileHandler {
	// Constants
	public static final int MODE_UNKNOWN = -1;
	public static final int MODE_BIG_CHUNK = 1;
	public static final int MODE_ARRAYS = 2;

	// Declare Fields
	private int processMode = MODE_UNKNOWN;
	private String openEncoding = "UTF-8";


	// Declare Fields
	private String lineEnding = null;
	
	
	// Constructors
	public FileContentsInspector(String lineEnding) {
		this(lineEnding, MODE_BIG_CHUNK, "UTF-8");
	}

	public FileContentsInspector(
		String lineEnding, 
		int processMode, 
		String openEncoding
	) {
		setLineEnding(lineEnding);
		setProcessMode(processMode);
		setOpenEncoding(openEncoding);
	}

	
	// Accessors
	public String getLineEnding() {return lineEnding;}
	public void setLineEnding(String lineEnding) {this.lineEnding = lineEnding;}

	public int getProcessMode() {return processMode;}
	public void setProcessMode(int processMode) {this.processMode = processMode;}

	public String getOpenEncoding() {return openEncoding;}
	public void setOpenEncoding(String openEncoding) {this.openEncoding = openEncoding;}

	
	// FileHandler Interface
	public void handleFile(File file) {
		if (getProcessMode() == MODE_BIG_CHUNK) {
			inspectContents(file, FileTools.readFileToString(file, lineEnding));
		} else if (getProcessMode() == MODE_ARRAYS) {
			ArrayList lines = new ArrayList();
			ArrayList lineEndings = new ArrayList();
			FileTools.readFileToArrayOfLines(file, getOpenEncoding(), lines, lineEndings);
			inspectContents(file, lines, lineEndings);
		} else {
			System.out.println("Error: Unknown process mode.");
		}
	}
	
	protected void inspectContents(File file, String contents) {}

	protected void inspectContents(File file, ArrayList lines, ArrayList lineEndings) {}
}