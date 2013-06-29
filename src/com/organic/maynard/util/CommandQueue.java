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

package com.organic.maynard.util;

import java.util.*;
import java.io.*;

public class CommandQueue extends Queue {
	protected boolean fileOpen = false;
	protected boolean eof = false;
	
	protected FileInputStream theStream = null;
	protected BufferedReader buffer = null;
	
	// The Constructors
	public CommandQueue() {
		super();
	}

	public CommandQueue(int max_size) {
		super(max_size);
	}

	public synchronized Object getNext() {
		Object obj = super.getNext();
		if (fileOpen) {
			try {
				while (processLine()) {}
				if (!fileOpen) {theStream.close();}
			} catch (IOException ioe) {
				System.err.println("Could not close FileReader: " + "\n" + ioe);
			}

		}
		return obj;
	}

	public synchronized void loadFromFile(String filename) {
		try {
			theStream = new FileInputStream(filename);
			InputStreamReader theReader = new InputStreamReader(theStream);
			buffer = new BufferedReader(theReader);
			
			fileOpen = true;
			
			while (processLine()) {}
			
			if (!fileOpen) {
				theStream.close();
			}
		} catch (FileNotFoundException fnfe) {
			System.err.println("File Not Found: " + filename + "\n" + fnfe);		
		} catch (Exception e) {
			System.err.println("Could not create FileReader: " + filename + "\n" + e);
		}

	}
	
	private synchronized boolean processLine() throws IOException {
		if (!isFull() && (eof == false)) {
			String theLine = buffer.readLine();
			if (theLine == null) {
				eof = true;
				fileOpen = false;
				return false;
			} else {
				add(theLine);
				return true;
			}
		} else {
			return false;
		}
	}
}