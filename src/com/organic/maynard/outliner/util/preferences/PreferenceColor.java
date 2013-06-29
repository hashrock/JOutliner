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

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import java.awt.*;
import org.xml.sax.*;

public class PreferenceColor extends AbstractPreference implements GUITreeComponent {
	
	// Instance Fields
	public Color def = new Color(255,255,255);
	public Color cur = new Color(255,255,255);
	public Color tmp = new Color(255,255,255);


	// Constructors
	public PreferenceColor() {
	
	}
	
	public PreferenceColor(Color def, String command) {
		this(def,new Color(255,255,255),command);
	}

	public PreferenceColor(Color def, Color cur, String command) {
		this.def = new Color(def.getRGB());
		this.cur = new Color(cur.getRGB());
		this.tmp = new Color(cur.getRGB());
		setCommand(command);
	}


	// GUITreeComponent Interface
	public void endSetup(Attributes atts) {
		super.endSetup(atts);
	}	


	// Setters with Validation	
	public void setDef(String value) {this.def = parseColor(value);}
	public void setCur(String value) {this.cur = parseColor(value);}
	public void setTmp(String value) {this.tmp = parseColor(value);}

	
	// Misc Methods
	public String toString() {return ("" + cur.getRGB());}


	// Preference Interface
	public void restoreCurrentToDefault() {cur = new Color(def.getRGB());}
	public void restoreTemporaryToDefault(){tmp = new Color(def.getRGB());}
	public void restoreTemporaryToCurrent(){tmp = new Color(cur.getRGB());}
	public void applyTemporaryToCurrent(){cur = new Color(tmp.getRGB());}

	public String getCur() {return cur.toString();}
	public String getDef() {return def.toString();}
	public String getTmp() {return tmp.toString();}

	// Class Methods
	protected static final Color parseColor(String rgb) {
		try {
			return new Color(Integer.parseInt(rgb));
		} catch (Exception e) {
			return new Color(0);
		}
	}

}