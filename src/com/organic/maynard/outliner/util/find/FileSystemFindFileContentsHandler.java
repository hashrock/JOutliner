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
package com.organic.maynard.outliner.util.find;

import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.util.crawler.*;

import java.io.*;
import java.util.*;

import com.organic.maynard.io.*;
import com.organic.maynard.util.string.*;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.MatchResult;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.7 $, $Date: 2002/07/16 21:25:30 $
 */

public class FileSystemFindFileContentsHandler extends FileContentsInspector {

	private String query = null;
	private Perl5Util util = new Perl5Util();
	private PatternMatcherInput input = null;
	private MatchResult result = null;

	private FindReplaceResultsModel results = null;
	private boolean isRegexp;
	private boolean ignoreCase;

	// Constructors
	public FileSystemFindFileContentsHandler(String query, FindReplaceResultsModel results, boolean isRegexp, boolean ignoreCase, String lineEnding) {
		super(lineEnding, FileContentsHandler.MODE_ARRAYS, Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
		//super(lineEnding);
		this.results = results;
		this.isRegexp = isRegexp;
		this.ignoreCase = ignoreCase;

		if (!isRegexp && ignoreCase) {
			this.query = query.toLowerCase();
		} else {
			this.query = query;
		}
	}

	// Overridden Methods
	protected void inspectContents(File file, ArrayList lines, ArrayList lineEndings) {
		for (int lineCount = 1; lineCount <= lines.size(); lineCount++) {
			String line = (String) lines.get(lineCount - 1);

			if (isRegexp) {
				input = new PatternMatcherInput(line);
				try {
					while(util.match(query, input)) {
						result = util.getMatch();
						results.addResult(new FindReplaceResult(file, lineCount, result.beginOffset(0), result.group(0), "", false));
					}
				} catch (MalformedPerl5PatternException e) {
					System.out.println("MalformedPerl5PatternException: " + e.getMessage());
					System.out.println("Valid expression: [m]/pattern/[i][m][s][x]");
					return;
				}
			} else {
				String searchLine = null;
				if (ignoreCase) {
					searchLine = line.toLowerCase();
				} else {
					searchLine = line;
				}

				int start = 0;
				int end = line.length();

				while (start < end) {
					start = searchLine.indexOf(query, start);
					if (start != -1) {
						String match = StringTools.substring(line, start, start + query.length());
						results.addResult(new FindReplaceResult(file, lineCount, start, match, "", false));
						start = start + query.length();
					} else {
						start = end;
					}
				}
			}
		}
	}
}