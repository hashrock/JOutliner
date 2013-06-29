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

import com.organic.maynard.outliner.util.preferences.*;
import java.util.*;
import java.awt.*;

import com.organic.maynard.util.string.*;

public class PadSelection implements JoeReturnCodes {
	
	// The Constructors
	public PadSelection() {}


	// This method provides backwards compatibility but is now depricated.
	public static Node pad(String text, JoeTree tree, int targetDepth, String lineEndString) {
		Node tempRoot = new NodeImpl(tree,"");

		int success = pad(text, tree, targetDepth, lineEndString, tempRoot);
		
		return tempRoot;
	}
	
	private static int padRetVal = FAILURE;
	
	// This code should be rewritten to use instances of a PadSelection object so that there can be
	// one object per thread. Synchonizing it is a cheap short term fix.
	public synchronized static int pad(
		String text, 
		JoeTree tree, 
		int targetDepth, 
		String lineEndString, 
		Node tempRoot
	) {
		padRetVal = SUCCESS;
		
		tempRoot.setDepth(targetDepth - 1);
		
		// Break the text up into lines
		NodeList nodes = new NodeList();
		int shallowest = -1;
		
		StringSplitter splitter = new StringSplitter(text,lineEndString);
		while (splitter.hasMoreElements()) {
			String line = (String) splitter.nextElement();
			
			int depth = Count.startsWith(line,Preferences.DEPTH_PAD_STRING);
			line = Replace.replace(line, Preferences.DEPTH_PAD_STRING, "");
			
			// Record Shallowest
			if ((depth < shallowest) || (shallowest == -1)) {
				shallowest = depth;
			}
			
			Node node = new NodeImpl(tree,line);
			node.setDepth(depth);
			nodes.add(node);
		}
		
		// Abort if we've got an empty node list.
		if (nodes.size() <= 0) {
			return padRetVal;
		}
		
		// Loop over node list and pad as necessary.
		Node previousNode = null;
		int previousDepth = -1;
		
		for (int i = 0, limit = nodes.size(); i < limit; i++) {
			Node node = nodes.get(i);
			
			int currentDepth = node.getDepth() - shallowest + targetDepth;
			node.setDepth(currentDepth);
			
			if (previousNode == null) {
				// Kickoff, should only happen for first node in list.
				appendChildPaddedForDepth(tempRoot,node,tree,tempRoot);
			} else if (currentDepth == previousDepth) {
				previousNode.getParent().appendChild(node);
			} else if (currentDepth < previousDepth) {
				Node parent = getParentNodeOfDepth(previousNode, currentDepth).getParent();
				parent.appendChild(node);			
			} else {
				// Only happens when currentDepth > previousDepth
				appendChildPaddedForDepth(previousNode,node,tree,tempRoot);				
			}
			
			previousDepth = currentDepth;
			previousNode = node;				
		}

		return padRetVal;
	}
	
	private static Node getParentNodeOfDepth(Node node, int depth) {
		while (true) {
			if (node.getDepth() == depth) {
				return node;
			}
			node = node.getParent();		
		}
	}
	
	// Inserts empty nodes as needed to maintainthe depth of the childNode.
	private static void appendChildPaddedForDepth(Node parentNode, Node childNode, JoeTree tree, Node tempRoot) {
	
		//This should only happen when we are at the top of the tree.
		if (parentNode == null) {
			parentNode = new NodeImpl(tree,"");
			tempRoot.appendChild(parentNode);
		}
		
		int childDepth = childNode.getDepth();
		while ((parentNode.getDepth() + 1) < childDepth) {
			Node newNode = new NodeImpl(tree,"");
			parentNode.appendChild(newNode);
			parentNode = newNode;
			padRetVal = SUCCESS_MODIFIED;
		}
		
		parentNode.appendChild(childNode);
		return;
	}	
}