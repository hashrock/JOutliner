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

import java.util.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.9 $, $Date: 2002/12/22 04:24:35 $
 */

public class FileSystemReplace implements JoeReturnCodes {
	
	private DirectoryCrawler crawler = null;
	
	public FileSystemReplace() {
		crawler = new DirectoryCrawler();
	}
	
	public int replace(
		FindReplaceResultsModel model, 
		FileFilter fileFilter,
		FileFilter dirFilter,
		String startingPath, 
		String query,
		String replacement,
		
		boolean isRegexp,
		boolean ignoreCase,
		boolean makeBackups,
		boolean includeSubDirectories
	) {
		// Setup the Crawler
		String lineEnd = PlatformCompatibility.platformToLineEnding(Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END).cur);
		
		crawler.setFileHandler(new FileSystemReplaceFileContentsHandler(query, replacement, model, isRegexp, ignoreCase, makeBackups, lineEnd));
		crawler.setFileFilter(fileFilter);
		crawler.setDirectoryFilter(dirFilter);
		crawler.setProgressMonitor(FindReplaceFrame.monitor);
		crawler.setVerbose(false);
		
		// Do the Crawl
		int status = crawler.crawl(startingPath);
		if (status == DirectoryCrawler.FAILURE) {
			return FAILURE;
		}
		
		// Cleanup so things get GC'd
		crawler.setFileHandler(null);
		crawler.setFileFilter(null);
		crawler.setDirectoryFilter(null);
		crawler.setProgressMonitor(null);
		
		return SUCCESS;
	}
}