/**
 * Copyright (C) 2002 Maynard Demmon, maynard@organic.com
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
 
package com.organic.maynard.outliner.actions;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.util.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.organic.maynard.util.string.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2002/08/27 09:44:27 $
 */
 
public class CutAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("CutAction");
		
		OutlinerCellRendererImpl textArea  = null;
		boolean isIconFocused = true;
		Component c = (Component) e.getSource();
		if (c instanceof OutlineButton) {
			textArea = ((OutlineButton) c).renderer;
		} else if (c instanceof OutlineLineNumber) {
			textArea = ((OutlineLineNumber) c).renderer;
		} else if (c instanceof OutlineCommentIndicator) {
			textArea = ((OutlineCommentIndicator) c).renderer;
		} else if (c instanceof OutlinerCellRendererImpl) {
			textArea = (OutlinerCellRendererImpl) c;
			isIconFocused = false;
		}
		
		// Shorthand
		Node node = textArea.node;
		JoeTree tree = node.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		//System.out.println(e.getModifiers());
		switch (e.getModifiers()) {
			case 2:
				if (isIconFocused) {
					cut(tree, layout);
				} else {
					cutText(textArea, tree, layout);
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void cutText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		// Abort if node is not editable
		if (!currentNode.isEditable()) {
			return;
		}

		// Copy Text
		String text = textArea.getSelectedText();
		
		if (text != null) {
			java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
		}

		// Delete Text
		String oldText = textArea.getText();
		int oldCaretPosition = textArea.getCaretPosition();
		int oldMarkPosition = textArea.getCaret().getMark();

		String newText = new StringBuffer().append(oldText.substring(0,textArea.getSelectionStart())).append(oldText.substring(textArea.getSelectionEnd(),oldText.length())).toString();
		int newCaretPosition = textArea.getSelectionStart();
		Undoable undoable = new UndoableEdit(currentNode, oldText, newText, oldCaretPosition, newCaretPosition, oldMarkPosition, newCaretPosition);
		undoable.setName("Cut Text");
		tree.getDocument().getUndoQueue().add(undoable);
		UndoableEdit.freezeUndoEdit(currentNode);
		
		textArea.node.setValue(newText);
		textArea.setText(newText);
		textArea.setCaretPosition(newCaretPosition);
		textArea.moveCaretPosition(newCaretPosition);

		// Record the EditingNode, Mark and CursorPosition
		tree.setEditingNode(currentNode);
		tree.setCursorMarkPosition(newCaretPosition);
		tree.setCursorPosition(newCaretPosition, false);
		tree.getDocument().setPreferredCaretPosition(newCaretPosition);

		// Do the Redraw if we have wrapped or if we are currently off screen.
		if (textArea.getPreferredSize().height != textArea.height || !currentNode.isVisible()) {
			layout.draw(currentNode, OutlineLayoutManager.TEXT);
		}
	}
		

	// IconFocusedMethods
	public static void cut(JoeTree tree, OutlineLayoutManager layout) {
		NodeSet nodeSet = new NodeSet();
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			Node node = nodeList.get(i);
			
			// Abort if node is not editable
			if (!node.isEditable()) {
				continue;
			}
			
			Node newNode = node.cloneClean();
			newNode.setDepthRecursively(0);	
			nodeSet.addNode(newNode);
		}
		
		if (!nodeSet.isEmpty()) {
			// [md] This conditional is here since StringSelection subclassing seems to be broken in Java 1.3.1.
			if (PlatformCompatibility.isJava1_3_1()) {
				java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(nodeSet.toString()), null);
			} else {
				Outliner.clipboard.setContents(new NodeSetTransferable(nodeSet), Outliner.outliner);
			}
		}
		
		// Delete selection
		Node youngestNode = tree.getYoungestInSelection();
		Node parent = youngestNode.getParent();
		CompoundUndoableReplace undoable = new CompoundUndoableReplace(parent, false);

		int startDeleting = 0;
		if (tree.isWholeDocumentSelected()) {
			// Abort if the doc is empty.
			if (tree.isDocumentEmpty()) {
				return;
			}
			
			// Swap in a new node for the first node since a doc always has at least one child of root.
			Node newNode = new NodeImpl(tree,"");
			newNode.setDepth(0);
			undoable.addPrimitive(new PrimitiveUndoableReplace(parent, youngestNode, newNode));
			
			startDeleting++;
		}

		// Iterate over the remaining selected nodes deleting each one
		int deleteCount = 0;
		for (int i = startDeleting, limit = nodeList.size(); i < limit; i++) {
			Node node = nodeList.get(i);

			// Abort if node is not editable
			if (!node.isEditable()) {
				continue;
			}
			
			undoable.addPrimitive(new PrimitiveUndoableReplace(parent, node, null));
			deleteCount++;
		}

		if (!undoable.isEmpty()) {
			if (deleteCount == 1) {
				undoable.setName("Cut Node");
			} else {
				undoable.setName(new StringBuffer().append("Cut ").append(deleteCount).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
			undoable.redo();
		}
	}
}