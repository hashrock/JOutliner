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
import java.awt.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2002/08/27 09:42:13 $
 */
 
public class PrimitiveUndoableReplace extends AbstractUndoable implements Undoable {

	private Node parent = null;
	private Node oldNode = null;
	private Node newNode = null;
	
	private int index = -1;
	
	// The Constructors
	public PrimitiveUndoableReplace(Node parent, Node oldNode, Node newNode) {
		this.parent = parent;
		this.oldNode = oldNode;
		this.newNode = newNode;
		this.index = oldNode.currentIndex();
	}

	public void destroy() {
		parent = null;
		oldNode = null;
		newNode = null;
	}

	// Accessors
	public Node getOldNode() {
		return oldNode;
	}
	
	public Node getNewNode() {
		return newNode;
	}
		
	public void undo() {
		JoeTree tree = parent.getTree();
		
		if (newNode != null) {
			// Remove node from visible nodes cache
			tree.removeNode(newNode);
			
			// Swap the nodes
			parent.removeChild(newNode,index);
		}

		// Swap the nodes
		parent.insertChild(oldNode,index);
		
		// Insert the node into the visible nodes cache
		tree.insertNode(oldNode);
		
		// Handle Selection
		tree.addNodeToSelection(oldNode);	
	}
	
	// Undoable Interface
	public void redo() {
		JoeTree tree = parent.getTree();

		// Remove node from visible nodes cache
		tree.removeNode(oldNode);
		
		// Swap the nodes
		parent.removeChild(oldNode, index);	
			
		if (newNode != null) {
			parent.insertChild(newNode, index);
			
			// Insert the node into the visible nodes cache
			tree.insertNode(newNode);
	
			// Handle Selection
			tree.addNodeToSelection(newNode);
		}
	}
}