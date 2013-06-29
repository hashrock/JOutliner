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

import com.organic.maynard.outliner.guitree.*;
import java.util.*;
import javax.swing.*;

public class HoistStack {
	
	// Instance Variables
	private OutlinerDocument doc = null;
	
	private Stack stack = new Stack();
	
	
	// The Constructor
	public HoistStack(OutlinerDocument doc) {
		this.doc = doc;
	}
	
	public void destroy() {
		stack = null;
	}
	
	
	// Methods
	public void clear() {
		stack.clear();
		
		// Fire Event
		Outliner.documents.fireHoistDepthChangedEvent(this.doc);
	}
	
	public int getHoistDepth() {
		return this.stack.size();
	}
	
	public boolean isHoisted() {
		if (getHoistDepth() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getLineCountOffset() {
		int offset = 0;
		for (int i = 0; i < stack.size(); i++) {
			offset += ((HoistStackItem) stack.get(i)).getLineCountOffset();
		}
		
		return offset;
	}
	
	public synchronized void temporaryHoistAll() {
		for (int i = 0; i < stack.size(); i++) {
			((HoistStackItem) stack.get(i)).hoist();
		}
	}
	
	public synchronized void temporaryDehoistAll() {
		for (int i = stack.size() - 1; i >= 0; i--) {
			((HoistStackItem) stack.get(i)).dehoist();
		}
	}
	
	public void hoist(HoistStackItem item) {
		Node currentNode = item.getNode();
		if (!currentNode.isLeaf() && !currentNode.isHoisted()) {
			
			// Shorthand
			JoeTree tree = currentNode.getTree();
			OutlineLayoutManager layout = tree.getDocument().panel.layout;
			
			// Clear the undoQueue
			if (!doc.getUndoQueue().isEmpty()) {
				String msg = GUITreeLoader.reg.getText("confirm_hoist");
				
				int result = JOptionPane.showConfirmDialog(doc, msg,"",JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					doc.getUndoQueue().clear();
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
			
			// Do the hoist
			item.hoist();
			
			// Update Selection
			tree.setSelectedNodesParent(currentNode);
			tree.addNodeToSelection(currentNode.getFirstChild());
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			tree.setEditingNode(currentNode.getFirstChild());
			tree.setCursorPosition(0);
			tree.setComponentFocus(OutlineLayoutManager.ICON);
			
			// Throw it onto the stack
			stack.push(item);
			
			// Redraw and Set Focus
			Node nodeToDrawFrom = currentNode.getFirstChild();
			int ioNodeToDrawFrom = tree.getVisibleNodes().indexOf(nodeToDrawFrom);
			layout.setNodeToDrawFrom(nodeToDrawFrom, ioNodeToDrawFrom);
			
			layout.redraw();
			//layout.setFocus(nodeToDrawFrom, OutlineLayoutManager.ICON);
			
			// Fire Event
			Outliner.documents.fireHoistDepthChangedEvent(this.doc);
		}
		return;
	}
	
	public void dehoist() {
		if (isHoisted()) {
			
			// Shorthand
			JoeTree tree = doc.tree;
			OutlineLayoutManager layout = doc.panel.layout;
			
			// Clear the undoQueue
			if (!doc.getUndoQueue().isEmpty()) {
				String msg = GUITreeLoader.reg.getText("confirm_dehoist");
				
				int result = JOptionPane.showConfirmDialog(doc, msg,"",JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					doc.getUndoQueue().clear();
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
			
			// Remove it from the stack
			HoistStackItem item = (HoistStackItem) stack.pop();
			
			// Do the dehoist
			item.dehoist();
			
			// Update Selection
			tree.setSelectedNodesParent(item.getNodeParent());
			tree.addNodeToSelection(item.getNode());
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			tree.setEditingNode(item.getNode());
			tree.setCursorPosition(0);
			tree.setComponentFocus(OutlineLayoutManager.ICON);
			
			// Redraw and Set Focus
			Node nodeToDrawFrom = item.getNode();
			int ioNodeToDrawFrom = tree.getVisibleNodes().indexOf(nodeToDrawFrom);
			layout.setNodeToDrawFrom(nodeToDrawFrom, ioNodeToDrawFrom);
			
			layout.redraw();
			//layout.setFocus(nodeToDrawFrom, OutlineLayoutManager.ICON);
			
			// Fire Event
			Outliner.documents.fireHoistDepthChangedEvent(this.doc);
		}
	}
	
	public void dehoistAll() {
		if (isHoisted()) {
		
			// Shorthand
			JoeTree tree = doc.tree;
			OutlineLayoutManager layout = doc.panel.layout;
			
			// Clear the undoQueue
			if (!doc.getUndoQueue().isEmpty()) {
				String msg = GUITreeLoader.reg.getText("confirm_dehoist");
				
				int result = JOptionPane.showConfirmDialog(doc, msg,"",JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					doc.getUndoQueue().clear();
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
				
			HoistStackItem item = null;
			
			while (isHoisted()) {
				// Remove it from the stack
				item = (HoistStackItem) stack.pop();
				
				// Do the dehoist
				item.dehoist();
			}
			
			// Update Selection
			tree.setSelectedNodesParent(item.getNodeParent());
			tree.addNodeToSelection(item.getNode());
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			tree.setEditingNode(item.getNode());
			tree.setCursorPosition(0);
			tree.setComponentFocus(OutlineLayoutManager.ICON);
			
			// Redraw and Set Focus
			Node nodeToDrawFrom = item.getNode();
			int ioNodeToDrawFrom = tree.getVisibleNodes().indexOf(nodeToDrawFrom);
			layout.setNodeToDrawFrom(nodeToDrawFrom, ioNodeToDrawFrom);
			
			layout.redraw();
			//layout.setFocus(nodeToDrawFrom, OutlineLayoutManager.ICON);
			
			// Fire Event
			Outliner.documents.fireHoistDepthChangedEvent(this.doc);
		}
	}
}