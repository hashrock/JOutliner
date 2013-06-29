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

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.io.GlobFilenameFilter;

import java.io.*;
import java.util.*;

public class TypeGlobFileFilter implements FileFilter {

	// Constants
	private static final String GLOB_SEPERATOR = ";";
	
	
	// Declare Fields
	private ArrayList globIncludeFilters = new ArrayList();
	private ArrayList globExcludeFilters = new ArrayList();


	// Constructors
	public TypeGlobFileFilter() {}
	
	public TypeGlobFileFilter(
		String typeGlobIncludeListString, 
		boolean ignoreCaseInclude,
		String typeGlobExcludeListString, 
		boolean ignoreCaseExclude
	) {
		// Create Include Globs
		StringTokenizer tok = new StringTokenizer(typeGlobIncludeListString, GLOB_SEPERATOR);
		while (tok.hasMoreElements()) {
			String glob = tok.nextToken();
			
			GlobFilenameFilter filter = null;
			
			if (ignoreCaseInclude) {
				filter = new GlobFilenameFilter(glob, GlobCompiler.CASE_INSENSITIVE_MASK);
			} else {
				filter = new GlobFilenameFilter(glob);
			}
			
			globIncludeFilters.add(filter);
		}

		// Create Exclude Globs
		tok = new StringTokenizer(typeGlobExcludeListString, GLOB_SEPERATOR);
		while (tok.hasMoreElements()) {
			String glob = tok.nextToken();
			
			GlobFilenameFilter filter = null;
			
			if (ignoreCaseExclude) {
				filter = new GlobFilenameFilter(glob, GlobCompiler.CASE_INSENSITIVE_MASK);
			} else {
				filter = new GlobFilenameFilter(glob);
			}
			
			globExcludeFilters.add(filter);
		}
	}
	
	
	// FileFilter Interface
	public boolean isValid(File file) {
		if (globIncludeFilters.size() > 0) {
			boolean matchFound = false;
			for (int i = 0; i < globIncludeFilters.size(); i++) {
				GlobFilenameFilter filter = (GlobFilenameFilter) globIncludeFilters.get(i);
				if (filter.accept(file)) {
					matchFound = true;
					break;
				}
			}
			
			if (!matchFound) {
				return false;
			}
		}

		for (int i = 0; i < globExcludeFilters.size(); i++) {
			GlobFilenameFilter filter = (GlobFilenameFilter) globExcludeFilters.get(i);
			if (filter.accept(file)) {
				return false;
			}
		}
		
		return true;
	}
}