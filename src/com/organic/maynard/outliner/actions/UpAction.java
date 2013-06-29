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
 * @version $Revision: 1.3 $, $Date: 2002/08/29 08:26:39 $
 */
 
public class UpAction extends AbstractAction {

	// Constants
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;

	public void actionPerformed(ActionEvent e) {
		//System.out.println("UpAction");
		
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
			case 0:
				if (isIconFocused) {
					navigate(tree, layout, UP);
				} else {
					moveUpText(textArea, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					select(tree, layout, UP);
				} else {
					selectUpText(textArea, tree, layout);
				}
				break;
			case 2:
				if (isIconFocused) {
					moveUp(tree,layout);
				} else {
					
				}
				break;
			case 3:
				if (isIconFocused) {
					deselect(tree, layout, UP);
				} else {
					
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void moveUpText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		// Get Prev Node
		Node prevNode = tree.getPrevNode(currentNode);
		if (prevNode == null) {
			return;
		}

		// Record the EditingNode and CursorPosition
		tree.setEditingNode(prevNode);
		tree.setCursorPosition(OutlinerDocument.findNearestCaretPosition(textArea.getCaretPosition(), tree.getDocument().getPreferredCaretPosition(), prevNode));
			
		// Clear Text Selection
		textArea.setCaretPosition(0);
		textArea.moveCaretPosition(0);

		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);

		// Redraw and Set Focus
		if (prevNode.isVisible()) {
			layout.setFocus(prevNode,OutlineLayoutManager.TEXT);
		} else {
			layout.draw(prevNode,OutlineLayoutManager.TEXT);
		}
	}

	public static void selectUpText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		int currentPosition = textArea.getCaretPosition();

		if (currentPosition == 0) {
			return;
		}
		
		// Update Preferred Caret Position
		int newCaretPosition = 0;
		tree.getDocument().setPreferredCaretPosition(newCaretPosition);

		// Record the CursorPosition only since the EditingNode should not have changed
		tree.setCursorPosition(newCaretPosition, false);

		textArea.moveCaretPosition(newCaretPosition);

		// Redraw and Set Focus if this node is currently offscreen
		if (!currentNode.isVisible()) {
			layout.draw(currentNode,OutlineLayoutManager.TEXT);
		}
		
		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);
	}

	// IconFocusedMethods
	public static void deselect(JoeTree tree, OutlineLayoutManager layout, int type) {
		Node node = null;
		Node youngestNode = null;
		Node oldestNode = null;
		
		switch(type) {
			case UP:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.prevSelectedSibling();
				if ((node == null) || (node == oldestNode)) {return;}
				tree.removeNodeFromSelection(oldestNode);
				break;

			case DOWN:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.nextSelectedSibling();
				if ((node == null) || (node == youngestNode)) {return;}
				tree.removeNodeFromSelection(youngestNode);
				break;

			case LEFT:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.prevSibling();
				if ((node == null) || (node == oldestNode)) {return;}
				tree.removeNodeFromSelection(oldestNode);
				tree.addNodeToSelection(node);
				break;

			case RIGHT:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.nextSibling();
				if ((node == null) || (node == youngestNode)) {return;}
				tree.removeNodeFromSelection(youngestNode);
				tree.addNodeToSelection(node);
				break;
				
			default:
				return;
		}

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(node);

		// Redraw and Set Focus
		layout.draw(node,OutlineLayoutManager.ICON);		
	}

	public static void select(JoeTree tree, OutlineLayoutManager layout, int type) {
		Node node = null;
		Node youngestNode = null;
		Node oldestNode = null;
		
		switch(type) {
			case UP:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.prevSibling();
				if (node == youngestNode) {return;}
				break;

			case DOWN:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.nextSibling();
				if (node == oldestNode) {return;}
				break;

			case LEFT:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.prevSibling();
				if (node == youngestNode) {return;}
				tree.removeNodeFromSelection(youngestNode);
				break;

			case RIGHT:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.nextSibling();
				if (node == oldestNode) {return;}
				tree.removeNodeFromSelection(oldestNode);
				break;
				
			default:
				return;
		}
		
		tree.addNodeToSelection(node);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(node);

		// Redraw and Set Focus
		layout.draw(node,OutlineLayoutManager.ICON);		
	}	

	public static void moveUp(JoeTree tree, OutlineLayoutManager layout) {
		Node youngestNode = tree.getYoungestInSelection();
		Node node = youngestNode.prevSibling();
		if (node == youngestNode) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(node.getParent(), node.getParent());
		int targetIndex = node.currentIndex();
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		int moveCount = 0;
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));
			targetIndex++;
			moveCount++;
		}

		if (!undoable.isEmpty()) {
			if (moveCount == 1) {
				undoable.setName("Move Node Above Sibling");
			} else {
				undoable.setName(new StringBuffer().append("Move ").append(moveCount).append(" Nodes Above Sibling").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
			undoable.redo();
		}
	}

	public static void navigate(JoeTree tree, OutlineLayoutManager layout, int type) {
		Node node = null;
		Node youngestNode = null;
		Node oldestNode = null;
		
		switch(type) {
			case UP:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.prevSibling();
				if (node == youngestNode) {return;}
				tree.clearSelection();
				break;

			case DOWN:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.nextSibling();
				if (node == oldestNode) {return;}
				tree.clearSelection();
				break;

			case LEFT:
				youngestNode = tree.getYoungestInSelection();
				node = tree.getPrevNode(youngestNode);
				if (node == null) {return;}
				tree.setSelectedNodesParent(node.getParent());
				break;

			case RIGHT:
				oldestNode = tree.getOldestInSelection();
				node = tree.getNextNode(oldestNode);
				if (node == null) {return;}
				tree.setSelectedNodesParent(node.getParent());
				break;
				
			default:
				return;
		}
		
		tree.addNodeToSelection(node);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(node);

		// Redraw and Set Focus
		layout.draw(node,OutlineLayoutManager.ICON);
	}
}