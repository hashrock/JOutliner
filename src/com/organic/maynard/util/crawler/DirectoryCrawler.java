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

import com.organic.maynard.swing.ProgressMonitor;
import java.util.*;
import java.io.*;

public class DirectoryCrawler {

	// Define Constants
	public static final int DEPTH_FIRST = 0;
	public static final int BREADTH_FIRST = 1;

	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;


	// Declare Fields
	private ArrayList fileList = new ArrayList();

	private boolean verbose = true;	
	private int crawlStyle = -1;
	private FileHandler fileHandler = null;
	private FileFilter dirFilter = null;
	private FileFilter fileFilter = null;
	private Comparator directoryComparator = null;

	private ProgressMonitor monitor = null;


	// The Constructors
	public DirectoryCrawler() {
		this(new BasicFileHandler(), new BasicFileFilter(), new BasicFileFilter(), null, DEPTH_FIRST, true, null);
	}

	public DirectoryCrawler(

			FileHandler fileHandler, 

			FileFilter dirFilter, 

			FileFilter fileFilter, 

			Comparator directoryComparator, 
			int crawlStyle, 
			boolean verbose,
			ProgressMonitor monitor
	) {
		setFileHandler(fileHandler);
		setDirectoryFilter(dirFilter);
		setFileFilter(fileFilter);
		setDirectoryComparator(directoryComparator);
		setCrawlStyle(crawlStyle);
		setVerbose(verbose);
		setProgressMonitor(monitor);
	}


	// The Accessors
	public void setProgressMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public FileHandler getFileHandler() {
		return fileHandler;
	}

	public void setFileHandler(FileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

	public FileFilter getDirectoryFilter() {
		return dirFilter;
	}

	public void setDirectoryFilter(FileFilter dirFilter) {
		this.dirFilter = dirFilter;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public int getCrawlStyle() {
		return crawlStyle;
	}

	public void setCrawlStyle(int crawlStyle) {
		if ((crawlStyle == DEPTH_FIRST) || (crawlStyle == BREADTH_FIRST)) {
			this.crawlStyle = crawlStyle;
		} else {
			// Invalid Setting so make no changes
		}
	}

	public Comparator getDirectoryComparator() {
		return directoryComparator;
	}

	public void setDirectoryComparator(Comparator directoryComparator) {
		this.directoryComparator = directoryComparator;
	}

	
	// Doin the Work
	public int crawl(String path) {
		return crawl(new File(path));
	}

	public int crawl(File start) {
		// Clear the fileList just in case
		fileList.clear();
		
		// Make sure the file exists
		if (!start.exists()) {
			System.out.println("File or Directory does not exist: " + start.getPath());
			return FAILURE;
		}

		// Initialize the DirectoryList
		if (start.isDirectory()) {
			if (dirFilter.isValid(start)) {
				fileList.add(start);
			}
		} else {
			if (fileFilter.isValid(start)) {
				fileList.add(start);
			}
		}

		// Crawl the directory structure.
		int progressDone = 0;

		while (fileList.size() > 0) {			
			File file = (File) fileList.get(0);
			
			// Update Monitor
			if (monitor != null) {
				if (monitor.isCanceled()) {
					monitor.close();
				    break;
				}

				//System.out.println("" + progressDone + ":" + (progressDone + fileList.size()) + ":" + file.getPath());
				monitor.setNote(file.getPath());
				monitor.setMaximum(progressDone + fileList.size());
				monitor.setProgress(progressDone);
			}

			fileList.remove(0);

			if (file.isDirectory()) {
				if (verbose) {System.out.println("Scanning Directory: " + file.getPath());}
				File[] files = file.listFiles();

				// Sort the directory list. The default is to sort by name.
				sortFiles(files);

				// Add the file to the list if it is valid
				int validCount = 0;
				for (int i = 0; i < files.length; i++) {
					File fileToAdd = files[i];
					if (fileToAdd.isDirectory()) {
						if (dirFilter.isValid(fileToAdd)) {
							addFile(fileToAdd, validCount);
							validCount++;
						}
					} else {
						if (fileFilter.isValid(fileToAdd)) {
							addFile(fileToAdd, validCount);
							validCount++;
						}
					}
				}
			} else {
				if (verbose) {System.out.println("       Handle File: " + file.getPath());}
				fileHandler.handleFile(file);
			}

			progressDone++;
		}

		if (monitor != null) {
			monitor.setProgress(progressDone);
		}

		return SUCCESS;
	}

	private void sortFiles(File[] files) {
		if (directoryComparator != null) {
			try {
				Arrays.sort(files, directoryComparator);
			} catch (ClassCastException cce) {
				cce.printStackTrace();
			}
		} else {
			Arrays.sort(files);
		}
	}

	private void addFile(File file, int validCount) {
		if (crawlStyle == DEPTH_FIRST) {
			fileList.add(validCount, file);
		} else if (crawlStyle == BREADTH_FIRST) {
			fileList.add(file);
		}
	}
}