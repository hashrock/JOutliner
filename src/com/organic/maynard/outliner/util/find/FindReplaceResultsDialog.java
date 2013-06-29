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
 
package com.organic.maynard.outliner.util.find;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.menus.file.FileMenu;
import com.organic.maynard.outliner.menus.window.WindowMenu;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.event.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * 
 * Holds a list of results from a find.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.10 $, $Date: 2004/01/30 00:12:43 $
 */

public class FindReplaceResultsDialog extends AbstractOutlinerJDialog implements DocumentRepositoryListener, MouseListener {
	
	// Constants
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 150;
 	private static final int INITIAL_WIDTH = 450;
	private static final int INITIAL_HEIGHT = 150;
	
	private static final String TOTAL_MATCHES = "Total Matches: ";
	
	
	// GUI
	private JTable table = null;
	private JLabel totalMatches = null;
	
	
	// Model
	private FindReplaceResultsModel model = null;
	
	
	// The Constructor
	public FindReplaceResultsDialog() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}
	
	private void initialize() {
		totalMatches = new JLabel(new StringBuffer().append(TOTAL_MATCHES).append("0").toString());
		
		table = new JTable();
		
		table.addMouseListener(this);
		
		
		Outliner.documents.addDocumentRepositoryListener(this);
		
		JScrollPane jsp = new JScrollPane(table);
		getContentPane().add(jsp, BorderLayout.CENTER);
		getContentPane().add(totalMatches, BorderLayout.SOUTH);
		
		setTitle("Find/Replace Results");
		
		setVisible(false);
	}
	
	private boolean initialized = false;
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	
	// DocumentRepositoryListener Interface
	public void documentAdded(DocumentRepositoryEvent e) {}
	
	public void documentRemoved(DocumentRepositoryEvent e) {
		if (isVisible()) {
			getModel().removeAllResultsForDocument(e.getDocument());
		}
	}
	
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {}
	
	public FindReplaceResultsModel getModel() {
		return this.model;
	}
	
	public void show(FindReplaceResultsModel model) {
		// Lazy Instantiation
		if (!initialized) {
			initialize();
			initialized = true;
		}
		
		this.model = model;
		model.setView(this);
		
		// Setup the JTable
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		TableColumn column = null;
		
		// Document column
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(220);
		
		// Line column
		column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(35);
		column.setMaxWidth(60);
		
		// Column column
		column = table.getColumnModel().getColumn(2);
		column.setPreferredWidth(35);
		column.setMaxWidth(45);
		
		// Match column
		column = table.getColumnModel().getColumn(3);
		column.setPreferredWidth(55);
		
		// Replacement column
		column = table.getColumnModel().getColumn(4);
		column.setPreferredWidth(95);
		
		updateTotalMatches();
		
		show();
		
		SwingUtilities.invokeLater(new Runnable(){public void run(){Outliner.outliner.requestFocus();}});
	}
	
	public void updateTotalMatches() {
		totalMatches.setText(new StringBuffer().append(TOTAL_MATCHES).append(model.size()).toString());
	}
	
	public void requestFocus() {}
	
	// MouseListener Interface
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {
		int row = table.rowAtPoint(e.getPoint());
		
		FindReplaceResult result = model.getResult(row);
		
		int type = result.getType();
		if (type == FindReplaceResult.TYPE_DOC) {
			handleDocumentClick(result);
		} else if (type == FindReplaceResult.TYPE_FILE) {
			handleFileClick(result);
		} else {
			System.out.println("ERROR: Unknown FindReplaceResult type.");
		}
	}
	
	private static final int DOCUMENT_MODE = 0;
	private static final int FILE_MODE = 1;
	
	private void handleDocumentClick(FindReplaceResult result) {
		handleDocumentClick(result, DOCUMENT_MODE);
	}
	
	private void handleDocumentClick(FindReplaceResult result, int mode) {
		OutlinerDocument doc = result.getDocument();
		int line = result.getLine();
		int start = result.getStart();
		int end = start;
		
		if (result.isReplacement()) {
			end = start + result.getReplacement().length();
		} else {
			end = start + result.getMatch().length();
		}
		
		Outliner.outliner.requestFocus();
		Node node = null;
		if (mode == FILE_MODE) {
			node = GoToDialog.goToLineAndColumn(doc, line, start, true, true);
			end -= node.getDepth();
			result.setStart(start - node.getDepth());
		} else {
			node = GoToDialog.goToLineAndColumn(doc, line, start, false, true);
		}
		doc.tree.setCursorMarkPosition(end);
		doc.panel.layout.draw(node, OutlineLayoutManager.TEXT);
		WindowMenu.changeToWindow(doc);
	}
	
	private void handleFileClick(FindReplaceResult result) {
		try {
			String filepath = result.getFile().getCanonicalPath();
			Document doc = Outliner.documents.getDocument(filepath);
			
			if (doc == null) {
				// grab the file's extension
				String extension = filepath.substring(filepath.lastIndexOf(".") + 1, filepath.length());
				
				// use the extension to figure out the file's format
				String fileFormat = Outliner.fileFormatManager.getOpenFileFormatNameForExtension(extension);
				
				// crank up a fresh docInfo struct
				DocumentInfo docInfo = new DocumentInfo();
				PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_PATH, filepath);
				PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE, Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
				PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT, fileFormat);
				
				// try to open up the file
				FileMenu.openFile(docInfo, Outliner.fileProtocolManager.getDefault());
				
				doc = Outliner.documents.getDocument(filepath);
				
				if (doc == null) {
					// Something went wrong opening the file so abort.
					return;
				}
			}
			result.setDocument((OutlinerDocument) doc);
			
			// Handle like an open document now
			handleDocumentClick(result, FILE_MODE);
		} catch (IOException e) {
			System.out.println("IOException while handling click to open file: " + e.getMessage());
		}
	}
}