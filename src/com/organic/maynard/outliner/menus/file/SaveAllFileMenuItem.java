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

public class SaveAllFileMenuItem extends AbstractOutlinerMenuItem implements DocumentListener, DocumentRepositoryListener, ActionListener, GUITreeComponent {
	
	// DocumentListener Interface
	public void modifiedStateChanged(DocumentEvent e) {
		calculateEnabledState();
	}
	
	
	// DocumentRepositoryListener Interface
	public void documentAdded(DocumentRepositoryEvent e) {
		calculateEnabledState();
	}
	
	public void documentRemoved(DocumentRepositoryEvent e) {
		calculateEnabledState();
	}
	
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {
		calculateEnabledState();
	}
	
	private void calculateEnabledState() {
		for (int i = 0, limit = Outliner.documents.openDocumentCount(); i < limit; i++) {
			Document doc = Outliner.documents.getDocument(i);
			
			if ((doc.isModified() || doc.getFileName().equals("")) && !PropertyContainerUtil.getPropertyAsBoolean(doc.getDocumentInfo(), DocumentInfo.KEY_IMPORTED)) {
				setEnabled(true);
				return;
			}
		}
		
		setEnabled(false);
	}
	
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		Outliner.documents.addDocumentListener(this);
		Outliner.documents.addDocumentRepositoryListener(this);
		
		setEnabled(false);
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		saveAllOutlinerDocuments();
	}
	
	/**
	 * Saves all outliner documents that are currently modified and are not imported.
	 * We don't want to save imported docs since they won't have a valid current save
	 * format, i.e. they're imported.
	 */
	protected static void saveAllOutlinerDocuments() {
		for (int i = 0; i < Outliner.documents.openDocumentCount(); i++) {
			OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getDocument(i);
			
			if (doc.isModified() && !PropertyContainerUtil.getPropertyAsBoolean(doc.getDocumentInfo(), DocumentInfo.KEY_IMPORTED)) {
				SaveFileMenuItem.saveOutlinerDocument(doc);
			}
		}
	}
}
