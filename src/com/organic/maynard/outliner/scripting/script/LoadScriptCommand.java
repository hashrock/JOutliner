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
 
package com.organic.maynard.outliner.scripting.script;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.menus.popup.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.util.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;

/**
 * Loads the script instances from the "scripts.txt" file and loads them
 * into the ScriptsManager.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/03/13 06:31:31 $
 */

public class LoadScriptCommand extends Command {
	
	private static final boolean VERBOSE = false;
	
	// Constants
	private static final String EXTENSION_SEPARATOR = ".";
	
	
	// The Constructors
	public LoadScriptCommand(String name) {
		super(name);
	}
	
	
	public void execute(ArrayList signature) {
		String path = (String) signature.get(1);
		String className = (String) signature.get(2);
		
		// BACKWARDS COMPATIBILITY: 1.8.10.2 -> 1.8.10.3
		// Convert classnames from: com.organic.maynard.outliner.*
		// to: com.organic.maynard.outliner.scripting.macro.*
		if (className.matches("^com\\.organic\\.maynard\\.outliner\\.\\w+$")) {
			System.out.println("Doing classname conversion for script for 1.8.10.3+ compatibility.");
			System.out.println("  Classname before conversion: " + className);
			className = className.replaceFirst("^com\\.organic\\.maynard\\.outliner\\.","com.organic.maynard.outliner.scripting.macro.");
			System.out.println("  Classname after conversion: " + className);
		}
		
		boolean isStartupScript = false;
		if (signature.size() > 3) {
			isStartupScript = (new Boolean((String) signature.get(3))).booleanValue();
		}
		
		boolean isShutdownScript = false;
		if (signature.size() > 4) {
			isShutdownScript = (new Boolean((String) signature.get(4))).booleanValue();
		}
		
		try {
			// Turn path into a File
			File file = new File(Outliner.SCRIPTS_DIR + path);
			
			// Create Instance
			Script obj = (Script) Class.forName(className).newInstance();
			obj.setStartupScript(isStartupScript);
			obj.setShutdownScript(isShutdownScript);
			
			// Initialize it
			int end = path.lastIndexOf(EXTENSION_SEPARATOR);
			if (end == -1) {
				end = path.length();
			}
			obj.setName(path.substring(0, end));
			boolean success = obj.init(file);
			
			if (!success) {
				return;
			}
			
			// Add it to the Model
			if (ScriptsManagerModel.validateUniqueness(obj.getName()) && MacroPopupMenu.validateRestrictedChars(obj.getName())) {
				if (VERBOSE) {
					System.out.println(new StringBuffer().append("  ").append(path).toString());
				}
				int i = Outliner.scriptsManager.model.add(obj);
			} else {
				System.out.println("  WARNING: duplicate script entry: " + path);
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
			JOptionPane.showMessageDialog(null, GUITreeLoader.reg.getText("message_could_not_save_scripts_config") + ": " + e);
		}
	}
	
	// Need to fix. [md] I don't see what's wrong anymore?
	private static String prepareConfigFile() {
		StringBuffer buffer = new StringBuffer();
		ScriptsManagerModel model = Outliner.scriptsManager.model;
		
		for (int i = 0; i < model.getSize(); i++) {
			Script script = (Script) model.get(i);
			
			buffer.append(Outliner.COMMAND_SCRIPT);
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.getFileName());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.getClass().getName());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.isStartupScript());
			buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);
			buffer.append(script.isShutdownScript());
			buffer.append(System.getProperty("line.separator"));
		}
		
		return buffer.toString();
	}
}