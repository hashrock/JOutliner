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

import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.10 $, $Date: 2002/07/16 21:23:48 $
 */

public class GoToDialog extends AbstractGUITreeJDialog implements ActionListener, JoeReturnCodes {

	// Constants
	private static final int INITIAL_WIDTH = 300;
	private static final int INITIAL_HEIGHT = 150;
 	private static final int MINIMUM_WIDTH = 300;
	private static final int MINIMUM_HEIGHT = 150;
	
	private static String GO = null;
	private static String GOTO_LINE_AND_COLUMN = null;
	private static String CANCEL = null;
	
	// GUI ELements
	private static JTextField lineNumberTextField = null;
	private static JTextField columnNumberTextField = null;
	private static JCheckBox countDepthCheckBox = null;
	private static JButton goButton = null;
	private static JButton gotoLineAndColumnButton = null;
	private static JButton cancelButton = null;

	
	// Fields
	private static OutlinerDocument doc = null;
	private static GoToDialog dialog = null;


	// The Constructor
	public GoToDialog() {
		super(false, true, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		dialog = this;
	}
	
	private void initialize() {
		GO = GUITreeLoader.reg.getText("goto_dialog_go");
		GOTO_LINE_AND_COLUMN = GUITreeLoader.reg.getText("goto_dialog_line_and_column");
		CANCEL = GUITreeLoader.reg.getText("cancel");
			
		lineNumberTextField = new JTextField(10);
		columnNumberTextField = new JTextField(10);
		countDepthCheckBox = new JCheckBox(GUITreeLoader.reg.getText("goto_dialog_count_indents"));
		goButton = new JButton(GO);
		gotoLineAndColumnButton = new JButton(GOTO_LINE_AND_COLUMN);
		cancelButton = new JButton(CANCEL);

		// Create the layout
		setResizable(false);
		
		this.getContentPane().setLayout(new BorderLayout());

		goButton.addActionListener(this);
		gotoLineAndColumnButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		Box vBox = Box.createVerticalBox();
		
		vBox.add(new JLabel(GUITreeLoader.reg.getText("goto_dialog_enter_column")));
		vBox.add(columnNumberTextField);
		vBox.add(countDepthCheckBox);
		
		vBox.add(Box.createVerticalStrut(5));
		
		vBox.add(new JLabel(GUITreeLoader.reg.getText("goto_dialog_enter_line")));
		vBox.add(lineNumberTextField);

		vBox.add(Box.createVerticalStrut(5));
		
		Box hBox = Box.createHorizontalBox();
		hBox.add(cancelButton);
		hBox.add(Box.createHorizontalStrut(5));
		hBox.add(gotoLineAndColumnButton);
		hBox.add(Box.createHorizontalStrut(5));
		hBox.add(goButton);

		// Put it all together
		this.getContentPane().add(vBox,BorderLayout.CENTER);
		this.getContentPane().add(hBox,BorderLayout.SOUTH);

		// Set the default button
		getRootPane().setDefaultButton(goButton);

		// This let's us actually set the focus in our modal dialog.
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				lineNumberTextField.requestFocus();
			}
			
			public void windowOpened(WindowEvent e) {
				lineNumberTextField.requestFocus();
			}
		});

		dialog.pack();	
	}

	private boolean initialized = false;
	
	public boolean isInitialized() {
		return this.initialized;
	}

	public static void setStateAndShow(OutlinerDocument document) {

		// Lazy Instantiation
		if (!dialog.initialized) {
			dialog.initialize();
			dialog.initialized = true;
		}

		doc = document;
		
		// Populate Column Number
		int currentColumnNumber = doc.tree.getCursorPosition();

		if (countDepthCheckBox.isSelected()) {
			currentColumnNumber += doc.tree.getEditingNode().getDepth();
		}

		String columnNumber = "" + currentColumnNumber;
		columnNumberTextField.setText(columnNumber);

		// Populate Line Number
		int currentLineNumber = doc.tree.getEditingNode().getLineNumber();
		String lineNumber = "" + currentLineNumber;
		lineNumberTextField.setText(lineNumber);

		lineNumberTextField.setCaretPosition(0);
		lineNumberTextField.moveCaretPosition(lineNumber.length());

		dialog.show();
	}
	

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(GO)) {
			go(false);
		} else if (e.getActionCommand().equals(GOTO_LINE_AND_COLUMN)) {
			go(true);
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}
	
	private static void go(boolean gotoColumn) {
		// Get a valid line number
		int lineNumber = 1;
		String lineNumberString = lineNumberTextField.getText();
		
		if (lineNumberString == null) {
			return;
		}
		try {
			lineNumber = Integer.parseInt(lineNumberString);
		} catch (NumberFormatException nfe) {
			return;
		}

		// Geta a valid column number
		int columnNumber = 0;
		String columnNumberString = columnNumberTextField.getText();
		
		if (columnNumberString == null) {
			return;
		}
		try {
			columnNumber = Integer.parseInt(columnNumberString);
		} catch (NumberFormatException nfe) {
			return;
		}
		
		Node currentNode = goToLineAndColumn(doc, lineNumber, columnNumber, countDepthCheckBox.isSelected(), gotoColumn);
		
		// draw and Set Focus
		if (gotoColumn) {
			doc.panel.layout.draw(currentNode, OutlineLayoutManager.TEXT);
		} else {
			doc.panel.layout.draw(currentNode, OutlineLayoutManager.ICON);
		}
	
		dialog.hide();
	}

	public static Node goToLineAndColumn(OutlinerDocument doc, int lineNumber, int columnNumber, boolean countDepth, boolean gotoColumn) {
		// Get a valid line number
		if (lineNumber < 1) {
			lineNumber = 1;
		}

		// Find the nth node.
		Node currentNode = doc.tree.getRootNode();
		Node nextNode;
		for (int i = 0; i < lineNumber; i++) {
			nextNode = currentNode.nextNode();
			if (nextNode.isRoot()) {
				break;
			} else {
				currentNode = nextNode;
			}
		}

		// Geta a valid column number
		if (countDepth) {
			columnNumber -= currentNode.getDepth();
		}
		
		if (columnNumber < 0) {
			columnNumber = 0;
		}

		// Insert the node into the visible nodes.
		doc.tree.insertNode(currentNode);

		doc.tree.setEditingNode(currentNode);
		
		if (gotoColumn) {
			// Select the node
			doc.tree.clearSelection();
			
			// Correct the columnNumber if it's larger than the nodes text
			if (columnNumber > currentNode.getValue().length()) {
				columnNumber = currentNode.getValue().length();
			}
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			doc.tree.setCursorPosition(columnNumber);
			doc.setPreferredCaretPosition(columnNumber);
			doc.tree.setComponentFocus(OutlineLayoutManager.TEXT);
		} else {
			// Select the node
			doc.tree.setSelectedNodesParent(currentNode.getParent());
			doc.tree.addNodeToSelection(currentNode);
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			doc.tree.setComponentFocus(OutlineLayoutManager.ICON);
		}
		
		return currentNode;
	}
	
	private static void cancel() {
		dialog.hide();
	}
}
