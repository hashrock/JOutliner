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
 
package com.organic.maynard.outliner.menus.file;

import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.scripting.script.*;
import com.organic.maynard.outliner.menus.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import org.xml.sax.*;
import java.util.*;
import com.organic.maynard.xml.XMLTools;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.outliner.model.propertycontainer.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2004/03/22 04:48:03 $
 */

public class QuitMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		addActionListener(this);
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		quit();
	}
	
	
	// Static Methods
	public static void quit() {
		
		// Store List of Open Documents and close documents
		if (!saveOpenDocumentList()) {
			return;
		}
		
		// Store current window position
		Dimension size = Outliner.outliner.getSize();
		Point location = Outliner.outliner.getLocation();
		
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_W).cur = size.width;
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_H).cur = size.height;
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_X).cur = location.x;
		Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_Y).cur = location.y;
		
		// Hide Desktop
		Outliner.outliner.setVisible(false);
		//Outliner.outliner.dispose();
		
		// Save config and quit
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
		RecentFilesList.saveConfigFile(Outliner.RECENT_FILES_FILE);
		if (Outliner.findReplace.isInitialized()) {
			Outliner.findReplace.model.saveConfigFile();
		}
		LoadScriptCommand.saveConfigFile(new File(Outliner.SCRIPTS_FILE));
		
		// Run shutdown scripts. This is the last thing we do before quitting.
		ScriptsManagerModel.runShutdownScripts();
		
		System.exit(0);
	}
	
	/**
	 * Saves a List of the current open documents to disk so that they can be
	 * re-opened when JOE is relaunched.
	 */
	private static boolean saveOpenDocumentList() {
		// Harvest Data to save and close each document
		ArrayList openFileList = new ArrayList();
		Iterator it = Outliner.documents.getDefaultOpenDocumentIterator();
		while (it.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) it.next();
			
			if (OutlinerWindowMonitor.closeInternalFrame(doc)) {
				if (!doc.isModified()) { // Don't store untitled docs that haven't been saved.
					openFileList.add(doc.getDocumentInfo());
				}
			} else {
				return false;
			}
		}
		
		// Save the file
		StringBuffer buf = new StringBuffer();
		buf.append(XMLTools.getXMLDeclaration());
		String line_ending = "\n";
		buf.append(line_ending);
		PropertyContainerUtil.writeXML(buf, openFileList, 0, line_ending);
		try {
			FileTools.dumpStringToFile(new File(Outliner.OPEN_FILES_FILE), buf.toString(), "UTF-8");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return true;
	}
}