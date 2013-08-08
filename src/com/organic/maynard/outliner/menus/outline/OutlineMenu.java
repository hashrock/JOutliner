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
 
package com.organic.maynard.outliner.menus.outline;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.event.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.menus.*;
import javax.swing.*;
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/02/03 22:33:06 $
 */

public class OutlineMenu extends AbstractOutlinerMenu implements DocumentRepositoryListener, GUITreeComponent {
	
	// Pseudo Constants
	public static String OUTLINE_HOIST = "";
	
	
	// The Constructors
	public OutlineMenu() {
		super();
	}
	
	
	// DocumentRepositoryListener Interface
	public void documentAdded(DocumentRepositoryEvent e) {
		// Enable menu since we've got at least one document now.
		setEnabled(true);
	}
	
	public void documentRemoved(DocumentRepositoryEvent e) {
		if (e.getDocument().getDocumentRepository().openDocumentCount() <= 0) {
			// Disable menu since no documents are open.
			setEnabled(false);
		}
	}
	
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {}
	
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		
		Outliner.menuBar.outlineMenu = this;
		setEnabled(false);
	}
	
	public void endSetup(Attributes atts) {
		super.endSetup(atts);
		
		JMenuItem hoistItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.OUTLINE_HOIST_MENU_ITEM);
		OUTLINE_HOIST = hoistItem.getText();
		
		Outliner.documents.addDocumentRepositoryListener(this);
	}
}