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
 
package com.organic.maynard.io;

import java.io.*;
import java.util.*;

public class SystemLogger extends PrintStream {
	// These will hold references to the "standard" out and err from System.
	public static PrintStream out = System.out;
	public static PrintStream err = System.err;
	
	
	// This is for file logging
	public static FileOutputStream fileOut = null;
	
	
	// Constructors
	public SystemLogger() {
		super(fileOut);
		System.setOut(this);
		System.setErr(this);
	}
	
	
	// Methods
	public void printlnToLog(String s) {
		synchronized (this) {
		    printToLog(s + System.getProperty("line.separator"));
		}
	}

	public void printToLog(String s) {
		if (s == null) {
	    	s = "null";
		}
		try {
			fileOut.write(s.getBytes(), 0, s.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	// Overridden Methods
	public void close() {
		super.close();
		out.close();
	}
	
	public void flush() {
		super.flush();
		out.flush();
	}

	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len);
		out.write(buf, off, len);
	}

	public void write(int x) {
		super.write(x);
		out.write(x);
	}
}