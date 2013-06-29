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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2004/02/02 10:17:42 $
 */

public abstract class AbstractPreferencesGUITreeComponent implements PreferencesGUITreeComponent {
	
	// Instance Fields
	private String labelText = null;
	private JComponent component = null;
	private Preference pref = null;
	
	
	// Constants
	public static final String A_LABEL = "label";
	public static final String A_STYLE = "style";
	
	public static final String STYLE_SIDE_BY_SIDE = "side_by_side"; // The default
	public static final String STYLE_SINGLE_CENTERED = "single_centered";


	// PreferencesGUITreeComponent Interface
	public void setComponent(JComponent c) {
		this.component = c;
	}
	
	public JComponent getComponent() {
		return this.component;
	}
	
	public void setLabelText(String text) {
		this.labelText = text;
	}
	
	public String getLabelText() {
		return this.labelText;
	}

	public void setPreference(Preference pref) {
		this.pref = pref;
	}
	
	public Preference getPreference() {
		return this.pref;
	}
	
	
	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}

	public void startSetup(Attributes atts) {
		// Set the Label
		setLabelText(atts.getValue(A_LABEL));

		// Set the Preference
		Preference pref = (Preference) GUITreeLoader.getAncestorElementOfClass("com.organic.maynard.outliner.util.preferences.Preference");
		setPreference(pref);
	}

	public void endSetup(Attributes atts) {
		AbstractPreferencesPanel prefPanel = (AbstractPreferencesPanel) GUITreeLoader.getAncestorElementOfClass("com.organic.maynard.outliner.util.preferences.AbstractPreferencesPanel");
		
		// Add it to the PreferenceList in the parent panel
		prefPanel.addPreference(this);
		
		Container c = prefPanel.getCurrentContainer();
		
		if (STYLE_SINGLE_CENTERED.equals(atts.getValue(A_STYLE))) {
			AbstractPreferencesPanel.addSingleItemCentered(new JLabel(getLabelText()), c);
			AbstractPreferencesPanel.addSingleItemCentered(getComponent(), c);
		} else {
			AbstractPreferencesPanel.addPreferenceItem(getLabelText(), getComponent(), c);
		}
	}
}