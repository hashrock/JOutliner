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
 
public class PromoteDemoteAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("PromoteDemoteAction");
		
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
					demote(node, tree, layout);
				} else {
					demoteText(node, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					promote(node, tree, layout);
				} else {
					promoteText(node, tree, layout);
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void promoteText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		// Abort if node is not moveable
		if (!currentNode.isMoveable()) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		Node targetNode = currentNode.getParent().getParent();
		int targetIndex = currentNode.getParent().currentIndex() + 1;
		if (currentNode.getParent().isRoot()) {
			// Our parent is root. Since we can't be promoted to root level, Abort.
			return;
		}
		
		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(),targetNode);
		undoable.setName("Promote Node");
		tree.getDocument().getUndoQueue().add(undoable);

		// Record the Insert in the undoable
		int index = currentNode.currentIndex();
		undoable.addPrimitive(new PrimitiveUndoableMove(undoable,currentNode,index,targetIndex));

		tree.promoteNode(currentNode, index);

		// Redraw and Set Focus
		layout.draw(currentNode,OutlineLayoutManager.TEXT);
	}

	public static void demoteText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		if (currentNode.isFirstChild()) {
			return;
		}

		// Abort if node is not moveable
		if (!currentNode.isMoveable()) {
			return;
		}
		
		// Put the Undoable onto the UndoQueue
		Node targetNode = currentNode.prevSibling();

		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(), targetNode);
		undoable.setName("Demote Node");
		tree.getDocument().getUndoQueue().add(undoable);
		
		// Record the Insert in the undoable
		int index = currentNode.currentIndex();
		int targetIndex = targetNode.numOfChildren();
		undoable.addPrimitive(new PrimitiveUndoableMove(undoable,currentNode,index,targetIndex));

		tree.demoteNode(currentNode,targetNode, index);

		// Redraw and Set Focus
		layout.draw(currentNode,OutlineLayoutManager.TEXT);
	}


	// IconFocusedMethods
	public static void promote(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		Node parent = currentNode.getParent();
		
		if (parent.isRoot()) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		Node targetNode = parent.getParent();
		int targetIndex = parent.currentIndex() + 1;
		
		CompoundUndoableMove undoable = new CompoundUndoableMove(parent, targetNode);

		JoeNodeList nodeList = tree.getSelectedNodes();
		int moveCount = 0;
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));
			moveCount++;
		}
		
		if (!undoable.isEmpty()) {
			if (moveCount == 1) {
				undoable.setName("Promote Node");
			} else {
				undoable.setName(new StringBuffer().append("Promote ").append(moveCount).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
			undoable.redo();
		}
	}

	public static void demote(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		if (tree.getYoungestInSelection().isFirstChild()) {
			return;
		}
	
		// Put the Undoable onto the UndoQueue
		Node targetNode = tree.getYoungestInSelection().prevSibling();

		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(),targetNode);
		
		int existingChildren = targetNode.numOfChildren();
		JoeNodeList nodeList = tree.getSelectedNodes();
		int moveCount = 0;
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}

			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), existingChildren));
			moveCount++;
		}

		if (!undoable.isEmpty()) {
			if (moveCount == 1) {
				undoable.setName("Demote Node");
			} else {
				undoable.setName(new StringBuffer().append("Demote ").append(moveCount).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
			undoable.redo();
		}
	}
}