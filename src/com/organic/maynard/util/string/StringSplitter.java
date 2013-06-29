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

public class StringSplitter implements Enumeration {
	protected String match = null;
	protected String text = null;
	
	protected int matchLength = 0;
	
	protected int startIndex = 0;
	protected int endIndex = 0;
	
	protected boolean hasMore = true;

	public StringSplitter(String text, String match) {
		this.matchLength = match.length();
		this.text = text;
		this.match = match;
		
		if ((text == null) || (text.equals(""))) {
			hasMore = false;
		}
	}
	
	public Object nextElement() {
		if (!hasMoreElements()) {return null;}
		
		endIndex = text.indexOf(match,startIndex);
		if (endIndex == -1) {
			endIndex = text.length();
		}
		String token = text.substring(startIndex,endIndex);
		
		startIndex = endIndex + matchLength;
		
		if (startIndex >= text.length()) {
			hasMore = false;
		}
		
		return token;
	}
		
	public boolean hasMoreElements() {
		return hasMore;
	}

}