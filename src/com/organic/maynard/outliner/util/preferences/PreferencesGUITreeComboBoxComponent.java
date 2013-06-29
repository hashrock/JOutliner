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

public class PreferencesGUITreeComboBoxComponent extends AbstractPreferencesGUITreeComponent {

	// Constants
	public static final String A_LIST = "list";
	
	private static final String FONT_FAMILY_NAMES = "font_family_names";
	private static final String LINE_WRAP_OPTIONS = "line_wrap_options";

	private static final String FILE_PROTOCOLS = "file_protocols";
	private static final String PLATFORM_IDENTIFIERS = "platform_identifiers";
	private static final String ENCODINGS = "encodings";
	private static final String FILE_FORMATS_OPEN = "file_formats_open";
	private static final String FILE_FORMATS_IMPORT = "file_formats_import";
	private static final String FILE_FORMATS_SAVE = "file_formats_save";
	private static final String FILE_FORMATS_EXPORT = "file_formats_export";

	private static final String RECENT_FILES_ORDERINGS = "recent_files_orderings";
	private static final String RECENT_FILES_NAME_FORMS = "recent_files_name_forms";
	private static final String RECENT_FILES_DIRECTIONS = "recent_files_directions";
	
	
	// GUITree Component Interface
	public void startSetup(Attributes atts) {
		String listName = atts.getValue(A_LIST);
		
		// Set the Component
		JComboBox component = new JComboBox();
		addList(listName, component);
		setComponent(component);
		super.startSetup(atts);
		component.addItemListener(new ComboBoxListener(component, getPreference()));
	}
	
	/**
	 * Used to add a named list to a combo box component during gui_tree loading.
	 */
	private static void addList(String listName, JComboBox component) {
		if (FONT_FAMILY_NAMES.equals(listName)) {
			addArrayToComboBox(Preferences.FONT_FAMILY_NAMES, component);
		} else if (LINE_WRAP_OPTIONS.equals(listName)) {
			addArrayToComboBox(Preferences.LINE_WRAP_OPTIONS, component);
		} else if (FILE_PROTOCOLS.equals(listName)) {
			addArrayToComboBox(Preferences.FILE_PROTOCOLS.toArray(), component);
		} else if (PLATFORM_IDENTIFIERS.equals(listName)) {
			addArrayToComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS, component);
		} else if (ENCODINGS.equals(listName)) {
			addArrayToComboBox(Preferences.ENCODINGS.toArray(), component);
		} else if (FILE_FORMATS_OPEN.equals(listName)) {
			addArrayToComboBox(Preferences.FILE_FORMATS_OPEN.toArray(), component);
		} else if (FILE_FORMATS_IMPORT.equals(listName)) {
			addArrayToComboBox(Preferences.FILE_FORMATS_IMPORT.toArray(), component);
		} else if (FILE_FORMATS_SAVE.equals(listName)) {
			addArrayToComboBox(Preferences.FILE_FORMATS_SAVE.toArray(), component);
		} else if (FILE_FORMATS_EXPORT.equals(listName)) {
			addArrayToComboBox(Preferences.FILE_FORMATS_EXPORT.toArray(), component);
		} else if (RECENT_FILES_ORDERINGS.equals(listName)) {
			addArrayToComboBox(Preferences.RECENT_FILES_ORDERINGS, component);
		} else if (RECENT_FILES_NAME_FORMS.equals(listName)) {
			addArrayToComboBox(Preferences.RECENT_FILES_NAME_FORMS, component);
		} else if (RECENT_FILES_DIRECTIONS.equals(listName)) {
			addArrayToComboBox(Preferences.RECENT_FILES_DIRECTIONS, component);
		} else {
			// Add nothing
		}
	}
	
	private static void addArrayToComboBox(Object[] array, JComboBox component) {
		for (int i = 0; i < array.length; i++) {
			component.addItem(array[i].toString());
		}	
	}
}
