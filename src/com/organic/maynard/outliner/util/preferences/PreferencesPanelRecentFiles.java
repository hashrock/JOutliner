/**
 * Copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

import com.organic.maynard.outliner.menus.file.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.xml.sax.*;

/**
 * class to handle the Recent Files list options prefs panel
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2004/01/20 08:12:24 $
 */

public class PreferencesPanelRecentFiles extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {
	
	public void applyCurrentToApplication() {
		int limit = 0;
		String currentSetting = null;
		boolean coolToApply = true;
		int ordering = 0;
		int nameForm = 0 ;
		int direction = 0 ;
		
		// first, deal with any possible size changes
		RecentFilesList.syncSize();
		
		// grab what's been set in the panel
		Preferences prefs = Outliner.prefs;
		
		PreferenceString pRF_Ordering = (PreferenceString) prefs.getPreference(Preferences.RECENT_FILES_ORDERING);
		PreferenceString pRF_Name_Form = (PreferenceString) prefs.getPreference(Preferences.RECENT_FILES_NAME_FORM);
		PreferenceString pRF_Direction = (PreferenceString) prefs.getPreference(Preferences.RECENT_FILES_DIRECTION);
		
		// find the position of those strings in their arrays
		for (ordering = 0, limit = Preferences.RECENT_FILES_ORDERINGS.length, currentSetting = pRF_Ordering.getCur(); ordering < limit ; ordering++) {
			if (currentSetting.equals(Preferences.RECENT_FILES_ORDERINGS[ordering])) {
				break;
			}
		}
		
		if (ordering == limit) {
			coolToApply = false;
		}
		
		for (nameForm = 0, limit = Preferences.RECENT_FILES_NAME_FORMS.length, currentSetting = pRF_Name_Form.getCur(); nameForm < limit ; nameForm++) {
			if (currentSetting.equals(Preferences.RECENT_FILES_NAME_FORMS[nameForm])) {
				break;
			}
		}
		
		if (nameForm == limit) {
			coolToApply = false;
		}
		
		for (direction = 0, limit = Preferences.RECENT_FILES_DIRECTIONS.length, currentSetting = pRF_Direction.getCur(); direction < limit ; direction++) {
			if (currentSetting.equals(Preferences.RECENT_FILES_DIRECTIONS[direction])) {
				break;
			}
		}
		
		if (direction == limit) {
			coolToApply = false;
		}
		
		// if we've got a set of valid values
		if (coolToApply) {
			// grab a ref to the recent files list menu item
			RecentFilesList rflmi = (RecentFilesList) GUITreeLoader.reg.get(GUITreeComponentRegistry.RECENT_FILE_MENU);
			
			// apply 'em
			rflmi.setDisplayOptions(ordering, nameForm, direction);
		}
	}
}
