/**
 * Portions opyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions opyright (C) 2002  Stan Krute <Stan@StanKrute.com>
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
 
package com.organic.maynard.outliner.io;

import com.organic.maynard.outliner.menus.*;
import com.organic.maynard.outliner.menus.file.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2004/01/20 08:12:23 $
 */

public class FileProtocolManager {
	
	private static final boolean VERBOSE = false;
	
	// Instance Fields
	private ArrayList protocols = new ArrayList();
	private FileProtocol defaultProtocol = null;
	
	
	// The Constructor
	public FileProtocolManager() {}
	
	public void createFileProtocol(String protocolName, String className) {
		try {
			Class theClass = Class.forName(className);
			
			FileProtocol fileProtocol = (FileProtocol) theClass.newInstance();
			fileProtocol.setName(protocolName);
			
			boolean success = addProtocol(fileProtocol);
			
			if (success) {
				if (VERBOSE) {
					System.out.println("  File Protocol: " + className + " -> " + protocolName);
				}
			} else {
				System.out.println("  Duplicate File Protocol Name: " + protocolName);
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// Accessors
	public FileProtocol getDefault() {
		return defaultProtocol;
	}
	
	public void setDefault(FileProtocol defaultProtocol) {
		this.defaultProtocol = defaultProtocol;
	}
	
	public boolean addProtocol(FileProtocol protocol) {
		if (isNameUnique(protocol)) {
			protocols.add(protocol);
			
			// Also add it to the list of protocols stored in the preferences
			Preferences.FILE_PROTOCOLS.add(protocol.getName());
			
			return true;
		}
		return false;
	}
	
	public FileProtocol getProtocol(String protocolName) {
		for (int i = 0, limit = protocols.size(); i < limit; i++) {
			FileProtocol protocol = (FileProtocol) protocols.get(i);
			if (protocol.getName().equals(protocolName)) {
				return protocol;
			}
		}
		return null;
	}
	
	public boolean removeProtocol(String protocolName) {
		for (int i = 0, limit = protocols.size(); i < limit; i++) {
			FileProtocol protocol = (FileProtocol) protocols.get(i);
			if (protocol.getName().equals(protocolName)) {
				protocols.remove(i);
				
				// Also remove it from the list of formats stored in the preferences
				Preferences.FILE_PROTOCOLS.remove(i);
				
				return true;
			}
		}
		return false;
	}
	
	// Synchronized default to current prefs state.
	public void synchronizeDefault() {
		String protocolName = Preferences.getPreferenceString(Preferences.FILE_PROTOCOL).cur;
		
		FileProtocol protocol = getProtocol(protocolName);
		
		setDefault(protocol);
	}
	
	public void synchonizeDefaultMenuItem() {
		OutlinerSubMenuItem openMenu = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.OPEN_MENU_ITEM);
		OpenFileMenuItem openMenuItem = (OpenFileMenuItem) openMenu.getItem(0);
		openMenuItem.setProtocol(getDefault());
		
		OutlinerSubMenuItem importMenu = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.IMPORT_MENU_ITEM);
		ImportFileMenuItem importMenuItem = (ImportFileMenuItem) importMenu.getItem(0);
		importMenuItem.setProtocol(getDefault());
		
		OutlinerSubMenuItem saveAsMenu = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_AS_MENU_ITEM);
		SaveAsFileMenuItem saveAsMenuItem = (SaveAsFileMenuItem) saveAsMenu.getItem(0);
		saveAsMenuItem.setProtocol(getDefault());
		
		OutlinerSubMenuItem exportMenu = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.EXPORT_MENU_ITEM);
		ExportFileMenuItem exportMenuItem = (ExportFileMenuItem) exportMenu.getItem(0);
		exportMenuItem.setProtocol(getDefault());
		
		OutlinerSubMenuItem exportSelectionMenu = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.EXPORT_SELECTION_MENU_ITEM);
		ExportSelectionFileMenuItem exportSelectionMenuItem = (ExportSelectionFileMenuItem) exportSelectionMenu.getItem(0);
		exportSelectionMenuItem.setProtocol(getDefault());
	}
	
	// Menu Synchronization
	public void synchronizeMenus() {
		// Get SubMenu Items
		OutlinerSubMenuItem openMenuItem = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.OPEN_MENU_ITEM);
		OutlinerSubMenuItem importMenuItem = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.IMPORT_MENU_ITEM);
		OutlinerSubMenuItem saveAsMenuItem = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_AS_MENU_ITEM);
		OutlinerSubMenuItem exportMenuItem = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.EXPORT_MENU_ITEM);
		OutlinerSubMenuItem exportSelectionMenuItem = (OutlinerSubMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.EXPORT_SELECTION_MENU_ITEM);
		
		// Add Default Protocol
		FileProtocol def = getDefault();
		
		if (def != null) {
			JMenuItem openItem = new OpenFileMenuItem(def);
			openItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
			openMenuItem.add(openItem);
			importMenuItem.add(new ImportFileMenuItem(def));
			saveAsMenuItem.add(new SaveAsFileMenuItem(def));
			exportMenuItem.add(new ExportFileMenuItem(def));
			exportSelectionMenuItem.add(new ExportSelectionFileMenuItem(def));
		}
		
		// Add separators
		openMenuItem.addSeparator();
		importMenuItem.addSeparator();
		saveAsMenuItem.addSeparator();
		exportMenuItem.addSeparator();
		exportSelectionMenuItem.addSeparator();
		
		
		// Add list of all protocols
		for (int i = 0, limit = protocols.size(); i < limit; i++) {
			FileProtocol protocol = (FileProtocol) protocols.get(i);
			
			openMenuItem.add(new OpenFileMenuItem(protocol));
			importMenuItem.add(new ImportFileMenuItem(protocol));
			saveAsMenuItem.add(new SaveAsFileMenuItem(protocol));
			exportMenuItem.add(new ExportFileMenuItem(protocol));
			exportSelectionMenuItem.add(new ExportSelectionFileMenuItem(protocol));
		}
	}
	
	
	// Utility Methods
	private boolean isNameUnique(FileProtocol protocolToCheck) {
		for (int i = 0, limit = protocols.size(); i < limit; i++) {
			FileProtocol protocol = (FileProtocol) protocols.get(i);
			if (protocol.getName().equals(protocolToCheck.getName())) {
				return false;
			}
		}
		return true;
	}
}