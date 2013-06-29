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
 
public class ToggleCommentAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("ToggleCommentAction");
		
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
					toggleCommentAndClear(node, tree, layout);
				} else {
					toggleCommentAndClearText(node, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					toggleComment(node, tree, layout);
				} else {
					toggleCommentText(node, tree, layout);
				}
				break;
			case 2:
				if (isIconFocused) {
					toggleCommentInheritance(node, tree, layout);
				} else {
					toggleCommentInheritanceText(node, tree, layout);
				}
				break;
			case 3:
				if (isIconFocused) {
					clearComment(node, tree, layout);
				} else {
					clearCommentText(node, tree, layout);
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void clearCommentText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		clearCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Clear Comment for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleCommentAndClearText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleCommentAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Comment and Clear Decendants for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleCommentText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Comment for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}

	public static void toggleCommentInheritanceText(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		toggleCommentInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Comment Inheritance for Node");
			tree.getDocument().getUndoQueue().add(undoable);
		}

		// Redraw
		layout.draw(currentNode, OutlineLayoutManager.TEXT);
	}


	// IconFocusedMethods
	public static void clearComment(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			clearCommentForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Clear Comment for Node");
			} else {
				undoable.setName(new StringBuffer().append("Clear Comment for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void clearCommentForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getCommentState();
		int newValue = Node.COMMENT_INHERITED;
		
		if (oldValue != Node.COMMENT_INHERITED) {
			node.setCommentState(Node.COMMENT_INHERITED);
			undoable.addPrimitive(new PrimitiveUndoableCommentChange(node, oldValue, newValue));
		}
				
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearCommentForSingleNode(node.getChild(i), undoable);
		}
	}
	
	public static void toggleCommentAndClear(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleCommentAndClearForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Comment and Clear Decendants for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Comment and Clear Decendants for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void toggleCommentAndClearForSingleNode(Node node, CompoundUndoable undoable) {
		toggleCommentForSingleNode(node, undoable);
		
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearCommentForSingleNode(node.getChild(i), undoable);
		}		
	}
			
	public static void toggleComment(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleCommentForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Comment for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Comment for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	public static void toggleCommentForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getCommentState();
		int newValue = Node.COMMENT_INHERITED;
		boolean isComment = node.isComment();
		
		if (oldValue == Node.COMMENT_FALSE) {
			node.setCommentState(Node.COMMENT_TRUE);
			newValue = Node.COMMENT_TRUE;
					
		} else if (oldValue == Node.COMMENT_TRUE) {
			node.setCommentState(Node.COMMENT_FALSE);
			newValue = Node.COMMENT_FALSE;
		
		} else {
			if (isComment) {
				node.setCommentState(Node.COMMENT_FALSE);
				newValue = Node.COMMENT_FALSE;
			} else {
				node.setCommentState(Node.COMMENT_TRUE);
				newValue = Node.COMMENT_TRUE;
			}
		}
				
		undoable.addPrimitive(new PrimitiveUndoableCommentChange(node, oldValue, newValue));
	}
	
	public static void toggleCommentInheritance(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0; i < nodeList.size(); i++) {
			toggleCommentInheritanceForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Toggle Comment Inheritance for Node");
			} else {
				undoable.setName(new StringBuffer().append("Toggle Comment Inheritance for ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().getUndoQueue().add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}
	
	public static void toggleCommentInheritanceForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getCommentState();
		int newValue = Node.COMMENT_INHERITED;
		boolean isComment = node.isComment();
		
		if (oldValue == Node.COMMENT_INHERITED) {
			if (isComment) {
				node.setCommentState(Node.COMMENT_TRUE);
				newValue = Node.COMMENT_TRUE;
			} else {
				node.setCommentState(Node.COMMENT_FALSE);
				newValue = Node.COMMENT_FALSE;
			}
								
		} else {
			node.setCommentState(Node.COMMENT_INHERITED);
		}
				
		undoable.addPrimitive(new PrimitiveUndoableCommentChange(node, oldValue, newValue));
	}
}