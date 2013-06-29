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
 
package com.organic.maynard.outliner.util.preferences;

import java.net.*;
import java.util.*;
import java.io.UnsupportedEncodingException;
import org.xml.sax.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.data.StringList;

public class PreferenceStringList extends AbstractPreference implements GUITreeComponent {
	
	// Constants
	private static final String DELIMITER = ",";
	
	
	// Instance Fields
	public StringList def = new StringList();
	public StringList cur = new StringList();
	public StringList tmp = new StringList();
	
	
	// Constructors
	public PreferenceStringList() {}
	
	public PreferenceStringList(StringList def, String command) {
		this(def,new StringList(),command);
	}
	
	public PreferenceStringList(StringList def, StringList cur, String command) {
		this.def = def;
		this.cur = cur;
		this.tmp = cur;
		setCommand(command);
	}
	
	
	// GUITreeComponent Interface
	public void endSetup(Attributes atts) {
		super.endSetup(atts);
	}
	
	
	// Setters
	public void setDef(String value) {
		this.def = convertToStringList(value);
	}
	
	public void setCur(String value) {
		this.cur = convertToStringList(value);
	}
	
	public void setTmp(String value) {
		this.tmp = convertToStringList(value);
	}
	
	public String getCur() {
		return convertToString(cur);
	}
	
	public String getDef() {
		return convertToString(def);
	}
	
	public String getTmp() {
		return convertToString(tmp);
	}
	
	
	// Misc Methods
	public String convertToString(StringList list) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			try {
				buf.append(URLEncoder.encode(list.get(i), "UTF-8"));
				if (i < list.size() - 1) {
					buf.append(DELIMITER);
				}
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}
		
		return buf.toString();
	}
	
	public StringList convertToStringList(String s) {
		StringList list = new StringList();
		
		StringTokenizer tokenizer = new StringTokenizer(s,DELIMITER);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			try {
				list.add(URLDecoder.decode(token, "UTF-8"));
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}
		
		return list;
	}
	
	public String toString() {
		return convertToString(cur);
	}
	
	
	// Preference Interface
	public void restoreCurrentToDefault() {
		cur = (StringList) def.clone();
	}
	
	public void restoreTemporaryToDefault() {
		tmp = (StringList) def.clone();
	}
	
	public void restoreTemporaryToCurrent() {
		tmp = (StringList) cur.clone();
	}
	
	public void applyTemporaryToCurrent() {
		cur = (StringList) tmp.clone();
	}
}