/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

package com.organic.maynard.outliner.guitree;

import com.organic.maynard.outliner.*;

import java.util.HashMap;
import com.organic.maynard.util.string.Replace;

/**
 * This class serves two funtions. Firstly, it stores GUITreeComponent objects
 * so that they can be accessed by the rest of the application in one centralized
 * place by name. Second, it stores text assets so they too, can be accessed from
 * a single location.
 *
 * A GUITreeComponentRegistry is typically populated with components and text by
 * a GUITreeLoader as the loader parses a gui_tree.xml file. In the Outliner
 * application, there are different gui_tree.xml files for each language supported.
 * These are named like this: gui_tree.en.xml, gui_tree.ja.xml, etc. A language
 * argument provided at startup is used to choose the appropriate file.
 *
 * Note: If you create or modify a gui_tree.xml file make sure you save it as UTF-8
 * since that will correctly preserve any non-ascii characters you use.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2002/07/16 21:25:29 $
 */

public class GUITreeComponentRegistry {

	// Constants
	
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String GOTO_MENU_ITEM = "goto";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String FIND_MENU_ITEM = "find";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String STACK_MENU_ITEM = "stack";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String RECENT_FILE_MENU = "recent_file_list";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OPEN_MENU_ITEM = "open";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String SAVE_MENU_ITEM = "save";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String SAVE_AS_MENU_ITEM = "save_as";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String SAVE_ALL_MENU_ITEM = "save_all";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String REVERT_MENU_ITEM = "revert";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String EXPORT_MENU_ITEM = "export";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String EXPORT_SELECTION_MENU_ITEM = "export_selection";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String IMPORT_MENU_ITEM = "import";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String CLOSE_MENU_ITEM = "close";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String CLOSE_ALL_MENU_ITEM = "close_all";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String QUIT_MENU_ITEM = "quit";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String UNDO_MENU_ITEM = "undo";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String REDO_MENU_ITEM = "redo";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String UNDO_ALL_MENU_ITEM = "undo_all";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String REDO_ALL_MENU_ITEM = "redo_all";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String CUT_MENU_ITEM = "cut";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COPY_MENU_ITEM = "copy";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PASTE_MENU_ITEM = "paste";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String DELETE_MENU_ITEM = "delete";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String SELECT_ALL_MENU_ITEM = "select_all";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String SELECT_NONE_MENU_ITEM = "select_none";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String SELECT_INVERSE_MENU_ITEM = "select_inverse";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String EDIT_DOCUMENT_SETTINGS_MENU_ITEM = "edit_document_settings";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String EDIT_DOCUMENT_ATTRIBUTES_MENU_ITEM = "edit_document_attributes";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_TOGGLE_ATTRIBUTES_MENU_ITEM = "toggle_attributes";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_TOGGLE_COMMENT_MENU_ITEM = "toggle_comment";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_TOGGLE_EXPANSION_MENU_ITEM = "toggle_expansion";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_EXPAND_ALL_SUBHEADS_MENU_ITEM = "expand_all_subheads";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_EXPAND_EVERYTHING_MENU_ITEM = "expand_everything";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_COLLAPSE_TO_PARENT_MENU_ITEM = "collapse_to_parent";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_COLLAPSE_EVERYTHING_MENU_ITEM = "collapse_everything";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_MOVE_UP_MENU_ITEM = "move_up";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_MOVE_DOWN_MENU_ITEM = "move_down";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_MOVE_RIGHT_MENU_ITEM = "move_right";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_MOVE_LEFT_MENU_ITEM = "move_left";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_PROMOTE_MENU_ITEM = "promote";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_DEMOTE_MENU_ITEM = "demote";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_MERGE_MENU_ITEM = "merge";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_MERGE_WITH_SPACES_MENU_ITEM = "merge_with_spaces";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_HOIST_MENU_ITEM = "hoist";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_DEHOIST_MENU_ITEM = "dehoist";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String OUTLINE_DEHOIST_ALL_MENU_ITEM = "dehoist_all";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PREFERENCES = "prefs";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PREFERENCES_FRAME = "preferences_frame";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PREFERENCES_PANEL_EDITOR = "preferences_panel_editor";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PREFERENCES_PANEL_MISC = "preferences_panel_misc";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PREFERENCES_PANEL_OPEN_AND_SAVE = "preferences_panel_open_and_save";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PREFERENCES_PANEL_LOOK_AND_FEEL = "preferences_panel_look_and_feel";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String PREFERENCES_PANEL_RECENT_FILES = "preferences_panel_recent_files";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_FONT_FACE = "font_face_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_LINE_WRAP = "line_wrap_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_FILE_PROTOCOL = "file_protocol_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_LINE_ENDING = "line_end_component";
	
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_ENCODING_WHEN_OPENING = "open_encoding_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_ENCODING_WHEN_IMPORTING = "import_encoding_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_ENCODING_WHEN_SAVING = "save_encoding_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_ENCODING_WHEN_EXPORTING = "export_encoding_component";
	
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_FORMAT_WHEN_OPENING = "open_format_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_FORMAT_WHEN_IMPORTING = "import_format_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_FORMAT_WHEN_SAVING = "save_format_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_FORMAT_WHEN_EXPORTING = "export_format_component";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_DOCUMENT_TITLES_NAME_FORM = "document_titles_name_form_component";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_RECENT_FILES_ORDERING = "recent_files_ordering_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_RECENT_FILES_DIRECTION = "recent_files_direction_component";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String COMPONENT_RECENT_FILES_NAME_FORM = "recent_files_name_form_component";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String JDIALOG_DOCUMENT_SETTINGS_VIEW = "document_settings_view";

	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String BSH_CONSOLE = "bsh_console";
	/** An ID used to reference a GUITreeComponent so we don't hardcode a bunch of strings throughout the application. */
	public static final String RUN_AS_BSH_SCRIPT_MENU_ITEM = "run_as_bsh_script";


	// Other Constants
	
	/** String to use a a placeholder for the first dynamic chunk in a text asset. */
	public static final String PLACEHOLDER_1 = "{$value_1}";
	
	/** String to use a a placeholder for the second dynamic chunk in a text asset. */
	public static final String PLACEHOLDER_2 = "{$value_2}";
	
	/** String to use a a placeholder for the third dynamic chunk in a text asset. */
	public static final String PLACEHOLDER_3 = "{$value_3}";


	// Instance Fields
	private HashMap reg = new HashMap();
	private HashMap textResources = new HashMap();


	// Constructors
	public GUITreeComponentRegistry() {}


	/**
	 * Puts a GUITreeComponent into this registry. The key used to store
	 * the component is taken from the getGUITreeComponentID() method of
	 * the component you are adding.
	 *
	 * @param comp the GUITreeComponent being added.
	 */
	public void add(GUITreeComponent comp) {
		reg.put(comp.getGUITreeComponentID(), comp);
	}

	/**
	 * Gets a GUITreeComponent from this registry.
	 *
	 * @param name the unique name for the GUITreeComponent you want to get.
	 *             This name corresponds to the componentID of the
	 *             GUITreeComponent you want to get.
	 * @return the GUITreeComponent, or null if none found.
	 */
	public GUITreeComponent get(String name) {
		return (GUITreeComponent) reg.get(name);
	}

	/**
	 * Puts a text asset into this text resource repository. If the key already
	 * exists, it's value is overwritten and a warning message is printed to 
	 * the console. Also, the strings "\n" and "\\" in the text asset are 
	 * replaced with a line feed and '\' character respectively.
	 *
	 * @param key the unique reference for the text asset you are adding.
	 * @param value the text asset being added.
	 */
	public void addText(String key, String value) {
		value = Replace.replace(value,"\\n", "\n");
		value = Replace.replace(value,"\\\\", "\\");

		if (textResources.get(key) != null) {
			System.out.println("WARNING: Writing over existing text repository key: " + key);
		}

		textResources.put(key, value);
	}

	/**
	 * Gets a text asset from this text resource repository.
	 *
	 * @param key the unique reference for the text asset you want to get.
	 * @return the text asset.
	 */
	public String getText(String key) {
		String retVal = (String) textResources.get(key);
		if (retVal == null) {
			System.out.println("Invalid text resource key: " + key);
		}
		return retVal;
	}
}
