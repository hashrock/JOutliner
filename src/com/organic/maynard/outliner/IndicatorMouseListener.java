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

import com.organic.maynard.outliner.util.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;

import com.organic.maynard.util.string.*;

import com.organic.maynard.outliner.actions.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.12 $, $Date: 2002/08/28 05:56:36 $
 */
 
public class IndicatorMouseListener implements MouseListener {

	// Instance Fields
	private OutlinerCellRendererImpl textArea = null;

	// The Constructors
	public IndicatorMouseListener() {}
	
	public void destroy() {
		textArea = null;
	}	

	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		Component c = e.getComponent();
		if (c instanceof OutlineCommentIndicator) {
			// Make sure it's in the icon, not just the JLabel.
			Point p = e.getPoint();
			if ((p.x <= OutlineCommentIndicator.TRUE_WIDTH) && (p.y <= OutlineCommentIndicator.BUTTON_HEIGHT)) {
				textArea = ((OutlineCommentIndicator) c).renderer;
				Node node = textArea.node;
	 			JoeTree tree = textArea.node.getTree();
				
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearComment(tree);
					} else {
						toggleCommentInheritance(tree);
					}
				} else if (e.isShiftDown()) {
					toggleComment(tree);
				} else {
					toggleCommentAndClear(tree);
				}			
	
				// Redraw and set focus
				tree.getDocument().panel.layout.redraw();
				//tree.getDocument().panel.layout.setFocus(tree.getEditingNode(), tree.getComponentFocus());
			}
		} else if (c instanceof OutlineEditableIndicator) {
			// Make sure it's in the icon, not just the JLabel.
			Point p = e.getPoint();
			if ((p.x <= OutlineEditableIndicator.TRUE_WIDTH) && (p.y <= OutlineEditableIndicator.BUTTON_HEIGHT)) {
				textArea = ((OutlineEditableIndicator) c).renderer;
				Node node = textArea.node;
	 			JoeTree tree = textArea.node.getTree();
				
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearEditable(tree);
					} else {
						toggleEditableInheritance(tree);
					}
				} else if (e.isShiftDown()) {
					toggleEditable(tree);
				} else {
					toggleEditableAndClear(tree);
				}			
	
				// Redraw and set focus
				tree.getDocument().panel.layout.redraw();
				//tree.getDocument().panel.layout.setFocus(tree.getEditingNode(), tree.getComponentFocus());
			}
		} else if (c instanceof OutlineMoveableIndicator) {
			// Make sure it's in the icon, not just the JLabel.
			Point p = e.getPoint();
			if ((p.x <= OutlineMoveableIndicator.TRUE_WIDTH) && (p.y <= OutlineMoveableIndicator.BUTTON_HEIGHT)) {
				textArea = ((OutlineMoveableIndicator) c).renderer;
				Node node = textArea.node;
	 			JoeTree tree = textArea.node.getTree();
				
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearMoveable(tree);
					} else {
						toggleMoveableInheritance(tree);
					}
				} else if (e.isShiftDown()) {
					toggleMoveable(tree);
				} else {
					toggleMoveableAndClear(tree);
				}			
	
				// Redraw and set focus
				tree.getDocument().panel.layout.redraw();
				//tree.getDocument().panel.layout.setFocus(tree.getEditingNode(), tree.getComponentFocus());
			}
		}
	}
	
	// Comments
	private void clearComment(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleCommentAction.clearCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Clear Comment for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}

	private void toggleCommentAndClear(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleCommentAction.toggleCommentAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Comment and Clear Decendants for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}

	private void toggleComment(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleCommentAction.toggleCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Comment for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}

	private void toggleCommentInheritance(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleCommentAction.toggleCommentInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Comment Inheritance for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}

	// Editable
	private void clearEditable(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleEditableAction.clearEditableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Clear Editability for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
		
		tree.getDocument().attPanel.update();
	}

	private void toggleEditableAndClear(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleEditableAction.toggleEditableAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Editability and Clear Decendants for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
		
		tree.getDocument().attPanel.update();
	}

	private void toggleEditable(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleEditableAction.toggleEditableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Editability for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
		
		tree.getDocument().attPanel.update();
	}

	private void toggleEditableInheritance(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleEditableAction.toggleEditableInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Editability Inheritance for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
		
		tree.getDocument().attPanel.update();
	}

	// Moveable
	private void clearMoveable(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleMoveableAction.clearMoveableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Clear Moveability for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}

	private void toggleMoveableAndClear(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleMoveableAction.toggleMoveableAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Moveability and Clear Decendants for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}

	private void toggleMoveable(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleMoveableAction.toggleMoveableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Moveability for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}

	private void toggleMoveableInheritance(JoeTree tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		ToggleMoveableAction.toggleMoveableInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			undoable.setName("Toggle Moveability Inheritance for Node");
			tree.getDocument().undoQueue.add(undoable);
		}
	}
}