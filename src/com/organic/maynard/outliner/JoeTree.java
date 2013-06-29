/**
 * Copyright (C) 2002 Stan Krute <Stan@StanKrute.com>
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

public interface JoeTree 
	extends AttributeContainer {

	// accessors
	public JoeNodeList getVisibleNodes () ;
	public JoeNodeList getSelectedNodes () ;
	public Node getRootNode () ;
	public void setRootNode (Node someNode) ;	
	
	public boolean getRootNodeCommentState();
	public void setRootNodeCommentState(boolean comment);
	public boolean getRootNodeEditableState();
	public void setRootNodeEditableState(boolean editable);
	public boolean getRootNodeMoveableState();
	public void setRootNodeMoveableState(boolean moveable);

	public int getLineCountKey();
	public void incrementLineCountKey();
	
	public void reset () ;
	public void destroy () ;

	public int getLineCount();
	public int getCharCount();
	
	public Node getEditingNode();
	public void setEditingNode(Node editingNode);
	public void setEditingNode(Node editingNode, boolean updateAttPanel);

	public int getCursorMarkPosition();
	public void setCursorMarkPosition(int cursorMarkPosition);

	public void setComponentFocus(int componentFocus);
	public int getComponentFocus();

	public void setCursorPosition(int cursorPosition);
	public void setCursorPosition(int cursorPosition, boolean setMark);
	public int getCursorPosition();

	public Node getPrevNode(Node existingNode);
	public Node getNextNode(Node existingNode);
	public void addNode(Node node);
	public void removeNode(Node node);
	public void insertNode(Node node);
	public int insertNodeAfter(Node existingNode, Node newNode);
	public void insertNode(Node node, int index);
	public void insertNodeAndChildren(Node node, int index);
	public void promoteNode(Node currentNode, int currentNodeIndex);
	public void demoteNode(Node currentNode, Node targetNode, int currentNodeIndex);
	
	public int getNumberOfSelectedNodes();
	public void setSelectedNodesParent(Node node);
	public Node getSelectedNodesParent();
	public void clearSelection();
	public void addNodeToSelection(Node node);
	public void removeNodeFromSelection(Node node);
	public void selectRangeFromMostRecentNodeTouched(Node node);
	public Node getYoungestInSelection();
	public Node getOldestInSelection();
	public boolean isWholeDocumentSelected();

	public OutlinerDocument getDocument () ;
	public void setDocument (OutlinerDocument someDocument) ;
	public boolean isDocumentEmpty();

}