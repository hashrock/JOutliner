/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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
 
/**
 * @last touched by $Author: maynardd $
 * @version $Revision: 1.28 $, $Date: 2004/01/31 00:33:09 $
 */

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.menus.file.*;
import com.organic.maynard.outliner.guitree.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Rectangle;
import com.organic.maynard.util.string.Replace;
import com.organic.maynard.outliner.util.Ginsu;

public class OutlinerWindowMonitor extends InternalFrameAdapter {
	
	public void internalFrameClosing(InternalFrameEvent e) {
		closeInternalFrame(e.getInternalFrame());
	}
	
	public static boolean closeInternalFrame(JInternalFrame w) {
		// grab a copy of the document ref
		OutlinerDocument doc = (OutlinerDocument) w;
		
		// we may have to send messages
		String msg = null;
		
		// if the document is modified ....
		if (doc.isModified()) {
			
			// if it's untitled, do a Save As ...
			if (doc.getFileName().equals("")) {
				if (doc.tree.isDocumentEmpty() && doc.getUndoQueue().isEmpty()) {
					// Do Nothing since it doesn't look like the user has touched the document.
				} else {
					// set up dialog
					msg = GUITreeLoader.reg.getText("error_window_monitor_untitled_save_changes");
					msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, doc.getTitle());
					
					// run dialog
					int result = JOptionPane.showConfirmDialog(doc, msg);
					
					// deal with dialog results
					if (result == JOptionPane.YES_OPTION) {
						SaveAsFileMenuItem.saveAsOutlinerDocument(doc, Outliner.fileProtocolManager.getDefault());
					} else if (result == JOptionPane.NO_OPTION) {
						// Do Nothing
					} else if (result == JOptionPane.CANCEL_OPTION) {
						return false;
					}
				}
				
			// else if it's not imported, do a Save
			} else if (!PropertyContainerUtil.getPropertyAsBoolean(doc.getDocumentInfo(), DocumentInfo.KEY_IMPORTED)) {
				// set up dialog
				msg = GUITreeLoader.reg.getText("error_window_monitor_untitled_save_changes");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, doc.getFileName());
				
				// run dialog
				int result = JOptionPane.showConfirmDialog(doc, msg);
				
				// deal with dialog results
				if (result == JOptionPane.YES_OPTION) {
					SaveFileMenuItem item = (SaveFileMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_MENU_ITEM);
					item.saveOutlinerDocument(doc);
				} else if (result == JOptionPane.NO_OPTION) {
					// Do Nothing
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return false;
				}
				
			// else it IS imported, do a Save As
			} else {
				// set up dialog
				msg = GUITreeLoader.reg.getText("error_window_monitor_untitled_save_changes");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, doc.getFileName());
				
				// run dialog
				int result = JOptionPane.showConfirmDialog(doc, msg);
				
				// deal with dialog results
				if (result == JOptionPane.YES_OPTION) {
					SaveAsFileMenuItem.saveAsOutlinerDocument(doc, Outliner.fileProtocolManager.getProtocol(PropertyContainerUtil.getPropertyAsString(doc.getDocumentInfo(), DocumentInfo.KEY_PROTOCOL_NAME)));
				} else if (result == JOptionPane.NO_OPTION) {
					// Do Nothing
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return false;
				}
			}
		}
		
		// Record current state into DocumentInfo in the RecentFileList if we can
		DocumentInfo docInfo = RecentFilesList.getDocumentInfo(doc.getFileName());
		if (docInfo != null) {
			docInfo.recordWindowPositioning(doc);
		}
		
		// Hide the document
		doc.setVisible(false);
		
		// Remove the document.
		Outliner.documents.removeDocument(doc);
		
		// Explicitly Destroy since Swing has problems letting go.
		// Seems to make a difference when we also use -Xincgc.
		Ginsu.sliceAndDice(doc);
		
		return true;
	}
}