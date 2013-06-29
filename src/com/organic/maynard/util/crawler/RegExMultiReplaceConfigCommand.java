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

import com.organic.maynard.io.*;
import com.organic.maynard.util.*;
import com.organic.maynard.util.string.StringTools;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.perl.MalformedPerl5PatternException;

public class RegExMultiReplaceConfigCommand extends Command {
	public RegExMultiReplace app = null;
	
	// The Constructors
	public RegExMultiReplaceConfigCommand(String name, RegExMultiReplace app) {
		super(name);
		this.app = app;
	}

	public synchronized void execute(ArrayList signature) {
		String variableName = (String) signature.get(1);
		
		if (variableName.equals(RegExMultiReplace.COMMAND_START_PATH)) {
			if (!app.blockSetStartingPath) {
				app.startingPath = (String) signature.get(2);
			}
		} else if (variableName.equals(RegExMultiReplace.COMMAND_REGEX)) {
			String regex = (String) signature.get(2);
			
			String[] newArray;
			if (app.regexes != null) {
				newArray = new String[app.regexes.length + 1];
				System.arraycopy(app.regexes,0,newArray,0,app.regexes.length);
				newArray[app.regexes.length] = regex;
			} else {
				newArray = new String[1];
				newArray[0] = regex;
			}
			app.regexes = newArray;
		} else if (variableName.equals(RegExMultiReplace.COMMAND_LINE_ENDING)) {
			String lineEndingType = (String) signature.get(2);
			if (lineEndingType.equals(RegExMultiReplace.PLATFORM_MAC)) {
				app.lineEnding = FileTools.LINE_ENDING_MAC;
			} else if (lineEndingType.equals(RegExMultiReplace.PLATFORM_WIN)) {
				app.lineEnding = FileTools.LINE_ENDING_WIN;
			} else if (lineEndingType.equals(RegExMultiReplace.PLATFORM_UNIX)) {
				app.lineEnding = FileTools.LINE_ENDING_UNIX;
			}
		} else if (variableName.equals(RegExMultiReplace.COMMAND_FILE_EXTENSION)) {
			String fileExtension = (String) signature.get(2);
			
			String[] newArray;
			if (app.fileExtensions != null) {
				newArray = new String[app.fileExtensions.length + 1];
				System.arraycopy(app.fileExtensions,0,newArray,0,app.fileExtensions.length);
				newArray[app.fileExtensions.length] = fileExtension;
			} else {
				newArray = new String[1];
				newArray[0] = fileExtension;
			}
			app.fileExtensions = newArray;
		}
	}
}