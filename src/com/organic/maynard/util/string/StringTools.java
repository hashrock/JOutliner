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

package com.organic.maynard.util.string;

import java.util.*;

public class StringTools {
	
	// Constructor
	public StringTools() {}
	
	
	public static String substring(String s, int beginIndex, int endIndex) throws IndexOutOfBoundsException {
		// Discovered somthing new. String.substring is basically a 
		// memory leak. See: http://developer.java.sun.com/developer/bugParade/bugs/4637640.html for more details.
		// Use string.getChars to avoid this whenever you will be keeping the substring around for a while.
		// basically what happens is the substring has a ref to the original string so it doesn't get GC'd.
		int length = endIndex - beginIndex;
		char[] charArray = new char[length];
		s.getChars(beginIndex, endIndex, charArray, 0);
		return new String(charArray);
	}
	
	
	// Class Methods
	public static String replace(String in, String match, String replacement) {
		// check for null refs
		if (in == null || match == null || replacement == null) {
			return in;
		}
		
		StringBuffer out = new StringBuffer();
		
		int matchLength = match.length();
		int inLength = in.length();
		
		for (int i = 0; i < inLength; i++) {
			int upperSearhLimit = i + matchLength;
			if ((upperSearhLimit <= inLength) && (in.substring(i,upperSearhLimit).equals(match))) {
				out.append(replacement);
				i = upperSearhLimit - 1;
			} else {
				out.append(in.charAt(i));
			}
		}
		return out.toString();
	}
	
	public static int startsWith(String text, String match) {
		int matchLength = match.length();
		int count = 0;
		int index = 0;
		while (text.startsWith(match,index)) {
			count++;
			index += matchLength;
		}
		return count;
	}
	
	public static int contains(String text, String match) {
		int matchLength = match.length();
		int count = 0;
		int index = 0;
		while (true) {
			index = text.indexOf(match,index);
			if (index == -1) {break;}
			index += matchLength;
			count++;
		}
		return count;
	}
	
	public static String join(Object[] array) {
		return join(array,"");
	}
	
	public static String join(Object[] array, String glue) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			buf.append(array[i].toString());
			if (i < array.length - 1) {
				buf.append(glue);
			}
		}
		return buf.toString();
	}
	
	public static String trimFront(String text, String match, int numToTrim) {
		int substringStart = 0;
		for (int i = 0; i < numToTrim; i++) {
			if (text.startsWith(match, substringStart)) {
				substringStart += match.length();
			}
		}
		return text.substring(substringStart, text.length());
	}
	
	public static String trimExtension(String text, String separator) {
		int index = text.lastIndexOf(separator);
		if (index == -1) {
			return text;
		} else {
			return text.substring(0, index);
		}
	}
	
	public static String escape(String text, char escapeChar,  char[] reserved) {
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			// Is the char reserved
			boolean isReserved = false;
			if (reserved != null) {
				for (int j = 0; j < reserved.length; j++) {
					if (c == reserved[j]) {
						isReserved = true;
						break;
					}
				}
			}
			
			// Deal with the type of char
			if (c == escapeChar) {
				buf.append(escapeChar).append(escapeChar);
			} else if (isReserved) {
				buf.append(escapeChar).append(c);
			} else {
				buf.append(c);
			}
		}
		
		return buf.toString();
	}
	
	public static Vector split(String text, char escapeChar, char[] delimiters) {
		Vector parts = new Vector();
		
		boolean isEscaped = false;
		
		StringBuffer part = new StringBuffer();
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			
			// Is the char a delimiter
			boolean isDelimiter = false;
			for (int j = 0; j < delimiters.length; j++) {
				if (c == delimiters[j]) {
					isDelimiter = true;
					break;
				}
			}
			
			// Deal with the type of char
			if (c == escapeChar) {
				if (isEscaped) {
					part.append(c);
					isEscaped = false;
				} else {
					isEscaped = true;
				}			
			} else if (isDelimiter) {
				if (isEscaped) {
					part.append(c);
					isEscaped = false;
				} else {
					parts.add(part.toString());
					part.setLength(0);
				}
			} else {
				if (isEscaped) {
					part.append(c);
					isEscaped = false;
				} else {
					part.append(c);
				}
			}
		}
		
		// Append the last section
		parts.add(part.toString());
		
		return parts;
	}
	
	public static void split(ArrayList parts, String text, char escapeChar, char[] delimiters) {
		boolean isEscaped = false;
		boolean isDelimiter = false;
		StringBuffer part = new StringBuffer();
		
		for (int i = 0, limit = text.length(); i < limit; i++) {
			char c = text.charAt(i);
			
			// Is the char a delimiter
			for (int j = 0; j < delimiters.length; j++) {
				if (c == delimiters[j]) {
					isDelimiter = true;
					break;
				}
			}
			
			// Deal with the type of char
			if (isDelimiter) {
				if (isEscaped) {
					part.append(c);
					isEscaped = false;
				} else {
					parts.add(part.toString());
					part.setLength(0);
				}
				isDelimiter = false;
			} else if (c == escapeChar) {
				if (isEscaped) {
					part.append(c);
					isEscaped = false;
				} else {
					isEscaped = true;
				}
			} else {
				if (isEscaped) {
					isEscaped = false;
				}
				part.append(c);
			}
		}
		
		// Append the last section
		parts.add(part.toString());
	}
}