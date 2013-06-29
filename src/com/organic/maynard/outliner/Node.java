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

public interface Node extends AttributeContainer {

	public void destroy();
	
	public Node cloneClean();

	// Statistics Methods
	public int getDecendantCount();
	public int getDecendantCharCount();
	
	public void adjustDecendantCount(int amount);
	public void adjustDecendantCharCount(int amount);
	
	public int getLineNumber();
	public void setLineNumber(int lineNumber); // I wish this could be protected.
	public int getLineNumber(int key);
	public void setLineNumberKey(int lineNumberKey); // I wish this could be protected.
	
	// Parent Methods
	public void setParent(Node node);
	public Node getParent();
	
	// Child Methods
	public int numOfChildren();
	public void appendChild(Node node);
	public void removeChild(Node node);
	public void removeChild(Node node, int index);
	
	public Node getChild(int i);
	public Node getFirstChild();
	public Node getLastChild();
	public Node getLastDecendent();
	public Node getLastViewableDecendent();
	public void insertChild(Node node, int i);
	
	public int getChildIndex(Node node);
	
	public boolean isLeaf();
	public boolean isRoot();
	public boolean isDecendantOf(Node node);
	
	// Tree Accessor Methods
	public JoeTree getTree();
	public void setTree(JoeTree tree, boolean recursive);
	
	// Comment Methods
	public static final int COMMENT_INHERITED = 0;
	public static final int COMMENT_TRUE = 1;
	public static final int COMMENT_FALSE = 2;
	
	public void setCommentState(int commentState);
	public int getCommentState();
	public boolean isComment();

	// Editability Methods
	public static final int EDITABLE_INHERITED = 0;
	public static final int EDITABLE_TRUE = 1;
	public static final int EDITABLE_FALSE = 2;
	
	public void setEditableState(int editableState);
	public int getEditableState();
	public boolean isEditable();

	// Moveability Methods
	public static final int MOVEABLE_INHERITED = 0;
	public static final int MOVEABLE_TRUE = 1;
	public static final int MOVEABLE_FALSE = 2;
	
	public void setMoveableState(int moveableState);
	public int getMoveableState();
	public boolean isMoveable();
	
	// Hoisting Methods
	public void setHoisted(boolean hoisted);
	public boolean isHoisted();
	public Node getHoistedAncestorOrSelf();
	
	// Visibility Methods
	public void setVisible(boolean visible);
	public boolean isVisible();

	// Selection Methods
	public void setSelected(boolean selected);
	public boolean isSelected();
	public boolean isAncestorSelected();

	// Depth Methods
	public void setDepth(int depth);
	public int getDepth();
	
	public void setDepthRecursively(int depth);
	
	// Navigation Methods
	public void setExpandedClean(boolean expanded);
	public void setExpanded(boolean expanded);
	public void setExpanded(boolean expanded, boolean collapseChildrenWhenCollapsing);
	public boolean isExpanded();
	public void ExpandAllSubheads();
	public void CollapseAllSubheads();
	public void expandAllAncestors();

	public int currentIndex();
	
	public boolean isFirstChild();
	public boolean isLastChild();
	
	public Node nextSibling();
	public Node prevSibling();
	public Node prevSiblingOrParent();
	public Node next();
	public Node nextUnSelectedNode();
	public Node nextSelectedSibling();
	public Node nextNode();
	public Node prev();
	public Node prevUnSelectedNode();
	public Node prevSelectedSibling();
	
	// Data Methods
	public void setValue(String str);
	public String getValue();

	// String Representation Methods
	public void depthPaddedValue(StringBuffer buf, String lineEndString);
	public void getRecursiveValue(StringBuffer buf, String lineEndString, boolean includeComments);
	
	public void getMergedValue(StringBuffer buf);
	public void getMergedValueWithSpaces(StringBuffer buf, int count);
}