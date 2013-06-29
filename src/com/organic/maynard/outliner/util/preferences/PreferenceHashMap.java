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
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import org.xml.sax.*;

public class PreferenceHashMap extends AbstractPreference implements GUITreeComponent {
	
	// Constants
	private static final String DELIMITER_MINOR = ",";
	private static final String DELIMITER_MAJOR = ";";
	
	
	// Instance Fields
	public HashMap def = new HashMap();
	public HashMap cur = new HashMap();
	public HashMap tmp = new HashMap();
	
	
	// Constructors
	public PreferenceHashMap() {}
	
	public PreferenceHashMap(HashMap def, String command) {
		this(def,new HashMap(),command);
	}
	
	public PreferenceHashMap(HashMap def, HashMap cur, String command) {
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
		this.def = convertToHashMap(value);
	}
	
	public void setCur(String value) {
		this.cur = convertToHashMap(value);
	}
	
	public void setTmp(String value) {
		this.tmp = convertToHashMap(value);
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
	public String convertToString(HashMap map) {
		StringBuffer buf = new StringBuffer();
		
		Iterator it = map.keySet().iterator();
		
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) map.get(key);
			
			try {
				buf.append(URLEncoder.encode(key, "UTF-8"));
				buf.append(DELIMITER_MINOR);
				buf.append(URLEncoder.encode(value, "UTF-8"));
				
				if (it.hasNext()) {
					buf.append(DELIMITER_MAJOR);
				}
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}
		
		return buf.toString();
	}
	
	public HashMap convertToHashMap(String s) {
		HashMap map = new HashMap();
		
		StringTokenizer tokenizerMajor = new StringTokenizer(s,DELIMITER_MAJOR);
		while (tokenizerMajor.hasMoreTokens()) {
			String tokenMajor = tokenizerMajor.nextToken();
			StringTokenizer tokenizerMinor = new StringTokenizer(tokenMajor,DELIMITER_MINOR);
			
			String key = null;
			String value = null;
			try {
				if (tokenizerMinor.hasMoreTokens()) {
					key = URLDecoder.decode(tokenizerMinor.nextToken(), "UTF-8");
				}
				if (tokenizerMinor.hasMoreTokens()) {
					value = URLDecoder.decode(tokenizerMinor.nextToken(), "UTF-8");
				}
				
				if (value == null) {
					value = "";
				}
				
				if (key == null) {
					key = "";
				}
				
				map.put(key,value);
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}
		
		return map;
	}
	
	public String toString() {
		return convertToString(cur);
	}
	
	
	// Preference Interface
	public void restoreCurrentToDefault() {
		copyHashMap(def,cur);
	}
	
	public void restoreTemporaryToDefault() {
		copyHashMap(def,tmp);
	}
	
	public void restoreTemporaryToCurrent() {
		copyHashMap(cur,tmp);
	}
	
	public void applyTemporaryToCurrent() {
		copyHashMap(tmp,cur);
	}
	
	private void copyHashMap(HashMap from, HashMap to) {
		to.clear();
		
		Iterator it = from.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) from.get(key);
			to.put(key,value);
		}
	}
}