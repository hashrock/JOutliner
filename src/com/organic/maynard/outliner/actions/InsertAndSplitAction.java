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
 
public class InsertAndSplitAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("InsertAndSplitAction");
		
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
					insert(node, tree, layout);
				} else {
					insertText(node, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					insertAbove(node, tree, layout);
				} else {
					insertAboveText(node, tree, layout);
				}
				break;
			case 2:
				if (isIconFocused) {
					
				} else {
					splitText(textArea, tree, layout);
				}
				break;
			case 3:
				if (isIconFocused) {
					
				} else {
					splitAboveText(textArea, tree, layout);
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void splitAboveText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		// Get Text for nodes.
		String oldText = currentNode.getValue();
		String oldNodeText = currentNode.getValue().substring(0,textArea.getCaretPosition());
		String newNodeText = currentNode.getValue().substring(textArea.getCaretPosition(), currentNode.getValue().length());
		currentNode.setValue(oldNodeText);
		
		// Create a new node and insert it as a sibling immediatly before this node.
		Node newNode = new NodeImpl(currentNode.getTree(), newNodeText);
		newNode.setDepth(currentNode.getDepth());
		currentNode.getParent().insertChild(newNode,currentNode.currentIndex());
		
		tree.insertNode(newNode);

		// Record the EditingNode and CursorPosition
		tree.setEditingNode(newNode);
		tree.setCursorPosition(0);

		// Update Preferred Caret Position
		tree.getDocument().setPreferredCaretPosition(0);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableEdit undoableEdit = new CompoundUndoableEdit(tree);
		undoableEdit.addPrimitive(new PrimitiveUndoableEdit(currentNode, oldText, oldNodeText));
		
		CompoundUndoableInsert undoableInsert = new CompoundUndoableInsert(newNode.getParent());
		undoableInsert.addPrimitive(new PrimitiveUndoableInsert(newNode.getParent(),newNode,newNode.currentIndex()));
		
		CompoundUndoableImpl undoable = new CompoundUndoableImpl(true);
		undoable.setName("Split Text and Insert Above");
		undoable.addPrimitive(undoableEdit);
		undoable.addPrimitive(undoableInsert);
		
		tree.getDocument().getUndoQueue().add(undoable);

		// Redraw and Set Focus
		layout.draw(newNode,OutlineLayoutManager.TEXT);
	}
		
	public static void splitText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Get Text for nodes.
		String oldText = currentNode.getValue();
		String oldNodeText = currentNode.getValue().substring(0,textArea.getCaretPosition());
		String newNodeText = currentNode.getValue().substring(textArea.getCaretPosition(), currentNode.getValue().length());
		currentNode.setValue(oldNodeText);
		
		// Create a new node and insert it as a sibling immediatly after this node, unless
		// the current node is expanded and has children. Then, we should insert it as the first child of the
		// current node.
		Node newNode = new NodeImpl(currentNode.getTree(),newNodeText);
		
		if ((!currentNode.isLeaf()) && (currentNode.isExpanded())) {
			newNode.setDepth(currentNode.getDepth() + 1);
			currentNode.insertChild(newNode,0);				
		} else {
			newNode.setDepth(currentNode.getDepth());
			currentNode.getParent().insertChild(newNode,currentNode.currentIndex() + 1);
		}
		
		tree.insertNode(newNode);

		// Record the EditingNode and CursorPosition
		tree.setEditingNode(newNode);
		tree.setCursorPosition(0);

		// Update Preferred Caret Position
		tree.getDocument().setPreferredCaretPosition(0);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableEdit undoableEdit = new CompoundUndoableEdit(tree);
		undoableEdit.addPrimitive(new PrimitiveUndoableEdit(currentNode, oldText, oldNodeText));
		
		CompoundUndoableInsert undoableInsert = new CompoundUndoableInsert(newNode.getParent());
		undoableInsert.addPrimitive(new PrimitiveUndoableInsert(newNode.getParent(),newNode,newNode.currentIndex()));
		
		CompoundUndoableImpl undoable = new CompoundUndoableImpl(true);
		undoable.setName("Split Text and Insert Below");
		undoable.addPrimitive(undoableEdit);
		undoable.addPrimitive(undoableInsert);
		
		tree.getDocument().getUndoQueue().add(undoable);

		// Redraw and Set Focus
		layout.draw(newNode,OutlineLayoutManager.TEXT);
	}
	
	public static void insertText(Node node, JoeTree tree, OutlineLayoutManager layout) {

		// Abort if node is not editable
		if (!node.isEditable()) {
			return;
		}
		
		Node newNode = new NodeImpl(tree,"");
		int newNodeIndex = 0;
		Node newNodeParent = null;
		
		if ((!node.isLeaf()) && (node.isExpanded())) {
			newNodeParent = node;
			newNode.setDepth(node.getDepth() + 1);
			node.insertChild(newNode, newNodeIndex);
		} else {
			newNodeIndex = node.currentIndex() + 1;
			newNodeParent = node.getParent();
			newNode.setDepth(node.getDepth());
			newNodeParent.insertChild(newNode, newNodeIndex);
		}
		
		int visibleIndex = tree.insertNodeAfter(node, newNode);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(newNode);
		tree.setCursorPosition(0);
		tree.getDocument().setPreferredCaretPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableInsert undoable = new CompoundUndoableInsert(newNodeParent);
		undoable.setName("Insert Node Below");
		undoable.addPrimitive(new PrimitiveUndoableInsert(newNodeParent, newNode, newNodeIndex));
		tree.getDocument().getUndoQueue().add(undoable);
		
		// Redraw and Set Focus
		layout.draw(newNode, visibleIndex, OutlineLayoutManager.TEXT);	
	}

	public static void insertAboveText(Node node, JoeTree tree, OutlineLayoutManager layout) {
		// Abort if node is not editable
		if (!node.isEditable()) {
			return;
		}
		
		Node newNode = new NodeImpl(tree,"");
		int newNodeIndex = node.currentIndex();
		Node newNodeParent = node.getParent();
		
		newNode.setDepth(node.getDepth());
		newNodeParent.insertChild(newNode, newNodeIndex);
		tree.insertNode(newNode);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(newNode);
		tree.setCursorPosition(0);
		tree.getDocument().setPreferredCaretPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableInsert undoable = new CompoundUndoableInsert(newNodeParent);
		undoable.setName("Insert Node Above");
		undoable.addPrimitive(new PrimitiveUndoableInsert(newNodeParent, newNode, newNodeIndex));
		tree.getDocument().getUndoQueue().add(undoable);
		
		// Redraw and Set Focus
		layout.draw(newNode, OutlineLayoutManager.TEXT);	
	}


	// IconFocusedMethods
	public static void insertAbove(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		// Abort if node is not editable
		if (!currentNode.isEditable()) {
			return;
		}
		
		tree.clearSelection();
		
		insertAboveText(currentNode, tree, layout);
	}

	public static void insert(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		// Abort if node is not editable
		if (!currentNode.isEditable()) {
			return;
		}
		
		tree.clearSelection();

		Node newNode = new NodeImpl(tree,"");
		int newNodeIndex = currentNode.currentIndex() + 1;
		Node newNodeParent = currentNode.getParent();

		newNode.setDepth(currentNode.getDepth());
		newNodeParent.insertChild(newNode, newNodeIndex);

		//int visibleIndex = tree.insertNodeAfter(node, newNode);
		tree.insertNode(newNode);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(newNode);
		tree.setCursorPosition(0);
		tree.getDocument().setPreferredCaretPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableInsert undoable = new CompoundUndoableInsert(newNodeParent);
		undoable.setName("Insert Node Below");
		undoable.addPrimitive(new PrimitiveUndoableInsert(newNodeParent, newNode, newNodeIndex));
		tree.getDocument().getUndoQueue().add(undoable);
		
		// Redraw and Set Focus
		layout.draw(newNode, OutlineLayoutManager.TEXT);	
	}
}