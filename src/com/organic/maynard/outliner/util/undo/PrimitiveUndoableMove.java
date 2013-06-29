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
 
public class PrimitiveUndoableMove extends AbstractUndoable implements Undoable {

	private CompoundUndoableMove undoable = null;
	
	private Node node = null;
	private int index = 0;
	private int targetIndex = 0;
	
	
	// The Constructors
	public PrimitiveUndoableMove(CompoundUndoableMove undoable, Node node, int index, int targetIndex) {
		this.undoable = undoable;
		this.node = node;
		this.index = index;
		this.targetIndex = targetIndex;
	}

	public void destroy() {
		undoable = null;
		node = null;
	}
	
	// Accessors
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return this.node;
	}

	public int getIndex() {
		return this.index;
	}
	
	public int getTargetIndex() {
		return this.targetIndex;
	}
	
	public void undo() {
		// Remove the Node
		node.getTree().removeNode(node);
		undoable.getTargetParent().removeChild(node);

		// Insert the Node
		undoable.getParent().insertChild(node,index);
		node.getTree().insertNode(node);
		
		// Set depth if neccessary.
		if (undoable.getTargetParent().getDepth() != undoable.getParent().getDepth()) {
			node.setDepthRecursively(undoable.getParent().getDepth() + 1);
		}
		
		// Update selection
		node.getTree().addNodeToSelection(node);
	}
	
	// Undoable Interface
	public void redo() {
		// Remove the Node
		node.getTree().removeNode(node);
		undoable.getParent().removeChild(node);

		// Insert the Node
		undoable.getTargetParent().insertChild(node,targetIndex);
		node.getTree().insertNode(node);

		// Set depth if neccessary.
		if (undoable.getTargetParent().getDepth() != undoable.getParent().getDepth()) {
			node.setDepthRecursively(undoable.getTargetParent().getDepth() + 1);
		}
		
		// Update selection
		node.getTree().addNodeToSelection(node);
	}
}