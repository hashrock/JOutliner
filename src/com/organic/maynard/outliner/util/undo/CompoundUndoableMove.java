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
 
package com.organic.maynard.outliner.util.undo;

import com.organic.maynard.outliner.*;

import java.util.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2002/08/27 09:42:13 $
 */

public class CompoundUndoableMove extends AbstractCompoundUndoable {
	
	// Constants
	private static final String DEFAULT_NAME = "Move Node";
	
	
	// Instance Fields
	private Node parent = null;
	private Node targetParent = null;
	
	// The Constructors
	public CompoundUndoableMove(Node parent, Node targetParent) {
		this(true, parent, targetParent);
	}

	public CompoundUndoableMove(boolean isUpdatingGui, Node parent, Node targetParent) {
		super(isUpdatingGui);
		this.parent = parent;
		this.targetParent = targetParent;
	}
		
	// Accessors
	public Node getParent() {return parent;}
	public Node getTargetParent() {return targetParent;}
	
	
	// Undoable Interface
	public void destroy() {
		super.destroy();
		parent = null;
		targetParent = null;
	}

	public String getName() {
		String name = super.getName();
		if (name == null) {
			return DEFAULT_NAME;
		} else {
			return name;
		}
	}

	public void undo() {
		// Shorthand
		Node youngestNode = ((PrimitiveUndoableMove) primitives.get(0)).getNode();
		JoeTree tree = youngestNode.getTree();

		if (isUpdatingGui()) {
			// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
			OutlineLayoutManager layout = tree.getDocument().panel.layout;
			Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();

			// Do all the Inserts
			tree.setSelectedNodesParent(parent);

			for (int i = primitives.size() - 1; i >= 0; i--) {
				PrimitiveUndoableMove primitive = (PrimitiveUndoableMove) primitives.get(i);
				
				// ShortHand
				Node node = primitive.getNode();
				
				// Remove the Node
				tree.removeNode(node);
				targetParent.removeChild(node);

				// Insert the Node
				parent.insertChild(node, primitive.getIndex());
				tree.insertNode(node);
				
				// Set depth if neccessary.
				if (targetParent.getDepth() != parent.getDepth()) {
					node.setDepthRecursively(parent.getDepth() + 1);
				}

				// Update selection
				tree.addNodeToSelection(node);
			}

			// Record the EditingNode
			tree.setEditingNode(youngestNode);
			tree.setComponentFocus(OutlineLayoutManager.ICON);

			// Redraw and Set Focus
			if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the top when multiple nodes are selected.
				Node visNode = layout.getNodeToDrawFrom().prev();
				int ioVisNode = tree.getVisibleNodes().indexOf(visNode);
				int ioNodeToDrawFromTmp = tree.getVisibleNodes().indexOf(nodeToDrawFromTmp);
				if (ioVisNode < ioNodeToDrawFromTmp) {
					layout.setNodeToDrawFrom(visNode, ioVisNode);
				} else {
					layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
				}
			}
			
			layout.draw(tree.getYoungestInSelection(), OutlineLayoutManager.ICON);		
		} else {
			for (int i = primitives.size() - 1; i >= 0; i--) {
				PrimitiveUndoableMove primitive = (PrimitiveUndoableMove) primitives.get(i);
				
				// ShortHand
				Node node = primitive.getNode();
				
				// Remove the Node
				tree.removeNode(node);
				targetParent.removeChild(node);

				// Insert the Node
				parent.insertChild(node, primitive.getIndex());
				tree.insertNode(node);
				
				// Set depth if neccessary.
				if (targetParent.getDepth() != parent.getDepth()) {
					node.setDepthRecursively(parent.getDepth() + 1);
				}
			}		
		}
	}
	
	public void redo() {
		// Shorthand
		Node youngestNode = ((PrimitiveUndoableMove) primitives.get(0)).getNode();
		JoeTree tree = youngestNode.getTree();

		if (isUpdatingGui()) {
			// Store nodeToDrawFrom if neccessary. Used when the selection is disconnected.
			OutlineLayoutManager layout = tree.getDocument().panel.layout;
			Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();

			// Do all the Inserts
			tree.setSelectedNodesParent(targetParent);
			
			for (int i = 0, limit = primitives.size(); i < limit; i++) {
				PrimitiveUndoableMove primitive = (PrimitiveUndoableMove) primitives.get(i);

				// ShortHand
				Node node = primitive.getNode();

				// Remove the Node
				tree.removeNode(node);
				parent.removeChild(node);

				// Insert the Node
				targetParent.insertChild(node, primitive.getTargetIndex());
				tree.insertNode(node);

				// Set depth if neccessary.
				if (targetParent.getDepth() != parent.getDepth()) {
					node.setDepthRecursively(targetParent.getDepth() + 1);
				}
				
				// Update selection
				tree.addNodeToSelection(node);
			}

			// Record the EditingNode
			tree.setEditingNode(youngestNode);
			tree.setComponentFocus(OutlineLayoutManager.ICON);

			// Redraw and Set Focus
			if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the top when multiple nodes are selected.
				Node visNode = layout.getNodeToDrawFrom().prev();
				int ioVisNode = tree.getVisibleNodes().indexOf(visNode);
				int ioNodeToDrawFromTmp = tree.getVisibleNodes().indexOf(nodeToDrawFromTmp);
				if (ioVisNode < ioNodeToDrawFromTmp) {
					layout.setNodeToDrawFrom(visNode, ioVisNode);
				} else {
					layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
				}
			}
			
			layout.draw(tree.getYoungestInSelection(), OutlineLayoutManager.ICON);		
		} else {
			for (int i = 0, limit = primitives.size(); i < limit; i++) {
				PrimitiveUndoableMove primitive = (PrimitiveUndoableMove) primitives.get(i);

				// ShortHand
				Node node = primitive.getNode();

				// Remove the Node
				tree.removeNode(node);
				parent.removeChild(node);

				// Insert the Node
				targetParent.insertChild(node, primitive.getTargetIndex());
				tree.insertNode(node);

				// Set depth if neccessary.
				if (targetParent.getDepth() != parent.getDepth()) {
					node.setDepthRecursively(targetParent.getDepth() + 1);
				}
			}		
		}
	}
}