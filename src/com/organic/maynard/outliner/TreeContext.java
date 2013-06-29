/**
 * Portions Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions Copyright (C) 2002 Stan Krute <Stan@StanKrute.com>
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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class TreeContext extends AttributeContainerImpl implements JoeTree {
	
	// Private Instance Variables
	private OutlinerDocument document = null;
	
	private JoeNodeList visibleNodes = Outliner.newNodeList(1000);
	private JoeNodeList selectedNodes = Outliner.newNodeList(100);
	private Node rootNode = null;
	
	
	// The Constructors
	public TreeContext(OutlinerDocument document) {
		this.document = document;
		
		resetStructure();
		
		document.panel.layout.setNodeToDrawFrom(getEditingNode(),0);
	}
	
	public TreeContext() {
		resetStructure();
	}
	
	public void destroy() {
		visibleNodes = null;
		selectedNodes = null;
		rootNode.destroy();
		rootNode = null;
		editingNode = null;
		mostRecentNodeTouched = null;
		selectedNodesParent = null;
		document = null;
	}
	
	public void reset() {
		document = null;
		visibleNodes.clear();
		selectedNodes.clear();
		rootNode = null;
		
		resetStructure();
	}
	
	private void resetStructure() {
		// Create an empty Tree
		setRootNode(new NodeImpl(this,"ROOT"));
		rootNode.setHoisted(true);
		
		NodeImpl child = new NodeImpl(this,"");
		child.setDepth(0);
		rootNode.insertChild(child, 0);
		insertNode(child);
		
		// Record the current location
		setEditingNode(child, false);
	}
	
	
	// Comments
	private boolean comment = false;
	
	// these two methods are part of JoeTree interface
	public void setRootNodeCommentState(boolean comment) {this.comment = comment;}
	public boolean getRootNodeCommentState() {return this.comment;}
	
	// Editablity
	private boolean editable = true;
	
	// these two methods are part of JoeTree interface
	public void setRootNodeEditableState(boolean editable) {this.editable = editable;}
	public boolean getRootNodeEditableState() {return this.editable;}
	
	// Moveability
	private boolean moveable = true;
	
	// these two methods are part of JoeTree interface
	public void setRootNodeMoveableState(boolean moveable) {this.moveable = moveable;}
	public boolean getRootNodeMoveableState() {return this.moveable;}
	
	
	// Line Count Control
	private int lineCountKey = 0;
	
	
	public int getLineCountKey() {
		return lineCountKey;
	}
	
	public void incrementLineCountKey() {
		lineCountKey++;
		
		// Lets not grow forever since it could be possible to 
		// exceed max int although very very very unlikely, but still, better to be safe.
		if (lineCountKey > 1000000) {
			lineCountKey = 0;
		}
	}
	
	
	// Accessors
	
	// Statistics
	public int getLineCount() {
		int total = 0;
		for (int i = 0, limit = rootNode.numOfChildren(); i < limit; i++) {
			total++;
			total += rootNode.getChild(i).getDecendantCount();
		}
		return total;
	}
	
	public int getCharCount() {
		return rootNode.getDecendantCharCount();
	}
	
	
	// Tracking the Editing Location
	private Node editingNode = null;
	private int cursorPosition = 0;
	private int cursorMarkPosition = 0;
	private int componentFocus = OutlineLayoutManager.TEXT;
	
	public void setEditingNode(Node editingNode) {
		setEditingNode(editingNode, true);
	}
	
	public void setEditingNode(Node editingNode, boolean updateAttPanel) {
		this.editingNode = editingNode;
		
		if (updateAttPanel) {
			document.attPanel.update();
		}
	}
	
	public Node getEditingNode() {
		return editingNode;
	}
	
	public void setCursorMarkPosition(int cursorMarkPosition) {
		this.cursorMarkPosition = cursorMarkPosition;
		
		// fireEvent
		Outliner.documents.fireSelectionChangedEvent(this, getComponentFocus());
	}
	
	public int getCursorMarkPosition() {
		return cursorMarkPosition;
	}
	
	public void setComponentFocus(int componentFocus) {
		this.componentFocus = componentFocus;
		
		// fireEvent
		Outliner.documents.fireSelectionChangedEvent(this, componentFocus);
	}
	
	public int getComponentFocus() {
		return componentFocus;
	}
	
	public void setCursorPosition(int cursorPosition) {
		setCursorPosition(cursorPosition,true);
	}
	
	public void setCursorPosition(int cursorPosition, boolean setMark) {
		this.cursorPosition = cursorPosition;
		if (setMark) {
			setCursorMarkPosition(cursorPosition);
		}
		
		// fireEvent
		Outliner.documents.fireSelectionChangedEvent(this, getComponentFocus());
	}
	
	public int getCursorPosition() {
		return cursorPosition;
	}
	
	// TBD: need a method that sets componentfocus, mark and cursor all in one and only sends one event.
	
	
	// Tree Methods
	public Node getPrevNode(Node existingNode) {
		int prevNodeIndex = visibleNodes.indexOf(existingNode) - 1;
		if (prevNodeIndex < 0) {
			return null;
		}
		return visibleNodes.get(prevNodeIndex);
	}
	
	public Node getNextNode(Node existingNode) {
		int nextNodeIndex = visibleNodes.indexOf(existingNode) + 1;
		if (nextNodeIndex >= visibleNodes.size()) {
			return null;
		}
		return visibleNodes.get(nextNodeIndex);
	}
	
	public void addNode(Node node) {
		visibleNodes.add(node);
	}
	
	public void removeNode(Node node) {
		int index = visibleNodes.indexOf(node);
		
		if (index != -1) {
			int lastIndex = visibleNodes.indexOf(node.getLastViewableDecendent());
			visibleNodes.removeRange(index, lastIndex + 1);
		}
	}
	
	public void insertNode(Node node) {
		// Find the first Ancestor that is in the cache or Root
		Node ancestor = TreeContext.getYoungestVisibleAncestor(node, this);
		
		// Expand all nodes in the path down to the node
		node.expandAllAncestors();
		
		// Walk the tree Downwards inserting all expanded nodes and their children
		insertChildrenIntoVisibleNodesCache(ancestor, this, visibleNodes.indexOf(ancestor));
	}
	
	public int insertNodeAfter(Node existingNode, Node newNode) {
		int nodeIndex = visibleNodes.indexOf(existingNode) + 1;
		if (nodeIndex >= 0) {
			visibleNodes.add(nodeIndex, newNode);
		}
		return nodeIndex;
	}
	
	public void insertNode(Node node, int index) {
		visibleNodes.add(index, node);
	}
	
	public void insertNodeAndChildren(Node node, int index) {
		visibleNodes.add(index, node);
		
		// Walk the tree Downwards inserting all expanded nodes and their children
		insertChildrenIntoVisibleNodesCache(node, this, index);
	}
	
	// Handling Node Selection
	private Node mostRecentNodeTouched = null;
	private Node selectedNodesParent = null;
	
	public int getNumberOfSelectedNodes() {
		return selectedNodes.size();
	}
	
	public void setSelectedNodesParent(Node node) {
		setSelectedNodesParent(node,true);
	}
	
	public void setSelectedNodesParent(Node node, boolean doClear) {
		if (doClear) {
			clearSelection();
		}
		this.selectedNodesParent = node;
	}
	
	public Node getSelectedNodesParent() {return selectedNodesParent;}
	
	public void clearSelection() {
		for (int i = selectedNodes.size() - 1; i >= 0; i--) {
			selectedNodes.get(i).setSelected(false);
		}
		
		selectedNodes.clear();
	}
	
	public void addNodeToSelection(Node node) {
		if (node.isSelected()) {
			return; // Don't add a node if it is already selected.
		} else if (node.getParent() == getSelectedNodesParent()) {			
			node.setSelected(true);
			mostRecentNodeTouched = node;
			
			// Maintain the selected nodes in order from youngest to oldest
			if (selectedNodes.size() > 0) {
				int nodeIndex = node.currentIndex();
				
				NodeImpl parent = (NodeImpl) selectedNodesParent;
				int searchStartIndex = 0;
				int childCount = parent.children.size() - 1;
				
				for (int i = 0, limit = selectedNodes.size(); i < limit; i++) {
					searchStartIndex = parent.children.indexOf(selectedNodes.get(i), searchStartIndex, childCount);
					if (searchStartIndex > nodeIndex) {
						selectedNodes.add(i, node);
						return;
					}
				}
			}
			
			selectedNodes.add(node);
		}
	}
	
	public void removeNodeFromSelection(Node node) {
		if (node.getParent() == getSelectedNodesParent()) {
			node.setSelected(false);
			int index = selectedNodes.indexOf(node);
			if (index != -1) {
				selectedNodes.remove(index);
			}
		}	
	}
	
	public void selectRangeFromMostRecentNodeTouched(Node node) {
		if (node.getParent() == getSelectedNodesParent()) {
			int indexA = mostRecentNodeTouched.currentIndex();
			int indexB = node.currentIndex();
			
			int start = Math.min(indexA,indexB);
			int end = Math.max(indexA,indexB);
			
			clearSelection();
			
			for (int i = start; i <= end; i++) {
				Node theNode = getSelectedNodesParent().getChild(i);
				theNode.setSelected(true);
				selectedNodes.add(theNode);
			}
		}
	}
	
	public Node getYoungestInSelection() {
		try {
			return selectedNodes.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public Node getOldestInSelection() {
		try {
			return selectedNodes.get(selectedNodes.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	
	// Tree Manipulation
	public void promoteNode(Node currentNode, int currentNodeIndex) {
		if (currentNode.getParent().isRoot()) {
			// Our parent is root. Since we can't be promoted to root level, Abort.
			return;
		}
		
		Node targetNode = currentNode.getParent().getParent();
		int insertIndex = currentNode.getParent().currentIndex() + 1;
		
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode, currentNodeIndex);
		
		// Append the selected node to the target node.
		targetNode.insertChild(currentNode, insertIndex);
		currentNode.setDepthRecursively(targetNode.getDepth() + 1);
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);
	}
	
	public void demoteNode(Node currentNode, Node targetNode, int currentNodeIndex) {
		if (targetNode == currentNode) {
			// We have no previous sibling, so Abort.
			return;
		}
		
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode, currentNodeIndex);
		
		// Append the selected node to the target node.
		targetNode.insertChild(currentNode,targetNode.numOfChildren());
		currentNode.setDepthRecursively(targetNode.getDepth() + 1);
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);
	}
	
	
	// Misc Methods
	public boolean isWholeDocumentSelected() {
		if ((selectedNodesParent != null) &&
			selectedNodesParent.isRoot() && 
			(getNumberOfSelectedNodes() == selectedNodesParent.numOfChildren()))
		{
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isDocumentEmpty() {
		if ((rootNode.numOfChildren() == 1) &&
			rootNode.getFirstChild().isLeaf() &&
			rootNode.getFirstChild().getValue().equals(""))
		{
			return true;
		} else {
			return false;
		}
	}
	
	// ----------------------- JoeTree interface ------------------------
	
	public OutlinerDocument getDocument () {
		return document;
	}
	
	
	public void setDocument (OutlinerDocument someDocument) {
		document = someDocument;
	}
	
	
	public JoeNodeList getVisibleNodes() {
		return visibleNodes;
	}
	
	
	public JoeNodeList getSelectedNodes() {
		return selectedNodes;
	}
	
	
	public Node getRootNode () {
		return rootNode ;
	}
	
	
	public void setRootNode(Node someNode) {
		rootNode = someNode;
		rootNode.setExpandedClean(true);
	}
	
	
	// Static Methods formerly instance methods of Node
	public static Node getYoungestVisibleAncestor(Node node, TreeContext tree) {
		Node parent = node.getParent();
		
		if (parent.isRoot()) {
			return parent;
		} else if (tree.getVisibleNodes().contains(parent)) {
			return parent;
		} else {
			return getYoungestVisibleAncestor(parent, tree);
		}
	}
	
	// This method could be optomized better by first finding the range and then doing a batch insert.
	public static int insertChildrenIntoVisibleNodesCache(Node node, TreeContext tree, int index) {
		if (node.isExpanded()) {
			int childrenCount = node.numOfChildren();
			for (int i = 0; i < childrenCount; i++) {
				index++;
				Node child = node.getChild(i);
				
				if (index < tree.getVisibleNodes().size()) {
					if (tree.getVisibleNodes().get(index) != child) {
						tree.getVisibleNodes().add(index, child);
					}
				} else {
					tree.getVisibleNodes().add(index, child);
				}
				index = insertChildrenIntoVisibleNodesCache(child, tree, index);
			}
		}
		return index;
	}
}