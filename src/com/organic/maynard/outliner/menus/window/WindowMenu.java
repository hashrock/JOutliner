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
 
package com.organic.maynard.outliner.menus.window;

import com.organic.maynard.outliner.menus.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:42 $
 */

public class WindowMenu extends AbstractOutlinerMenu implements DocumentRepositoryListener, ActionListener, GUITreeComponent {
	
	// Class Fields
	protected static int WINDOW_LIST_START = -1;
	protected static int indexOfOldSelection = -1;
	
	
	// DocumentRepositoryListener Interface
	public void documentAdded(DocumentRepositoryEvent e) {
		// Enable menu since we've got at least one document now.
		setEnabled(true);
		
		// Add WindowMenuItem
		OutlinerDocument document = (OutlinerDocument) e.getDocument();
		WindowMenuItem item = new WindowMenuItem(document.getTitle(),document);
		item.addActionListener(this);
		add(item);
	}
	
	public void documentRemoved(DocumentRepositoryEvent e) {
		// Remove WindowMenuItem
		OutlinerDocument document = (OutlinerDocument) e.getDocument();
		int index = getIndexOfDocument(document);
		WindowMenuItem item = (WindowMenuItem) getItem(index);
		remove(index);
		item.destroy();
		
		if (e.getDocument().getDocumentRepository().openDocumentCount() <= 0) {
			// Disable menu since no documents are open.
			setEnabled(false);
		}
	}
	
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {
		if(e.getDocument() != null) {
			// DeSelect Old Window
			if ((WindowMenu.indexOfOldSelection >= WindowMenu.WINDOW_LIST_START) && (WindowMenu.indexOfOldSelection < getItemCount())) {
				getItem(indexOfOldSelection).setSelected(false);
			}
			
			// Select New Window
			WindowMenu.indexOfOldSelection = getIndexOfDocument(e.getDocument());
			getItem(indexOfOldSelection).setSelected(true);
		}
	}
	
	private int getIndexOfDocument(Document doc) {
		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem item = getItem(i);
			if (item instanceof WindowMenuItem) {
				WindowMenuItem wmItem = (WindowMenuItem) item;
				if (doc == wmItem.doc) {
					return i;
				}
			}
		}
		return -1;
	}
	
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		Outliner.menuBar.windowMenu = this;
		
		setEnabled(false);
	}
	
	public void endSetup(Attributes atts) {
		WINDOW_LIST_START = getItemCount();
		
		Outliner.documents.addDocumentRepositoryListener(this);
	}
	
	
	// Misc Methods
	public void updateWindow(OutlinerDocument doc) {
		int index = getIndexOfDocument(doc);
		Outliner.menuBar.windowMenu.getItem(index).setText(doc.getTitle());
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		changeToWindow(((WindowMenuItem) e.getSource()).doc);
	}
	
	// Window Menu Methods
	public static void changeToWindow(OutlinerDocument doc) {
		if (doc != null) {
			try {
				// DeIconify if neccessary
				if (doc.isIcon()) {
					doc.setIcon(false);
				}
				
				doc.moveToFront();
				
				if (Outliner.desktop.isMaximized()) {
					// Minimize the previous document if it exists
					OutlinerDocument prevDoc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
					if (prevDoc != null && prevDoc != doc) {
						OutlinerDesktopManager.activationBlock = true;
						prevDoc.setMaximum(false);
						prevDoc.setSelected(false);
						OutlinerDesktopManager.activationBlock = false;
					}
					
					// Maximize the current document.
					doc.setMaximum(true);
				}
				
				doc.setSelected(true);
			} catch (java.beans.PropertyVetoException pve) {
				pve.printStackTrace();
			}
			
			// Document may not be visible if this is the first time we're changing to it.
			if (!doc.isVisible()) {
				doc.setVisible(true);
				doc.validate();
				doc.panel.layout.redraw();
			}
		}
	}
}