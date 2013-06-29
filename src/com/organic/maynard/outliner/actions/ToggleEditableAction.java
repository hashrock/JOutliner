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
 
public class ToggleEditableAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("ToggleEditableAction");
		
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
					toggleEditableAndClear(node, tree, layout);
				} else {
					toggleEditableAndClearText(node, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					toggleEditable(node, tree, layout);
				} else {
					toggleEditableText(node, tree, layout);
				}
				break;
			case 2:
				if (isIconFocused) {
					toggleEditableInheritance(node, tree, layout);
				} else {
					toggleEditableInheritanceText(node, tree, layout);
				}
				break;
			case 3:
				if (isIconFocused) {
					clearEditable(node, tree, layout);
				} else {
					clearEditableText(node, tree, layout);
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void clearEditableText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		clearEditableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Clear Editability for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleEditableAndClearText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleEditableAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Editability and Clear Decendants for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleEditableText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleEditableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Editability for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleEditableInheritanceText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleEditableInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Editability Inheritance for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}


	// IconFocusedMethods
	public static void clearEditable(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			clearEditableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Clear Editability for Node");
			} else {
				undoable.setName(new StringBuffer().append("Clear Editability for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void clearEditableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getEditableState();
		int newValue = Node.EDITABLE_INHERITED;
		
		if (oldValue != Node.EDITABLE_INHERITED) {
			node.setEditableState(Node.EDITABLE_INHERITED);
			undoable.addPrimitive(new PrimitiveUndoableEditableChange(node, oldValue, newValue));
		}
				
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearEditableForSingleNode(node.getChild(i), undoable);
		}
	}
	
	public static void toggleEditableAndClear(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleEditableAndClearForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Editability and Clear Decendants for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Editability and Clear Decendants for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void toggleEditableAndClearForSingleNode(Node node, CompoundUndoable undoable) {
		toggleEditableForSingleNode(node, undoable);
		
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearEditableForSingleNode(node.getChild(i), undoable);
		}		
	}
			
	public static void toggleEditable(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleEditableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Editability for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Editability for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void toggleEditableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getEditableState();
		int newValue = Node.EDITABLE_INHERITED;
		boolean isEditable = node.isEditable();
		
		if (oldValue == Node.EDITABLE_FALSE) {
			node.setEditableState(Node.EDITABLE_TRUE);
			newValue = Node.EDITABLE_TRUE;
					
		} else if (oldValue == Node.EDITABLE_TRUE) {
			node.setEditableState(Node.EDITABLE_FALSE);
			newValue = Node.EDITABLE_FALSE;
		
		} else {
			if (isEditable) {
				node.setEditableState(Node.EDITABLE_FALSE);
				newValue = Node.EDITABLE_FALSE;
			} else {
				node.setEditableState(Node.EDITABLE_TRUE);
				newValue = Node.EDITABLE_TRUE;
			}
		}
				
		undoable.addPrimitive(new PrimitiveUndoableEditableChange(node, oldValue, newValue));
	}
	
	public static void toggleEditableInheritance(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleEditableInheritanceForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Editability Inheritance for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Editability Inheritance for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}
	
	public static void toggleEditableInheritanceForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getEditableState();
		int newValue = Node.EDITABLE_INHERITED;
		boolean isEditable = node.isEditable();
		
		if (oldValue == Node.EDITABLE_INHERITED) {
			if (isEditable) {
				node.setEditableState(Node.EDITABLE_TRUE);
				newValue = Node.EDITABLE_TRUE;
			} else {
				node.setEditableState(Node.EDITABLE_FALSE);
				newValue = Node.EDITABLE_FALSE;
			}
								
		} else {
			node.setEditableState(Node.EDITABLE_INHERITED);
		}
				
		undoable.addPrimitive(new PrimitiveUndoableEditableChange(node, oldValue, newValue));
	}
}