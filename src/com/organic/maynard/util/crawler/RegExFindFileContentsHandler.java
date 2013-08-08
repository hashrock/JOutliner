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

import com.organic.maynard.util.string.StringSplitter;
import java.io.File;
import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternMatcherInput;

public class RegExFindFileContentsHandler extends FileContentsInspector {

	private int totalNumberOfMatches = 0;

	private String regex = null;
	private Perl5Util util = new Perl5Util();
	private PatternMatcherInput input = null;
	private MatchResult result = null;
	
	
	// Constructors
	public RegExFindFileContentsHandler(String regex, String lineEnding) {
		super(lineEnding);
		setRegEx(regex);
	}


	// Accessors
	public String getRegEx() {return regex;}
	public void setRegEx(String regex) {this.regex = regex;}
	
	public int getTotalNumberOfMatches() {return totalNumberOfMatches;}
	public void setTotalNumberOfMatches(int totalNumberOfMatches) {this.totalNumberOfMatches = totalNumberOfMatches;}
	
	// Overridden Methods
	protected void inspectContents(File file, String contents) {
		StringBuffer buf = new StringBuffer();
		
		// Split it into lines
		StringSplitter splitter = new StringSplitter(contents, getLineEnding());
		
		// Scan each line
		int totalMatchesForThisFile = 0;
		int lineCount = 1;
		while (splitter.hasMoreElements()) {
			String line = (String) splitter.nextElement();
			
			input = new PatternMatcherInput(line);
			try {
				while(util.match(regex, input)) {
					result = util.getMatch();  
					totalMatchesForThisFile++;
					totalNumberOfMatches++;
					buf.append("  line: " + lineCount + " position: " + result.beginOffset(0) + getLineEnding());
				}
			} catch (MalformedPerl5PatternException e) {
				System.out.println("MalformedPerl5PatternException: " + e.getMessage());
				System.out.println("Valid expression: [m]/pattern/[i][m][s][x]");
				return;
			}
			
			lineCount++;
		}

		// Output the results
		if (totalMatchesForThisFile > 0) {
			if (totalMatchesForThisFile == 1) {
				System.out.println("Found " + totalMatchesForThisFile + " match in file: " + file.getPath());
			} else {
				System.out.println("Found " + totalMatchesForThisFile + " matches in file: " + file.getPath());			
			}
			System.out.print(buf.toString());
			System.out.println("");
		}
	}	
}