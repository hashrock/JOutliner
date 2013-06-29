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
 * @version $Revision: 1.1 $, $Date: 2002/08/20 08:54:55 $
 */
 
public class NodeSetTransferable extends StringSelection implements Transferable {
	
	private NodeSet nodeSet = null;
	
	public static DataFlavor nsFlavor;
	
	static {
		try {
			nsFlavor = new DataFlavor(Class.forName("com.organic.maynard.outliner.NodeSet"), "NodeSet");
		} catch (ClassNotFoundException ex) {}
	}
	
	private static final int STRING = 0;
	private static final int NODESET = 1;
	
	private DataFlavor[] flavors = {
		DataFlavor.stringFlavor,
		nsFlavor
	};
	
	
	// The Constructors
	public NodeSetTransferable(NodeSet nodeSet) {
		super(nodeSet.toString());
		this.nodeSet = nodeSet;
	}


	// Transferable Interface
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (
			flavor.equals(flavors[STRING]) || 
			flavor.equals(flavors[NODESET])
		);    
	}
    
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(flavors[STRING])) {
			return nodeSet.toString();
			
		} else if (flavor.equals(flavors[NODESET])) {
			return nodeSet.clone();
			
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}