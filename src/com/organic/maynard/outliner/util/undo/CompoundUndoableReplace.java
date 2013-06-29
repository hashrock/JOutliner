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
 * @version $Revision: 1.4 $, $Date: 2002/08/27 09:42:13 $
 */
 
public class CompoundUndoableReplace extends AbstractCompoundUndoable {
	
	// Constants
	private static final String DEFAULT_NAME = "Replace Node";
	
	
	// Instance Fields
	private Node parent = null;
	private boolean deleteMode = false;
	
	// The Constructors
	public CompoundUndoableReplace(Node parent) {
		this(parent, false);
	}

	public CompoundUndoableReplace(Node parent, boolean deleteMode) {
		this(true, parent, deleteMode);
	}

	public CompoundUndoableReplace(boolean isUpdatingGui, Node parent, boolean deleteMode) {
		super(isUpdatingGui);
		this.parent = parent;
		this.deleteMode = deleteMode;
	}


	// Accessors
	public Node getParent() {
		return this.parent;
	}


	// Undoable Interface
	public void destroy() {
		super.destroy();
		parent = null;
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
		JoeTree tree = parent.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		tree.setSelectedNodesParent(parent);
		
		// Replace Everything
		for (int i = 0, limit = primitives.size(); i < limit; i++) {
			primitives.get(i).undo();
		}

		// Find fallback node for drawing and editing
		if (tree.getSelectedNodes().size() <= 0) {
			Node fallbackNode = ((PrimitiveUndoableReplace) primitives.get(primitives.size() - 1)).getNewNode();
			if (fallbackNode != null) {
				fallbackNode = fallbackNode.next();
				if (fallbackNode.isRoot()) {
					fallbackNode = fallbackNode.prevUnSelectedNode();
					if (fallbackNode.isRoot()) {
						tree.setSelectedNodesParent(tree.getRootNode());
					} else {
						layout.setNodeToDrawFrom(fallbackNode, tree.getVisibleNodes().indexOf(fallbackNode));
						tree.setSelectedNodesParent(fallbackNode.getParent());
						tree.addNodeToSelection(fallbackNode);
					}
				} else {
					tree.setSelectedNodesParent(fallbackNode.getParent());
					tree.addNodeToSelection(fallbackNode);
				}
			}
		}
		
		Node newSelectedNode = determineNewSelectedNode(tree);
		
		tree.setEditingNode(newSelectedNode);
		tree.setCursorPosition(0);
		tree.setCursorMarkPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.ICON);
		
		layout.draw(newSelectedNode, OutlineLayoutManager.ICON);
	}
	
	public void redo() {
		// Shorthand
		JoeTree tree = parent.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		// We must ensure that the nodes we will be deleting/replacing are currently selected.
		tree.setSelectedNodesParent(parent);

		for (int i = primitives.size() - 1; i >= 0; i--) {
			tree.addNodeToSelection(((PrimitiveUndoableReplace) primitives.get(i)).getOldNode());
		}		


		// Let's be aware if we are deleting the nodeToDrawFrom.
		boolean nodeToDrawFromDeleted = false;
		if (tree.getDocument().panel.layout.getNodeToDrawFrom().isAncestorSelected()) {
			nodeToDrawFromDeleted = true;
		}

		// Find fallback node for drawing and editing
		boolean allWillBeDeleted = false;
		Node fallbackNode = null;
		if (deleteMode) {
			Node oldNode = ((PrimitiveUndoableReplace) primitives.get(primitives.size() - 1)).getOldNode();
			fallbackNode = oldNode.nextUnSelectedNode();
			
			if (fallbackNode.isRoot()) {
				fallbackNode = oldNode.prevUnSelectedNode();
				if (fallbackNode.isRoot()) {
					allWillBeDeleted = true;
				}
			}
		} else {
			Node oldNode = ((PrimitiveUndoableReplace) primitives.get(0)).getOldNode();
			fallbackNode = oldNode.prev();
			
			if (fallbackNode.isRoot()) {
				fallbackNode = oldNode.nextUnSelectedNode();
				if (fallbackNode.isRoot()) {
					allWillBeDeleted = true;
				}
			}
		}
		
		tree.setSelectedNodesParent(parent);
		
		// Replace Everything
		for (int i = primitives.size() - 1; i >= 0; i--) {
			primitives.get(i).redo();
		}
		
		if (tree.getSelectedNodes().size() <= 0) {
			if (fallbackNode.isRoot()) {
				if (allWillBeDeleted) {
					tree.setSelectedNodesParent(tree.getRootNode());
				} else {
					layout.setNodeToDrawFrom(fallbackNode, tree.getVisibleNodes().indexOf(fallbackNode));
					tree.setSelectedNodesParent(fallbackNode.getParent());
					tree.addNodeToSelection(fallbackNode);
				}
			} else {
				if (nodeToDrawFromDeleted) {
					layout.setNodeToDrawFrom(fallbackNode, tree.getVisibleNodes().indexOf(fallbackNode));
				}
				tree.setSelectedNodesParent(fallbackNode.getParent());
				tree.addNodeToSelection(fallbackNode);
			}
		}

		Node newSelectedNode = determineNewSelectedNode(tree);

		//System.out.println("node: " + newSelectedNode.getValue());
		tree.setEditingNode(newSelectedNode);
		tree.setCursorPosition(0);
		tree.setCursorMarkPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.ICON);

		layout.draw(newSelectedNode, OutlineLayoutManager.ICON);
	}
	
	private Node determineNewSelectedNode(JoeTree tree) {
		Node newSelectedNode = null;

		// Find the range
		Node firstNewSelectedNode = tree.getYoungestInSelection();
		//int ioFirstNewSelectedNode = tree.getVisibleNodes().indexOf(firstNewSelectedNode);
		Node lastNewSelectedNode = tree.getOldestInSelection();
		//int ioLastNewSelectedNode = tree.getVisibleNodes().indexOf(lastNewSelectedNode);

		// Handle Boundary conditions for the selection.
		if (firstNewSelectedNode == tree.getVisibleNodes().get(0)) {
			tree.getDocument().panel.layout.setNodeToDrawFrom(firstNewSelectedNode, 0);
			newSelectedNode = firstNewSelectedNode;
		} else if (lastNewSelectedNode == tree.getVisibleNodes().get(tree.getVisibleNodes().size() - 1)) {
			newSelectedNode = lastNewSelectedNode;
		} else {
			newSelectedNode = firstNewSelectedNode;
		}
		
		return newSelectedNode;
	}
}