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

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.menus.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.io.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.event.*;
import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:41 $
 */

public class SaveFileMenuItem extends AbstractOutlinerMenuItem implements DocumentListener, DocumentRepositoryListener, ActionListener, GUITreeComponent {
	
	// DocumentListener Interface
	public void modifiedStateChanged(DocumentEvent e) {
		calculateEnabledState(e.getDocument());
	}
	
	// DocumentRepositoryListener Interface
	public void documentAdded(DocumentRepositoryEvent e) {}
	
	public void documentRemoved(DocumentRepositoryEvent e) {}
	
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {
		calculateEnabledState(e.getDocument());
	}
	
	private void calculateEnabledState(Document doc) {
		// if there was none ...
		if (doc == null) {
			setEnabled(false);
			
		// else if it has no name (e.g., it's a new doc, not yet saved) ...
		} else if (doc.getFileName().equals("")) {
			setEnabled(true);
			
		// else if it has a name, thus it's not a new doc, and it's been modified
		} else if (doc.isModified()) {
			// If we're imported then we can't save.
			if (PropertyContainerUtil.getPropertyAsBoolean(doc.getDocumentInfo(), DocumentInfo.KEY_IMPORTED)) {
				setEnabled(false);
			} else {
				setEnabled(true);
			}
			
		// else it has a name, but has not been modified
		} else {
			setEnabled(false);
		}
	}
	
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		
		setEnabled(false);
		
		addActionListener(this);
		Outliner.documents.addDocumentListener(this);
		Outliner.documents.addDocumentRepositoryListener(this);
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		saveOutlinerDocument((OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched());
	}
	
	public static void saveOutlinerDocument(OutlinerDocument document) {
		FileProtocol protocol = Outliner.fileProtocolManager.getProtocol(PropertyContainerUtil.getPropertyAsString(document.getDocumentInfo(), DocumentInfo.KEY_PROTOCOL_NAME));
		
		// Get the default protocol if none was found.
		if (protocol == null) {
			protocol = Outliner.fileProtocolManager.getDefault();
		}
		
		if (!document.getFileName().equals("")) {
			FileMenu.saveFile(document.getFileName(), document,  protocol, false);
		} else {
			SaveAsFileMenuItem.saveAsOutlinerDocument(document, protocol);
		}
	}
}