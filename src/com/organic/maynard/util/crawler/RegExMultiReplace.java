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

public class RegExMultiReplace {
	
	// Constants
	public static final String COMMAND_PARSER_SEPARATOR = "\t";
	public static final String COMMAND_SET = "set";
	
	public static final String COMMAND_START_PATH = "start_path";
	public static final String COMMAND_REGEX = "regex";
	public static final String COMMAND_FILE_EXTENSION = "file_ext";
	public static final String COMMAND_LINE_ENDING = "line_ending";

	public static final String PLATFORM_MAC = "mac";
	public static final String PLATFORM_WIN = "win";
	public static final String PLATFORM_UNIX = "unix";


	// Declare Fields
	public String[] regexes;
	public String[] fileExtensions;
	public String startingPath = null;
	public String lineEnding = FileTools.LINE_ENDING_WIN;
	
	public boolean blockSetStartingPath = false;
	
	
	// Constructors
	public RegExMultiReplace(String args[]) {
		
		// Get argument for configFile
		String configPath = null;
		try {configPath = args[0];} catch (ArrayIndexOutOfBoundsException e) {}
		
		if (configPath != null) {
		
			// Set startpath from command line if it was provided
			String startPathFromArgs = null;
			try {startPathFromArgs = args[1];} catch (ArrayIndexOutOfBoundsException e) {}
			
			if (startPathFromArgs != null) {
				this.startingPath = startPathFromArgs;
				// Block any further configuration of the start path so the config file 
				// doesn't change it.
				blockSetStartingPath = true;
			}

			// Get input from a configFile
			CommandParser parser = new CommandParser(COMMAND_PARSER_SEPARATOR);
			parser.addCommand(new RegExMultiReplaceConfigCommand(COMMAND_SET,this));
	
			// Load things from the config file
			CommandQueue commandQueue = new CommandQueue(30);
			commandQueue.loadFromFile(configPath);
			
			while (commandQueue.getSize() > 0) {
				try {
					parser.parse((String) commandQueue.getNext());
				} catch (UnknownCommandException uce) {
					System.out.println("Unknown Command");
				}
			}
		} else {
			// Get input from the console
			startingPath = ConsoleTools.getNonEmptyInput("Enter starting path: ");
			
			regexes = ConsoleTools.getSeriesOfInputs("Enter regular expression: ");
			while (regexes.length <= 0) {
				regexes = ConsoleTools.getSeriesOfInputs("Enter regular expression: ");
			}
			
			fileExtensions = ConsoleTools.getSeriesOfInputs("Enter file extension to match: ");
			while (fileExtensions.length <= 0) {
				fileExtensions = ConsoleTools.getSeriesOfInputs("Enter file extension to match: ");
			}
			System.out.println("");
		}
		
		// Setup the Crawler
		DirectoryCrawler crawler = new DirectoryCrawler();
		crawler.setFileHandler(new RegExMultiReplacementFileContentsHandler(regexes,lineEnding));
		crawler.setFileFilter(new FileExtensionFilter(fileExtensions));
		
		// Do the Crawl
		System.out.println("STARTING...");
		crawler.crawl(startingPath);
		System.out.println("DONE");
	}

	
	public static void main(String args[]) {
		RegExMultiReplace sr = new RegExMultiReplace(args);
	}
}