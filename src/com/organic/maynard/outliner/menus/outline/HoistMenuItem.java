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
 * @version $Revision: 1.3 $, $Date: 2004/05/26 20:59:09 $
 */

public class HoistMenuItem 
	extends AbstractOutlinerMenuItem 
	implements OutlinerDocumentListener, DocumentRepositoryListener, TreeSelectionListener, ActionListener, GUITreeComponent 
{
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		Outliner.documents.addOutlinerDocumentListener(this);
		Outliner.documents.addDocumentRepositoryListener(this);
		Outliner.documents.addTreeSelectionListener(this);
		
		setEnabled(false);
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		hoist((OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched());
	}
	
	private static void hoist(OutlinerDocument doc) {
		try {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				TextKeyListener.hoist(doc.tree.getEditingNode());
			} else if (doc.tree.getComponentFocus() == OutlineLayoutManager.ICON) {
				IconKeyListener.hoist(doc.tree);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
	
	
	// OutlinerDocumentListener Interface
	public void modifiedStateChanged(DocumentEvent e) {}
	
	public void attributesVisibilityChanged(OutlinerDocumentEvent e) {}
	
	public void hoistDepthChanged(OutlinerDocumentEvent e) {
		if (e.getOutlinerDocument() == Outliner.documents.getMostRecentDocumentTouched()) {
			updateText(e.getOutlinerDocument());
		}
	}
	
	
	// DocumentRepositoryListener Interface
	public void documentAdded(DocumentRepositoryEvent e) {}
	
	public void documentRemoved(DocumentRepositoryEvent e) {}
	
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {
		OutlinerDocument doc = (OutlinerDocument) e.getDocument();
		updateText(doc);
		if (doc != null) {
			calculateEnabledState(doc.getTree());
		}
	}
	
	private void updateText(OutlinerDocument doc) {
		if (doc == null) {
			return;
		}
		
		setText(OutlineMenu.OUTLINE_HOIST + " (" + doc.hoistStack.getHoistDepth() + ")");	
	}
	
	
	// TreeSelectionListener Interface
	public void selectionChanged(TreeSelectionEvent e) {
		calculateEnabledState(e.getTree());
	}
	
	private void calculateEnabledState(JoeTree tree) {
		Document doc = tree.getDocument();
		
		if (doc == Outliner.documents.getMostRecentDocumentTouched()) {
			Node node = tree.getEditingNode();
			
			if (tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (node.isLeaf()) {
					setEnabled(false);
				} else {
					setEnabled(true);
				}
			} else if (tree.getComponentFocus() == OutlineLayoutManager.ICON) {
				if (tree.getNumberOfSelectedNodes() == 1) {
					if (node.isLeaf()) {
						setEnabled(false);
					} else {
						setEnabled(true);
					}
				}
			}
		}
	}
}
