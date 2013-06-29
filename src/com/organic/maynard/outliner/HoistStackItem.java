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

import java.util.*;
import javax.swing.*;

public class HoistStackItem {

	// Instance Variables
	private Node hoistedNode = null;
	private Node hoistedNodeParent = null;
	private int hoistedNodeIndex = -1;
	private int hoistedNodeDepth = -1;
	
	private Node oldNodeSet = null;
	private int lineCountOffset = 0;
	
	private boolean oldTreeCommentState = false;
	private boolean newTreeCommentState = false;
		
	
	// The Constructor
	public HoistStackItem(Node hoistedNode) {
		this.hoistedNode = hoistedNode;
		this.hoistedNodeParent = hoistedNode.getParent();
		this.hoistedNodeIndex = hoistedNodeParent.getChildIndex(hoistedNode);
		this.hoistedNodeDepth = hoistedNode.getDepth();
		
		this.oldNodeSet = hoistedNode.getTree().getRootNode();
		this.lineCountOffset = hoistedNode.getLineNumber();
		
		this.oldTreeCommentState = hoistedNode.getTree().getRootNodeCommentState();
		this.newTreeCommentState = hoistedNode.isComment();
	}
	
	public void destroy() {
		hoistedNode = null;
	}
	
	
	// Accessors
	public Node getNode() {return this.hoistedNode;}
	public Node getNodeParent() {return this.hoistedNodeParent;}

	public Node getOldNodeSet() {return this.oldNodeSet;}
	public int getLineCountOffset() {return this.lineCountOffset;}
	
	// Methods
	public void dehoist() {
		// Shorthand
		JoeTree tree = hoistedNode.getTree();
		tree.setRootNodeCommentState(oldTreeCommentState);
		
		hoistedNode.setHoisted(false);

		// Prune things
		tree.setRootNode(oldNodeSet);
		tree.getVisibleNodes().clear();
		hoistedNodeParent.insertChild(hoistedNode, hoistedNodeIndex);
		hoistedNode.setDepthRecursively(hoistedNodeDepth);
		for (int i = 0; i < oldNodeSet.numOfChildren(); i++) {
			Node node = oldNodeSet.getChild(i);
			tree.insertNode(node);
		}
				
		return;
	}
	
	public void hoist() {
		
		// Shorthand
		JoeTree tree = hoistedNode.getTree();
		tree.setRootNodeCommentState(newTreeCommentState);
		
		hoistedNode.setHoisted(true);
		
		// Prune things
		hoistedNode.getParent().removeChild(hoistedNode);
		hoistedNode.setDepthRecursively(-1);
		tree.setRootNode(hoistedNode);
		tree.getVisibleNodes().clear();
		for (int i = 0; i < hoistedNode.numOfChildren(); i++) {
			Node node = hoistedNode.getChild(i);
			tree.insertNode(node);
		}
		
		return;
	}
}