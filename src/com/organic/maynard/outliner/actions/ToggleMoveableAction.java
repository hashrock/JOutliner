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
 
public class ToggleMoveableAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("ToggleMoveableAction");
		
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
					toggleMoveableAndClear(node, tree, layout);
				} else {
					toggleMoveableAndClearText(node, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					toggleMoveable(node, tree, layout);
				} else {
					toggleMoveableText(node, tree, layout);
				}
				break;
			case 2:
				if (isIconFocused) {
					toggleMoveableInheritance(node, tree, layout);
				} else {
					toggleMoveableInheritanceText(node, tree, layout);
				}
				break;
			case 3:
				if (isIconFocused) {
					clearMoveable(node, tree, layout);
				} else {
					clearMoveableText(node, tree, layout);
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void clearMoveableText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		clearMoveableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Clear Moveability for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleMoveableAndClearText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleMoveableAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Moveability and Clear Decendants for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleMoveableText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleMoveableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Moveability for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleMoveableInheritanceText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleMoveableInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Moveability Inheritance for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}


	// IconFocusedMethods
	public static void clearMoveable(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			clearMoveableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Clear Moveability for Node");
			} else {
				undoable.setName(new StringBuffer().append("Clear Moveability for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void clearMoveableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getMoveableState();
		int newValue = Node.MOVEABLE_INHERITED;
		
		if (oldValue != Node.MOVEABLE_INHERITED) {
			node.setMoveableState(Node.MOVEABLE_INHERITED);
			undoable.addPrimitive(new PrimitiveUndoableMoveableChange(node, oldValue, newValue));
		}
				
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearMoveableForSingleNode(node.getChild(i), undoable);
		}
	}
	
	public static void toggleMoveableAndClear(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleMoveableAndClearForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Moveability and Clear Decendants for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Moveability and Clear Decendants for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void toggleMoveableAndClearForSingleNode(Node node, CompoundUndoable undoable) {
		toggleMoveableForSingleNode(node, undoable);
		
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearMoveableForSingleNode(node.getChild(i), undoable);
		}		
	}
			
	public static void toggleMoveable(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleMoveableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Moveability for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Moveability for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void toggleMoveableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getMoveableState();
		int newValue = Node.MOVEABLE_INHERITED;
		boolean isMoveable = node.isMoveable();
		
		if (oldValue == Node.MOVEABLE_FALSE) {
			node.setMoveableState(Node.MOVEABLE_TRUE);
			newValue = Node.MOVEABLE_TRUE;
					
		} else if (oldValue == Node.MOVEABLE_TRUE) {
			node.setMoveableState(Node.MOVEABLE_FALSE);
			newValue = Node.MOVEABLE_FALSE;
		
		} else {
			if (isMoveable) {
				node.setMoveableState(Node.MOVEABLE_FALSE);
				newValue = Node.MOVEABLE_FALSE;
			} else {
				node.setMoveableState(Node.MOVEABLE_TRUE);
				newValue = Node.MOVEABLE_TRUE;
			}
		}
				
		undoable.addPrimitive(new PrimitiveUndoableMoveableChange(node, oldValue, newValue));
	}
	
	public static void toggleMoveableInheritance(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleMoveableInheritanceForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Moveability Inheritance for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Moveability Inheritance for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}
	
	public static void toggleMoveableInheritanceForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getMoveableState();
		int newValue = Node.MOVEABLE_INHERITED;
		boolean isMoveable = node.isMoveable();
		
		if (oldValue == Node.MOVEABLE_INHERITED) {
			if (isMoveable) {
				node.setMoveableState(Node.MOVEABLE_TRUE);
				newValue = Node.MOVEABLE_TRUE;
			} else {
				node.setMoveableState(Node.MOVEABLE_FALSE);
				newValue = Node.MOVEABLE_FALSE;
			}
								
		} else {
			node.setMoveableState(Node.MOVEABLE_INHERITED);
		}
				
		undoable.addPrimitive(new PrimitiveUndoableMoveableChange(node, oldValue, newValue));
	}
}