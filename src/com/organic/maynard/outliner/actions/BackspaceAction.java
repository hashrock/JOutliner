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
 * @version $Revision: 1.4 $, $Date: 2002/12/11 05:57:06 $
 */
 
public class BackspaceAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("BackspaceAction");
		
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
		} else {
			System.out.println("Error: Invalid Component as source of Action in BackspaceAction.");
			return;
		}
		
		// Shorthand
		Node node = textArea.node;
		JoeTree tree = node.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;
		
		//System.out.println(e.getModifiers());
		switch (e.getModifiers()) {
			case 0:
				if (isIconFocused) {
					delete(tree, layout, false);
				} else {
					deleteText(textArea, tree, layout);
				}
				break;
		}
	}
	
	
	// KeyFocusedMethods
	public static void deleteText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		// Abort if node is not editable
		if (!currentNode.isEditable()) {
			return;
		}
		
		int caretPosition = textArea.getCaretPosition();
		int markPosition = textArea.getCaret().getMark();
		
		if ((caretPosition == 0) && (caretPosition == markPosition) && textArea.node.isLeaf()) {
			mergeWithPrevVisibleNode(textArea, tree, layout);
		} else {
			String oldText = currentNode.getValue();
			String newText = null;
			int oldCaretPosition = textArea.getCaretPosition();
			int oldMarkPosition = textArea.getCaret().getMark();
			int newCaretPosition = -1;
			int newMarkPosition = -1;
			
			int startSelection = Math.min(oldCaretPosition, oldMarkPosition);
			int endSelection = Math.max(oldCaretPosition, oldMarkPosition);
			
			if (startSelection != endSelection) {
				newCaretPosition = startSelection;
				newMarkPosition = startSelection;
				// TBD [srk] bug here: args to substring can have values < 0
				//	causing a StringIndexOutOfBoundsException
				// that's gotta be startSelection
				// which gets here as the minimum of oldCaretPosition and oldMarkPosition
				// one of which must have the value -1
				//	-- this needs to be investigated
				// set a bug trap
				if (startSelection < 0 || endSelection < 0) {
					String msg = "Error at TextKeyListener:keyTyped:Backspace:01\n" ;
					msg = msg + "startSelection: -1\n" ;
					msg = msg + "oldCaretPosition: " + oldCaretPosition + "\n" ;
					msg = msg + "oldMarkPosition: " + oldMarkPosition ;
					System.out.println("Stan_Debug:\t" + msg); 
					return ;
				} // end bug trap
				newText = oldText.substring(0, startSelection) + oldText.substring(endSelection, oldText.length());				
			} else if (startSelection == 0) {
				newCaretPosition = 0;
				newMarkPosition = 0;
				newText = oldText;				
			} else {
				newCaretPosition = startSelection - 1;
				newMarkPosition = startSelection - 1;
				// TBD [srk] bug here: 2nd arg to substring can have the value -1
				//	causing a StringIndexOutOfBoundsException
				// that's newCaretPosition
				// which means that startSelection has the value 0
				//	-- this needs to be investigated
				// set a bug trap
				if ((newCaretPosition < 0) || (oldText.length() < 0)) {
					String msg = "Error at TextKeyListener:keyTyped:Backspace:02\n" ;
					msg = msg + "startSelection: 0\n" ;
					msg = msg + "newCaretPosition: -1" ;
					msg = msg + "oldCaretPosition: -1" + oldCaretPosition + "\n" ;
					msg = msg + "oldMarkPosition: " + oldMarkPosition ;
					System.out.println("Stan_Debug:\t" + msg); 
					return ;
				} // end bug trap
				newText = oldText.substring(0, newCaretPosition) + oldText.substring(newCaretPosition + 1, oldText.length());				
			} // end else
			
			UndoableEdit undoable = tree.getDocument().getUndoQueue().getIfEdit();
			if ((undoable != null) && (undoable.getNode() == currentNode) && (!undoable.isFrozen())) {
				undoable.setNewText(newText);
				undoable.setNewPosition(newCaretPosition);
				undoable.setNewMarkPosition(newMarkPosition);
			} else {
				UndoableEdit newUndoable = new UndoableEdit(
					currentNode, 
					oldText, 
					newText, 
					oldCaretPosition, 
					newCaretPosition, 
					oldMarkPosition, 
					newMarkPosition
				);
				newUndoable.setName("Delete Text");
				tree.getDocument().getUndoQueue().add(newUndoable);
			}
			
			currentNode.setValue(newText);
			
			// Record the EditingNode, Mark and CursorPosition
			tree.setEditingNode(currentNode);
			tree.setCursorMarkPosition(newMarkPosition);
			tree.setCursorPosition(newCaretPosition, false);
			tree.getDocument().setPreferredCaretPosition(newCaretPosition);
			
			textArea.setText(newText);
			textArea.setCaretPosition(newMarkPosition);
			textArea.moveCaretPosition(newCaretPosition);
			
			// Do the Redraw if we have wrapped or if we are currently off screen.
			if (textArea.getPreferredSize().height != textArea.height || !currentNode.isVisible()) {
				layout.draw(currentNode, OutlineLayoutManager.TEXT);
			}
		}
	}
	
	private static void mergeWithPrevVisibleNode(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		Node prevNode = tree.getPrevNode(currentNode);
		if (prevNode == null) {
			return;
		}
		
		// Abort if prevNode is not editable
		if (!prevNode.isEditable()) {
			return;
		}
		
		Node parent = currentNode.getParent();
		
		// Get Text for nodes.
		String prevNodeText = prevNode.getValue();
		String currentNodeText = currentNode.getValue();
		String newPrevNodeText = prevNodeText + currentNodeText;
		
		// Put the Undoable onto the UndoQueue
		UndoableEdit undoableEdit = new UndoableEdit(
			prevNode, 
			prevNodeText, 
			newPrevNodeText, 
			0, 
			prevNodeText.length(), 
			0, 
			prevNodeText.length()
		);
		
		CompoundUndoableReplace undoableReplace = new CompoundUndoableReplace(parent);
		undoableReplace.addPrimitive(new PrimitiveUndoableReplace(parent, currentNode, null));
		
		CompoundUndoableImpl undoable = new CompoundUndoableImpl(true);
		undoable.addPrimitive(undoableReplace);
		undoable.addPrimitive(undoableEdit);
		undoable.setName("Merge with Previous Node");
		
		tree.getDocument().getUndoQueue().add(undoable);
		
		undoable.redo();
	}
	
	
	// IconFocusedMethods
	public static void delete(JoeTree tree, OutlineLayoutManager layout, boolean deleteMode) {
		Node youngestNode = tree.getYoungestInSelection();
		
		if (youngestNode == null) {
			return;
		}
		
		Node parent = youngestNode.getParent();
		CompoundUndoableReplace undoable = new CompoundUndoableReplace(parent, deleteMode);
		
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
		JoeNodeList nodeList = tree.getSelectedNodes();
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
				undoable.setName("Delete Node");
			} else {
				undoable.setName(new StringBuffer().append("Delete ").append(deleteCount).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
			undoable.redo();
		}
		
		return;
	}
}