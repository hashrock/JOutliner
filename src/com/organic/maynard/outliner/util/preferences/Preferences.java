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
import java.awt.Font;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.*;
import com.organic.maynard.data.StringList;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.13 $, $Date: 2004/05/18 01:07:46 $
 */

public class Preferences implements GUITreeComponent {
	
	// Constants
	public static final String EXTENSION_SEPARATOR = ".";
	
	public static final String DEPTH_PAD_STRING = "\t"; // Specific to Outliner Docs
	public static final String LINE_END_STRING = "\n"; // Specific to Outliner Docs
	
	public static String TXT_WORDS = null;
	public static String TXT_CHARACTERS = null;
	public static String[] LINE_WRAP_OPTIONS = new String[2];
	public static String[] FONT_FAMILY_NAMES = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	
	public static String[] RECENT_FILES_ORDERINGS = new String[3];
	public static String[] RECENT_FILES_NAME_FORMS = new String[3];
	public static String[] RECENT_FILES_DIRECTIONS = new String[2];
	
	public static final StringList ENCODINGS = new StringList();
	public static final StringList FILE_FORMATS_OPEN = new StringList();
	public static final StringList FILE_FORMATS_IMPORT = new StringList();
	public static final StringList FILE_FORMATS_SAVE = new StringList();
	public static final StringList FILE_FORMATS_EXPORT = new StringList();
	
	public static final ArrayList FILE_PROTOCOLS = new ArrayList();
	
	
	// Start Preference Keys: All the preferences used by the core app are here. If you are adding your own
	// preferences you don't need to put your keys here. These should probably end up in an interface since
	// they are called from other classes everywhere. Important: the value of the key must match the
	// string you use in the "id" attribute of your prefs component in the gui_tree.xml file.
		// Hidden Prefs
			// Misc
			public static final String RENDERER_WIDGIT_CACHE_SIZE = "renderer_widgit_cache_size";
			public static final String TIME_ZONE_FOR_SAVING_DATES = "time_zone_for_saving_dates";
			public static final String MOST_RECENT_SAVE_DIR = "most_recent_save_dir";
			public static final String MOST_RECENT_OPEN_DIR = "most_recent_open_dir";
			public static final String IS_MAXIMIZED = "is_maximized";
			
			// Main Window
			public static final String MAIN_WINDOW_W = "main_window_width";
			public static final String MAIN_WINDOW_H = "main_window_height";
			public static final String MAIN_WINDOW_X = "main_window_x_offset";
			public static final String MAIN_WINDOW_Y = "main_window_y_offset";
			
			// Help System	[srk] 8/11/01 3:17PM
			public static final String USER_GUIDE_PATH = "user_guide_path";
			public static final String DEVELOPER_GUIDE_PATH = "developer_guide_path";
			public static final String BOOKMARKS_PATH = "bookmarks_path";
			public static final String TUTORIALS_PATH = "tutorials_path";
			public static final String ABOUT_PATH = "about_path";
			
			// InnerFrame State Maintenance [srk]
			public static final String FRAME_INFO_LIST_SIZE = "frame_info_list_size";
			
			
		// Editor Panel
		public static final String FONT_FACE = "font_face";
		public static final String FONT_SIZE = "font_size";
		public static final String LINE_WRAP = "line_wrap";
		public static final String UNDO_QUEUE_SIZE = "undo_queue_size";
		public static final String SHOW_LINE_NUMBERS = "show_line_numbers";
		public static final String SINGLE_CLICK_EXPAND = "single_click_expand";
		public static final String SHOW_INDICATORS = "show_indicators";
		public static final String SHOW_ATTRIBUTES = "show_attributes";
		public static final String APPLY_FONT_STYLE_FOR_COMMENTS = "apply_font_style_for_comments";
		public static final String APPLY_FONT_STYLE_FOR_EDITABILITY = "apply_font_style_for_editability";
		public static final String APPLY_FONT_STYLE_FOR_MOVEABILITY = "apply_font_style_for_moveability";
		public static final String USE_CREATE_MOD_DATES = "use_create_mod_dates";
		public static final String CREATE_MOD_DATES_FORMAT = "create_mod_dates_format";
		
		// Look & Feel Panel
		public static final String DESKTOP_BACKGROUND_COLOR = "desktop_background_color";
		public static final String PANEL_BACKGROUND_COLOR = "panel_background_color";
		public static final String TEXTAREA_BACKGROUND_COLOR = "textarea_background_color";
		public static final String TEXTAREA_FOREGROUND_COLOR = "textarea_foreground_color";
		public static final String TEXTAREA_COMMENT_COLOR = "textarea_comment_color";
		public static final String SELECTED_CHILD_COLOR = "selected_child_color";
		public static final String LINE_NUMBER_COLOR = "line_number_color";
		public static final String LINE_NUMBER_SELECTED_COLOR = "line_number_selected_color";
		public static final String LINE_NUMBER_SELECTED_CHILD_COLOR = "line_number_selected_child_color";
		public static final String INDENT = "indent";
		public static final String VERTICAL_SPACING = "vertical_spacing";
		public static final String LEFT_MARGIN = "left_margin";
		public static final String TOP_MARGIN = "top_margin";
		public static final String RIGHT_MARGIN = "right_margin";
		public static final String BOTTOM_MARGIN = "bottom_margin";
		public static final String DOCUMENT_TITLES_NAME_FORM = "document_titles_name_form";
		
		// Open & Save Panel
		public static final String FILE_PROTOCOL = "file_protocol";
		public static final String SAVE_LINE_END = "save_line_end";
		
		public static final String OPEN_ENCODING = "open_encoding";
		public static final String SAVE_ENCODING = "save_encoding";
		public static final String IMPORT_ENCODING = "import_encoding";
		public static final String EXPORT_ENCODING = "export_encoding";
		
		public static final String OPEN_FORMAT = "open_format";
		public static final String SAVE_FORMAT = "save_format";
		public static final String IMPORT_FORMAT = "import_format";
		public static final String EXPORT_FORMAT = "export_format";
		
		// File Format: Justified Plaintext
		public static final String JUSTIFIED_PLAINTEXT_COL_WIDTH = "justified_plaintext_col_width";
		public static final String JUSTIFIED_PLAINTEXT_DRAW_LINES = "justified_plaintext_draw_lines";
		public static final String JUSTIFIED_PLAINTEXT_NUMBER = "justified_plaintext_number";
		
		// Recent Files List Options
		public static final String RECENT_FILES_LIST_SIZE = "recent_files_list_size";
		
		public static final String RECENT_FILES_ORDERING = "recent_files_ordering";
		public static final String RECENT_FILES_DIRECTION = "recent_files_direction";
		public static final String RECENT_FILES_NAME_FORM= "recent_files_name_form";
		
		public static final String RF_D_BOTTOMTOTOP= "rf_d_bottomtotop";
		public static final String RF_D_TOPTOBOTTOM= "rf_d_toptobottom";
		
		public static final String RF_NF_FILENAME= "rf_nf_filename";
		public static final String RF_NF_FULL_PATHNAME= "rf_nf_full_pathname";
		public static final String RF_NF_TRUNC_PATHNAME= "rf_nf_trunc_pathname";
		
		public static final String RF_O_ALPHABETICAL= "rf_o_alphabetical";
		public static final String RF_O_ASCII= "rf_o_ascii";
		public static final String RF_O_CHRONOLOGICAL= "rf_o_chronological";
		
		// Misc Panel
		public static final String PRINT_ENVIRONMENT = "print_environment";
		public static final String NEW_DOC_ON_STARTUP = "new_doc_on_startup";
		public static final String OPEN_DOCS_ON_STARTUP = "open_docs_on_startup";
		public static final String MOUSE_WHEEL_SCROLL_SPEED = "mouse_wheel_scroll_speed";
		public static final String OWNER_NAME = "owner_name";
		public static final String OWNER_EMAIL = "owner_email";
		public static final String TRIM_ENABLED_FOR_MERGE_WITH_DELIMITER = "trim_for_merge_with_delimiter";
		public static final String INCLUDE_EMPTY_NODES_FOR_MERGE_WITH_DELIMITER = "include_empty_nodes_for_merge_with_delimiter";
		public static final String MERGE_DELIMITER = "delimiter_for_merge_with_delimiter";
		
		// WebFile
		public static final String WEB_FILE_URL = "web_file_url";
		public static final String WEB_FILE_USER = "web_file_user";
		public static final String WEB_FILE_PASSWORD = "web_file_password";
		
	// End Preference Keys
	
	
	// The Constructors
	public Preferences() {
		TXT_WORDS = GUITreeLoader.reg.getText("wrap_words");
		TXT_CHARACTERS = GUITreeLoader.reg.getText("wrap_characters");
		LINE_WRAP_OPTIONS[0] = TXT_WORDS;
		LINE_WRAP_OPTIONS[1] = TXT_CHARACTERS;
		
		RECENT_FILES_ORDERINGS[0] = GUITreeLoader.reg.getText(RF_O_CHRONOLOGICAL); 
		RECENT_FILES_ORDERINGS[1] = GUITreeLoader.reg.getText(RF_O_ALPHABETICAL); 
		RECENT_FILES_ORDERINGS[2] = GUITreeLoader.reg.getText(RF_O_ASCII);
		
		RECENT_FILES_NAME_FORMS[0] = GUITreeLoader.reg.getText(RF_NF_FULL_PATHNAME);
		RECENT_FILES_NAME_FORMS[1] = GUITreeLoader.reg.getText(RF_NF_TRUNC_PATHNAME); 
		RECENT_FILES_NAME_FORMS[2] = GUITreeLoader.reg.getText(RF_NF_FILENAME);
		
		RECENT_FILES_DIRECTIONS[0] = GUITreeLoader.reg.getText(RF_D_TOPTOBOTTOM); 
		RECENT_FILES_DIRECTIONS[1] = GUITreeLoader.reg.getText(RF_D_BOTTOMTOTOP);
		
		// Place a reference to this object in the outliner
		Outliner.prefs = this;
	}
	
	
	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}
	
	public void startSetup(Attributes atts) {
		Outliner.loadPrefsFile(Outliner.PARSER, Outliner.CONFIG_FILE);	
	}
	
	public void endSetup(Attributes atts) {}
	
	
	// PreferencesPanel Registry
	private HashMap prefsPanelReg = new HashMap();
	
	public void addPreferencesPanel(String key, PreferencesPanel prefPanel) {
		prefsPanelReg.put(key, prefPanel);
	}
	
	public PreferencesPanel getPreferencesPanel(String key) {
		return (PreferencesPanel) prefsPanelReg.get(key);
	}
	
	public Iterator getPreferencesPanelKeys() {
		return prefsPanelReg.keySet().iterator();
	}
	
	
	// Temporary values from file loading. The pref string values from the prefs file
	// are first stored here. Then each preference pulls its value from here as it is
	// loaded. This allows modules which are loaded later to still use the same prefs
	// as the rest of the application.
	private HashMap tempValues = new HashMap();
	
	public void addTempValue(String key, String value) {
		tempValues.put(key, value);
	}
	
	public String getTempValue(String key) {
		return (String) tempValues.get(key);
	}
	
	
	// Preferences Registry
	private HashMap prefsReg = new HashMap();
	
	public void addPreference(String key, Preference pref) {
		if (key != null) {
			prefsReg.put(key, pref);
		} else {
			System.out.println("Warning: attempt addPreference with a NULL key");
		}
	}
	
	public Preference getPreference(String key) {
		if (key != null) {
			return (Preference) prefsReg.get(key);
		} else {
			return null;
		}
	}
	
	public Iterator getPreferenceKeys() {
		return prefsReg.keySet().iterator();
	}
	
	
	// Static Registry Accessors
	public static PreferenceBoolean getPreferenceBoolean(String key) {
		return (PreferenceBoolean) Outliner.prefs.getPreference(key);
	}
	
	public static PreferenceInt getPreferenceInt(String key) {
		return (PreferenceInt) Outliner.prefs.getPreference(key);
	}
	
	public static PreferenceString getPreferenceString(String key) {
		return (PreferenceString) Outliner.prefs.getPreference(key);
	}
	
	public static PreferenceColor getPreferenceColor(String key) {
		return (PreferenceColor) Outliner.prefs.getPreference(key);
	}
	
	public static PreferenceLineEnding getPreferenceLineEnding(String key) {
		return (PreferenceLineEnding) Outliner.prefs.getPreference(key);
	}
	
	public static PreferenceStringList getPreferenceStringList(String key) {
		return (PreferenceStringList) Outliner.prefs.getPreference(key);
	}
	
	public static PreferenceHashMap getPreferenceHashMap(String key) {
		return (PreferenceHashMap) Outliner.prefs.getPreference(key);
	}	
	
	// Syncing Preferences
	public static void restoreCurrentToDefault() {
		Iterator it = Outliner.prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Outliner.prefs.getPreference(key).restoreCurrentToDefault();
		}
	}
	
	public static void restoreTemporaryToDefault() {
		Iterator it = Outliner.prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Outliner.prefs.getPreference(key).restoreTemporaryToDefault();
		}
	}
	
	public static void restoreTemporaryToCurrent() {
		Iterator it = Outliner.prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Outliner.prefs.getPreference(key).restoreTemporaryToCurrent();
		}
	}
	
	public static void applyTemporaryToCurrent() {
		Iterator it = Outliner.prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Outliner.prefs.getPreference(key).applyTemporaryToCurrent();
		}
	}
	
	public static void applyCurrentToApplication() {
		Iterator it = Outliner.prefs.getPreferencesPanelKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Outliner.prefs.getPreferencesPanel(key).applyCurrentToApplication();
		}
	}
	
	
	// Saving Config File
	public static void saveConfigFile(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			fw.write(prepareConfigFile());
			fw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, GUITreeLoader.reg.getText("message_could_not_save_preferences") + ": " + e);
		}
	}
	
	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();
		
		Iterator it = Outliner.prefs.getPreferenceKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Preference pref = Outliner.prefs.getPreference(key);
			buffer.append(Outliner.COMMAND_SET);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(pref.getCommand());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(StringTools.escape(pref.toString(), '\\', null));
			buffer.append(PlatformCompatibility.LINE_END_DEFAULT);
		}
		
		return buffer.toString();
	}
}
