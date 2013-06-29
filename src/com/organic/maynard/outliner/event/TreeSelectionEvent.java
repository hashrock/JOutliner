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

package com.organic.maynard.outliner.event;

import com.organic.maynard.outliner.JoeTree;
import com.organic.maynard.outliner.dom.Document;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2002/07/16 21:25:28 $
 */

public class TreeSelectionEvent {
	
	// Constants
	public static final int UNKNOWN_SELECTION = -1;
	public static final int NODE_SELECTION = 1; // Synced to OutlineLayoutManger ICON til the port is finished.
	public static final int VALUE_SELECTION = 0; // Synced to OutlineLayoutManger TEXT til the port is finished.

	// Instance Variables
	private JoeTree tree = null;
	private int type = -1;
	
	// The Constructor
	public TreeSelectionEvent(JoeTree tree, int type) {
		setTree(tree);
		setType(type);
	}

	// Accessors
	public void setTree(JoeTree tree) {this.tree = tree;}
	public JoeTree getTree() {return this.tree;}
	
	public void setType(int type) {this.type = type;}
	public int getType() {return this.type;}
}