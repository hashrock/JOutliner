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
import java.io.*;
import java.awt.datatransfer.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.12 $, $Date: 2002/08/20 08:57:22 $
 */
 
public class NodeSet implements Cloneable {

	// Good Info: http://developer.java.sun.com/developer/bugParade/bugs/4066902.html

	// Instance Fields
	private NodeList nodes = new NodeList();
		

	// The Constructors
	public NodeSet() {}
	
	
	// Accessors
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public Node getNode(int i) {
		return nodes.get(i);
	}
	
	public void removeNode(int i) {
		nodes.remove(i);
	}
	
	public int getSize() {
		return nodes.size();
	}
	
	public boolean isEmpty() {
		if (getSize() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	// Cloneable Interface
	public Object clone() {
		NodeSet nodeSet = new NodeSet();
		
		for (int i = 0, limit = nodes.size(); i < limit; i++) {
			nodeSet.addNode(nodes.get(i).cloneClean());
		}
		
		return nodeSet;
	}

	
	// Overridden Methods
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0, limit = nodes.size(); i < limit; i++) {
			Node node = nodes.get(i);
			
			// Since a node may be a root node, and depthPaddedValue doesn't throw in root level text,
			// let's put it back in.
			if (node.isRoot()) {
				for (int j = 0, limit2 = node.getDepth(); j < limit2; j++) {
					buf.append(Preferences.DEPTH_PAD_STRING);
				}
				buf.append(node.getValue()).append(Preferences.LINE_END_STRING);
			}
			
			// for both root nodes and not-root-nodes: append the not-root nodes' text		[sk]
			node.depthPaddedValue(buf,  Preferences.LINE_END_STRING);
		}
		return buf.toString();
	}
}