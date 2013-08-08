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
 
package com.organic.maynard.outliner;

import com.organic.maynard.outliner.dom.* ;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.util.undo.*;
import com.organic.maynard.util.string.StanStringTools;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.44 $, $Date: 2004/01/31 00:33:09 $
 */

public class OutlinerDocument extends JInternalFrame implements Document, ComponentListener, PropertyChangeListener {
	
	// Constants
	private static final String UNTITLED_DOCUMENT_NAME = GUITreeLoader.reg.getText("untitled");
	
	public static final ImageIcon ICON_DOCUMENT_SAVED = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/document_saved.gif"));
	public static final ImageIcon ICON_DOCUMENT_UNSAVED = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/document_unsaved.gif"));
	
	public static final int MIN_WIDTH = 300;
	public static final int MIN_HEIGHT = 100;
	
	public static final int INITIAL_WIDTH = 450;
	public static final int INITIAL_HEIGHT = 450;
	
	public static final int INITIAL_X = 5; // Default starting location
	public static final int INITIAL_Y = 5; // Default starting location
	
	
	// Class Variables
	private static int untitledDocumentCount = 0;
	private static OutlinerWindowMonitor monitor = new OutlinerWindowMonitor();
	
	
	// document title name forms
	private static final int FULL_PATHNAME = 0;
	private static final int TRUNC_PATHNAME = 1;
	private static final int JUST_FILENAME = 2;
	
	private static final String TRUNC_STRING = GUITreeLoader.reg.getText("trunc_string");
	
	
	// Instance Variables
	private int preferredCaretPosition = 0;
	private boolean isShowingAttributes = false;
	private int dividerPosition = 0;
	private String fileName = "";
	private boolean fileModified = true;
	private Border border = null;
	private DocumentRepository repository = null;
	private DocumentInfo docInfo = null;
	// TBD: move these into the constructor if possible.
	public OutlinerPanel panel = new OutlinerPanel(this); // Needs to come before JoeTree declaration.
	public DocumentSettings settings = new DocumentSettings(this);
	public JoeTree tree = Outliner.newTree(this); // Needs to come after OutlinerPanel declaration.
	public HoistStack hoistStack = new HoistStack(this);
	public AttributesPanel attPanel = new AttributesPanel(this);
	public UndoQueue undoQueue = new UndoQueue(this);
	private JSplitPane splitPane = null;
	private JScrollPane attJSP = new JScrollPane(attPanel);
	
	
	// The Constructors
	public OutlinerDocument(String title) {
		this(title, new DocumentInfo());
	}
	
	public OutlinerDocument(String title, DocumentInfo docInfo) {
		super("",true,true,true,true);
		
		setDocumentInfo(docInfo);
		
		Outliner.desktop.add(this, JLayeredPane.DEFAULT_LAYER);
		
		// Set the window title
		if (title.equals("")) {
			untitledDocumentCount++;
			setTitle(new StringBuffer().append(UNTITLED_DOCUMENT_NAME).append(" ").append(untitledDocumentCount).toString());
		} else {
			setTitle(title);
		}
		
		setFileName(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH));
		
		// Add it to the openDocuments list
		Outliner.documents.addDocument(this);
		
		// Set the Component & Window Listeners
		addComponentListener(this);
		addInternalFrameListener(monitor);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		// Create the Layout
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panel, attJSP);
		splitPane.setResizeWeight(1.0);
		splitPane.addPropertyChangeListener(this);
		
		// Now let's resize since the panel and dummy panel are linked together so no NPE when we get the panel's size.
		restoreWindowToInitialSize();
		setLocation(INITIAL_X, INITIAL_Y);
		
		// Set the icon in the frame header.
		setFrameIcon(ICON_DOCUMENT_UNSAVED);
		
		dividerPosition = getSize().height - 120;
		splitPane.setDividerLocation(dividerPosition); // This sets the position in the event that we don't show the atts initially.
		
		if (Preferences.getPreferenceBoolean(Preferences.SHOW_ATTRIBUTES).cur) {
			showAttributes(true);
		} else {
			showAttributes(false);
		}
	}
	
	public void destroy() {
		removeInternalFrameListener(monitor);
		removeComponentListener(this);
		
		docInfo = null;
		
		panel.destroy();
		panel = null;
		
		tree.destroy();
		tree = null;
		
		undoQueue.destroy();
		undoQueue = null;
		
		settings.destroy();
		settings = null;
		
		hoistStack.destroy();
		hoistStack = null;
		
		border = null;
		fileName = null;
		repository = null;
		attPanel = null;
		splitPane = null;
		attJSP = null;
		
		getContentPane().removeNotify();
		getContentPane().removeAll();
		removeNotify();
		removeAll();
		
		dispose();
	}
	
	
	// Accessors
	public DocumentSettings getSettings() {
		return this.settings ;
	}
	
	public void setDocumentRepository(DocumentRepository repository) {
		this.repository = repository;
	}
	
	public DocumentRepository getDocumentRepository() {
		return this.repository;
	}
	
	public void setTree(JoeTree tree) {
		this.tree = tree;
		tree.setDocument(this);
	}
	
	public JoeTree getTree() {
		return this.tree;
	}
	
	public void setUndoQueue(UndoQueue queue) {
		this.undoQueue = queue;
	}
	
	public UndoQueue getUndoQueue() {
		return this.undoQueue;
	}
	
	public DocumentInfo getDocumentInfo() {
		return this.docInfo;
	}
	
	public void setDocumentInfo(DocumentInfo docInfo) {
		this.docInfo = docInfo;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public static int getTitleNameForm() {
		String currentSettingStrung = Preferences.getPreferenceString(Preferences.DOCUMENT_TITLES_NAME_FORM).cur;
		int nameFormIndex = 0;
		for (int i = Preferences.RECENT_FILES_NAME_FORMS.length - 1; i >= 0; i--) {
			if (currentSettingStrung.equals(Preferences.RECENT_FILES_NAME_FORMS[i])) {
				nameFormIndex = i;
				break;
			}
		}
		return nameFormIndex;
	}
	
	public void setModified(boolean fileModified) {
		// Abort if we're not changing state.
		if (fileModified == this.fileModified) {
			return;
		}
		
		this.fileModified = fileModified;
		
		// Fire DocumentEvent
		getDocumentRepository().fireModifiedStateChangedEvent(this);
		
		if (fileModified) {
			setFrameIcon(ICON_DOCUMENT_UNSAVED);
		} else {
			setFrameIcon(ICON_DOCUMENT_SAVED);
		}
	}
	
	public boolean isModified() {
		return this.fileModified;
	}
	
	
	// Attributes Panel
	public boolean isShowingAttributes() {
		return this.isShowingAttributes;
	}
	
	public void showAttributes(boolean b) {
		isShowingAttributes = b;
		
		if (isShowingAttributes()) {
			// Swap the components
			getContentPane().remove(panel);
			splitPane.setTopComponent(panel);
			attPanel.update();
			getContentPane().add(splitPane, BorderLayout.CENTER);
			
			// Restore the divider position.
			splitPane.setDividerLocation(dividerPosition);
		} else {
			// Store the current divider position.
			dividerPosition = splitPane.getDividerLocation();
			
			// Swap the components
			getContentPane().remove(splitPane);
			getContentPane().add(panel, BorderLayout.CENTER);
		}
		
		if (isVisible()) {
			validate();
			panel.layout.redraw();
		}
		
		// Fire Event
		Outliner.documents.fireAttributesVisibilityChangedEvent(this);
	}
	
	
	public void restoreWindowToInitialSize() {
		setSize(INITIAL_WIDTH,INITIAL_HEIGHT);
	}
	
	
	// Border
	public void hideBorder() {
		border = getBorder();
		setBorder(null);
	}
	
	public void showBorder() {
		if (border != null) {
			setBorder(border);
		}
	}
	
	public void setSelected(boolean selected) throws java.beans.PropertyVetoException {
		super.setSelected(selected);
		if (selected) {
			panel.layout.redraw();
		}
	}
	
	
	// ComponentListener Interface
	public void componentResized(ComponentEvent e) {
		panel.layout.redraw();
	}
	
	public void componentHidden(ComponentEvent e) {} 
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	
	
	// Text Caret Positioning
	public int getPreferredCaretPosition() {
		return preferredCaretPosition;
	}
	
	public void setPreferredCaretPosition(int position) {
		this.preferredCaretPosition = position;
	}
	
	public static int findNearestCaretPosition(int currentPosition, int preferredCaretPosition, Node node) {
		int retVal = currentPosition;
		
		if (preferredCaretPosition > retVal) {
			retVal = preferredCaretPosition;
		}
		
		int nodeLength = node.getValue().length();
		if (retVal > nodeLength) {
			//if (preferredCaretPosition < currentPosition) {
			//	preferredCaretPosition = currentPosition; // This might be the source of a bug since this method is static and this might be intended to effect an instance variable.
			//}
			retVal = nodeLength;
		}
		return retVal;
	}
	
	
	// PropertyChangeListener Interface
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
			panel.layout.redraw();
			//panel.layout.setFocus(panel.doc.tree.getEditingNode(), panel.doc.tree.getComponentFocus());
		}
	}
	
	
	/**
	 * Syncs up the titles of all open documents to the
	 * Preferences setting for document titles.
	 */
	public static void syncTitleNameForms() {
		int nameFormIndex = getTitleNameForm();
		
		// Make the change
		for (int i = 0, limit = Outliner.documents.openDocumentCount(); i < limit; i++) {
			OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getDocument(i);
			String pathname = PropertyContainerUtil.getPropertyAsString(doc.getDocumentInfo(), DocumentInfo.KEY_PATH);
			
			// Pass over untitled documents
			if (pathname == null || pathname.equals("")) {
				continue;
			}
			
			// case out on the form to build the title
			String newTitle = null;
			switch (nameFormIndex) {
			
				case FULL_PATHNAME:
				
				default: 
					newTitle = pathname;
					break;
					
				case TRUNC_PATHNAME: 
					newTitle = StanStringTools.getTruncatedPathName(pathname, TRUNC_STRING);
					break;
					
				case JUST_FILENAME: 
					newTitle = StanStringTools.getFileNameFromPathName(pathname);
					break;
				
			}
			
			// set the title
			doc.setTitle(newTitle);
			
			// update the entry in the windows menu
			Outliner.menuBar.windowMenu.updateWindow(doc);
		}
	}
}