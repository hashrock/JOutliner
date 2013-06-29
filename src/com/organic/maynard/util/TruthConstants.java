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

package com.organic.maynard.util;

/**
 * Collects constants for indicating true and false via String representations.
 * Evaluation of these constants is handled in the TruthTest class.
 */
public interface TruthConstants {
	
	/** The cannonical representation of truth within an XML/HTML document. */
	public static final String VALUE_TRUE_CANNONICAL = "true";
	
	/** The cannonical representation of false within an XML/HTML document. */
	public static final String VALUE_FALSE_CANNONICAL = "false";
	
	/** The short text based representation of truth within an XML/HTML document. */
	public static final String VALUE_TRUE_SHORT = "t";
	
	/** The short text based representation of false within an XML/HTML document. */
	public static final String VALUE_FALSE_SHORT = "f";
	
	/** The numerical representation of truth within an XML/HTML document. */
	public static final String VALUE_TRUE_NUMERICAL = "1";
	
	/** The numerical representation of false within an XML/HTML document. */
	public static final String VALUE_FALSE_NUMERICAL = "0";
	
	/** The yes/no representation of truth within an XML/HTML document. */
	public static final String VALUE_YES = "yes";
	
	/** The yes/no representation of false within an XML/HTML document. */
	public static final String VALUE_NO = "no";
	
	/** The short yes/no representation of truth within an XML/HTML document. */
	public static final String VALUE_YES_SHORT = "y";
	
	/** The shortyes/no representation of false within an XML/HTML document. */
	public static final String VALUE_NO_SHORT = "n";
}