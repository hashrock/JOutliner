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
 
package com.organic.maynard.outliner.scripting.macro;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.menus.popup.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.util.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;

/**
 * Loads the macro instances from the "macros.txt" file and loads them
 * into the MacroManager and MacroPopupMenu.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2004/02/16 01:30:42 $
 */

public class LoadMacroCommand extends Command {
	
	private static final boolean VERBOSE = false;
	
	// Constants
	private static final String EXTENSION_SEPARATOR = ".";
	
	
	// The Constructors
	public LoadMacroCommand(String name) {
		super(name);
	}
	
	
	public void execute(ArrayList signature) {
		String path = (String) signature.get(1);
		String className = (String) signature.get(2);
		
		// BACKWARDS COMPATIBILITY: 1.8.10.2 -> 1.8.10.3
		// Convert classnames from: com.organic.maynard.outliner.*
		// to: com.organic.maynard.outliner.scripting.macro.*
		if (className.matches("^com\\.organic\\.maynard\\.outliner\\.\\w+$")) {
			System.out.println("Doing classname conversion for macro for 1.8.10.3+ compatibility.");
			System.out.println("  Classname before conversion: " + className);
			className = className.replaceFirst("^com\\.organic\\.maynard\\.outliner\\.","com.organic.maynard.outliner.scripting.macro.");
			System.out.println("  Classname after conversion: " + className);
		}
		
		try {
			// Create Instance
			Macro obj = (Macro) Class.forName(className).newInstance();
			
			// Set Macro's Name
			int end = path.lastIndexOf(EXTENSION_SEPARATOR);
			if (end == -1) {
				obj.setName(path);
			} else {
				obj.setName(path.substring(0, end));
			}
			
			// Initialize it, abort if failed.
			if (!obj.init(new File(new StringBuffer().append(Outliner.MACROS_DIR).append(path).toString()))) {
				return;
			}
			
			// Add it to the MacroPopupMenu
			if (MacroPopupMenu.validateUniqueness(obj.getName()) && MacroPopupMenu.validateRestrictedChars(obj.getName())) {
				if (VERBOSE) {
					System.out.println(new StringBuffer().append("  ").append(path).toString());
				}
				int i = Outliner.macroPopup.addMacro(obj);
				
				// Add it to the list in the MacroManager
				if (obj instanceof SortMacro) {
					((DefaultListModel) Outliner.macroManager.sortMacroList.getModel()).insertElementAt(obj.getName(),i);
				} else {
					((DefaultListModel) Outliner.macroManager.macroList.getModel()).insertElementAt(obj.getName(),i);
				}
			} else {
				System.out.println("  WARNING: duplicate macro entry: " + path);
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	// Config File
	public static void saveConfigFile(File file) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(prepareConfigFile());
			fw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, GUITreeLoader.reg.getText("message_could_not_save_macros_config") + ": " + e);
		}
	}
	
	// Need to fix. [md] I don't see what's wrong anymore?
	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, limit = MacroPopupMenu.macros.size(); i < limit; i++) {
			Macro macro = (Macro) MacroPopupMenu.macros.get(i);
			
			buffer.append(Outliner.COMMAND_MACRO);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(macro.getFileName());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(macro.getClass().getName());
			buffer.append(System.getProperty("line.separator"));
		}
		
		for (int i = 0, limit = MacroPopupMenu.sortMacros.size(); i < limit; i++) {
			Macro macro = (Macro) MacroPopupMenu.sortMacros.get(i);
			
			buffer.append(Outliner.COMMAND_MACRO);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(macro.getFileName());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(macro.getClass().getName());
			buffer.append(System.getProperty("line.separator"));
		}
		return buffer.toString();
	}
}