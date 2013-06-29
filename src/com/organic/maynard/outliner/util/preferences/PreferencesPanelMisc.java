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

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2004/05/18 01:07:46 $
 */
 
public class PreferencesPanelMisc extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {
	
	public void applyCurrentToApplication() {
		Preferences prefs = Outliner.prefs;
		
		PreferenceString pMergeDelimiter = (PreferenceString) prefs.getPreference(Preferences.MERGE_DELIMITER);
		PreferenceBoolean pTrimEnabled = (PreferenceBoolean) prefs.getPreference(Preferences.TRIM_ENABLED_FOR_MERGE_WITH_DELIMITER);
		PreferenceBoolean pIncludeEmptyNodes = (PreferenceBoolean) prefs.getPreference(Preferences.INCLUDE_EMPTY_NODES_FOR_MERGE_WITH_DELIMITER);
		
		NodeImpl.merge_delimiter = pMergeDelimiter.cur;
		NodeImpl.merge_trim_enabled = pTrimEnabled.cur;
		NodeImpl.merge_empty_nodes_enabled = pIncludeEmptyNodes.cur;
	}
}