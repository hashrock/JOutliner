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
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:42 $
 */

public class PreferencesGUITreeColorButtonComponent extends AbstractPreferencesGUITreeComponent implements ActionListener {

	public void startSetup(Attributes atts) {
		// Set the Component
		JButton component = new JButton("");
		setComponent(component);
		super.startSetup(atts);
		component.addActionListener(this);
		component.setActionCommand(getLabelText());
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
		PreferenceColor pref = (PreferenceColor) getPreference();
		
		if (!clickBlocker) {
			clickBlocker = true;
			Color newColor = JColorChooser.showDialog(pf, getLabelText(), pref.tmp);
			if (newColor != null) {
				pref.tmp = newColor;
				getComponent().setBackground(pref.tmp);
			}
			clickBlocker = false;
		}
	}
	
	// This prevents double clicks from launching the color chooser twice. This seems like a bug since
	// you would expect only one action event to be created when the user double-clicks on a button,
	// but apparently 2 are created. Until the "bug" is really figured out this hack will make things a 
	// little better.
	private boolean clickBlocker = false; 
}
