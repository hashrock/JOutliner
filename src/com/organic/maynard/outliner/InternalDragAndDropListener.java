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

import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.16 $, $Date: 2002/08/28 05:56:36 $
 */
 
public class InternalDragAndDropListener implements MouseListener {

	// Constants
	private static final int ICON = 0;
	private static final int TEXT = 1;
	private static final int OTHER = -1;
	
	
	// Instance Fields
	protected boolean isDragging = false;
	protected Node targetNode = null;
	protected int componentType = OTHER;
	
	protected OutlinerCellRendererImpl currentRenderer = null;
	protected OutlinerCellRendererImpl prevRenderer = null;


	// The Constructor
	public InternalDragAndDropListener() {}
	
	public void destroy() {
		targetNode = null;
		currentRenderer = null;
		prevRenderer = null;
	}
	
	
	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {
		if (isDragging) {
			//System.out.println("DND Mouse Entered: " + e.paramString());
			
			componentType = getUIComponents(e.getSource());
			targetNode = getNodeFromSource(e.getSource());
			
			// Update the UI
			if (!targetNode.isAncestorSelected()) {
				if (componentType == ICON) {
					currentRenderer.button.setIcon(OutlineButton.ICON_DOWN_ARROW);
				} else if (componentType == TEXT) {
					currentRenderer.button.setIcon(OutlineButton.ICON_SE_ARROW);
				}
			} else if (targetNode.isSelected() && (componentType == TEXT) && (!targetNode.prevSibling().isSelected())) {
				OutlineLayoutManager layout = targetNode.getTree().getDocument().panel.layout;
				OutlinerCellRendererImpl renderer = layout.getUIComponent(targetNode.prevSibling());
				if (renderer != null) {
					renderer.button.setIcon(OutlineButton.ICON_SE_ARROW);
				}
			}
		}
	}
	
	public void mouseExited(MouseEvent e) {
		if (isDragging) {
			// Update the UI
			if (targetNode.isSelected() && !targetNode.isFirstChild() && (componentType == TEXT)) {
				OutlineLayoutManager layout = targetNode.getTree().getDocument().panel.layout;
				OutlinerCellRendererImpl renderer = layout.getUIComponent(targetNode.prevSibling());
				if (renderer != null) {
					renderer.button.updateIcon();
				}
			} else {
				currentRenderer.button.updateIcon();
			}
			
			// Update targetNode
			targetNode = null;
		}
	}
	
	public void mousePressed(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		// Initiate Drag and Drop
		targetNode = getNodeFromSource(e.getSource());
		componentType = getUIComponents(e.getSource());
				
		if ((componentType == ICON) && targetNode.isSelected()) {
			isDragging = true;
		} else {
			reset();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isDragging) {
			//System.out.println("DND Mouse Released: " + e.paramString());
			
			// Handle the drop
			currentRenderer.button.updateIcon();
			
			if (targetNode != null) {
				if (!targetNode.isAncestorSelected()) {
					if (componentType == ICON) {
						moveAsOlderSibling();
					} else if (componentType == TEXT) {
						moveAsFirstChild();
					}
				} else if (targetNode.isSelected() && !targetNode.isFirstChild() && (componentType == TEXT)) {
					OutlineLayoutManager layout = targetNode.getTree().getDocument().panel.layout;
					OutlinerCellRendererImpl renderer = layout.getUIComponent(targetNode.prevSibling());
					if (renderer != null) {
						renderer.button.updateIcon();
					}
					
					targetNode = targetNode.prevSibling();
					moveAsFirstChild();
				}
			}
			
			// Terminate Drag and Drop
			reset();
		}
	}

	public void mouseClicked(MouseEvent e) {
		//System.out.println("DND Mouse Clicked: " + e.paramString());
	}


	// Utility Methods
	private void moveAsOlderSibling() {
		JoeTree tree = targetNode.getTree();

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(tree.getSelectedNodesParent(),targetNode.getParent());
		int targetIndexAdj = 0;
		int currentIndexAdj = 0;
		
		for (int i = 0, limit = tree.getSelectedNodes().size(); i < limit; i++) {
			// Record the Insert in the undoable
			Node nodeToMove = tree.getSelectedNodes().get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			int currentIndex = nodeToMove.currentIndex();
			int targetIndex = targetNode.currentIndex();
			
			if (nodeToMove.getParent() == targetNode.getParent()) {
				if (currentIndex > targetIndex) {
					targetIndexAdj++;
					targetIndex += targetIndexAdj;
				} else if (currentIndex < targetIndex) {
					currentIndex += currentIndexAdj;
					currentIndexAdj--;
				}
			} else {
				targetIndexAdj++;
				targetIndex += targetIndexAdj;
				currentIndex += currentIndexAdj;
				currentIndexAdj--;
			}
			
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, currentIndex, targetIndex));
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Move Node");
			} else {
				undoable.setName(new StringBuffer().append("Move ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
	}
	
	private void moveAsFirstChild() {
		JoeTree tree = targetNode.getTree();

		CompoundUndoableMove undoable = new CompoundUndoableMove(tree.getSelectedNodesParent(),targetNode);
		int currentIndexAdj = 0;
		
		for (int i = tree.getSelectedNodes().size() - 1; i >= 0; i--) {
			Node nodeToMove = tree.getSelectedNodes().get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}

			int currentIndex = nodeToMove.currentIndex();
			int targetIndex = 0;
			
			if (nodeToMove.getParent() == targetNode) {
				currentIndex += currentIndexAdj;
				currentIndexAdj++;
			}
			
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, currentIndex, targetIndex));
		}
		
		if (!undoable.isEmpty()) {
			if (undoable.getPrimitiveCount() == 1) {
				undoable.setName("Move Node");
			} else {
				undoable.setName(new StringBuffer().append("Move ").append(undoable.getPrimitiveCount()).append(" Nodes").toString());
			}
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
	}
	
		
	private Node getNodeFromSource(Object source) {
		if (source instanceof OutlinerCellRendererImpl) {
			return ((OutlinerCellRendererImpl) source).node;
		} else if (source instanceof OutlineButton) {
			return ((OutlineButton) source).renderer.node;
		} else if (source instanceof OutlineLineNumber) {
			return ((OutlineLineNumber) source).renderer.node;
		} else if (source instanceof OutlineCommentIndicator) {
			return ((OutlineCommentIndicator) source).renderer.node;
		} else {
			return null;
		}
	}

	private int getUIComponents(Object source) {
		if (source instanceof OutlinerCellRendererImpl) {
			prevRenderer = currentRenderer;
			currentRenderer = (OutlinerCellRendererImpl) source;
			return TEXT;
		} else if (source instanceof OutlineButton) {
			prevRenderer = currentRenderer;
			currentRenderer = ((OutlineButton) source).renderer;
			return ICON;
		} else if (source instanceof OutlineLineNumber) {
			prevRenderer = currentRenderer;
			currentRenderer = ((OutlineLineNumber) source).renderer;
			return ICON;
		} else if (source instanceof OutlineCommentIndicator) {
			prevRenderer = currentRenderer;
			currentRenderer = ((OutlineCommentIndicator) source).renderer;
			return ICON;
		} else {
			// Something went wrong.
			return OTHER;
		}
	}
	
	private void reset() {
		isDragging = false;
		targetNode = null;
		componentType = OTHER;
	
		currentRenderer = null;
		prevRenderer = null;
	}
}