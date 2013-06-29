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

import com.organic.maynard.outliner.util.undo.UndoQueue;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.event.UndoQueueEvent;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.10 $, $Date: 2002/12/09 22:55:15 $
 */
 
public class PreferencesPanelEditor extends AbstractPreferencesPanel implements PreferencesPanel, GUITreeComponent {
	
	// PreferencePanel Interface
	public void applyCurrentToApplication() {
		Preferences prefs = Outliner.prefs;
		
		PreferenceInt pUndoQueueSize = (PreferenceInt) prefs.getPreference(Preferences.UNDO_QUEUE_SIZE);
		PreferenceBoolean pShowLineNumbers = (PreferenceBoolean) prefs.getPreference(Preferences.SHOW_LINE_NUMBERS);
		PreferenceBoolean pSingleClickExpand = (PreferenceBoolean) prefs.getPreference(Preferences.SINGLE_CLICK_EXPAND);
		PreferenceBoolean pShowIndicators = (PreferenceBoolean) prefs.getPreference(Preferences.SHOW_INDICATORS);
		PreferenceString pFontFace = (PreferenceString) prefs.getPreference(Preferences.FONT_FACE);
		PreferenceInt pFontSize = (PreferenceInt) prefs.getPreference(Preferences.FONT_SIZE);
		PreferenceString pLineWrap = (PreferenceString) prefs.getPreference(Preferences.LINE_WRAP);
		PreferenceBoolean pUseCreateModDates = (PreferenceBoolean) prefs.getPreference(Preferences.USE_CREATE_MOD_DATES);
		PreferenceString pCreateModDatesFormat = (PreferenceString) prefs.getPreference(Preferences.CREATE_MOD_DATES_FORMAT);
		
		// Push UndoQueueSize into UndoQueue
		UndoQueue.MAX_QUEUE_SIZE = pUndoQueueSize.cur;
		
		// Update expand_mode in IconKeyListener
		if (pSingleClickExpand.cur) {
			IconKeyListener.expand_mode = IconKeyListener.MODE_EXPAND_SINGLE_CLICK;
		} else {
			IconKeyListener.expand_mode = IconKeyListener.MODE_EXPAND_DOUBLE_CLICK;
		}
		
		// Update the line numbers
		if (pShowLineNumbers.cur) {
			OutlineLineNumber.LINE_NUMBER_WIDTH = OutlineLineNumber.LINE_NUMBER_WIDTH_DEFAULT;
		} else {
			OutlineLineNumber.LINE_NUMBER_WIDTH = OutlineLineNumber.LINE_NUMBER_WIDTH_MIN;
		}
		
		// Update the Indicators
		// This is handled in PreferencesPanelLookAndFeel.
		
		// Update the cellRenderers
		boolean line_wrap = true;
		if (pLineWrap.cur.equals(Preferences.TXT_CHARACTERS)) {
			line_wrap = false;
		}
		
		OutlinerCellRendererImpl.pShowLineNumbers = pShowLineNumbers.cur;
		OutlinerCellRendererImpl.pShowIndicators = pShowIndicators.cur;
		
		// Update fonts
		OutlinerCellRendererImpl.updateFonts();
		
		// Update renderers in existing docs
		for (int i = 0, limit = Outliner.documents.openDocumentCount(); i < limit; i++) {
			OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getDocument(i);
			
			// Update the undo queue for all the documents immediatly if it is being downsized.
			doc.getUndoQueue().prefsTrim();
			
			for (int j = 0; j < OutlineLayoutManager.CACHE_SIZE; j++) {
				OutlinerCellRendererImpl renderer = doc.panel.layout.textAreas[j];
				
				renderer.setWrapStyleWord(line_wrap);
				
				// Hide line numbers if both indicators and line numbers are turned off.
				// We leave them showing otherwise, because it creates a better visual
				// representation in the display when there are indented nodes.
				OutlineLineNumber lineNumber = renderer.lineNumber;
				
				if (pShowLineNumbers.cur || pShowIndicators.cur) {
					lineNumber.setOpaque(true);
				} else {
					lineNumber.setOpaque(false);
				}
				
				if (!pShowLineNumbers.cur) {
					lineNumber.setText("");
				}
			}
		}
	}
}