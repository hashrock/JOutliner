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

import com.organic.maynard.outliner.*;
import java.awt.Container;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2002/07/16 21:25:30 $
 */

public interface PreferencesPanel {

	/**
	 * Applys the current value of each preference to the application. This is
	 * the method that gets executed when a user clicks on OK or Apply in a
	 * preferencePanel. 
	 */			
	public void applyCurrentToApplication();
	
	/**
	 * Syncs the GUI with the current value of each preference in this panel. This
	 * method gets executed before the preference panels are made visible, when the
	 * user selects the "application preferences" menu item. This ensures that the GUI
	 * reflects the current value of the preferences before editing begins.
	 */			
	public void setToCurrent();


	/**
	 * Appends the provided <code>PreferencesGUITreeComponent</code> object to the
	 * end of the preference list. 
	 *
	 * @param pref the preference to add. 
	 */			
	public void addPreference(PreferencesGUITreeComponent pref);

	/**
	 * Gets the <code>PreferencesGUITreeComponent</code> of index <code>i</code>.
	 *
	 * @return        the <code>PreferencesGUITreeComponent</code> matching
	 *                index <code>i</code> or <code>null</code> if index is out
	 *                of bounds.
	 */		
	public PreferencesGUITreeComponent getPreference(int i);

	/**
	 * Gets the number of <code>PreferencesGUITreeComponent</code> objects that
	 * have been added to this <code>PreferencePanel</code> so far.
	 *
	 * @return        the number of prefs for this panel.
	 */		
	public int getPreferenceListSize();


	/**
	 * Gets the <code>Container</code> that GUI elements should currently be added to.
	 *
	 * @return        Returns the most deeply nested <code>Container</code> object at 
	 *                the current moment during gui_tree.xml file processing. If no
	 *                nested containers exist then the default container for the
	 *                <code>PreferencePanle</code> is returned.
	 */		
	public Container getCurrentContainer();

	/**
	 * Should be called by a <code>PreferencesGUITreeSubContainer</code> object to
	 * inform this <code>PreferencePanel</code> to begin adding the sub-container.
	 * The sub-container stack must be updated by the <code>PreferencePanel</code> so
	 * that <code>getCurrentContainer()</code> will funtion correctly.
	 *
	 * @param c the sub-container to add. 
	 */		
	public void startAddSubContainer(Container c);

	/**
	 * Should be called by a <code>PreferencesGUITreeSubContainer</code> object to
	 * inform this <code>PreferencePanel</code> to end adding the sub-container.
	 * The sub-container stack must be updated by the <code>PreferencePanel</code> so
	 * that <code>getCurrentContainer()</code> will funtion correctly.
	 *
	 * @param c the sub-container to add. 
	 */		
	public void endAddSubContainer(Container c);
}	
