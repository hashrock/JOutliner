/**
 * Copyright (C) 2003, 2004 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.xml;

/**
 * Collects constants used by many of the SAX2 parsers we will implement.
 */
public interface XMLParserConstants {
	
	// Verbosity
	/**
	 * A valid verbosity setting as used by the "verbose" processing 
	 * instruction.
	 */
	public static final int VERBOSITY_NONE = 0;
	
	/**
	 * A valid verbosity setting as used by the "verbose" processing 
	 * instruction.
	 */
	public static final int VERBOSITY_MINIMAL = 1;
	
	/**
	 * A valid verbosity setting as used by the "verbose" processing 
	 * instruction.
	 */
	public static final int VERBOSITY_NORMAL = 2;
	
	/**
	 * A valid verbosity setting as used by the "verbose" processing 
	 * instruction.
	 */
	public static final int VERBOSITY_MAXIMAL = 3;
	
	/**
	 * A valid verbosity setting as used by the "verbose" processing 
	 * instruction.
	 */
	public static final int VERBOSITY_DEBUG = 4;
	
	
	// XML Processing Instructions
	/**
	 * A processing instruction tag name used for setting the verbosity during 
	 * processing.
	 */
	public static final String PI_VERBOSE = "verbose";
	
	/**
	 * A processing instruction attribute used for turning verbosity on and off.
	 */
	public static final String PI_NAME_ENABLED = "enabled";
	
	/**
	 * A processing instruction attribute that sets how verbose to be. Valid 
	 * values are provided by the VERBOSITY_XX connstants defined above.
	 */
	public static final String PI_NAME_LEVEL = "level";
}