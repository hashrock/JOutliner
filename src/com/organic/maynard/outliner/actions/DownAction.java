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
import com.organic.maynard.outliner.util.undo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2002/08/29 08:26:39 $
 */
 
public class DownAction extends AbstractAction {

        @Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println("DownAction");
		
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
					UpAction.navigate(tree, layout, UpAction.DOWN);
				} else {
					moveDownText(textArea, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					UpAction.select(tree, layout, UpAction.DOWN);
				} else {
					selectDownText(textArea, tree, layout);
				}
				break;
			case 2:
				if (isIconFocused) {
					moveDown(tree,layout);
				} else {
				}
				break;
			case 3:
				if (isIconFocused) {
					UpAction.deselect(tree, layout, UpAction.DOWN);
				} else {
					
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void moveDownText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Get Prev Node
		Node nextNode = tree.getNextNode(currentNode);
		if (nextNode == null) {
			return;
		}

		// Record the EditingNode and CursorPosition
		tree.setEditingNode(nextNode);
		tree.setCursorPosition(OutlinerDocument.findNearestCaretPosition(textArea.getCaretPosition(), tree.getDocument().getPreferredCaretPosition(), nextNode));
		
		// Clear Text Selection
		textArea.setCaretPosition(0);
		textArea.moveCaretPosition(0);

		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);

		// Redraw and Set Focus
		if (nextNode.isVisible()) {
			layout.setFocus(nextNode,OutlineLayoutManager.TEXT);
		} else {
			layout.draw(nextNode,OutlineLayoutManager.TEXT);
		}
	}

	public static void selectDownText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		int currentPosition = textArea.getCaretPosition();
		
		if (currentPosition == textArea.getText().length()) {
			return;
		}
		
		// Update Preferred Caret Position
		int newCaretPosition = textArea.getText().length();
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
	public static void moveDown(JoeTree tree, OutlineLayoutManager layout) {
		Node oldestNode = tree.getOldestInSelection();
		Node node = oldestNode.nextSibling();
		if (node == oldestNode) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(node.getParent(), node.getParent());
		int targetIndex = node.currentIndex();
		
		// Do the move
		JoeNodeList nodeList = tree.getSelectedNodes();
		int moveCount = 0;
                if(nodeList.size() == 0){
//                    Node currentNode = textArea.node;
                    System.out.println("選択なし");
                }
                
                
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
//			if (!nodeToMove.isMoveable()) {
//				continue;
//			}
		
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));
			targetIndex--;
			moveCount++;
		}

		if (!undoable.isEmpty()) {
			if (moveCount == 1) {
				undoable.setName("Move Node Below Sibling");
			} else {
				undoable.setName(new StringBuffer().append("Move ").append(moveCount).append(" Nodes Below Sibling").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
			undoable.redo();
		}
	}
}